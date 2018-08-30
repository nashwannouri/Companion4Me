package lightning.cyborg.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import lightning.cyborg.activity.ChatRoomActivity;
import lightning.cyborg.activity.UserHomepage;
import lightning.cyborg.app.Config;
import lightning.cyborg.app.MyApplication;
import lightning.cyborg.model.Message;
import lightning.cyborg.model.User;


public class MyGcmPushReceiver extends GcmListenerService {

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;

    /**
     * Called when message is received.
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        Log.d(TAG,"messageReceived");

        String title = bundle.getString("title");
        Boolean isBackground = Boolean.valueOf(bundle.getString("is_background"));
        String flag = bundle.getString("flag");
        String data = bundle.getString("data");

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "title: " + title);
        Log.d(TAG, "isBackground: " + isBackground);
        Log.d(TAG, "flag: " + flag);
        Log.d(TAG, "data: " + data);

        if (flag == null)
            return;

        if(MyApplication.getInstance().getPrefManager().getUser() == null){
            // user is not logged in, skipping push notification
            Log.e(TAG, "user is not logged in, skipping push notification");
            return;
        }

        if (flag.equals("PUSH_FLAG_USER")) {
            Log.d(TAG,"fLag is PUSH_FLAG_USER");
            processUserMessage(title, isBackground, data);

        }
        else if(flag.equals("PUSH_FLAG_CHAT_REQUEST")) {
            Log.d(TAG, "fLag is PUSH_FLAG_CHAT_REQUEST");
            // verifying whether the app is in background or foreground
            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("type", Config.PUSH_TYPE_CHAT_REQUEST);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils();
                notificationUtils.playNotificationSound();
            } else {

                // app is in background. show the message in notification try
                //Intent resultIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                //resultIntent.putExtra("chat_room_id", chatRoomId);
                //  showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
            }
        }else if(flag.equals("PUSH_FLAG_CALL_USER")){
            // app is in foreground, broadcast the push message
            JSONObject datObj = null;
            try {
                datObj = new JSONObject(data);
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("type", Config.PUSH_TYPE_CALL_USER);
                pushNotification.putExtra("message",datObj.getString("message"));
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

//

    /**
     * Processing user specific push message
     * It will be displayed with / without image in push notification tray
     * */
    private void processUserMessage(String title, boolean isBackground, String data) {
        Log.d("TAG","inside processUserMessage");
        if (!isBackground) {

            Log.d("TAG","inside processUserMessage first if");
            try {
                Log.d("TAG","inside processUserMessage start of try");
                JSONObject datObj = new JSONObject(data);

                String chatRoomId = datObj.getString("chat_room_id");
                String visibilityChange = datObj.getString("vis_change");
                String chatType =datObj.getString("chat_type");
                JSONObject mObj = datObj.getJSONObject("message");
                Message message = new Message();
                message.setMessage(mObj.getString("message"));
                message.setId(mObj.getString("message_id"));
                message.setCreatedAt(mObj.getString("created_at"));
                JSONObject uObj = datObj.getJSONObject("user");

                Log.d("TAG","before second if");
                // skip the message if the message belongs to same user as
                // the user would be having the same message when he was sending
                // but it might differs in your scenario
                if (uObj.getString("user_id").equals(MyApplication.getInstance().getPrefManager().getUser().getId())) {
                    Log.e(TAG, "Skipping the push message as it belongs to same user");
                    return;
                }
                Log.d("TAG","inside processUserMessage end of second if");

                User user = new User();
                user.setId(uObj.getString("user_id"));
                user.setEmail(uObj.getString("email"));
                user.setName(uObj.getString("name"));
                message.setUser(user);

                Log.d("TAG", "inside processUserMessage begin of third if");
                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                    Log.d(TAG,"application is not in background");
                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type", Config.PUSH_TYPE_CHATROOM);
                    pushNotification.putExtra("message", message);
                    pushNotification.putExtra("chat_room_id", chatRoomId);
                    pushNotification.putExtra("chat_type",chatType);
                    pushNotification.putExtra("visibilityChange",visibilityChange);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils();
                    notificationUtils.playNotificationSound();
                } else {

                    Log.d(TAG,"application is in background");
                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(), UserHomepage.class);
                    resultIntent.putExtra("chat_room_id", chatRoomId);

                    showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
                }
            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }

    /**
     * Showing notification with text only
     * */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     * */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
