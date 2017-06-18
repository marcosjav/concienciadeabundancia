package com.bnvlab.concienciadeabundancia.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.VideoActivity;
import com.bnvlab.concienciadeabundancia.adapters.QuizAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.Config;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
import com.bnvlab.concienciadeabundancia.clases.QuizItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bort0 on 21/03/2017.
 */

public class QuizFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
    ArrayList<QuizItem> list;
    QuizAdapter adapter;
    View view;
    boolean disable;

    TextView tvTitle, tvSubTitle, tvModule, tvDescription, tvFoot;
    ListView listView;
    Button buttonOk;
    Button buttonTestVideo;
    CheckBox checkBoxNoTestear;
    ScrollView scrollView;
    View layoutWait, myCoordinatorLayout;

    String video;
    String quizId;

    public QuizFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        list = new ArrayList<>();
        adapter = new QuizAdapter(getContext(), R.layout.item_quiz_row, list);

        layoutWait = view.findViewById(R.id.layout_wait);
        myCoordinatorLayout = view.findViewById(R.id.myCoordinatorLayout);

        tvTitle = (TextView) view.findViewById(R.id.text_view_quiz_title);
        Utils.setTypeface(tvTitle,getContext());

        tvSubTitle = (TextView) view.findViewById(R.id.text_view_quiz_subtitle);
        tvModule = (TextView) view.findViewById(R.id.text_view_quiz_module);
        tvDescription = (TextView) view.findViewById(R.id.text_view_quiz_description);
        tvFoot = (TextView) view.findViewById(R.id.text_view_foot);
        checkBoxNoTestear = (CheckBox) view.findViewById(R.id.checkbox);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        buttonOk = (Button) view.findViewById(R.id.button_quiz_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disable)
                    Toast.makeText(getContext(), "Disponible para usuarios registrados\nContáctate con nosotros para saber más", Toast.LENGTH_LONG).show();
                else {
                    sendQuiz();
                    showProgress(true);
                }
            }
        });
        buttonTestVideo = (Button) view.findViewById(R.id.video_test_button);
        buttonTestVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), VideoActivity.class);

                if (video == null || video.equals(""))
                    video = "https://youtu.be/WSVH_nF18Ls";

                myIntent.putExtra("video", video);
                getActivity().startActivity(myIntent);
            }
        });

        final Snackbar snackbar = Snackbar.make(view.findViewById(R.id.myCoordinatorLayout), "", Integer.MAX_VALUE);
        snackbar.setAction("ENVIAR", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disable)
                    Toast.makeText(getContext(), "Disponible para usuarios registrados\nContáctate con nosotros para saber más", Toast.LENGTH_LONG).show();
                else {
                    sendQuiz();
                    showProgress(true);
                }
            }
        });

        checkBoxNoTestear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    snackbar.show();
                else
                    snackbar.dismiss();
            }
        });

        listView = (ListView) view.findViewById(R.id.list_view_quiz);
        listView.setAdapter(adapter);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            quizId = bundle.getString("tag");
            String secondTag = bundle.getString("secondTag");
            if (secondTag != null && !secondTag.isEmpty())
                disable = secondTag.equals("true");
            if (!quizId.isEmpty())
                getQuiz();
        }

        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) this.getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(Config.YOUTUBE_API_KEY, this);

        this.view = view;

        return view;
    }

    private void getQuiz() {
        adapter.disable = disable;
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
                            else if (data.getKey().equals(References.QUIZ_CHILD_FOOT)) {
                                String text = data.getValue(String.class);
                                if (!text.equals(""))
                                    tvFoot.setText(text);
                            } else if (data.getKey().equals(References.QUIZ_CHILD_VIDEO))
                                video = data.getValue(String.class);
                            else if (!data.getKey().equals(References.QUIZ_CHILD_HIDDEN) && !data.getKey().equals(References.QUIZ_CHILD_REQUIRE)) {
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
        layoutWait.setVisibility(show? View.VISIBLE : View.GONE);
        myCoordinatorLayout.setVisibility(show? View.GONE : View.VISIBLE);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewPager.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount())) * 2;

        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    private void sendQuiz() {
        HashMap map = new HashMap();
        for (int i = 0; i < list.size(); i++) {
            map.put(i + "", list.get(i));
        }
        map.put(References.SENT_CHILD_CHECKED, false);

        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.SENT)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(quizId)
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle("FELICIDADES!")
                                    .setMessage("En 24HS estarán realizados los cambios. " +
                                            "\n\nTIPS PARA ACELERAR EL PROCESO: " +
                                            "Para acelerar el proceso te recomendamos que tomes al menos dos litros de agua al día y realices de 15 a 30 minutos" +
                                            " de ejercicios aeróbicos diarios\\nA partir de mañana podrás regalarle esta experiencia a todos tus conocidos, amigos" +
                                            " y familiares!")
                                    .setPositiveButton("ENTIENDO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            getActivity().onBackPressed();
                                            getActivity().onBackPressed();
                                            FragmentMan.changeFragment(getActivity(), TrainingFragment.class);
                                        }
                                    })
                                    .setCancelable(false);

                            builder.show();
                        }
                    }
                });
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            //I assume the below String value is your video id
            youTubePlayer.cueVideo("WSVH_nF18Ls");
            youTubePlayer.setShowFullscreenButton(false);
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
