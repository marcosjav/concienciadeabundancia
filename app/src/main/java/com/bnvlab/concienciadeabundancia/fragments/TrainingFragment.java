package com.bnvlab.concienciadeabundancia.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.bnvlab.concienciadeabundancia.auxiliaries.ICallback;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.TrainingItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Marcos on 08/04/2017.
 */

public class TrainingFragment extends Fragment implements ICallback {

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

//    Animation bounce;
    @Override
    public void onStart() {
        super.onStart();
//        bounce = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
//        MyBounceInterpolator interpolator = new MyBounceInterpolator();

//        bounce.setInterpolator(interpolator);
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
//                v.startAnimation(bounce);
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
//                view.startAnimation(bounce);
                if (item.isComplete()) {
                    FragmentMan.changeFragment(getActivity(), ResumeFragment.class, listId.get(position));
                } else {
//                    boolean complete = true;
//                    int i = -1;
//                    TrainingItem required = new TrainingItem("");
//                    if (!item.getRequire().equals("")) {
//                        i = listId.indexOf(item.getRequire());
//                        if (i != -1) {
//                            required = list.get(i);
//                            complete = required.isComplete();
//                        }
//                    }
//
//                    if (!complete) {
//                        Toast.makeText(getContext(), "Necesita hacer la guía:\n" + required.getTitle(), Toast.LENGTH_SHORT).show();
//                        listView.getChildAt(i).setBackgroundColor(0);
//                    } else if (item.isFree() || active_user) {
//                        FragmentMan.changeFragment(getActivity(), QuizFragment.class, listId.get(position));
//                    } else
                        FragmentMan.changeFragment(getActivity(), QuizFragment.class, listId.get(position), "video:" + list.get(position).getVideo());
//                        Toast.makeText(getContext(), "Disponible para usuarios registrados\nContáctate con nosotros para saber más", Toast.LENGTH_LONG).show();

                }
            }
        });

        listView.setAdapter(adapter);

        getTrainings();

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
                        getTrainingsStatus();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showProgress(false);
                    }
                });
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
}
