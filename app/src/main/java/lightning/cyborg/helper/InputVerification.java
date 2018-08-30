package lightning.cyborg.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Team CyborgLightning 2016 - King's College London - Project Run
 * InputVerification implements functions used in JUNIT testing
 */

public class InputVerification {

    /**
     * isValidDOB function returns true if param is date type object
     * @param date - String
     * @return boolean
     * @throws ParseException - Date Type Object
     */
    public static boolean isValidDOB(String date) throws ParseException {
        int age;
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        Date dateofBirth;
        try {
            dateofBirth = df.parse(date.toString());
        }catch (ParseException p){
            return false;
        }catch (NullPointerException e){
            return false;
        }
        Calendar dob = Calendar.getInstance();
        dob.setTime(dateofBirth);

        Calendar todayDate = Calendar.getInstance();
        age = todayDate.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (todayDate.get(Calendar.DAY_OF_YEAR) <= dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        if (age >= 18) {
            System.out.println("Above 18: " + age);
            return true;
        } else {
            System.out.println("Under 18: " + age);
            return false;
        }

    }

    //TODO ADD TO REGISTER ACTIVITY
    /**
     * isValidEmail function returns true if input string is a valid email input
     * @param email - String
     * @return boolean
     */
    public static  boolean isValidEmail(String email){
        String emailRegEx;
        Pattern pattern;
        // Regex for a valid email address
        emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
        // Compare the regex with the email address
        pattern = Pattern.compile(emailRegEx);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.find()) {
            return false;
        }
        return true;
    }



    //TODO ADD TO REGISTER ACTIVITY
    /**
     * isValidPasswordPair function returns true if both password are a match
     * @param password - String
     * @param confirmPassword - String
     * @return - boolean
     */
    public static  boolean isValidPasswordPair(String password, String confirmPassword) {
        if(isValidPassword(password)==false) return false;
        if(isValidPassword(confirmPassword)==false)return false;
        if (password.toString().equals(confirmPassword.toString()) && (!password.equals("") || !confirmPassword.equals(""))) {
            return true;
        }
        //  passwordET.setError("Please Enter same password");
        return false;
    }



    //TODO ADD TO REGISTER ACTIVITY
    /**
     * isStringAlphaVerification function returns true if input is String containing only Alphabetical Chars
     * @param input - String
     * @return boolean
     */
    public static boolean isStringAlphaVerification(String input){
        int charCount=0;
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if(input.length() == 0) return false;//zero length string ain't alpha
        for(int i=0;i<input.length();i++){
            for(int j=0;j<alphabet.length();j++){
                if(input.substring(i,i+1).equals(alphabet.substring(j,j+1))
                        || input.substring(i,i+1).equals(alphabet.substring(j,j+1).toLowerCase())) charCount++;
            }
            if(charCount != (i+1)){
                System.out.println("\n**Invalid input! Enter alpha values**\n");
                return false;
            }
        }
        return true;
    }

    //TODO ADD TO REGISTER ACTIVITY
    /**
     * isStringAlphaNumericalVerification function returns true if only AlphaNumerical Chars
     * @param input - String
     * @return boolean
     */
    public static boolean isStringAlphaNumericalVerification(String input){
        int charCount=0;
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        if(input.length() == 0) return false;//zero length string ain't alpha
        for(int i=0;i<input.length();i++){
            for(int j=0;j<alphabet.length();j++){
                if(input.substring(i,i+1).equals(alphabet.substring(j,j+1))
                        || input.substring(i,i+1).equals(alphabet.substring(j,j+1).toLowerCase())) charCount++;
            }
            if(charCount != (i+1)){
                System.out.println("\n**Invalid input! Enter alpha values**\n");
                return false;
            }
        }
        return true;
    }


    //TODO ADD TO REGISTER ACTIVITY
    /**
     * checkUserNameInput - Returns true if input (String) is a valid username
     * must be 15 long max, all chars, not null
     * @param name - String
     * @return boolean
     */
    public static boolean checkUserNameInput(String name){
        boolean valid = true;
        try{
            if(isStringAlphaVerification(name)) valid =true;
            else {valid = false;}

            if((name.length()>0&&name.length()<14)&&(!name.equals(null))&&(valid ==true)){
                return true;
            } else {
                return false;
            }
        }
        catch (NullPointerException e){
            return false;
        }

    }

    //TODO ADD TO REGISTER ACTIVITY
    /**
     * isValidPassword returns true if input (String) is only AlphaNumerical Chars
     * @param password - String
     * @return boolean
     */
    public static boolean isValidPassword(String password){
        boolean valid = true;
        if(isStringAlphaNumericalVerification(password)) valid =true;
        else {valid = false;}

        if((password.length()>5&&password.length()<16)&&(!password.equals(null))&&(valid ==true)){
            return true;
        } else {
            return false;
        }
    }

    public static String getValidMessage(String sin){
        return sin.replaceAll("\\\\", "").replaceAll(";", "");
    }

    public static String getValidInterest(String sin){
        return getValidMessage(sin).toLowerCase().replaceAll(", ", ",").replaceAll(" ,", ",");
    }
}
