package com.bnvlab.concienciadeabundancia.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.VideoActivity;
import com.bnvlab.concienciadeabundancia.adapters.QuizItemAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.Config;
import com.bnvlab.concienciadeabundancia.auxiliaries.ITimeCallback;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
import com.bnvlab.concienciadeabundancia.clases.QuizItem;
import com.bnvlab.concienciadeabundancia.clases.SentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by bort0 on 21/03/2017.
 * El @userCounter lo aumento al enviar un entrenamiento
 * Para enviar el entrenamiento, @sendQuiz, tengo que ver que el último envío haya sido hace una semana atrás
 */

public class QuizFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
    //public class QuizFragment extends Fragment {
    ArrayList<QuizItem> list;
//    QuizAdapter adapter;
    View view;
    boolean disable;

    //    TextView tvTitle, tvSubTitle, tvModule, tvDescription, tvFoot;
    TextView tvTitle;
//    ListView listView;
    Button buttonOk;
    Button buttonTestVideo;
//    CheckBox checkBoxNoTestear;
    ScrollView scrollView;
    View layoutWait, myCoordinatorLayout;

    // PARTE DEL RECYPLERVIEW
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String video = "";
    String quizId;
    SharedPreferences prefs;

    long lastSent = 0;
    String require = "";
    boolean requiredOK = false;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(References.REFERENCE);

    public QuizFragment() {
//        actualWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.new_fragment_quiz, container, false);

        layoutWait = view.findViewById(R.id.settings_progressbar);
        myCoordinatorLayout = view.findViewById(R.id.layout);

        view.findViewById(R.id.new_icon_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        prefs = getActivity().getSharedPreferences(
                MainActivity.APP_SHARED_PREF_KEY + MainActivity.user.getuId(), Context.MODE_PRIVATE);

        if (prefs.getBoolean(References.SHARED_PREFERENCES_FIRST_TIME, true))
            prefs.edit().putBoolean(References.SHARED_PREFERENCES_FIRST_TIME, false).apply();

        try {
            lastSent = MainActivity.user.getLastSent();
        } catch (Exception e) {
            databaseReference
                    .child(References.USERS)
                    .child(MainActivity.user.getuId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(References.USERS_CHILD_COUNTER).exists())
                                MainActivity.user.setCounter(dataSnapshot.child(References.USERS_CHILD_COUNTER).getValue(int.class));
                            else
                                MainActivity.user.setCounter(0);

                            if (dataSnapshot.child(References.USERS_CHILD_LAST_SENT).exists())
                                lastSent = dataSnapshot.child(References.USERS_CHILD_LAST_SENT).getValue(long.class);
                            else
                                lastSent = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            MainActivity.user.setCounter(0);
                        }
                    });
        }

        list = new ArrayList<>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new QuizItemAdapter(list);
        mRecyclerView.setAdapter(mAdapter);

//        adapter = new QuizAdapter(getContext(), R.layout.item_quiz_row, list);
//        listView = (ListView) view.findViewById(R.id.list_view_quiz);
//        listView.setAdapter(adapter);

        tvTitle = (TextView) view.findViewById(R.id.textView);

        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        buttonOk = (Button) view.findViewById(R.id.button_quiz_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disable)
                    Toast.makeText(getContext(), "Disponible para usuarios registrados\nContáctate con nosotros para saber más", Toast.LENGTH_LONG).show();
                else {
                    checkSend();
                }
            }
        });

        Button buttonNoTest = (Button) view.findViewById(R.id.button_quiz_no_test);
        buttonNoTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disable)
                    Toast.makeText(getContext(), "Disponible para usuarios registrados\nContáctate con nosotros para saber más", Toast.LENGTH_LONG).show();
                else {
                    checkSend();
                }
            }
        });

        buttonTestVideo = (Button) view.findViewById(R.id.video_test_button);
        buttonTestVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), VideoActivity.class);

//                if (video == null || video.equals(""))
//                    video = "https://youtu.be/IzP2QOnm7r4";

                myIntent.putExtra("video", MainActivity.videosURL.getTest());
                getActivity().startActivity(myIntent);
            }
        });


        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) this.getChildFragmentManager().findFragmentById(R.id.youtube_fragment);

        Bundle bundle = this.getArguments();

        video = MainActivity.videosURL.getTest();
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
                    Log.d(References.ERROR_LOG, "VIDEO: " + video + "   " + s.length);
                } else
                    frag.initialize(Config.YOUTUBE_API_KEY, this);

            if (!quizId.isEmpty())
                getQuiz();
        }

        this.view = view;

        return view;
    }

    private void getQuiz() {
//        adapter.disable = disable;
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
                            else if (data.getKey().equals(References.QUIZ_CHILD_SUBTITLE)) {
//                                tvSubTitle.setText(data.getValue(String.class));
                            } else if (data.getKey().equals(References.QUIZ_CHILD_MODULE)) {
//                                tvModule.setText(data.getValue(String.class));
                            } else if (data.getKey().equals(References.QUIZ_CHILD_DESCRIPTION)) {
//                                tvDescription.setText(data.getValue(String.class));
                            } else if (data.getKey().equals(References.FREE_CONTENT))
                                free = data.getValue(boolean.class);
                            else if (data.getKey().equals(References.QUIZ_CHILD_INDEX))
                                free = free;
                            else if (data.getKey().equals(References.QUIZ_CHILD_FOOT)) {
//                                String text = data.getValue(String.class);
//                                if (!text.equals(""))
//                                    tvFoot.setText(text);
                            } else if (data.getKey().equals(References.QUIZ_CHILD_VIDEO)) {
                                video = data.getValue(String.class);
                            } else if (data.getKey().equals(References.QUIZ_CHILD_REQUIRE))
                                require = data.getValue(String.class);
                            else if (!data.getKey().equals(References.QUIZ_CHILD_HIDDEN)) {
                                QuizItem quizItem = new QuizItem(data.getValue(String.class));
                                list.add(quizItem);
//                                Toast.makeText(getContext(), quizItem.getQuiz(), Toast.LENGTH_SHORT).show();
                            }

                        }
                        mAdapter.notifyDataSetChanged();
//                        setListViewHeightBasedOnChildren(listView);
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

    private void checkSend() {
        Log.d(References.ERROR_LOG, "Counter: " + MainActivity.user.getCounter() + " Reference:" + MainActivity.appValues.getCounter());
//        if (MainActivity.user.getCounter() < MainActivity.appValues.getCounter())
//            sendQuiz();
//        else {
            Utils.getServerTime(new ITimeCallback() {
                @Override
                public void callback(long value) {
                    Log.d(References.ERROR_LOG, "Callback time: " + value);
                    Date date = new Date(value);
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    long actualWeek = calendar.get(Calendar.YEAR)*100 + calendar.get(Calendar.WEEK_OF_YEAR);
                    sendQuiz(actualWeek);
                }
            });
//        }
    }

    private void sendQuiz(long time) {
        long spanTime = time - MainActivity.user.getLastSent();

        Log.d(References.ERROR_LOG, "ServerTime: " + time + " last:" + MainActivity.user.getLastSent() + " span: " + spanTime);

        if (spanTime > 0)
            resetCounter();

        if (MainActivity.user.getCounter() < MainActivity.appValues.getCounter())
            sendQuiz();
        else
            Toast.makeText(getActivity(), "No se puede enviar más de " + MainActivity.appValues.getCounter() + " guías por semana", Toast.LENGTH_LONG).show();
    }

    private void sendQuiz() {
        if (require.equals(""))
            requiredOK = true;

        databaseReference
                .child(References.SENT)
                .child(MainActivity.user.getuId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!requiredOK)
                            requiredOK = dataSnapshot.hasChild(require);

                        if (!requiredOK) {  //################## OBTENGO LOS DATOS DE LA GUÍA QUE FALTA ENVIAR
                            databaseReference
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
                                                Log.d(References.ERROR_LOG, "QuizFragment - No tiene guía requerida");
                                                showProgress(false);
                                            } else {
                                                Toast.makeText(getActivity(), "Ocurrió un error. Inténtelo en otro momento", Toast.LENGTH_SHORT).show();
                                                Log.d(References.ERROR_LOG, "QuizFragment - sendQuiz - Line 362\n    require: " + require + "\n    module: " + dataSnapshot.child(References.QUIZ_CHILD_MODULE).exists()
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
                            Log.d(References.ERROR_LOG, "QuizFragment - Enviando guía...");
                            showProgress(true);
                            HashMap map = new HashMap();
                            for (int i = 0; i < list.size(); i++) {
                                map.put(i + "", list.get(i));
                            }
                            map.put(References.SENT_CHILD_CHECKED, false);

                            FirebaseDatabase.getInstance()
                                    .getReference(References.REFERENCE)
                                    .child(References.SENT)
                                    .child(MainActivity.user.getuId())
                                    .child(quizId)
                                    .setValue(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                incrementCounter();
                                                updateLastSent();
                                                Log.d(References.ERROR_LOG, "QuizFragment - Guía enviada con éxito");
                                                FragmentMan.changeFragment(getActivity(), CongratsFragment.class);
                                                // GUARDO LA GUÍA ENVIADA
                                                MainActivity.sentUser.add(new SentUser(quizId, false));
                                                prefs.edit().putString(References.SHARED_PREFERENCES_USER_SENT_JSON, new Gson().toJson(MainActivity.sentUser)).apply();
                                            } else
                                                Log.d(References.ERROR_LOG, "QuizFragment - Error enviando la guía");
                                        }
                                    });
//                            try {
//                                // uC--
//                                if (!require.equals("")) {
//                                    userCounter--;
//                                    MainActivity.user.setCounter(userCounter);
//                                    DatabaseReference db = databaseReference.child(References.USERS).child(MainActivity.user.getuId());
//                                    db.child(References.USERS_CHILD_COUNTER).setValue(userCounter);
//                                    // lastSent = today
////                                        db.child(References.USERS_CHILD_LAST_SENT).setValue(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
//                                    db.child(References.USERS_CHILD_LAST_SENT).setValue(ServerValue.TIMESTAMP);
//                                }
//
//                            } catch (Exception e) {
//                                Log.d(References.ERROR_LOG, "QuizFragment - sendQuiz - Line 389\n    " + e.getMessage());
//                                e.printStackTrace();
//                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(References.ERROR_LOG, "QuizFragment - sendQuiz - Line 307\n    " + databaseError.getMessage());
                    }
                });


//        }
    }

    private void incrementCounter(boolean reset) {
        int newCounter;
        if (reset)
            newCounter = 0;
        else
            newCounter = MainActivity.user.getCounter() + 1;

        Log.d(References.ERROR_LOG, "Counter: " + MainActivity.user.getCounter() + " new: " + newCounter);

        MainActivity.user.setCounter(newCounter);

        databaseReference
                .child(References.USERS)
                .child(MainActivity.user.getuId())
                .child(References.USERS_CHILD_COUNTER)
                .setValue(newCounter);
    }

    private void incrementCounter(){
        incrementCounter(false);
    }

    private void resetCounter() {
        incrementCounter(true);
    }

    private void updateLastSent() {
        final DatabaseReference dbr = databaseReference
                .child(References.USERS)
                .child(MainActivity.user.getuId())
                .child(References.USERS_CHILD_LAST_SENT);

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainActivity.user.setLastSent(dataSnapshot.getValue(long.class));
                dbr.removeEventListener(this);
                Log.d(References.ERROR_LOG, "New LastSent: " +dataSnapshot.getValue(long.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        long week = Calendar.getInstance().get(Calendar.YEAR)*100 + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        dbr.setValue(week);
    }

    YouTubePlayer player;

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            youTubePlayer.cueVideo(video);
            youTubePlayer.setShowFullscreenButton(false);
            player = youTubePlayer;
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        String errorMessage = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), 1).show();
        }
        Log.d(References.ERROR_LOG, "QuizFragment - YOUTUBE ERROR:\n" + errorMessage);
    }
}
