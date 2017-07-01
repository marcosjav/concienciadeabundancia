package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.adapters.ResumeAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.Config;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
import com.bnvlab.concienciadeabundancia.clases.QuizItem;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Marcos on 08/04/2017.
 */

public class ResumeFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
//public class ResumeFragment extends Fragment {
    TextView tvTitle;
    ListView listView;
    ViewSwitcher viewSwitcher;
    TextView textViewResume;

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

        TextView title = (TextView) view.findViewById(R.id.textView);
        title.setTypeface(Utils.getTypeface(getContext()));

        tvTitle = (TextView) view.findViewById(R.id.text_view_quiz_resume_layout_title);
        tvTitle.setTypeface(Utils.getTypeface(getContext()));

        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher_quiz_resume_layout);
        listView = (ListView) view.findViewById(R.id.list_view_quiz_resume);

        listView.setAdapter(adapter);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            quizId = bundle.getString("tag");
            getQuiz();
        }

        textViewResume = (TextView) view.findViewById(R.id.text_view_resume);
        textViewResume.setTypeface(Utils.getTypeface(getContext()));

        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) this.getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(Config.YOUTUBE_API_KEY, this);

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
                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                            if (!data.getKey().equals(References.SENT_CHILD_CHECKED))
                                list.add(data.getValue(QuizItem.class));
                            else {
                                if (data.getValue(boolean.class)){
                                    FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                                            .child(References.TEXTS)
                                            .child(References.TEXTS_RESUME_FINISH)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getValue() != null)
                                                        textViewResume.setText(dataSnapshot.getValue(String.class));
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                }else{
                                    FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                                            .child(References.TEXTS)
                                            .child(References.TEXTS_RESUME_WAITING)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getValue() != null)
                                                        textViewResume.setText(dataSnapshot.getValue(String.class));
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }

                        }

                        adapter.notifyDataSetChanged();
                        Utils.setListViewHeightBasedOnChildren(listView);
                        viewSwitcher.showNext();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        viewSwitcher.showNext();
                    }
                });
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            youTubePlayer.cueVideo("WSVH_nF18Ls");
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), 1).show();
        } else {
            String errorMessage = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}
