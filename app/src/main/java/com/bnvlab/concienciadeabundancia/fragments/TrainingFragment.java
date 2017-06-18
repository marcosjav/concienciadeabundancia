package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.adapters.TrainingAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.ICallback;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
import com.bnvlab.concienciadeabundancia.clases.TrainingItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

    public TrainingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainings, container, false);

        TextView title = (TextView) view.findViewById(R.id.textView);
        title.setTypeface(Utils.getTypeface(getContext()));

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
                    boolean complete = true;
                    int i = -1;
                    TrainingItem required = new TrainingItem("");
                    if (!item.getRequire().equals("")) {
                        i = listId.indexOf(item.getRequire());
                        if (i != -1) {
                            required = list.get(i);
                            complete = required.isComplete();
                        }
                    }

                    if (!complete) {
                        Toast.makeText(getContext(), "Necesita hacer la guía:\n" + required.getTitle(), Toast.LENGTH_SHORT).show();
                        listView.getChildAt(i).setBackgroundColor(0);
                    }
                    else if (item.isFree() || active_user) {
                        FragmentMan.changeFragment(getActivity(), QuizFragment.class, listId.get(position));
                    } else
                        FragmentMan.changeFragment(getActivity(), QuizFragment.class, listId.get(position), "true");
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
                .child(References.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            active_user = dataSnapshot.child(References.USERS_CHILD_ACTIVE).getValue(boolean.class);
                        }catch (Exception e){
                            active_user = false;
                        }
                        getTrainings(active_user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getTrainings(final boolean active) {
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
                            int index = -1;
                            boolean freeContent = data.child(References.FREE_CONTENT).getValue(boolean.class);
                            boolean hidden = data.child(References.QUIZ_CHILD_HIDDEN).getValue(boolean.class);

                            if (data.child(References.QUIZ_CHILD_TITLE).exists())
                                title = data.child(References.QUIZ_CHILD_TITLE).getValue(String.class);

                            if (data.child(References.FAQ_CHILD_INDEX).exists())
                                index = data.child(References.FAQ_CHILD_INDEX).getValue(int.class);

                            if (data.child(References.QUIZ_CHILD_REQUIRE).exists())
                                require = data.child(References.QUIZ_CHILD_REQUIRE).getValue(String.class);

                            if (!hidden) {
                                TrainingItem item = new TrainingItem();
                                item.setTitle(title);
                                item.setFree(freeContent);
                                item.setRequire(require);
                                if (index < 0 )
                                    index = list.size();
                                item.setIndex(index);
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
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.SENT)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            int index = listId.indexOf(data.getKey());
                            if (index != -1) {
                                list.get(index).setComplete(true);
                                if (data.child(References.SENT_CHILD_CHECKED).getValue() != null)
                                    list.get(index).setFinished(data.child(References.SENT_CHILD_CHECKED).getValue(boolean.class));
                            }
                        }
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

    private class TrainingsSort implements Comparator<TrainingItem>{

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
