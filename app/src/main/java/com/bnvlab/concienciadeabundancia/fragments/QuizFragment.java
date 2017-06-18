package com.bnvlab.concienciadeabundancia.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import com.bnvlab.concienciadeabundancia.MainActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by bort0 on 21/03/2017.
 * El @userCounter lo aumento al enviar un entrenamiento
 * Para enviar el entrenamiento, @sendQuiz, tengo que ver que el último envío haya sido hace una semana atrás
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

    String video = "WSVH_nF18Ls";
    String quizId;
    SharedPreferences prefs;

    int userCounter = 0;
    long actualWeek = 1, lastSent = 0;
    String require = "";
    boolean requiredOK = false;

    public QuizFragment() {
        actualWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        try {
            userCounter = MainActivity.user.getCounter();
            lastSent = MainActivity.user.getLastSent();
        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                    .child(References.USERS)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(References.USERS_CHILD_COUNTER).exists())
                                userCounter = dataSnapshot.child(References.USERS_CHILD_COUNTER).getValue(int.class);
                            else
                                userCounter = 1;

                            if (dataSnapshot.child(References.USERS_CHILD_LAST_SENT).exists())
                                lastSent = dataSnapshot.child(References.USERS_CHILD_LAST_SENT).getValue(long.class);
                            else
                                lastSent = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            userCounter = 1;
                        }
                    });
        }

        list = new ArrayList<>();
        adapter = new QuizAdapter(getContext(), R.layout.item_quiz_row, list);

        layoutWait = view.findViewById(R.id.layout_wait);
        myCoordinatorLayout = view.findViewById(R.id.myCoordinatorLayout);

        tvTitle = (TextView) view.findViewById(R.id.text_view_quiz_title);
        Utils.setTypeface(tvTitle, getContext());

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

        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) this.getChildFragmentManager().findFragmentById(R.id.youtube_fragment);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            quizId = bundle.getString("tag");
            String secondTag = bundle.getString("secondTag");
            if (secondTag != null && !secondTag.isEmpty())
                if (secondTag.startsWith("video:")) {
                    String[] s = secondTag.split("/");
                    if (s.length > 1) {
                        video = s[s.length - 1];
                        if (player != null)
                            player.cueVideo(video);
                    }
                    frag.initialize(Config.YOUTUBE_API_KEY, this);
                    Log.d("ERRORR", "VIDEO: " + video + "   " + s.length);
                }
                else
                    frag.initialize(Config.YOUTUBE_API_KEY, this);

            if (!quizId.isEmpty())
                getQuiz();
        }

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
                            } else if (data.getKey().equals(References.QUIZ_CHILD_VIDEO)) {
                                video = data.getValue(String.class);
                            } else if (data.getKey().equals(References.QUIZ_CHILD_REQUIRE))
                                require = data.getValue(String.class);
                            else if (!data.getKey().equals(References.QUIZ_CHILD_HIDDEN)) {
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
        layoutWait.setVisibility(show ? View.VISIBLE : View.GONE);
        myCoordinatorLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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
        if (userCounter < 1 && !require.equals("")) {
            Toast.makeText(getContext(), "No estás habilitado para enviar guías\nPor favor contáctate con nosotros", Toast.LENGTH_LONG).show();
        } else if (lastSent == actualWeek) {
            Toast.makeText(getContext(), "Esta guía todavía no está disponible\nRecuerda que se habilitará una guía semanalmente", Toast.LENGTH_LONG).show();
        } else {

            if (require.equals(""))
                requiredOK = true;
            else {
                FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                        .child(References.SENT)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                requiredOK = dataSnapshot.hasChild(require);

                                if (!requiredOK) {
                                    FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                                            .child(References.QUIZ)
                                            .child(require)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.child(References.QUIZ_CHILD_MODULE).exists()
                                                            && dataSnapshot.child(References.QUIZ_CHILD_TITLE).exists()) {
                                                        Toast.makeText(getActivity(), "Para enviar esta guía necesitas:\n"
                                                                + dataSnapshot.child(References.QUIZ_CHILD_MODULE).getValue(String.class) + "\n"
                                                                + dataSnapshot.child(References.QUIZ_CHILD_TITLE).getValue(String.class), Toast.LENGTH_LONG).show();
                                                        getActivity().onBackPressed();
                                                        showProgress(false);
                                                    } else {
                                                        Toast.makeText(getActivity(), "Ocurrió un error. Inténtelo en otro momento", Toast.LENGTH_SHORT).show();
                                                        Log.d("ERRORR", "QuizFragment - sendQuiz - Line 324\n    require: " + require + "\n    module: " + dataSnapshot.child(References.QUIZ_CHILD_MODULE).exists()
                                                                + "\n    title: " + dataSnapshot.child(References.QUIZ_CHILD_TITLE).exists());
                                                        getActivity().onBackPressed();
                                                        showProgress(false);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                } else {
                                    showProgress(true);
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
                                    try {
                                        // uC--
                                        if (!require.equals("")) {
                                            userCounter--;
                                            MainActivity.user.setCounter(userCounter);
                                            DatabaseReference db = FirebaseDatabase.getInstance().getReference(References.REFERENCE).child(References.USERS).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            db.child(References.USERS_CHILD_COUNTER).setValue(userCounter);
                                            // lastSent = today
                                            db.child(References.USERS_CHILD_LAST_SENT).setValue(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
                                        }

                                    } catch (Exception e) {
                                        Log.d("ERRORR", "QuizFragment - sendQuiz - Line 389\n    " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("ERRORR", "QuizFragment - sendQuiz - Line 307\n    " + databaseError.getMessage());
                            }
                        });
            }


        }
    }

    YouTubePlayer player;

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            //I assume the below String value is your video id
//            if (video.equals("") || video == null || video.isEmpty())
//                youTubePlayer.cueVideo("WSVH_nF18Ls");
//            else
                youTubePlayer.cueVideo(video);
            youTubePlayer.setShowFullscreenButton(false);
            player = youTubePlayer;
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
