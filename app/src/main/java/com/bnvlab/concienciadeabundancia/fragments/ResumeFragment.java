package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.adapters.ResumeAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.QuizItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Marcos on 08/04/2017.
 */

public class ResumeFragment extends Fragment {

    TextView tvTitle;
    ListView listView;
    ViewSwitcher viewSwitcher;

    String quizId;
    ArrayList<QuizItem> list;

    ResumeAdapter adapter;

    public ResumeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_resume, container, false);

        list = new ArrayList<>();
        adapter = new ResumeAdapter(getContext(), R.layout.item_resume_row, list);

        tvTitle = (TextView) view.findViewById(R.id.text_view_quiz_resume_layout_title);
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher_quiz_resume_layout);
        listView = (ListView) view.findViewById(R.id.list_view_quiz_resume);

        listView.setAdapter(adapter);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            quizId = bundle.getString("tag");
            getQuiz();
        }

        return view;
    }

    private void getQuiz(){
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.QUIZ)
                .child(quizId)
                .child(References.QUIZ_CHILD_TITLE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        tvTitle.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.SENT)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(quizId)
                .orderByKey()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data: dataSnapshot.getChildren())
                            list.add(data.getValue(QuizItem.class));

                        adapter.notifyDataSetChanged();
                        viewSwitcher.showNext();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        viewSwitcher.showNext();
                    }
                });
    }
}
