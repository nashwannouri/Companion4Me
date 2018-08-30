package lightning.cyborg;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import java.security.Key;

import lightning.cyborg.activity.RegistrationActivity;
/**
 *          YOU MUST BE SIGNED OUT TO TEST
 * Team CyborgLightning 2016 - King's College London - Project Run
 * RegistrationTest implements the Instrumental testing for the Registration Activity
 * Tests focus and input
 */
public class RegistrationTest extends ActivityInstrumentationTestCase2<RegistrationActivity> {

    /**
     *          IMPORTANT
     * TO TEST YOU MUST BE SIGNED OUT
     */
    RegistrationActivity registrationActivity;//tested Activity

    /*Test class constructor*/
    public RegistrationTest(){
        super(RegistrationActivity.class);
    }

    /*Test LoginActivity Activity Exists - Test Passed*/
    public void testActivityExists(){
        RegistrationActivity registrationActivity = getActivity();
        assertNotNull(registrationActivity);
    }

    /**
     * testEmailEditText1 tests the Input Email Edit Text Field
     * All Instrumental Tests Passed
     */
    public void testEmailEditText(){

        registrationActivity = getActivity();
        final EditText emailET = (EditText)registrationActivity.findViewById(R.id.txtEmail);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                emailET.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_DPAD_RIGHT);
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_DEL);
        getInstrumentation().sendStringSync("100");
    }

    /**
     * testNextButton tests the Next Button
     * All Instrumental Tests Passed
     */
    public void testNextButton() {

        registrationActivity = getActivity();
        final Button btnNext = (Button) registrationActivity.findViewById(R.id.btnRegistor);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                btnNext.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();

    }

    /**
     * testConfirmEmailEditText2 tests the Input Confirm Email Edit Text Field
     * All Instrumental Tests Passed
     */
    public void testConfirmEmailEditText() {

        registrationActivity = getActivity();
        final EditText emailConfirmET = (EditText) registrationActivity.findViewById(R.id.txtConfirmEmail);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                emailConfirmET.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();

        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_DPAD_RIGHT);
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_DEL);
        getInstrumentation().sendStringSync("100");
    }


    /**
     * testEditTextName1 tests the Name Edit Text
     * All Instrumental Tests Passed
     */
    public void testEditTextName() {

        registrationActivity = getActivity();
        final EditText nameET = (EditText) registrationActivity.findViewById(R.id.txtName);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                nameET.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();

        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_DPAD_RIGHT);
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_DEL);
        getInstrumentation().sendStringSync("Simeon");
    }



    /**
     * testEditTextPassword1 tests the Password Edit Text
     * All Instrumental Tests Passed
     */
    public void testEditTextPassword() {

        registrationActivity = getActivity();
        final EditText passwordET = (EditText) registrationActivity.findViewById(R.id.txtPassword);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                passwordET.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();

        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_DPAD_RIGHT);
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_DEL);
        getInstrumentation().sendStringSync("zazaza");
    }




    /**
     * testEditTextDateOfBirth tests the DateOfBirth Edit Text
     * All Instrumental Tests Passed
     */
    public void testEditTextDateOfBirth() {

        registrationActivity = getActivity();
        final EditText dobET = (EditText) registrationActivity.findViewById(R.id.etDob);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                dobET.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();

        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_DPAD_RIGHT);
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_DEL);

    }
}
