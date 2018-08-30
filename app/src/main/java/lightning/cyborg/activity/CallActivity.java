package lightning.cyborg.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.DialerFilter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import lightning.cyborg.R;

import java.text.ParseException;

import lightning.cyborg.voip.IncomingCallReceiver;
import lightning.cyborg.voip.UserInformation;
import lightning.cyborg.voip.SipSettings;
import lightning.cyborg.app.MyApplication;
/**
 * This class is used for the call creating and managing from user to user.
 * Created by Team Cyborg Lightning
 */

public class CallActivity extends AppCompatActivity {
    public String sipAddress = null;

    private String TAG= CallActivity.class.getSimpleName();
    public SipManager manager = null;
    public SipProfile me = null;
    public SipAudioCall call = null;
    public IncomingCallReceiver callReceiver;
    public Chronometer chronometer;

    private ImageButton hangUp;
    private ImageButton muteMic;
    private ImageButton speaker;
    private ImageView avatarImage;

    private UserInformation caller;
    private String callee;

    private String intentCallerUsername;
    private String intentCallerPassword;
    private String intentCalleeUsername;

    private final String SIP_SERVER = "sip.antisip.com";
    private final String PREFIX_CALLEE = "sip:";
    private final String POSTFIX_CALLEE = "@sip.antisip.com";

    private String message;

    private AlertDialog alertDialog;

    private Boolean isMuted;
    private Boolean isSpeaker;

    private String permission;
    private String type;
    private String avatar;
    private String title;
    private String chatRoomId;


    /**
     * Default method that is ran by app for the layout
     * @param savedInstanceState where user previously left off
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);
        Toolbar toolbar = (Toolbar) findViewById(R.id.bottomcalltoolbar);
        setSupportActionBar(toolbar);


        isMuted = false;
        isSpeaker = false;

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        avatarImage = (ImageView) findViewById(R.id.callAvatar);

        Intent intent = getIntent();

        intentCallerUsername = intent.getStringExtra("callerUsername");
        intentCallerPassword = intent.getStringExtra("callerPassword");
        intentCalleeUsername = intent.getStringExtra("calleeUsername");
        chatRoomId = intent.getStringExtra("chat_room_id");
        title = intent.getStringExtra("name");
        type =intent.getStringExtra("type");
        avatar =intent.getStringExtra("avatar");
        permission = intent.getStringExtra("permission");
        message = intent.getStringExtra("Calltype");

        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);

        Bitmap image = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(Integer.parseInt(avatar), 0));
        avatarImage.setImageBitmap(image);
        Log.d(TAG,image.toString());



        caller = new UserInformation(intentCallerUsername, intentCallerPassword, SIP_SERVER);//this.callee = callee;
        callee = PREFIX_CALLEE + intentCalleeUsername + POSTFIX_CALLEE;

        hangUp = (ImageButton) findViewById(R.id.HangUpBtn);
        speaker = (ImageButton) findViewById(R.id.speakerBtn);
        muteMic = (ImageButton) findViewById(R.id.muteMicBtn);





        /** Set up the intent filter.  This will be used to fire an
        * IncomingCallReceiver when someone calls the SIP address used by this
        * application.*/
        IntentFilter filter = new IntentFilter();

        filter.addAction("android.SipDemo.INCOMING_CALL");
        callReceiver = new IncomingCallReceiver();
        this.registerReceiver(callReceiver, filter);


        chronometer = (Chronometer) findViewById(R.id.chronometer);

        speakerListener();
        muteListener();


        initializeManager();
        initializeLocalProfile();

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Connecting to call...");
        alertDialog.setMessage("00:05");
        alertDialog.setCancelable(false);
        alertDialog.show();

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                alertDialog.setMessage("seconds remaining: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                alertDialog.dismiss();
                if(message.equals("makeCall")){
                    Log.d("CallActivity", "inside make call if statment");
                    initiateCall();
                }
                else if(message.equals("waitCall")){
                    Log.d("CallActivity", "inside make call else statment");
                }
            }
        }.start();

        hangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
                endCallIntent();
            }
        });

    }

    public CallActivity() {
        //
    }


    @Override
    public void onStart() {
        super.onStart();
        // When we get back from the preference setting Activity, assume
        // settings have changed, and re-login with new auth info.
        initializeManager();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.close();
        }

        closeLocalProfile();

        if (callReceiver != null) {
            this.unregisterReceiver(callReceiver);
        }
    }

    public void initializeManager() {
        if(manager == null) {
            manager = SipManager.newInstance(this);
        }
        Log.d("Reached here", "initializeManager()");
        initializeLocalProfile();
        Log.d("Reached here", "initializeManager() fin");
    }

    /**
     * Logs you into your SIP provider, registering this device as the location to
     * send SIP calls to for your SIP address.
     */
    public void initializeLocalProfile() {
        /*if (manager == null) {
            return;
        }*/

        if (me != null) {
            closeLocalProfile();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String username = prefs.getString("namePref", "");
        String domain = prefs.getString("domainPref", "");
        String password = prefs.getString("passPref", "");

        /*if (username.length() == 0 || domain.length() == 0 || password.length() == 0) {
            showDialog(UPDATE_SETTINGS_DIALOG);
            return;
        }*/

        try {
            Log.d("caller username: ", caller.getUsername());
            Log.d("caller password: ", caller.getPassword());
            Log.d("caller server: ", caller.getServer());
            SipProfile.Builder builder = new SipProfile.Builder(caller.getUsername(), caller.getServer());
            builder.setPassword(caller.getPassword());
            me = builder.build();

            Intent i = new Intent();
            i.setAction("android.SipDemo.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
            manager.open(me, pi, null);


            // This listener must be added AFTER manager.open is called,
            // Otherwise the methods aren't guaranteed to fire.

            manager.setRegistrationListener(me.getUriString(), new SipRegistrationListener() {
                public void onRegistering(String localProfileUri) {
                    updateStatus("Registering with SIP Server...");
                }

                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    updateStatus("Ready");
                }

                public void onRegistrationFailed(String localProfileUri, int errorCode,
                                                 String errorMessage) {
                    updateStatus("Registration failed.  Please check settings.");
                }
            });
        } catch (ParseException pe) {
            updateStatus("Connection Error.");
        } catch (SipException se) {
            updateStatus("Connection error.");
        }
    }

    /**
     * Closes out your local profile, freeing associated objects into memory
     * and unregistering your device from the server.
     */
    @SuppressLint("LongLogTag")
    public void closeLocalProfile() {
        if (manager == null) {
            return;
        }
        try {
            if (me != null) {
                manager.close(me.getUriString());
            }
        } catch (Exception ee) {
            Log.d("onDestroy", "Failed to close local profile.", ee);
        }
    }

    /**
     * Make an outgoing call.
     */
    @SuppressLint("LongLogTag")
    public void initiateCall() {

        updateStatus(sipAddress);

        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing call, don't
                // forget to set up a listener to set things up once the call is established.
                @Override
                public void onCallEstablished(SipAudioCall call) {
                    Log.d("AAAAAAA", "Beginning of call established");
                    call.startAudio();
                    updateStatus(call);
                    chronometer.start();
                    Log.d("AAAAAAA", "End of call established");
                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                    Log.d("AAAAAAA", "Beginning of call ended");
                    updateStatus("Ready.");
                    chronometer.stop();
                    endCallIntent();
                    Log.d("AAAAAAA", "End of call");
                }

                @Override
                public void onCallHeld(SipAudioCall call){
                    Log.d("AAAAAAA", "Beginning of call held");
                    chronometer.stop();
                    call.toggleMute();
                    Log.d("AAAAAAA", "End of call held");
                }

                @Override
                public void onCallBusy(SipAudioCall call){
                    try {
                        Log.d("AAAAAAA", "Beginning of call busy");
                        call.endCall();
                        Log.d("AAAAAAA", "End of call busy");
                    } catch (SipException e) {
                        e.printStackTrace();
                    }
                }
            };

            Log.i("Reached", "here");
            call = manager.makeAudioCall(me.getUriString(), callee, listener, 30);
            Log.i("Reached", "here after call");

        }
        catch (Exception e) {
            Log.i("InitiateCall", "Error when trying to close manager.", e);
            Toast.makeText(CallActivity.this, "Error connecting to call", Toast.LENGTH_SHORT).show();
            endCallIntent();

            if (me != null) {
                try {
                    manager.close(me.getUriString());
                } catch (Exception ee) {
                    Log.i("InitiateCall",
                            "Error when trying to close manager 2", ee);
                    ee.printStackTrace();
                }
            }
            if (call != null) {
                call.close();
            }
        }
    }

    public void endCall(){
        try {
            call.endCall();
            //  endCallIntent();
        } catch (SipException e) {
            e.printStackTrace();
        }
    }

    public void endCallIntent() {
        Intent intent = new Intent(this, UserHomepage.class);
        intent.putExtra("chatRoomId",chatRoomId);
        intent.putExtra("name",title);
        intent.putExtra("type",type);
        intent.putExtra("avatar",avatar);
        intent.putExtra("permission",permission);
        startActivity(intent);
    }

    public void muteListener(){

        muteMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMuted == false) {
                    call.toggleMute();
                    isMuted = true;
                    muteMic.setImageResource(R.drawable.ic_mic_off_white_36dp);
                } else {
                    call.toggleMute();
                    isMuted = false;
                    muteMic.setImageResource(R.drawable.ic_mic_white_36dp);
                }
            }
        });

    }

    public void speakerListener(){

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSpeaker == false) {
                    call.setSpeakerMode(true);
                    isSpeaker = true;
                    speaker.setImageResource(R.drawable.ic_volume_up_white_36dp);
                } else {
                    call.setSpeakerMode(false);
                    isSpeaker = false;
                    speaker.setImageResource(R.drawable.ic_volume_down_white_36dp);
                }
            }
        });


    }

    /**
     * Updates the status box at the top of the UI with a messege of your choice.
     * @param status The String to display in the status box.
     */
    public void updateStatus(final String status) {
        // Be a good citizen.  Make sure UI changes fire on the UI thread.
        this.runOnUiThread(new Runnable() {
            public void run() {
               /* TextView labelView = (TextView) findViewById(R.id.sipLabel);
                labelView.setText(status);*/
            }
        });
    }

    /**
     * Updates the status box with the SIP address of the current call.
     * @param call The current, active call.
     */
    public void updateStatus(SipAudioCall call) {
        String useName = call.getPeerProfile().getDisplayName();
        if(useName == null) {
            useName = call.getPeerProfile().getUserName();
        }
        updateStatus(useName + "@" + call.getPeerProfile().getSipDomain());
    }



}
