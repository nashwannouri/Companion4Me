package lightning.cyborg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
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
import lightning.cyborg.helper.InputVerification;
import lightning.cyborg.model.User;

/**
 * This class creates a grid which displays all of the avatars for the users to pick
 * once clicked it will change the profile avatar.
 *
 * Created by Team Cyborg Lightning
 */
public class LoginActivity extends AppCompatActivity {
    private String TAG = LoginActivity.class.getSimpleName();
    private EditText _inputEmail, _inputPassword;
    private TextInputLayout inputLayoutName, inputLayoutEmail;
    private Button loginBtn;
    private Button forgotBtn;
    private static int request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Check for login session. It user is already logged in
         * redirect him to main activity
         * */
        if (MyApplication.getInstance().getPrefManager().getUser() != null) {
            toUserHomePageActivity();
        }

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _inputEmail = (EditText) findViewById(R.id.txtEmailLogin);
        _inputPassword = (EditText) findViewById(R.id.txtPasswordLogin);
        loginBtn = (Button) findViewById(R.id.btnLogin);
        forgotBtn = (Button) findViewById(R.id.buttonForgot);
        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendPassword(v);

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InputVerification.isValidEmail(_inputEmail.getText().toString()) && InputVerification.isValidPassword(_inputPassword.getText().toString())) {
                    loginBtn.setEnabled(false);
                    login();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    /**
     * Method that navigates user to the homepage
     */
    private void toUserHomePageActivity(){
        Intent intent = new Intent(this,UserHomepage.class);
        Bundle bundle = new Bundle();
        bundle.putString("FragmentNum", "0");
        intent.replaceExtras(bundle);
        startActivity(intent);
        finish();
    }

    /**
     * Method that sends password
     * @param view the container that calls this method
     */
    private void sendPassword(View view) {
        final String url = EndPoints.SEND_PASS;

        //parameters to post to php file
        final Map<String, String> params = new HashMap<>();
        params.put("email", _inputEmail.getText().toString());

        //request to get the user from the mysql database using php
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getString("success").equals("1");
                            Log.d("Success", String.valueOf(success));
                            String message = jsonResponse.getString("message");
                            Log.d("Message is", message);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON failed to parse: ", response);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError at url ", url);
                Toast.makeText(getApplicationContext(), "Server timed out, please try again.", Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            //Parameters inserted
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        //put the request in the static queue
        MyApplication.getInstance().addToRequestQueue(request);
    }

    /**
     * Navigates user to register screen
     * @param view the container that calls this method
     */
    public void goToRegisterScreen(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    /**
     * logging in user. Will make http post request with name, email
     * as parameters
     */
    private void login() {
        final String email = _inputEmail.getText().toString();
        final String password = _inputPassword.getText().toString();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("success").equals("1")) {
                        JSONObject userObj = obj.getJSONObject("user");
                        Log.d("login", userObj.toString());
                        User user = new User(userObj);
                        MyApplication.getInstance().getPrefManager().storeUser(user);
                        toUserHomePageActivity();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "" + obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                loginBtn.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginBtn.setEnabled(true);
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
}