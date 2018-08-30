package lightning.cyborg.fragment;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import lightning.cyborg.R;
import lightning.cyborg.app.EndPoints;
import lightning.cyborg.app.MyApplication;
import lightning.cyborg.avator.Avator_Logo;
import lightning.cyborg.helper.InputVerification;
import lightning.cyborg.model.User;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the fragment responsible for searching for people via interests and/or location
 * Created by Team Cyborg Lightning
 */
public class UserProfileFragment extends Fragment {
    public static ImageView  imageview;
    private ArrayList<String> items = new ArrayList<>();
    private ArrayAdapter adapter;
    private ListView listview;
    private int avator_id;
    private TextView tvFirstandLast, tvlocation, tvEdu, tvDob;
    private EditText etInterest;
    private Button addInterestButt;
    private Button delInterestButt;
    private String year,month,day;
    private Bitmap [] images;
    private String[] menuItems;
    private User localUser;

    /**
     * Default constructor for the class
     */
    public UserProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Default method that is ran by app
     * @param savedInstanceState  where user previously left off
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View viewroot = inflater.inflate(R.layout.user_profile_fragment, container, false);
        localUser = MyApplication.getInstance().getPrefManager().getUser();
        Log.d("SharedPrefTest", localUser.getInterests());
        menuItems = getResources().getStringArray(R.array.education_array);
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        images = new Bitmap[imgs.length()];
        for(int i = 0; i < imgs.length(); i++){
            images[i] = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, 0));
        }
        imageview = (ImageView) viewroot.findViewById(R.id.profile_image);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setPositiveButton("Change Avatar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), Avator_Logo.class);
                        startActivityForResult(intent, 1);
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialogBuilder.show();
            }
        });

        //Add items to ArrayList
        adapter = new ArrayAdapter<String>(this.getActivity().getApplicationContext(), R.layout.list_black, R.id.list_content, items);
        listview = (ListView) viewroot.findViewById(R.id.listInterest);
        listview.setAdapter(adapter);

        //location, user profile name and last and bio
        tvFirstandLast = (TextView) viewroot.findViewById(R.id.tvfirstandLast);
        tvlocation = (TextView) viewroot.findViewById(R.id.tvLocation);
        tvEdu = (TextView) viewroot.findViewById(R.id.tvEdu);
        tvDob = (TextView) viewroot.findViewById(R.id.tvDob);

        //creating function to add more items into the interest

        registerForContextMenu(listview);
        etInterest = (EditText) viewroot.findViewById(R.id.etAddText);
        addInterestButt = (Button) viewroot.findViewById(R.id.addInterestB);

        //insert values into database... for interest of users
        addInterestButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = InputVerification.getValidInterest(etInterest.getText().toString());
                if (temp.length() > 0) {
                    try {
                        addInterestButt.setEnabled(false);
                        addInterest(temp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "Interest cannot be blank", Toast.LENGTH_LONG).show();
                }
            }
        });

        delInterestButt = (Button) viewroot.findViewById(R.id.delInterestB);
        delInterestButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = etInterest.getText().toString().replaceAll("\\s", "").toLowerCase();
                if (temp.length() > 0) {
                    try {
                        delInterestButt.setEnabled(false);
                        deleteInterests(temp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "Interest cannot be blank", Toast.LENGTH_LONG).show();
                }
            }
        });
        loadProfile();
        return viewroot;
    }

    /**
     * Method that allows contextMenu interaction
     * @param item the MenuItem that was interacted with
     * @return boolean the variable that signifies end of the interaction
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case 0:
                try {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                    deleteInterests(items.get(info.position));
                    Log.d("String to Delete", items.get(info.position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

    /**
     * Method that creates contextMenu
     * @param menu the ContextMenu to be created
     * @param view the container that called this method
     * @param menuInfo the information of the contextMenu contents
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (view.getId()==R.id.listInterest) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(items.get(info.position));
            String[] menuItems = getResources().getStringArray(R.array.interest_menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    /**
     * Method that loads user's profile via cache or database connection
     */
    private void loadProfile() {
        String name = localUser.getName()+ " " + localUser.getLname();
        tvFirstandLast.setText(name);
        tvlocation.setText("Lat:" + localUser.getLat() + " Lon:" + localUser.getLon());
        year = localUser.getDob().substring(0,4); month= localUser.getDob().substring(4,6); day = localUser.getDob().substring(6,8);
        tvDob.setText("DOB:" + day+"-"+month+"-"+year);
        imageview.setImageBitmap(images[Integer.parseInt(localUser.getAvatar())]);
        adapter.notifyDataSetChanged();
        Log.d("Education L", menuItems[Integer.parseInt(localUser.getEdu_level())]);
        tvEdu.setText("Education LVL: " + menuItems[Integer.parseInt(localUser.getEdu_level())]);
        Log.d("interLoad1", localUser.getInterests() + "");

        items.clear();
        if(localUser.getInterests().length() > 0){
            String[] interests = localUser.getInterests().split(",");
            for (int i = 0; i < interests.length; i++) {
                items.add(interests[i]);
            }
            adapter.notifyDataSetChanged();
        }
        else {
            try {
                loadInterests();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("loadProfile", "Error loading interests from server");
                Toast.makeText(getActivity(), "Error loading interests", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Method that adds interest for the user to the database
     * @param interests the interest to be added
     * @throws JSONException exception throw to prevent app crashes
     */
    private void addInterest(final String interests) throws JSONException {
        Log.d("interAdd1", localUser.getInterests());
        //parameters to post to php file
        final Map<String, String> params = new HashMap<String, String>();
        params.put("userID", MyApplication.getInstance().getPrefManager().getUser().getId());
        params.put("interests", interests);

        //request to insert the user into the mysql database using php
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.ADD_INTERESTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getString("success").equals("1");
                            String message = jsonResponse.getString("message");
                            if (success) {
                                for (String s : interests.split(",")) {
                                    if (!items.contains(s)) {
                                        items.add(InputVerification.getValidInterest(s));
                                        localUser.addInterest(InputVerification.getValidInterest(s));
                                    }
                                }
                                MyApplication.getInstance().getPrefManager().storeUser(localUser);
                            }

                            etInterest.setText("");
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON failed to parse: ", response);
                        }
                        Log.d("interAdd2", localUser.getInterests());
                        addInterestButt.setEnabled(true);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError at url ", EndPoints.ADD_INTERESTS);
                addInterestButt.setEnabled(true);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        //put the request in the static queue
        MyApplication.getInstance().addToRequestQueue(request);
    }

    /**
     * Loads interests to be displayed in the ListView
     * @throws JSONException exception throw to prevent app crashes
     */
    private void loadInterests() throws JSONException {
        Log.d("interLoad1", localUser.getInterests() + "");
        //parameters to post to php file
        final Map<String, String> params = new HashMap<String, String>();
        params.put("userID", MyApplication.getInstance().getPrefManager().getUser().getId());

        //request to insert the user into the mysql database using php
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_INTERESTS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("loadInterests", "posting to "+ EndPoints.GET_INTERESTS);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getString("success").equals("1");
                            String message = jsonResponse.getString("message");
                            JSONArray interests = jsonResponse.getJSONArray("interests");

                            localUser.clearInterests();
                            if (success) {
                                for (int i = 0; i < interests.length(); i++) {
                                    if (!items.contains(interests.getString(i))) {
                                        items.add(interests.getString(i));
                                        localUser.addInterest(interests.getString(i));
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            }
                            MyApplication.getInstance().getPrefManager().storeUser(localUser);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON failed to parse: ", response);
                        }
                        delInterestButt.setEnabled(true);
                        Log.d("interLoad2", localUser.getInterests());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError at url ", EndPoints.GET_INTERESTS);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        //put the request in the static queue
        MyApplication.getInstance().addToRequestQueue(request);
    }

    /**
     * Method that allows deletion of interests via Database connection
     * @param interests the interest to be deleted
     * @throws JSONException exception throw to prevent app crashes
     */
    private void deleteInterests(final String interests) throws JSONException {
        Log.d("interDel1", localUser.getInterests());
        //parameters to post to php file
        final Map<String, String> params = new HashMap<String, String>();
        params.put("userID", MyApplication.getInstance().getPrefManager().getUser().getId());
        params.put("interests", interests);

        //request to insert the user into the mysql database using php
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.DEL_INTERESTS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getString("success").equals("1");
                            String message = jsonResponse.getString("message");

                            if (success) {
                                for (String s : interests.split(",")) {
                                    items.remove(s);
                                    localUser.delInterest(s);
                                }
                                MyApplication.getInstance().getPrefManager().storeUser(localUser);
                            }

                            etInterest.setText("");
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON failed to parse: ", response);
                        }
                        delInterestButt.setEnabled(true);
                        Log.d("interDel2", localUser.getInterests());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                delInterestButt.setEnabled(true);
                Log.d("VolleyError at url ", EndPoints.ADD_INTERESTS);
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

    /**
     * Dispatch incoming result to the correct fragment
     * @param requestCode checks to see if requestCode mathes
     * @param resultCode ensures that the request was successful
     * @param data allows data to be retrieves from another fragment
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap b = BitmapFactory.decodeByteArray(data.getByteArrayExtra("Bitmap"), 0, data.getByteArrayExtra("Bitmap").length);
                avator_id = data.getExtras().getInt("imageID");
                Log.d("avatar ID", avator_id + "");

                final Map<String, String> params = new HashMap<String, String>();
                params.put("userID", MyApplication.getInstance().getPrefManager().getUser().getId());

                imageview.setImageBitmap(b);
                if (!Integer.toString(avator_id).replaceAll(", ", ",").replaceAll(" ,", ",").equals("")) {
                    params.put("avatar", Integer.toString(avator_id));
                }

                //request to insert the user into the mysql database using php
                StringRequest request = new StringRequest(Request.Method.POST, EndPoints.UPDATE_USERS,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    String message = jsonResponse.getString("message");
                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("JSON failed to parse: ", response);
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VolleyError at url ", EndPoints.ADD_INTERESTS);
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
    }
}