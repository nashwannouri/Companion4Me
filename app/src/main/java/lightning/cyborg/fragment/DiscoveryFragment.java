package lightning.cyborg.fragment;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lightning.cyborg.R;
import lightning.cyborg.activity.UserHomepage;
import lightning.cyborg.adapter.CustomListAdapter;
import lightning.cyborg.app.EndPoints;
import lightning.cyborg.app.MyApplication;
import lightning.cyborg.helper.InputVerification;

/**
 * This class represents the fragment responsible for searching for people via interests and/or location
 * Created by Team Cyborg Lightning
 */
public class DiscoveryFragment extends Fragment {
    private View inflatedview;
    private EditText search;
    private TextView slideTV;
    private CheckBox checkBoxLoc;
    private ListView matchedList;
    private ArrayAdapter adapter;
    private Button searchButton;
    private Button loadButton;
    private ArrayList<String> matchedUserIDs;
    private ArrayList<JSONObject> matchedUserJson;
    private SeekBar seekDist;
    private Spinner genderSpin;
    private Spinner eduSpin;
    private Spinner lowAge, highAge;
    private String[] educationArr;

    /**
     * Default constructor for the class
     */
    public DiscoveryFragment() {
    }

    /**
     * Default method that is ran by app
     * @param savedInstanceState  where user previously left off
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Default method that is ran by app for the layout
     * @param inflater allows xml files to work with source code
     * @param container special view that allows content to be displayed
     * @param savedInstanceState where user previously left off
     * @return View to be returned
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedview = inflater.inflate(R.layout.discovery_fragment, container, false);

        slideTV = (TextView) inflatedview.findViewById(R.id.sliderTV);
        educationArr = getResources().getStringArray(R.array.education_array);
        matchedList = (ListView) inflatedview.findViewById(R.id.listMatched);
        seekDist = (SeekBar) inflatedview.findViewById(R.id.seekDist);
        checkBoxLoc = (CheckBox) inflatedview.findViewById(R.id.checkBoxLoc);
        search = (EditText) inflatedview.findViewById(R.id.searchMatches);
        search.setText("");
        searchButton = (Button) inflatedview.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButton.setEnabled(false);
                discover(v);
            }
        });

        loadButton = (Button) inflatedview.findViewById(R.id.loadButton);
        loadButton.setEnabled(false);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loadButton.setEnabled(false);
                    populateDiscovery(5);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getContext(), R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpin = (Spinner) inflatedview.findViewById(R.id.genderSpin);
        genderSpin.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> eduAdapter = ArrayAdapter.createFromResource(getContext(), R.array.education_array, android.R.layout.simple_spinner_item);
        eduAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eduSpin = (Spinner) inflatedview.findViewById(R.id.eduSpin);
        eduSpin.setAdapter(eduAdapter);
        eduSpin.setSelection(9);

        Integer[] age = new Integer[82];
        for (int i = 0; i < age.length; i++) {
            int temp = i + 18;
            age[i] = temp;
        }
        ArrayAdapter<Integer> ageAdapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_item, age);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lowAge = (Spinner) inflatedview.findViewById(R.id.lowAgeSpin);
        highAge = (Spinner) inflatedview.findViewById(R.id.highAgeSpin);
        lowAge.setAdapter(ageAdapter);
        highAge.setAdapter(ageAdapter);
        highAge.setSelection(age.length - 1);

        seekDist.setMax(95);
        seekDist.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                slideTV.setText(String.valueOf(progress + 5) + "km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        matchedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    createChatroom((matchedUserJson.get(position)).getString("userID"));
                    Toast.makeText(getContext(), "Chat request sent", Toast.LENGTH_SHORT).show();
                    matchedUserIDs.remove(position);
                    matchedUserJson.remove(position);
                    populateList();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return inflatedview;
    }

    /**
     * Method that allows user to search Database to look for matches
     * @param view the container that calls this method
     */
    private void discover(View view) {
        final String filtered = InputVerification.getValidInterest(search.getText().toString());
        String ownID = MyApplication.getInstance().getPrefManager().getUser().getId();
        int radius = seekDist.getProgress() + 5;
        matchedUserJson = new ArrayList<JSONObject>();
        //String year = Calendar.getInstance().get(Calendar.YEAR) + "" + Calendar.getInstance().get(Calendar.MONTH) + "" Calendar.getInstance().get(Calendar.DA);
        String strDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        int curDate = Integer.parseInt(strDate);
        int minDate = curDate - ((Integer) lowAge.getSelectedItem() * 10000);
        int maxDate = curDate - ((Integer) highAge.getSelectedItem() * 10000);

        //parameters to post to php file
        final Map<String, String> params = new HashMap<String, String>();
        params.put("userID", ownID);
        params.put("minDate", Integer.toString(minDate));
        params.put("maxDate", Integer.toString(maxDate));

        if (!filtered.equals("")) {
            params.put("interests", filtered);
        }
        if (checkBoxLoc.isChecked()) {
            params.put("radius", Integer.toString(radius));
        }
        if (!genderSpin.getSelectedItem().equals("Any")) {
            params.put("gender", genderSpin.getSelectedItem().toString());
        }
        if (!eduSpin.getSelectedItem().equals("Any")) {
            params.put("edu_level", Integer.toString(eduSpin.getSelectedItemPosition()));
        }

        //request to insert the user into the mysql database using php
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.DISCOVER_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getString("success").equals("1");
                            String message = jsonResponse.getString("message");
                            JSONArray jsonIDs = jsonResponse.getJSONArray("users");
                            Log.d("jsonIDs", jsonIDs.toString());

                            matchedUserIDs = new ArrayList();
                            for (int i = 0; i < jsonIDs.length(); i++) {
                                matchedUserIDs.add(jsonIDs.getString(i));
                            }

                            if (matchedUserIDs.size() > 0) {
                                populateDiscovery(5);

                                if (matchedUserIDs.size() > 5) {
                                    loadButton.setEnabled(true);
                                }
                            } else {
                                populateList();
                            }
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            Log.d("disMes", message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON failed to parse: ", response);
                        }
                        searchButton.setEnabled(true);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError at url ", EndPoints.DISCOVER_USERS);
                searchButton.setEnabled(true);
            }
        }) {
            //Parameters inserted
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        //put the request in the static queue
        MyApplication.getInstance().
                addToRequestQueue(request);
    }

    /**
     * Method matches that are found are stored as JSONObject
     * @param increment value that increments the number of results to be shown
     * @throws JSONException exception throw to prevent app crashes
     */
    private void populateDiscovery(int increment) throws JSONException {
        final String url = "http://nashdomain.esy.es/users_get_all.php";

        if (matchedUserJson.size() == matchedUserIDs.size()) {
            loadButton.setEnabled(false);
            Log.d("popDis", "No more users to load");
            Toast.makeText(getActivity(), "No more users to load", Toast.LENGTH_LONG).show();
            return;
        }

        if (increment + matchedUserJson.size() > matchedUserIDs.size()) {
            increment = matchedUserIDs.size() - matchedUserJson.size();
        }

        String idsToGet = "";

        for (int i = matchedUserJson.size(); i < increment + matchedUserJson.size(); i++) {
            idsToGet += matchedUserIDs.get(i) + ",";
        }

        //parameters to post to php file
        final Map<String, String> params = new HashMap<String, String>();
        params.put("userIDs", idsToGet);

        //request to insert the user into the mysql database using php
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray temp = jsonResponse.getJSONArray("users");

                            for (int i = 0; i < temp.length(); i++) {
                                matchedUserJson.add(temp.getJSONObject(i));
                                Log.d("popDis loading", temp.getJSONObject(i).toString());
                            }

                            populateList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON failed to parse: ", response);
                        }
                        loadButton.setEnabled(true);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError at url ", url);
                loadButton.setEnabled(true);
            }
        }) {
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
     * Method that fills up the listView to display search results to the user
     */
    private void populateList() {
        final String[] users = new String[matchedUserJson.size()];
        Integer[] avatars = new Integer[matchedUserJson.size()];

        for (int i = 0; i < matchedUserJson.size(); i++) {
            try {
                JSONObject user = matchedUserJson.get(i);
                int age = (Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date())) / 10000) - (Integer.parseInt(user.getString("dob")) / 10000);
                users[i] = " " + user.getString("fname") + " - " + user.getString("gender")
                        + " - " + age + " - " + educationArr[Integer.parseInt(user.getString("edu_level"))];
                avatars[i] = Integer.valueOf(user.getString("avatar"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Json error parsing ", users.toString());
            }
        }

        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        Bitmap[] images = new Bitmap[imgs.length()];
        for (int i = 0; i < imgs.length(); i++) {
            images[i] = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
        }
        imgs.recycle();

        CustomListAdapter customList = new CustomListAdapter(getActivity(), users, images, avatars);
        matchedList.setAdapter(customList);
        customList.notifyDataSetChanged();
    }

    /**
     * Method that assists in starting chat with other users
     * @param otherID primary key of a person that the user wishes to attempt communication with
     * @throws JSONException exception throw to prevent app crashes
     */
    private void createChatroom(String otherID) throws JSONException {
        //parameters to post to php file
        final Map<String, String> params = new HashMap<String, String>();
        params.put("cur_user_id", MyApplication.getInstance().getPrefManager().getUser().getId());
        params.put("other_user_id", otherID);
        Log.d("createChat", "My ID " + MyApplication.getInstance().getPrefManager().getUser().getId());
        Log.d("createChat", "otherID " + otherID);


        //request to insert the user into the mysql database using php
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.CREATE_CHATROOM,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("createChat resp", response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError at url ", EndPoints.CREATE_CHATROOM);
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