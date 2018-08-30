package lightning.cyborg.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import lightning.cyborg.R;
import lightning.cyborg.app.EndPoints;
import lightning.cyborg.app.MyApplication;
import lightning.cyborg.avator.Avator_Logo;
import lightning.cyborg.fragment.DateDialog;
import lightning.cyborg.helper.InputVerification;
import lightning.cyborg.model.User;

public class RegistrationActivity extends AppCompatActivity {

    //Info for register

   private EditText emailET, nameET, lastNameET, emailConfirmET, passwordConfirmET, dobET, passwordET;
    private ImageView avatorIcon;
    private int avator_id;
    private Button chooseAvatar;
    private Spinner eduSpin;
    private RadioButton maleRadioRegister;
    private ImageView backButton, forwardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        avator_id = 5;

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_register_main_menu);

        //USER INPUT

        chooseAvatar = (Button) findViewById(R.id.btnSelectAvator);
        forwardButton = (ImageView) findViewById(R.id.nextPageButton);
        emailET = (EditText) findViewById(R.id.txtEmail);
        emailConfirmET = (EditText) findViewById(R.id.txtConfirmEmail);
        nameET = (EditText) findViewById(R.id.txtName);

        passwordConfirmET = (EditText) findViewById(R.id.txtConfirmPass);
        passwordET = (EditText) findViewById(R.id.txtPassword);
        dobET = (EditText) findViewById(R.id.etDob);
        maleRadioRegister = (RadioButton) findViewById(R.id.rbMale);
        avatorIcon = (ImageView) findViewById(R.id.avatorIcon);

        lastNameET = (EditText) findViewById(R.id.txtLastName);
        maleRadioRegister.setChecked(true);


        ArrayAdapter<CharSequence> eduAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.education_array_not_any, android.R.layout.simple_spinner_item);
        eduAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eduSpin = (Spinner) findViewById(R.id.eduSpin);
        eduSpin.setAdapter(eduAdapter);

        backButton = (ImageView) findViewById(R.id.registerBackButton);
        chooseAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Loading Avatars...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RegistrationActivity.this, Avator_Logo.class);
                startActivityForResult(intent, 0);
            }

        });

        dobET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean hasfocus) {
                if (hasfocus) {
                    DateDialog dialog = new DateDialog(view);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");

                }
            }

        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertUser(v);

                //ToDo Make this actually use the same as the login activity which will take the users password and email and login
                //Right now its set to send them back to login after registering their details.

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backToLoginPage(v);

            }


        });
    }

    public void insertUser(View view) {
        String fname = nameET.getText().toString();
        String lname = lastNameET.getText().toString();
        Log.d(fname, lname);

        boolean validInput = true;
        //parameters to post to php file
        final Map<String, String> params = new HashMap<String, String>();

        if (emailET.getText().toString().equals(emailConfirmET.getText().toString())) {
            if (InputVerification.isValidEmail(emailET.getText().toString())) {
                params.put("email", emailET.getText().toString());
            } else {
                validInput = false;
                emailET.setError("Please enter a valid email");
            }
        } else {
            validInput = false;

            emailET.setError("Please make sure emails match");
        }

        if (InputVerification.checkUserNameInput(fname)) {
            params.put("fname", fname);
        } else {
            validInput = false;
            nameET.setError("Please enter a valid name");
        }

        if (InputVerification.checkUserNameInput(lname)) {
            params.put("lname", lname);
        } else {
            validInput = false;
            nameET.setError("Please enter a valid name");
        }

        if (InputVerification.isValidPasswordPair(passwordET.getText().toString(), passwordConfirmET.getText().toString())) {
            params.put("password", passwordET.getText().toString());
        } else {
            validInput = false;
            passwordET.setError("Password is invalid / passwords do not match");
        }

        try {
            if (InputVerification.isValidDOB(dobET.getText().toString())) {
                params.put("dob", dobET.getText().toString().replaceAll("-",""));
            } else {
                validInput = false;
                dobET.setError("Please a valid date yyyy-mm-dd");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            validInput = false;
            dobET.setError("Please a valid date yyyy-mm-dd errpr");
        }

        if (maleRadioRegister.isChecked()) {
            params.put("gender", "M");
        } else {
            params.put("gender", "F");
        }

        params.put("edu_level", eduSpin.getSelectedItem().toString());
        params.put("avatar", Integer.toString(avator_id));

        if (!validInput) {
            return;
        } else {
            //request to insert the user into the mysql database using php
            StringRequest request = new StringRequest(Request.Method.POST, EndPoints.USERS_ADD,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);

                                boolean success = jsonResponse.getString("success").equals("1");
                                Log.d("Success", String.valueOf(success));
                                if(success){
                                    User localUser = new User();
                                    localUser.setEmail(params.get("email"));
                                    localUser.setName(params.get("fname"));
                                    localUser.setLname(params.get("lname"));
                                    localUser.setDob(params.get("dob"));
                                    localUser.setGender(params.get("gender"));
                                    localUser.setAvatar(params.get("avatar"));
                                    localUser.setEdu_level(eduSpin.getSelectedItemPosition() + "");
                                    localUser.setId(jsonResponse.getString("id"));
                                    Log.d("logintohome", localUser.getId() + "");
                                    localUser.setLat("0");
                                    localUser.setLon("0");


                                    // storing user in shared preferences
                                    MyApplication.getInstance().getPrefManager().storeUser(localUser);

                                    // start main activity
                                    // startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    toUserHomePageActivity();

                                    finish();
                                }

                                String message = jsonResponse.getString("message");
                                Log.d("Message is", message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("JSON failed to parse: ", response);
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VolleyError at url ", EndPoints.USERS_ADD);
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
    }

    private void toUserHomePageActivity(){
        Intent intent = new Intent(this,UserHomepage.class);
        Bundle bundle = new Bundle();
        bundle.putString("FragmentNum", "0");
        intent.replaceExtras(bundle);
        startActivity(intent);
        finish();
    }

    //go back to login Page
    private void backToLoginPage(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK){
                Bitmap b = BitmapFactory.decodeByteArray(data.getByteArrayExtra("Bitmap"), 0, data.getByteArrayExtra("Bitmap").length);
                avator_id = data.getExtras().getInt("imageID");
                Log.d("Avatar id", avator_id + "");
                avatorIcon.setImageBitmap(b);
            }
        }
    }
}

