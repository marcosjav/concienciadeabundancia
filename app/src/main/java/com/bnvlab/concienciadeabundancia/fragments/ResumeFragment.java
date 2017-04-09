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

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Marcos on 08/04/2017.
 */

public class ResumeFragment extends Fragment {

    TextView tvTitle;
    ListView listView;
    ViewSwitcher viewSwitcher;

    String quizId;

    public ResumeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_resume, container, false);

        tvTitle = (TextView) view.findViewById(R.id.text_view_quiz_resume_layout_title);
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher_quiz_resume_layout);
        listView = (ListView) view.findViewById(R.id.list_view_quiz_resume);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            quizId = bundle.getString("tag");
            getQuiz();
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void getQuiz(){
        FirebaseDatabase.getInstance()
                .getReference(MainActivity.REFERENCE)
                .child("sent")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(quizId);
    }
}
