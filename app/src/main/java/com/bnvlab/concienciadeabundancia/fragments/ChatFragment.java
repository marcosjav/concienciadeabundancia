package com.bnvlab.concienciadeabundancia.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.adapters.ChatAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.Config;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.ChatMessageItem;
import com.bnvlab.concienciadeabundancia.clases.VideosURL;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcos on 18/04/2017.
 */

public class ChatFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
    private RecyclerView mRecyclerView;
    private View progressLayout;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageButton buttonSend;
    private ArrayList<ChatMessageItem> myDataset;
    private EditText textMessage;
    private DatabaseReference databaseReference;
    private TextView textViewDescription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_ask, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        buttonSend = (ImageButton) view.findViewById(R.id.button_send_message);
        textMessage = (EditText) view.findViewById(R.id.edit_text_message);
        progressLayout = view.findViewById(R.id.layout_progress);

        databaseReference = FirebaseDatabase.getInstance().getReference(References.REFERENCE);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        myDataset = new ArrayList<>();

        // specify an adapter (see also next example)
        mAdapter = new ChatAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);

        textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
        textViewDescription.setText(MainActivity.appText.getChatDescription().replace("&","\n"));

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(textMessage.getText().toString());
            }
        });

        getChat();
        ImageButton back = (ImageButton) view.findViewById(R.id.new_icon_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) this.getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(Config.YOUTUBE_API_KEY, this);

        return view;
    }

    private void getChat(){
        databaseReference.child(References.CHAT_CHILD)
                .child(MainActivity.user.getuId())
                .orderByChild(References.CHAT_TIME_CHILD)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()){
                            ChatMessageItem item = data.getValue(ChatMessageItem.class);
                            myDataset.add(item);
                        }
                        Collections.sort(myDataset);
                        mAdapter.notifyDataSetChanged();
                        showChat(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void showChat(boolean visible){
        mRecyclerView.setVisibility(visible? View.VISIBLE : View.GONE);
        progressLayout.setVisibility(!visible? View.VISIBLE : View.GONE);
    }

    private void sendMessage(final String text){
        Map msg = new HashMap();
        msg.put("time", ServerValue.TIMESTAMP);
        msg.put("text", text);
        msg.put("answer", false);

        databaseReference.child(References.CHAT_CHILD)
                .child(MainActivity.user.getuId())
                .push()
                .setValue(msg)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            myDataset.add(0, new ChatMessageItem(false,0,text));
                            mAdapter.notifyDataSetChanged();
                            textMessage.setText("");
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.chat_error_sent), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            String video = "";
            try {
                video = MainActivity.videosURL.getChat();
            }catch (Exception e) {
                SharedPreferences prefs = getActivity().getSharedPreferences(
                        MainActivity.APP_SHARED_PREF_KEY + MainActivity.user.getuId(), Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String vjson = prefs.getString(References.SHARED_PREFERENCES_APP_VIDEOS_URL, "");
                VideosURL v = gson.fromJson(vjson, VideosURL.class);
                video = v.getChat();
            }

            youTubePlayer.cueVideo(video);
            youTubePlayer.setShowFullscreenButton(false);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        String errorMessage = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), 1).show();
        }
        Log.d(References.ERROR_LOG, "YOUTUBE ERROR:\n" + errorMessage);
    }
}
