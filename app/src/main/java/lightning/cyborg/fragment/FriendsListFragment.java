package lightning.cyborg.fragment;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import lightning.cyborg.R;
import lightning.cyborg.activity.UserHomepage;
import lightning.cyborg.adapter.ChatRoomsAdapter;
import lightning.cyborg.helper.SimpleDividerItemDecoration;
import lightning.cyborg.model.ChatRoom;

/**
 * This class allows user to to interact with their Friends
 * Created by Team Cyborg Lightning
 */
public class FriendsListFragment extends Fragment {

    private View inflatedview;
    private String TAG = "chatRoomFragment";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ArrayList<ChatRoom> chatRoomArrayList;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;
    private TextView searchTV;
    ArrayList<ChatRoom> searchArrayList;
    public FriendsListFragment() {
        // Required empty public constructor
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
        this.inflatedview = inflater.inflate(R.layout.friends_list_fragment, container, false);

        recyclerView = (RecyclerView) inflatedview.findViewById(R.id.recycler_view13);
        Bundle b= getArguments();
        searchTV = (TextView) inflatedview.findViewById(R.id.searchView);
        textViewListener();
        chatRoomArrayList = new ArrayList<>();
        searchArrayList = chatRoomArrayList;

        chatRoomArrayList = (ArrayList<ChatRoom>) b.getSerializable("data");
        Log.d("chatRoomArrayList", chatRoomArrayList.toString());
        mAdapter= b.getParcelable("adapter");

        LinearLayoutManager layoutManager = new LinearLayoutManager(inflatedview.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getActivity().getApplicationContext()
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new ChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ChatRoom chatRoom;
                // when chat is clicked, launch full chat thread activity
                try{ chatRoom = searchArrayList.get(position);}
                catch (Exception e){
                    chatRoom = chatRoomArrayList.get(position);
                }

                if(chatRoom.getPermission().equals("y")||chatRoom.getPermission().equals("blocked"))
                {
                    ((UserHomepage) getActivity()).chatRoomActivityIntent(chatRoom.getId(), chatRoom.getName(),"f",chatRoom.getPermission(), chatRoom.getAvatar());
                }
                else{
                    if(!chatRoom.isChatRoomExists()){
                        recyclerView.removeView(view);
                        mAdapter.notifyDataSetChanged();
                    } }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        // Inflate the layout for this fragment
        return inflatedview;
    }

    /**
     * Method that filters out the friend's list according to the String typed in
     * @param str
     */
    public void searchResults(String str){
        searchArrayList.clear();

        if(str != ""){
            for(int i=0; i<chatRoomArrayList.size(); ++i){
                if(chatRoomArrayList.get(i).getName().toString().contains(str)){
                    searchArrayList.add(chatRoomArrayList.get(i));
                }
            }
        }else{
            for(int i=0; i< searchArrayList.size(); ++i)
                searchArrayList.remove(i);
        }
        mAdapter.setChatRoomArrayList(searchArrayList);
    }

    /**
     * Method that reacts to the change in a the searchTV textview
     */
    public void textViewListener(){
        searchTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchResults(searchTV.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}