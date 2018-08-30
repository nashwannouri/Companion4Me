package lightning.cyborg.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import lightning.cyborg.R;
import lightning.cyborg.adapter.ChatRoomsAdapter;
import lightning.cyborg.app.Config;
import lightning.cyborg.app.EndPoints;
import lightning.cyborg.app.MyApplication;
import lightning.cyborg.fragment.DiscoveryFragment;
import lightning.cyborg.fragment.FriendsListFragment;
import lightning.cyborg.fragment.UserProfileFragment;
import lightning.cyborg.fragment.chatRoomFragment;
import lightning.cyborg.gcm.GcmIntentService;
import lightning.cyborg.gcm.NotificationUtils;
import lightning.cyborg.model.ChatRoom;
import lightning.cyborg.model.Message;
import lightning.cyborg.setting.AboutUs;
import lightning.cyborg.setting.SettingsEditSipUserInfo;
import lightning.cyborg.setting.UserDetails;

/**
 * This class allows blocked users to be viewed
 * Created by Team Cyborg Lightning
 */
public class UserHomepage extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String TAG = UserHomepage.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ArrayList<ChatRoom> normalChatRoomArrayList;
    private ArrayList<ChatRoom> freindsChatRoomArrayList;
    private ChatRoomsAdapter normalChatAdapter;
    private ChatRoomsAdapter freindChatAdapter;
    private int onChatFragment = 0;
    private boolean requestBoth;

    private static final long RIPPLE_DURATION = 500;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.root)
    FrameLayout root;
    @InjectView(R.id.content_hamburger)
    View contentHamburger;

    /**
     * Default method that is ran by app
     * @param savedInstanceState  where user previously left off
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);
        ButterKnife.inject(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        //Auto-login feature if user has logged in  before
        if (MyApplication.getInstance().getPrefManager().getUser() == null) {
            launchLoginActivity();
        }

        normalChatRoomArrayList = new ArrayList<>();
        freindsChatRoomArrayList = new ArrayList<>();

        normalChatAdapter = new ChatRoomsAdapter(this, normalChatRoomArrayList, "n");
        freindChatAdapter = new ChatRoomsAdapter(this, freindsChatRoomArrayList, "f");
        /**
         * Broadcast receiver calls in two scenarios
         * 1. gcm registration is completed
         * 2. when new push notification is received
         * */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    //   subscribeToGlobalTopic();

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL
                    Log.d(TAG, "GCM registration id is sent to our server");

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    handlePushNotification(intent);
                }
            }
        };

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        /**
         * Always check for google play services availability before
         * proceeding further with GCM
         * */
        if (checkPlayServices()) {
            registerGCM();
        }
        try {
            Bundle b = getIntent().getExtras();
            String key = b.getString("FragmentNum");
            viewPager.setCurrentItem(Integer.parseInt(key));
        } catch (Exception e) {
            Log.d(TAG, "no bundle was attached");
        }
        normalChatRoomArrayList.clear();
        freindsChatRoomArrayList.clear();
        fetchChatRooms("n");
        fetchChatRooms("f");
    }

    /**
     * Method that fetches chatrooms of the user
     * @param type the type of chatroom, normal or friend
     */
    private void fetchChatRooms(final String type) {
        final String TYPE = type;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.CHAT_ROOMS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response: " + response);
                try {
                    JSONObject obj = new JSONObject(response);
                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        if(type.equals("n")){
                            normalChatRoomArrayList.clear();
                        }
                        else if(type.equals("f")){
                            freindsChatRoomArrayList.clear();
                        }
                        JSONArray chatRoomsArray = obj.getJSONArray("chat_rooms");
                        for (int i = 0; i < chatRoomsArray.length(); i++) {
                            JSONObject chatRoomsObj = (JSONObject) chatRoomsArray.get(i);
                            ChatRoom cr = new ChatRoom();
                            cr.setId(chatRoomsObj.getString("chat_room_id"));
                            cr.setName(chatRoomsObj.getString("name"));
                            cr.setPermission(chatRoomsObj.getString("permission"));
                            cr.setLastMessage(" ");
                            cr.setUnreadCount(Integer.parseInt(chatRoomsObj.getString("unread_count")));
                            cr.setTimestamp(chatRoomsObj.getString("created_at"));
                            cr.setVisibility(chatRoomsObj.getString("visibility"));
                            Log.d("FAFFa", cr.getPermission());
                            cr.setAvatar(chatRoomsObj.getString("avatar"));
                            Log.d("FAFFa", cr.getPermission() + "avtarar" + cr.getAvatar());
                            if(chatRoomsObj.getString("last_message").equals("null")){}
                            else{
                                cr.setLastMessage(chatRoomsObj.getString("last_message"));
                            }
                            if (cr.getPermission().equals("n") || cr.getVisibility().equals("n")) {

                            } else {
                                if (TYPE.equals("n")) {
                                    normalChatRoomArrayList.add(cr);
                                } else if (TYPE.equals("f")) {
                                    freindsChatRoomArrayList.add(cr);
                                }
                            }
                        }

                        if(TYPE.equals("n")){
                            normalChatAdapter.notifyDataSetChanged();
                        }else{

                            freindChatAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // error in fetching chat rooms
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("type", TYPE);
                params.put("user_id", MyApplication.getInstance().getPrefManager().getUser().getId());

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    /**
     * This method handles new push notification
     * @param intent the variable that will receive the notifications
     */
    private void handlePushNotification(Intent intent) {
        int type = intent.getIntExtra("type", -1);

        // if the push is of chat room message
        // simply update the UI unread messages count
        if (type == Config.PUSH_TYPE_CHATROOM) {
            Message message = (Message) intent.getSerializableExtra("message");
            String chatRoomId = intent.getStringExtra("chat_room_id");
            String chatType = intent.getStringExtra("chat_type");
            String visibilityChange = intent.getStringExtra("visibilityChange");

            Log.d("ffsaf", "insdie handle");
            if (message != null && chatRoomId != null) {
                if (chatType.equals("n")) {
                    if (visibilityChange.equals("true")) {
                        normalChatRoomArrayList.clear();
                        fetchChatRooms("n");
                    } else {
                        updateRow(chatRoomId, message, normalChatRoomArrayList, normalChatAdapter);
                    }
                } else if (chatType.equals("f")) {
                    if (visibilityChange.equals("true")) {
                        freindsChatRoomArrayList.clear();
                        fetchChatRooms("f");
                    } else {
                        updateRow(chatRoomId, message, freindsChatRoomArrayList, freindChatAdapter);
                    }
                }
            }
        }
        else if(type == Config.PUSH_TYPE_CHAT_REQUEST){
            Log.d("AAAAAPUSH_TYPE_CHAT", "recieved it");
            requestBoth=true;
            normalChatRoomArrayList.clear();
            fetchChatRooms("n");
            freindsChatRoomArrayList.clear();
            fetchChatRooms("f");

        }

    }

    /**
     * Updates the chat list unread count and the last message
     * @param chatRoomId id of the chatroom
     * @param message the message to be sent
     * @param normalChatAdapter adaptor for the normal chatroom
     * @param normalChatRoomArrayList ArrayList for normal chatroom
     */
    private void updateRow(String chatRoomId, Message message, ArrayList<ChatRoom> normalChatRoomArrayList, ChatRoomsAdapter normalChatAdapter) {
        for (ChatRoom cr : normalChatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                int index = normalChatRoomArrayList.indexOf(cr);
                cr.setLastMessage(message.getMessage());
                cr.setUnreadCount(cr.getUnreadCount() + 1);
                normalChatRoomArrayList.remove(index);
                normalChatRoomArrayList.add(index, cr);
                break;
            }
        }
        normalChatAdapter.notifyDataSetChanged();
    }

    /**
     * This method launches the LogInActivity for the user
     */
    private void launchLoginActivity() {
        Intent intent = new Intent(UserHomepage.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Go to the chat room
     * @param chatRoomId   the id of the chat room
     * @param chatRoomName the name of the chat room
     * @param type         the type of chatroom e.g friends or normal
     * @param permission the permission to be sent to the chatroom activity
     * @param avatar the avatar of the user
     */
    public void chatRoomActivityIntent(String chatRoomId, String chatRoomName, String type, String permission, String avatar) {
        Intent intent = new Intent(UserHomepage.this, ChatRoomActivity.class);
        intent.putExtra("chat_room_id", chatRoomId);
        intent.putExtra("name", chatRoomName);
        intent.putExtra("type", type);
        intent.putExtra("permission", permission);
        intent.putExtra("avatar", avatar);
        for (ChatRoom cr : normalChatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                cr.setUnreadCount(0);
                break;
            }
        }
        normalChatAdapter.notifyDataSetChanged();
        startActivity(intent);
    }

    /**
     * This method sets up fragments
     * @param viewPager is the ViewPager to be added
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter PageAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        PageAdapter.addFragment(new UserProfileFragment(), "Profile");
        PageAdapter.addFragment(new DiscoveryFragment(), "Search");
        PageAdapter.addFragment(new FriendsListFragment(), "Friends");
        PageAdapter.addFragment(new chatRoomFragment(), "Matches");
        viewPager.setAdapter(PageAdapter);
    }

    /**
     * This method allows fragments to be used
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> FragmentList = new ArrayList<>();
        private final List<String> FragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            if (position == 3) {
                onChatFragment = 1;
                Fragment f = FragmentList.get(position);
                Bundle b = new Bundle();
                b.putSerializable("data", normalChatRoomArrayList);
                b.putParcelable("adapter", normalChatAdapter);
                Log.d("dta", normalChatRoomArrayList.toArray().toString());
                f.setArguments(b);
                return f;
            }
            if (position == 2) {
                onChatFragment = 5;
                Fragment f = FragmentList.get(position);
                Bundle b = new Bundle();
                b.putSerializable("data", freindsChatRoomArrayList);
                b.putParcelable("adapter", freindChatAdapter);
                Log.d("dta", freindsChatRoomArrayList.toArray().toString());
                f.setArguments(b);
                return f;
            } else {
                onChatFragment = 0;
            }
            Log.d("onChatFragment", onChatFragment + "");
            return FragmentList.get(position);
        }

        @Override
        public int getCount() {
            return FragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            FragmentList.add(fragment);
            FragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return FragmentTitleList.get(position);
        }
    }

    /**
     * This method handles actions to be done when the app goes into onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clearing the notification tray
        NotificationUtils.clearNotifications();
    }

    /**
     * This method handles actions to be done when the app goes into onPause()
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * This method registersGCM for the user so that messaging can be done
     */
    public void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    /**
     * This method checks whether the phone is compatible with messaging API used
     * @return true or false for the compatibility check
     */
    public boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Navigates user to UserDetails
     * @param view the container that calls this method
     */
    public void clickUserProfile(View view) {
        Intent intent = new Intent(UserHomepage.this, UserDetails.class);
        startActivity(intent);
    }

    /**
     * Navigates user to aboutUS
     * @param view the container that calls this method
     */
    public void aboutUS(View view) {
        Intent intent = new Intent(UserHomepage.this, AboutUs.class);
        startActivity(intent);
    }

    /**
     * Method allows user to enter SIP registration page
     * @param view the container that calls this method
     */
    public void sipRegis(View view) {

        Intent intent = new Intent(UserHomepage.this, SettingsEditSipUserInfo.class);
        startActivity(intent);
    }

    /**
     * Method allows user to log out
     * @param view the container that calls this method
     */
    public void logout(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        MyApplication.getInstance().logout();
        startActivity(intent);
    }

    /**
     * Navigates user to ViewBlockedUsers
     * @param view the container that calls this method
     */
    public void blockedUser(View view) {
        Intent intent = new Intent(this, ViewBlockedUsers.class);
        startActivity(intent);
    }

    /**
     * Method that handles what is done when the back-button is pressed on the phone
     */
    @Override
    public void onBackPressed() {
    }
}