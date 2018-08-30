package lightning.cyborg.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * This class allows user to interact with a Dialog calendar to input date of birth
 * Created by Team Cyborg Lightning
 */
public class DateDialog extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{
    EditText etDate;

    /**
     * Default constructor for class
     * @param view the container that calls this method
     */
    public DateDialog(View view) {
        etDate = (EditText) view;
    }

    /**
     * Default method that is ran by app
     * @param savedInstanceState  where user previously left off
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * Method that helps to format the date that was chosen
     * @param view the container that calls this class object
     * @param year year that was chosen in the Calendar
     * @param monthIn month that was chosen in the Calendar
     * @param day day that was chosen in the Calendar
     */
    @Override
    public void onDateSet(DatePicker view, int year, int monthIn, int day) {

        int month = monthIn+1;
        String date;
        String daySingle;
        String monthSingle;
        if(day<10 && month <10){

            daySingle = "0"+day;
            monthSingle= "0"+month;
            date = year+"-"+monthSingle+"-"+daySingle;

        }else if(day<10){
            daySingle = "0"+day;
            date = year+"-"+month+"-"+daySingle;

        }else if(month<10){
            monthSingle= "0"+month;
            date = year+"-"+monthSingle+"-"+day;
        }else
            //show to the selected date in the text box
            date = String.format("%d-%d-%d", year, month, day);
        etDate.setText(date);
    }
}