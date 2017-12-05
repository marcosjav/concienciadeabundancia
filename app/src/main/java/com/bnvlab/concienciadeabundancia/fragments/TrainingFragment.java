package com.bnvlab.concienciadeabundancia.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.adapters.TrainingAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.Config;
import com.bnvlab.concienciadeabundancia.auxiliaries.ICallback;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.SentUser;
import com.bnvlab.concienciadeabundancia.clases.TrainingItem;
import com.bnvlab.concienciadeabundancia.clases.VideosURL;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Marcos on 08/04/2017.
 */

public class TrainingFragment extends Fragment implements ICallback, YouTubePlayer.OnInitializedListener {

    ArrayList<TrainingItem> list;
    TrainingAdapter adapter;
    ArrayList<String> listId;
    ViewSwitcher viewSwitcher;
    boolean active_user;
    ListView listView;
//    int userCounter = 0;
//    long lastSent = 0;
    SharedPreferences prefs;

    public TrainingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_trainings, container, false);

        prefs = getActivity().getSharedPreferences(
                MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(), Context.MODE_PRIVATE);

        TextView title = (TextView) view.findViewById(R.id.textView);

        view.findViewById(R.id.new_icon_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        view.findViewById(R.id.button_trainings_videos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), VideoFragment.class);
            }
        });

        list = new ArrayList<>();
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher_trainings);

        listId = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.list_view_trainings);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        adapter = new TrainingAdapter(getContext(), R.layout.item_training_row, list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrainingItem item = list.get(position);
                if (item.isComplete()) {
                    FragmentMan.changeFragment(getActivity(), ResumeFragment.class, listId.get(position));
                } else {
                        FragmentMan.changeFragment(getActivity(), QuizFragment.class, listId.get(position), "video:" + list.get(position).getVideo());

                }
            }
        });

        listView.setAdapter(adapter);

        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) this.getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(Config.YOUTUBE_API_KEY, this);

        return view;
    }

    private void getTrainings() {
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.QUIZ)
                .orderByChild(References.QUIZ_CHILD_INDEX)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String title = "";
                            String require = "";
                            String video = "";
                            int index = -1;
                            boolean freeContent = data.child(References.FREE_CONTENT).getValue(boolean.class);
                            boolean hidden = data.child(References.QUIZ_CHILD_HIDDEN).getValue(boolean.class);

                            if (data.child(References.QUIZ_CHILD_TITLE).exists())
                                title = data.child(References.QUIZ_CHILD_TITLE).getValue(String.class);

                            if (data.child(References.FAQ_CHILD_INDEX).exists())
                                index = data.child(References.FAQ_CHILD_INDEX).getValue(int.class);

                            if (data.child(References.QUIZ_CHILD_REQUIRE).exists())
                                require = data.child(References.QUIZ_CHILD_REQUIRE).getValue(String.class);

                            if (data.hasChild(References.QUIZ_CHILD_VIDEO))
                                video = data.child(References.QUIZ_CHILD_VIDEO).getValue(String.class);

                            if (!hidden) {
                                TrainingItem item = new TrainingItem();
                                item.setTitle(title);
                                item.setFree(freeContent);
                                item.setRequire(require);
                                if (index < 0)
                                    index = list.size();
                                item.setIndex(index);
                                item.setVideo(video);
                                list.add(item);
                                listId.add(data.getKey());
                            }
                        }
                        Collections.sort(list, new TrainingsSort());
                        getTrainingsStatus2();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showProgress(false);
                    }
                });
    }

    private void getTrainingsStatus2(){
        try {
            Type listType = new TypeToken<ArrayList<SentUser>>(){}.getType();
            ArrayList<SentUser> sent = new Gson().fromJson(prefs.getString(References.SHARED_PREFERENCES_USER_SENT_JSON, ""), listType);
            for (SentUser su : sent) {
                int index = listId.indexOf(su.getKey());
                if (index != -1) {
                    list.get(index).setComplete(true);
                    list.get(index).setFinished(su.isChecked());
                }
            }
            adapter.notifyDataSetChanged();
            showProgress(false);
        }catch (Exception e){
            Log.d(References.ERROR_LOG, e.getMessage());
        }
        if (prefs.getBoolean(References.SHARED_PREFERENCES_FIRST_TIME, true) && !list.get(0).isFinished()) {
            prefs.edit().putBoolean(References.SHARED_PREFERENCES_FIRST_TIME, false).apply();
            FragmentMan.changeFragment(getActivity(), QuizFragment.class, listId.get(0), "video:" + list.get(0).getVideo());
        }
    }

    private void getTrainingsStatus() {
        final DatabaseReference db = FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.SENT)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().equals(References.QUIZ_CHILD_INDEX)) {
//                        if (data.child(References.QUIZ_CHILD_LAST_SENT).exists())
//                            lastSent = data.child(References.QUIZ_CHILD_LAST_SENT).getValue(long.class);  //obtengo la última fecha de envío
//                        if (data.child(References.QUIZ_CHILD_INDEX).exists())
//                            userCounter = data.child(References.QUIZ_CHILD_INDEX).getValue(int.class);  // obtengo el índice de la última guía enviada
                    }
                    else {
                        int index = listId.indexOf(data.getKey());
                        if (index != -1) {
                            list.get(index).setComplete(true);
                            int i = list.get(index).getIndex();
//                            if (i > userCounter)
//                                userCounter = i;
                            if (data.child(References.SENT_CHILD_CHECKED).getValue() != null)
                                list.get(index).setFinished(data.child(References.SENT_CHILD_CHECKED).getValue(boolean.class));
                        }
                    }
                }
//                db.child(References.QUIZ_CHILD_INDEX)
//                        .setValue(userCounter);
//                try {
////                    prefs.edit().putInt(References.QUIZ_CHILD_INDEX, userCounter).apply();
//                    prefs.edit().putLong(References.QUIZ_CHILD_LAST_SENT, lastSent).apply();
//                } catch (Exception e) {
//                    Log.e("ERRORR", "TrainingFragment - getTrainingsStatus - Line 212\n    " + e.getMessage());
//                    e.printStackTrace();
//                }
                adapter.notifyDataSetChanged();
                showProgress(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress(false);
            }
        });
    }

    private void showProgress(boolean show) {
        if (show)
            viewSwitcher.showPrevious();
        else
            viewSwitcher.showNext();
    }


    @Override
    public void callback() {
        if (list != null) {
            list.clear();
            listId.clear();
            adapter.notifyDataSetChanged();
            getTrainings();
        }
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            String video = "";
            try {
                video = MainActivity.videosURL.getTrainings();
            }catch (Exception e) {
                SharedPreferences prefs = getActivity().getSharedPreferences(
                        MainActivity.APP_SHARED_PREF_KEY + MainActivity.user.getuId(), Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String vjson = prefs.getString(References.SHARED_PREFERENCES_APP_VIDEOS_URL, "");
                VideosURL v = gson.fromJson(vjson, VideosURL.class);
                video = v.getTrainings();
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
        Log.d(References.ERROR_LOG, "ElectionsFragment - YOUTUBE ERROR:\n" + errorMessage);
    }

    private class TrainingsSort implements Comparator<TrainingItem> {

        @Override
        public int compare(TrainingItem o1, TrainingItem o2) {
            return o1.getIndex() - o2.getIndex();
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getTrainings();
    }
}
