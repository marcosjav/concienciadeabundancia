package com.bnvlab.concienciadeabundancia.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.VideoActivity;
import com.bnvlab.concienciadeabundancia.adapters.QuizAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.QuizItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by bort0 on 21/03/2017.
 */

public class QuizFragment extends Fragment {
    ArrayList<QuizItem> list;
    QuizAdapter adapter;
    View view;

    TextView tvTitle, tvSubTitle, tvModule, tvDescription, tvFoot;
    ViewSwitcher viewSwitcher;
    ListView listView;
    Button buttonOk;
    Button buttonTestVideo;

    String video;
    String quizId;

    public QuizFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        list = new ArrayList<>();
        adapter = new QuizAdapter(getContext(), R.layout.item_quiz_row, list);

        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher_quiz_layout);
        tvTitle = (TextView) view.findViewById(R.id.text_view_quiz_title);
        tvSubTitle = (TextView) view.findViewById(R.id.text_view_quiz_subtitle);
        tvModule = (TextView) view.findViewById(R.id.text_view_quiz_module);
        tvDescription = (TextView) view.findViewById(R.id.text_view_quiz_description);
        tvFoot = (TextView) view.findViewById(R.id.text_view_foot);

        buttonOk = (Button) view.findViewById(R.id.button_quiz_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendQuiz();
                viewSwitcher.showNext();
            }
        });
        buttonTestVideo = (Button) view.findViewById(R.id.video_test_button);
        buttonTestVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), VideoActivity.class);

                if (video == null || video.equals(""))
                    video = "https://youtu.be/WSVH_nF18Ls";

                myIntent.putExtra("video", video); //Optional parameters
//                myIntent.putExtra("list", list);
                getActivity().startActivity(myIntent);
            }
        });

        listView = (ListView) view.findViewById(R.id.list_view_quiz);
        listView.setAdapter(adapter);

//        setListViewHeightBasedOnChildren(listView);



        Bundle bundle = this.getArguments();

        if (bundle != null) {
            quizId = bundle.getString("tag");
            if (!quizId.isEmpty())
                getQuiz();
        }

        this.view = view;
        return view;
    }

    private void getQuiz() {
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.QUIZ)
                .child(quizId)
                .orderByKey()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean free = false;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.getKey().equals(References.QUIZ_CHILD_TITLE))
                                tvTitle.setText(data.getValue(String.class));
                            else if (data.getKey().equals(References.QUIZ_CHILD_SUBTITLE))
                                tvSubTitle.setText(data.getValue(String.class));
                            else if (data.getKey().equals(References.QUIZ_CHILD_MODULE))
                                tvModule.setText(data.getValue(String.class));
                            else if (data.getKey().equals(References.QUIZ_CHILD_DESCRIPTION))
                                tvDescription.setText(data.getValue(String.class));
                            else if (data.getKey().equals(References.FREE_CONTENT))
                                free = data.getValue(boolean.class);
                            else if (data.getKey().equals(References.QUIZ_CHILD_INDEX))
                                free = free;
                            else if (data.getKey().equals(References.QUIZ_CHILD_FOOT)){
                                String text = data.getValue(String.class);
                                if (!text.equals(""))
                                    tvFoot.setText(text);
                            }
                            else if (data.getKey().equals(References.QUIZ_CHILD_VIDEO))
                                video = data.getValue(String.class);
                            else if (!data.getKey().equals(References.QUIZ_CHILD_HIDDEN)){
                                QuizItem quizItem = new QuizItem(data.getValue(String.class));
                                list.add(quizItem);
                                adapter.notifyDataSetChanged();
//                                Toast.makeText(getContext(), quizItem.getQuiz(), Toast.LENGTH_SHORT).show();
                            }

                        }
                        setListViewHeightBasedOnChildren(listView);
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

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight=0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewPager.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount()))*2;

        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    private void sendQuiz()
    {
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child("sent")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(quizId)
                .setValue(list)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            getActivity().onBackPressed();
                            getActivity().onBackPressed();
                            FragmentMan.changeFragment(getActivity(), TrainingFragment.class);
                        }
                    }
                });
    }
}
