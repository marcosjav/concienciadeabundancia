package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.adapters.TrainingAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.ICallback;
import com.bnvlab.concienciadeabundancia.clases.QuizItem;
import com.bnvlab.concienciadeabundancia.clases.TrainingItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Marcos on 08/04/2017.
 */

public class TrainingFragment extends Fragment implements ICallback {

    ArrayList<TrainingItem> list;
    TrainingAdapter adapter;
    ArrayList<String> listId;
    ViewSwitcher viewSwitcher;

    public TrainingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainings, container, false);

        list = new ArrayList<>();
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher_trainings);

        listId = new ArrayList<>();

        ListView listView = (ListView) view.findViewById(R.id.list_view_trainings);

        adapter = new TrainingAdapter(getContext(), R.layout.item_training_row, list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).isComplete()) {
                    FragmentMan.changeFragment(getActivity(), ResumeFragment.class, listId.get(position));
                } else {
                    FragmentMan.changeFragment(getActivity(), QuizFragment.class, listId.get(position));
                }
            }
        });

        listView.setAdapter(adapter);

        getTrainings();

        return view;
    }

    private void getTrainings() {
        FirebaseDatabase.getInstance()
                .getReference(MainActivity.REFERENCE)
                .child(QuizItem.CHILD)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String title = data.child("title").getValue(String.class);
                            list.add(new TrainingItem(title));
                            listId.add(data.getKey());
                            getTrainingsStatus();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showProgress(false);
                    }
                });
    }

    private void getTrainingsStatus() {
        FirebaseDatabase.getInstance()
                .getReference(MainActivity.REFERENCE)
                .child("sent")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            int index = listId.indexOf(data.getKey());
                            if (index != -1)
                                list.get(index).setComplete(true);
//                            Toast.makeText(getContext(), data.getKey(), Toast.LENGTH_SHORT).show();
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
}
