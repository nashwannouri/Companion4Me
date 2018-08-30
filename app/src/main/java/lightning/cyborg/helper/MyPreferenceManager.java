package lightning.cyborg.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import lightning.cyborg.model.User;

/**
 * This class  manages the SharedPreferences for saving and loading off the cache
 * Created by team Cyborg Lightning
 */
public class MyPreferenceManager {

    private String TAG = MyPreferenceManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "saveddata";

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_FNAME = "user_fname";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_LNAME = "user_lname";
    private static final String KEY_USER_AVATAR = "user_avatar";
    private static final String KEY_USER_LON = "user_lon";
    private static final String KEY_USER_LAT = "user_lat";
    private static final String KEY_USER_EDU_LEVEL = "user_edu_level";
    private static final String KEY_USER_GENDER = "user_gender";
    private static final String KEY_USER_DOB = "user_dob";
    private static final String KEY_USER_INTERESTS = "user_interests";
    private static final String KEY_NOTIFICATIONS = "notifications";

    /**
     * Default constructor for the class
     * @param context
     */
    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    /**
     * Method that allows us to save User type data into the SharedPreferences
     * @param user the variable that will be stored
     */
    public void storeUser(User user) {
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_FNAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_LNAME, user.getLname());
        editor.putString(KEY_USER_AVATAR, user.getAvatar());
        editor.putString(KEY_USER_LON, user.getLon());
        editor.putString(KEY_USER_LAT, user.getLat());
        editor.putString(KEY_USER_EDU_LEVEL, user.getEdu_level());
        editor.putString(KEY_USER_GENDER, user.getGender());
        editor.putString(KEY_USER_DOB, user.getDob());
        editor.putString(KEY_USER_INTERESTS, user.getInterests());
        editor.commit();

        Log.d(TAG, "User is stored in shared preferences. " + user.getName() + ", " + user.getEmail());
    }

    /**
     * Returns a User type variable from the SharedPreference
     * @return user type variable from the SharedPreference
     */
    public User getUser() {
        if (pref.getString(KEY_USER_ID, null) != null) {
            String id, name, email, lname, avatar, lon, lat, edu_level, gender, dob;

            id = pref.getString(KEY_USER_ID, null);
            name = pref.getString(KEY_USER_FNAME, null);
            email = pref.getString(KEY_USER_EMAIL, null);
            lname = pref.getString(KEY_USER_LNAME, null);
            avatar = pref.getString(KEY_USER_AVATAR, null);
            lon = pref.getString(KEY_USER_LON, null);
            lat = pref.getString(KEY_USER_LAT, null);
            edu_level = pref.getString(KEY_USER_EDU_LEVEL, null);
            gender = pref.getString(KEY_USER_GENDER, null);
            dob = pref.getString(KEY_USER_DOB, null);

            User user = new User( id,  name,  email,  lname,  dob,  lat,  lon,  gender,  avatar,  edu_level);
            user.addInterest(pref.getString(KEY_USER_INTERESTS, null));
            return user;
        }
        return null;
    }

    /**
     * This method allows notifications to work for the app
     * @param oldNotifs the variable that will be received by the user
     */
    public void addNotification(String oldNotifs) {
        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + oldNotifs;
        } else {
            oldNotifications = oldNotifs;
        }
        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    /**
     * Getter for the notifications
     * @return
     */
    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    /**
     * This method clears the SharedPreferences
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * Getter for the userID
     * @return the userID
     */
    public String getId() {
        return pref.getString(KEY_USER_ID, null);
    }

    /**
     * Getter for the first name of the user
     * @return the first name of the user
     */
    public String getFname() {
        return pref.getString(KEY_USER_FNAME, null);
    }

    /**
     * Getter for the email of the user
     * @return the email of the user
     */
    public String getEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Getter for the last name of the user
     * @return the last name of the user
     */
    public String getLname() {
        return pref.getString(KEY_USER_LNAME, null);
    }

    /**
     * Getter for the avatar id of the user
     * @return the avatar id of the user
     */
    public String getAvatar() {
        return pref.getString(KEY_USER_AVATAR, null);
    }

    /**
     * Getter for the longitude of the user
     * @return the longitude of the user
     */
    public String getLon() {
        return pref.getString(KEY_USER_LON, null);
    }

    /**
     * Getter for the latitude of the user
     * @return the latitude of the user
     */
    public String getLat() {
        return pref.getString(KEY_USER_LAT, null);
    }

    /**
     * Getter for the educational level of the user
     * @return the educational level of the user
     */
    public String getEduLevel() {
        return pref.getString(KEY_USER_EDU_LEVEL, null);
    }

    /**
     * Getter for the gender of the user
     * @return the gender of the user
     */
    public String getGender() {
        return pref.getString(KEY_USER_GENDER, null);
    }

    /**
     * Getter for the date of birth of the user
     * @return the date of birthof the user
     */
    public String getDob() {
        return pref.getString(KEY_USER_DOB, null);
    }
}
