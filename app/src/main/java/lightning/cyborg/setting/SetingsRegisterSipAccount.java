package lightning.cyborg.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;

import lightning.cyborg.R;
import lightning.cyborg.activity.SetSipUserInfo;
import lightning.cyborg.activity.UserHomepage;


/**
 * Created by Amos Madalin Neculau on 28/02/2016.
 */


/* This class allows the user to Register an account with Antisip. to be used for voip */
public class SetingsRegisterSipAccount extends Activity{

    // UI ELEMENTS
    private WebView wv;
    private ImageButton doneBtn;
    private ImageButton homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sip_account_registration);

        // Initializig the UI elements
        wv = (WebView) findViewById(R.id.webView);
        doneBtn = (ImageButton) findViewById(R.id.webviewdoneBtn);

        //Allow JavaScript in order to have a very simple Captcha, also meant for improving the User friendliness
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //load the URL to the ANTISIP registration page.
        wv.loadUrl("https://www.antisip.com/sip-antisip-com-register");
    }

    /*
    After registration is done we give the intent to another Activity,
    where the users will give the SIP credentials.
    */
    public void doneRegistration(View view){

        Intent i = new Intent(this, SettingsEditSipUserInfo.class);
        startActivity(i);
    }



}
