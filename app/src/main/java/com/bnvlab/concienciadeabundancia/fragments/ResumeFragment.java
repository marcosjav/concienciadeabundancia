package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
//    ViewSwitcher viewSwitcher;
//    TextView textViewResume;
    ScrollView scrollView;
    LinearLayout layoutProgress;
    String quizId;
    ArrayList<QuizItem> list;

    ResumeAdapter adapter;
//    Animation bounce;
    @Override
    public void onStart() {
        super.onStart();
/*        bounce = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator();

        bounce.setInterpolator(interpolator);*/
    }

    public ResumeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_resume, container, false);

        list = new ArrayList<>();
        adapter = new ResumeAdapter(getContext(), R.layout.item_resume_row, list);

//        TextView title = (TextView) view.findViewById(R.id.textView);
//        title.setTypeface(Utils.getTypeface(getContext()));

        tvTitle = (TextView) view.findViewById(R.id.textView);
        layoutProgress = (LinearLayout) view.findViewById(R.id.layout_progress);
//        tvTitle.setTypeface(Utils.getTypeface(getContext()));

//        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher_quiz_resume_layout);
        listView = (ListView) view.findViewById(R.id.list_view_quiz_resume);
        scrollView = (ScrollView) view.findViewById(R.id.layout);
        listView.setAdapter(adapter);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            quizId = bundle.getString("tag");
            getQuiz();
        }

//        textViewResume = (TextView) view.findViewById(R.id.text_view_resume);
//        textViewResume.setTypeface(Utils.getTypeface(getContext()));

        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) this.getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(Config.YOUTUBE_API_KEY, this);

//        ((TextView)view.findViewById(R.id.text_back)).setTypeface(Utils.getTypeface(getContext()));
        view.findViewById(R.id.new_icon_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(bounce);
                getActivity().onBackPressed();
            }
        });
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
                        showProgress(false);
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
//                                if (data.getValue(boolean.class)){
//                                    FirebaseDatabase.getInstance().getReference(References.REFERENCE)
//                                            .child(References.TEXTS)
//                                            .child(References.TEXTS_RESUME_FINISH)
//                                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                    if (dataSnapshot.getValue() != null)
//                                                        textViewResume.setText(dataSnapshot.getValue(String.class));
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                }else{
//                                    FirebaseDatabase.getInstance().getReference(References.REFERENCE)
//                                            .child(References.TEXTS)
//                                            .child(References.TEXTS_RESUME_WAITING)
//                                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                    if (dataSnapshot.getValue() != null)
//                                                        textViewResume.setText(dataSnapshot.getValue(String.class));
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                }
                            }

                        }

                        adapter.notifyDataSetChanged();
                        Utils.setListViewHeightBasedOnChildren(listView);
//                        viewSwitcher.showNext();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

//                        viewSwitcher.showNext();
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

    private void showProgress(boolean show){
        scrollView.setVisibility(show? View.GONE : View.VISIBLE);
        layoutProgress.setVisibility(show? View.VISIBLE : View.GONE);
    }
}
