package lightning.cyborg.voip;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import lightning.cyborg.R;

/**
 * Created by amosmadalinneculau on 15.03.2016.
 */
public class IncomingGui extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_receiver);
    }
    public void onCallAcceptButton(View view){
        IncomingCallReceiver.answerIncomingCall();
    }

    public void onCallRejectButton(View view) throws Throwable {
        IncomingCallReceiver.rejectIncomingCall();
        this.finish();

    }

}
