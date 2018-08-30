package lightning.cyborg.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class User implements Serializable {
    private String id;
    private String name;
    private String email;
    private String lname;
    private String gender;
    private String dob;
    private String lat;
    private String lon;
    private String avatar;
    private String edu_level;
    private String interests = "";

    public User() {
    }

    public User(JSONObject jsonIn) {
        try {
            this.id = jsonIn.getString("id");
            this.email = jsonIn.getString("email");
            this.name = jsonIn.getString("fname");
            this.lname = jsonIn.getString("lname");
            this.dob = jsonIn.getString("dob");
            this.lat = jsonIn.getString("lat");
            this.lon = jsonIn.getString("lon");
            this.avatar = jsonIn.getString("avatar");
            this.edu_level = jsonIn.getString("edu_level");
            this.gender = jsonIn.getString("gender");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public User(String id, String name, String email, String lname, String dob, String lat, String lon, String gender, String avatar, String edu_level){
        this.id = id;
        this.email = email;
        this.name = name;
        this.lname = lname;
        this.dob = dob;
        this.lat = lat;
        this.lon = lon;
        this.avatar = avatar;
        this.edu_level = edu_level;
        this.gender = gender;
    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public void addInterest(String stringIn){
        if (interests.equals("")){
            interests = stringIn;
        }
        else{
            interests += "," + stringIn;
        }
    }

    public void delInterest(String stringIn){
        ArrayList<String> interArr = new ArrayList<>(Arrays.asList(interests.split(",")));
        interests = "";

        for (int i = 0; i < interArr.size(); i++){
            if(interArr.get(i).equals(stringIn)){
                interArr.remove(i);
            }
            else{
                addInterest(interArr.get(i));
            }
        }
    }

    public void clearInterests(){
        interests = "";
    }

    public String getInterests(){
        return interests;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEdu_level() {
        return edu_level;
    }

    public void setEdu_level(String edu_level) {
        this.edu_level = edu_level;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
