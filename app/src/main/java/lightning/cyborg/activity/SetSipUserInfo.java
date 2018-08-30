package lightning.cyborg.activity;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import lightning.cyborg.R;
import lightning.cyborg.app.EndPoints;
import lightning.cyborg.app.MyApplication;
import lightning.cyborg.app.VolleyQueue;

import lightning.cyborg.fragment.UserProfileFragment;
import lightning.cyborg.setting.SetingsRegisterSipAccount;

/**
 *This class make the connection between the databases and the app regarding the SIP account info of the user.
 * Created by Team Cyborg Lightning
 */
public class SetSipUserInfo extends AppCompatActivity {
    //UI ELEMENTS
    private TextView userTV;
    private TextView passwordTV;
    private ImageButton  doneBtn;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_edit_sip_profile);
        //INITIALIZATION OF UI ELEMENTS
        userTV = (TextView) findViewById(R.id.usernameTextField);
        passwordTV = (TextView) findViewById(R.id.passwordTextField);
        doneBtn = (ImageButton) findViewById(R.id.doneBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSipUserInfo(v);
            }
        });

    }

    //Updates the database entries
    public void insertSipUserInfo(View view){
        //parameters to post to php file
        final Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", MyApplication.getInstance().getPrefManager().getUser().getId());
        params.put("sip_username", userTV.getText().toString());
        params.put("sip_password", passwordTV.getText().toString());

        //request to insert the user into the mysql database using php
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.UPDATE_SIP,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            // check for error flag
                            if (obj.getBoolean("error") == false) {
                                Log.d("Message is", "no error");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON failed to parse: ", response);
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.d("VolleyError at url ", EndPoints.UPDATE_SIP);
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
        Intent intent = new Intent(this, UserProfileFragment.class);
        startActivity(intent);
    }

    //Intent for going to a new Activity
    public void goToInterests(View view){
        Intent intent = new Intent(this, UserProfileFragment.class);
        startActivity(intent);
    }

    //Intent for going to a new Activity
    public void goToRegisterSipAccount(View view){
        Intent intent = new Intent(this, SetingsRegisterSipAccount.class);
        startActivity(intent);
    }
}