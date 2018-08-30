package lightning.cyborg.voip;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;

import lightning.cyborg.activity.CallActivity;
import lightning.cyborg.activity.ChatRoomActivity;

/**
 * Created by Amos Madalin Neculau on 26/02/2016.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    /**
     * Processes the incoming call, answers it, and hands it over to the
     * WalkieTalkieActivity.
     *
     * @param context The context under which the receiver is running.
     * @param intent The intent being received.
     */
    public static SipAudioCall incomingCall;

    @Override
    public void onReceive(Context context, Intent intent) {
        incomingCall = null;
        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                @Override
                public void onRinging(SipAudioCall call, SipProfile caller) {
                    try {
                        call.answerCall(30);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            CallActivity wtActivity = (CallActivity) context;
            incomingCall = wtActivity.manager.takeAudioCall(intent, listener);
            incomingCall.setListener(listener, true);
            //showIncomingCallGui(intent, context);
            wtActivity.call = incomingCall;

            answerIncomingCall();
            //delete this rows.

            //incomingCall.answerCall(30);
            //incomingCall.startAudio();

            /*
            incomingCall.setSpeakerMode(true);
            if(incomingCall.isMuted()) {
                incomingCall.toggleMute();
            }
            wtActivity.call = incomingCall;
            */
           // wtActivity.updateStatus(incomingCall);

        } catch (Exception e) {
            e.printStackTrace();
            if (incomingCall != null) {
                e.printStackTrace();
                incomingCall.close();
            }
        }
    }

    public void showIncomingCallGui(Intent intent, Context context) {

        Intent incomingCall = new Intent(context, IncomingGui.class);
        context.startActivity(incomingCall);
    }

    public static void answerIncomingCall() {

        try {
            incomingCall.answerCall(30);
            incomingCall.startAudio();
            incomingCall.setSpeakerMode(false);

            if (incomingCall.isMuted()) {
                incomingCall.toggleMute();

            }
        } catch (Exception e) {

            System.out.println(e.toString());
        }

    }

    public static void rejectIncomingCall() {

        try {
            if (incomingCall != null) {

                incomingCall.endCall();
                incomingCall.close();
            }

        } catch (Exception e) {

            System.out.println(e.toString());
        }
    }
}
