package lightning.cyborg.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lightning.cyborg.R;
import lightning.cyborg.voip.IncomingGui;
import lightning.cyborg.adapter.ChatRoomThreadAdapter;
import lightning.cyborg.app.Config;
import lightning.cyborg.app.EndPoints;
import lightning.cyborg.app.MyApplication;
import lightning.cyborg.app.VolleyQueue;
import lightning.cyborg.gcm.NotificationUtils;
import lightning.cyborg.model.Message;
import lightning.cyborg.model.User;

/**
 * This class represents the activity responsible for chats
 * Created by Team Cyborg Lightning
 */

public class ChatRoomActivity extends AppCompatActivity {

    private String TAG = ChatRoomActivity.class.getSimpleName();

    private String chatRoomId;
    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText inputMessage;
    private Button btnSend;
    private String permission;
    private String type;
    private String avatar;
    private String title;
    private String sipUsername;
    private String sipPassword;
    private String sipCaleeUsername;

    private AlertDialog alertDialog;

    private MenuItem callButton;
    private MenuItem addFriend;


    /**
     * Default method that is ran by app
     * @param savedInstanceState  where user previously left off
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (Button) findViewById(R.id.btn_send);

        Intent intent = getIntent();
        chatRoomId = intent.getStringExtra("chat_room_id");
        title = intent.getStringExtra("name");
        type =intent.getStringExtra("type");
        avatar =intent.getStringExtra("avatar");
        permission = intent.getStringExtra("permission");



        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();

        // self user id is to identify the message owner
        String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();

        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push message is received
                    handlePushNotification(intent);


                }
            }
        };


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        sendMessage();

            }
        });
        fetchChatThread();
    }


    @Override
    protected void onResume() {

        super.onResume();
        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        NotificationUtils.clearNotifications();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     * */
    private void handlePushNotification(Intent intent) {
        int type1 = intent.getIntExtra("type", -1);
        if(type1 == Config.PUSH_TYPE_CALL_USER) {
            String message = intent.getStringExtra("message");
            if(message.equals("callRequest")) {
                dialogCallReceiver(this,false);
            }
            else if(message.equals("callAccepted")){
                Intent intent1 = new Intent(this,CallActivity.class);
                intent1.putExtra("Calltype","makeCall");
                intent1.putExtra("callerUsername", sipUsername);
                intent1.putExtra("callerPassword", sipPassword);
                intent1.putExtra("calleeUsername", sipCaleeUsername);
                intent1.putExtra("chatRoomId", chatRoomId);
                intent1.putExtra("name", title);
                intent1.putExtra("chat", type);
                intent1.putExtra("avatar", avatar);
                intent1.putExtra("permission", permission);
                startActivity(intent1);
                finish();
            }
            else if(message.equals("requestCanceled")){
                if(alertDialog!=null){
                    alertDialog.cancel();
                }
              alertDialog = new AlertDialog.Builder(ChatRoomActivity.this)
                        .setTitle("call canceled")
                        .setMessage("call has been canceled")
                        .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }

        }

        else {

            Message message = (Message) intent.getSerializableExtra("message");
            String chatRoomId = intent.getStringExtra("chat_room_id");
            if (message != null && chatRoomId != null && chatRoomId.equals(this.chatRoomId)) {
                messageArrayList.add(message);
                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > 1) {
                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                }
            }
        }
    }

    public void dialogCallReceiver(final Context context, final boolean typeOfgcm){
        if (typeOfgcm) {
          alertDialog =  new AlertDialog.Builder(context)
                    .setTitle("Waiting For Response")
                    .setIcon(android.R.drawable.sym_call_incoming)
                    .setMessage("waiting for  " + title+" to respond")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            IncomingCall(context, "requestCanceled");
                            //TODO more php
                        }
                    })
                    .show();
        }
        else {
           alertDialog = new AlertDialog.Builder(context)
                    .setTitle("Incoming call")
                    .setIcon(android.R.drawable.sym_call_incoming)
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent1 = new Intent(context, CallActivity.class);
                            intent1.putExtra("Calltype", "waitCall");
                            intent1.putExtra("callerUsername", sipUsername);
                            intent1.putExtra("callerPassword", sipPassword);
                            intent1.putExtra("calleeUsername", sipCaleeUsername);
                            intent1.putExtra("chatRoomId", chatRoomId);
                            intent1.putExtra("name", title);
                            intent1.putExtra("chat", type);
                            intent1.putExtra("avatar", avatar);
                            intent1.putExtra("permission", permission);
                            IncomingCall(context, "callAccepted");

                            startActivity(intent1);
                            finish();
                        }
                    })

                    .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            IncomingCall(context, "requestCanceled");

                        }})
                    .show();
        }
    }

    /**
     * Posting a new message in chat room
     * will make an http call to our server. Our server again sends the message
     * to all the devices as push notification
     * */
    private void sendMessage() {

        //Message to be sent
        final String message = this.inputMessage.getText().toString().trim();


        //if message is empty
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        //url of request
        String endPoint = EndPoints.CHAT_ROOM_MESSAGE; //chatRoomId);

        Log.e(TAG, "endpoint: " + endPoint);

        //
        this.inputMessage.setText("");

        //volley request
        StringRequest strReq = new StringRequest(Request.Method.POST,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    //convert response to json
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        JSONObject commentObj = obj.getJSONObject("message");

                        String commentId = commentObj.getString("message_id");
                        String commentText = commentObj.getString("message");
                        String createdAt = commentObj.getString("created_at");

                        JSONObject userObj = obj.getJSONObject("user");
                        String userId = userObj.getString("user_id");
                        String userName = userObj.getString("name");
                        User user = new User(userId, userName, null);

                        Message message = new Message();
                        message.setId(commentId);
                        message.setMessage(commentText);
                        message.setCreatedAt(createdAt);
                        message.setUser(user);

                        //message added to Array
                        messageArrayList.add(message);

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            // scrolling to bottom of the recycler view
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                inputMessage.setText(message);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", MyApplication.getInstance().getPrefManager().getUser().getId());
                params.put("message", message);
                params.put("chat_room_id", chatRoomId);

                Log.e(TAG, "Params: " + params.toString());

                return params;
            };
        };


        // disabling retry policy so that it won't make
        // multiple http calls
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        strReq.setRetryPolicy(policy);

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }


    /**
     * Method used to display incoming call and passes the intents
     * @param context  where user previously left off
     * @param message  last message sent
     */

    public void IncomingCall(final Context context,String message){

        final Context context1 =context;
        //parameters to post to php file
        final Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", MyApplication.getInstance().getPrefManager().getUser().getId());
        params.put("chat_room_id", chatRoomId.toString());
        params.put("message", message);

        //request to insert the user into the mysql database using php
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.INCOMING_CALL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            // check for error flag
                            if (obj.getBoolean("error") == false) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON failed to parse: ", response);
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.d("VolleyError at url ", EndPoints.FETCH_SIP);
            }
        }
        ){
            //Parameters inserted
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };
        //put the request in the static queue
        VolleyQueue.getInstance(this).addToRequestQueue(request);
    }
    /**
     * Fetching all the messages of a single chat room
     * */
    private void fetchChatThread() {

        String endPoint = EndPoints.CHAT_THREAD;

        final Map<String, String> params = new HashMap<>();
        params.put("chat_room_id", chatRoomId);
        params.put("user_id",MyApplication.getInstance().getPrefManager().getUser().getId());


        Log.e(TAG, "endPoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                endPoint, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);


                    // check for error
                    if (obj.getBoolean("error") == false) {

                        // JSONArray chatRoomObj = obj.getJSONArray("chat_info");
                        // JSONObject chatob = (JSONObject) chatRoomObj.get(0);
                        //   type =chatob.getString("type");

                        JSONArray commentsObj = obj.getJSONArray("messages");

                        for (int i = 0 ; i <commentsObj.length(); ++i) {
                            JSONObject commentObj = (JSONObject) commentsObj.get(i);

                            String commentId = commentObj.getString("message_id");
                            String commentText = commentObj.getString("message");
                            String createdAt = commentObj.getString("created_at");

                            JSONObject userObj = commentObj.getJSONObject("user");
                            String userId = userObj.getString("user_id");
                            String userName = userObj.getString("username");
                            User user = new User(userId, userName, null);

                            Message message = new Message();
                            message.setId(commentId);
                            message.setMessage(commentText);
                            message.setCreatedAt(createdAt);
                            message.setUser(user);


                            messageArrayList.add(message);
                        }

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            //Parameters inserted
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };


        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }


    /**
     * prevents the chat from being displayed
     */
    public void hideChat(){

        final Map<String, String> params = new HashMap<>();
        params.put("chat_room_id", chatRoomId);
        params.put("user_id", MyApplication.getInstance().getPrefManager().getUser().getId());



        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.HIDE_CHAT, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {

                        toUserHomePageActivity();

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            //Parameters inserted
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };


        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    public void fetchSip(){

        //parameters to post to php file
        final Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", MyApplication.getInstance().getPrefManager().getUser().getId());
        params.put("chat_room_id", chatRoomId.toString());

        //request to insert the user into the mysql database using php
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.FETCH_SIP,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            // check for error flag
                            if (obj.getBoolean("error") == false) {
                                Log.d(TAG, "no error");

                                //CALLER USERNAME
                                sipUsername = obj.getString("user_sip_username");
                                sipPassword = obj.getString("user_sip_password");

                                //CALLEE USERNAME
                                sipCaleeUsername = obj.getString("calling_sip_username");

                                if(!(sipUsername.equals("null") || sipPassword.equals("null") || sipCaleeUsername.equals("null"))){
                                    callButton.setEnabled(true);
                                    callButton.setVisible(true);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON failed to parse: ", response);
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.d("VolleyError at url ", EndPoints.FETCH_SIP);
            }
        }
        ){
            //Parameters inserted
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };
        //put the request in the static queue
        VolleyQueue.getInstance(this).addToRequestQueue(request);
    }

    private void toUserHomePageActivity(){
        Intent intent = new Intent(this,UserHomepage.class);

        Bundle bundle = new Bundle();
        if(type.equals("f")) {
            bundle.putString("FragmentNum", "2");
        }
        else if(type.equals("n")){
            bundle.putString("FragmentNum", "3");
        }

        intent.replaceExtras(bundle);
        startActivity(intent);
        finish();
    }


    public void blockUser(final Context context){

        //parameters to post to php file
        final Map<String, String> params = new HashMap<String, String>();
        params.put("cur_user_id", MyApplication.getInstance().getPrefManager().getUser().getId());
        params.put("chat_room_id", chatRoomId.toString());

        //request to insert the user into the mysql database using php
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.BLOCK_USER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            // check for error flag
                            if (obj.getBoolean("error") == false) {
                                Log.d(TAG, "no error");
                                toUserHomePageActivity();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON failed to parse: ", response);
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError at url ", EndPoints.FETCH_SIP);
            }
        }
        ){
            //Parameters inserted
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };
        //put the request in the static queue
        VolleyQueue.getInstance(this).addToRequestQueue(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatroom_menu, menu);
        addFriend = menu.findItem(R.id.action_addfriend);
        if(type.equals("f")){
            addFriend.setIcon(R.drawable.ic_person_remove_white_24dp);
            //addFreind.setVisible(false);
        }
        else{
            addFriend.setIcon(R.drawable.ic_person_add_white_24dp);
        }
        callButton = menu.findItem(R.id.action_calluser);
        callButton.setVisible(false);
        fetchSip();
        //fetchSip;
        return true;
    }
    /**
     * Change the change room from freinds to normal or vise versa
     * @param type1 new type of the chatroom
     */
    public void changeChatRoomType(final String type1){
        String endPoint = EndPoints.ADD_FREIND;

        final Map<String, String> params = new HashMap<>();
        params.put("chat_room_id", chatRoomId);
        params.put("user_id",MyApplication.getInstance().getPrefManager().getUser().getId());
        params.put("type",type1);

        Log.e(TAG, "endPoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                endPoint, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        if(type.equals("n")){
                            type="f";
                            addFriend.setIcon(R.drawable.ic_person_remove_white_24dp);
                        }
                        else if(type.equals("f")){
                            type ="n";
                            addFriend.setIcon(R.drawable.ic_person_add_white_24dp);
                        }


                    }
                    else {
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            //Parameters inserted
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };


        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case android.R.id.home:
                toUserHomePageActivity();
                break;
            case R.id.action_calluser:
                dialogCallReceiver(this,true);
                IncomingCall(this,"callRequest");
                break;
            case R.id.action_viewprofile:
                //hideChat();
                break;
            case R.id.action_addfriend:
                if(type.equals("f")){ changeChatRoomType("n");}
                else { changeChatRoomType("f");}
                break;
            case R.id.action_hideChat:
                hideChat();
                break;
            case R.id.action_blockuser:
                blockUser(this);
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }
    @Override
    public void onBackPressed() {
        toUserHomePageActivity();
    }
}
