package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.adapters.QuizListAdapter;
import com.bnvlab.concienciadeabundancia.clases.QuizItem;
import com.bnvlab.concienciadeabundancia.clases.QuizListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Marcos on 08/04/2017.
 */

public class QuizListFragment extends Fragment {

    ArrayList<QuizListItem> list;
    QuizListAdapter adapter;

    ViewSwitcher viewSwitcher;
    ListView listView;

    private String quizId;

    public QuizListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_list, container, false);

        list = new ArrayList<>();
        adapter = new QuizListAdapter(getContext(),R.layout.item_quiz_list,list);

        listView = (ListView) view.findViewById(R.id.list_view_fragment_quiz_list);
        listView.setAdapter(adapter);

        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher_quiz_list_loading);

        getQuizList();

        list.add(new QuizListItem("TÍTULO 1", false));
        list.add(new QuizListItem("TÍTULO 2", true));

        adapter.notifyDataSetChanged();

        return view;
    }

    private void getQuizList() {
        final ArrayList<String> quizId = new ArrayList<>();

        FirebaseDatabase.getInstance()
                .getReference(MainActivity.REFERENCE)
                .child("sent")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren())
                        {
                            quizId.add(data.getValue(String.class));
                        }

                        FirebaseDatabase.getInstance()
                                .getReference(MainActivity.REFERENCE)
                                .child(QuizItem.CHILD)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot data : dataSnapshot.getChildren())
                                        {
                                            boolean isComplete = quizId.contains(data.getKey());
                                            list.add(new QuizListItem(data.child("title").getValue(String.class), isComplete));
                                            Toast.makeText(getContext(), data.getKey(), Toast.LENGTH_SHORT).show();
                                        }
                                        adapter.notifyDataSetChanged();
                                        showProgress(false);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void showProgress(boolean show) {
        if (show)
            viewSwitcher.showPrevious();
        else
            viewSwitcher.showNext();
    }

}
