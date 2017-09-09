package com.bnvlab.concienciadeabundancia.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.Config;
import com.bnvlab.concienciadeabundancia.auxiliaries.Notify;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
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
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Marcos on 21/03/2017.
 */

public class MainFragment extends Fragment {
    //    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    ImageButton buttonWebLink, buttonFacebookLink, buttonInstaLink, buttonLogout, buttonSettings, buttonAddChilden, notificationButton, buttonSubscribe;
    ImageButton buttonFAQ, buttonAbout, buttonFundaments, buttonConference, buttonVideos;
    Button buttonTrainings, buttonShare;
//    View shareRow;
    private static final String fbUri0 = "https://www.facebook.com/";
    private static final String fbUri1 = "cda-internacional-256242691550706";
    ArrayList<JSONObject> notificationsList;
    View notificationsIndicator;
//    LinearLayout layoutTrainings;
    SharedPreferences prefs;
//    Animation bounce;
    final static String TAG = "ERRORR - MainFragment";

    public MainFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
//        bounce = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
//        // Use bounce interpolator with amplitude 0.2 and frequency 20
//        MyBounceInterpolator interpolator = new MyBounceInterpolator();
//
//        bounce.setInterpolator(interpolator);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.new_fragment_main, container, false);

        prefs = getActivity().getSharedPreferences(
                MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(), Context.MODE_PRIVATE);

        checkUserType();

        notificationButton = (ImageButton) view.findViewById(R.id.notifications);
//        shareRow = view.findViewById(R.id.layout_share);
        buttonFacebookLink = (ImageButton) view.findViewById(R.id.button_main_facebook);
        buttonWebLink = (ImageButton) view.findViewById(R.id.button_main_web);
        buttonInstaLink = (ImageButton) view.findViewById(R.id.button_main_instagram);
        buttonLogout = (ImageButton) view.findViewById(R.id.button_logout_main);
        buttonSettings = (ImageButton) view.findViewById(R.id.button_settings);
        buttonAddChilden = (ImageButton) view.findViewById(R.id.add_children);

        buttonShare = (Button) view.findViewById(R.id.button_main_share);
        buttonTrainings = (Button) view.findViewById(R.id.button_main_trainings);
        buttonFAQ = (ImageButton) view.findViewById(R.id.button_main_faq);
        buttonFundaments = (ImageButton) view.findViewById(R.id.button_main_fundaments);
        buttonAbout = (ImageButton) view.findViewById(R.id.button_main_about_us);
        buttonConference = (ImageButton) view.findViewById(R.id.button_main_conference);
        buttonVideos = (ImageButton) view.findViewById(R.id.button_main_videos);
        buttonSubscribe = (ImageButton) view.findViewById(R.id.button_main_subscribe);

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), NotificationsFragment.class);
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), SettingsFragment.class);
            }
        });

        buttonConference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), ConferenceFragment.class);
            }
        });

        buttonFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), FaqFragment.class);
            }
        });

        buttonVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), VideoFragment.class);
            }
        });

        buttonFundaments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), FundamentsFragment.class);
                //Intent intent = new Intent(getActivity(), MainEvolution.class);
//                startActivity(intent);
            }
        });

        buttonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), CDAFragment.class);
            }
        });

        buttonFacebookLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(fbUri0 + fbUri1); // missing 'http://' will cause crashed
                try {
                    int versionCode = getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                    boolean activated = getContext().getPackageManager().getApplicationInfo("com.facebook.katana", 0).enabled;
                    if (activated) {
                        if ((versionCode >= 3002850)) {
                            uri = Uri.parse("fb://facewebmodal/f?href=" + fbUri0 + fbUri1);
                        } else {
                            uri = Uri.parse("fb://page/" + fbUri1);
                        }
                    }
                } catch (PackageManager.NameNotFoundException ignored) {
                    Log.e("ERRORR", "MainFragment - OnCreate - Line 326\n    " + ignored.getMessage());
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        buttonWebLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.cdainter.com"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        buttonInstaLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/cdainternacional");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/cdainternacional")));
                }
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder
                        .setMessage("Seguro de cerrar sesión?")
                        .setTitle("SALIR")
                        .setIcon(R.drawable.attention_yellow)
                        .setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                try {
                                    FirebaseInstanceId.getInstance().deleteInstanceId();
                                } catch (IOException e) {
                                    Log.e("ERRORR", "MainFragment - OnCreate - Line 392\n    " + e.getMessage());
                                }
                                Utils.showLogin(getActivity());
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });

        showShare(prefs.getBoolean(References.SHARED_PREFERENCES_CAN_SHARE, false));


        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog();
            }
        });

        buttonTrainings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), TrainingFragment.class);
            }
        });

        buttonAddChilden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), PayFragment.class);
            }
        });

        return view;
    }

//    private void dialogFirstTime(){
//        // use a default value using new Date()
//        boolean firstTime = prefs.getBoolean(MainActivity.FIRST_TIME_PREF_KEY, true);
//
//        // PROPAGANDA DEL PRIMER INGRESO DEL USUSARIO
//        if (firstTime) {
//            try {
//                Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), Config.YOUTUBE_API_KEY, "GJZ45KiWLV4", 0, true, true);
//                startActivity(intent);
//                prefs.edit().putBoolean(MainActivity.FIRST_TIME_PREF_KEY, false).apply();
//            }catch (Exception e){
//                Log.e(TAG, e.getMessage());
//                e.printStackTrace();
//            }
//        }
//    }

    private void checkUserType() {
        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.ADMINISTRATORS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .child(fbUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            showShare(true);
                            prefs.edit().putBoolean(References.SHARED_PREFERENCES_CAN_SHARE, true).apply();
                        } else {
                            FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                                    .child(References.SENT)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("-KiaTj3gjkFLXOBDDBWa")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null)
                                                showShare(true);
                                                prefs.edit().putBoolean(References.SHARED_PREFERENCES_CAN_SHARE, true).apply();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e("ERRORR", "MainFragment - OnCreate - Line 459\n    " + databaseError.getMessage());
                                        }
                                    });
//                            FirebaseDatabase.getInstance().getReference(References.REFERENCE)
//                                    .child(References.USERS)
//                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                    .child(References.USERS_CHILD_ACTIVE)
//                                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.getValue() != null)
//                                                showShare(dataSnapshot.getValue(boolean.class));
//                                            prefs.edit().putBoolean(References.SHARED_PREFERENCES_CAN_SHARE, dataSnapshot.getValue(boolean.class)).apply();
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//                                            Log.e("ERRORR", "MainFragment - OnCreate - Line 459\n    " + databaseError.getMessage());
//                                        }
//                                    });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("ERRORR", "MainFragment - OnCreate - Line 468\n    " + databaseError.getMessage());
                    }
                });
    }

    private void shareDialog() {
        final String invitationCode = FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.INVITATION_CODES)
                .push()
                .getKey();

        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.INVITATION_CODES)
                .child(invitationCode)
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String message = "Hola, te invito a vivir una experiencia que va a potenciar tu vida.\n En 30 minutos lograrás potenciar tu confianza, valoración, seguridad, alegría y reducirás tu estrés diario, de forma inmediata y permanente.\n\nPrimero instalá la app:\n\n" +
                                    "https://play.google.com/store/apps/details?id=com.bnvlab.concienciadeabundancia\n\nDespués abrí este link y cuando te pregunte con qué aplicación, elegí la que instalaste en el paso anterior\n\n" +
                                    "http://concienciadeabundancia.com/code=" + invitationCode;

                            Notify.share(message, getContext());
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("ERRORR", "MainFragment - shareDialog - Line 559\n    " + task.getException().getMessage());
                        }
                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
//        dialogFirstTime();
    }

    private void dialogFirstTime() {

        if (prefs.getBoolean(MainActivity.FIRST_TIME_PREF_KEY, true)) {
            prefs.edit().putBoolean(MainActivity.FIRST_TIME_PREF_KEY, false).apply();
//            Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), Config.YOUTUBE_API_KEY, "GJZ45KiWLV4", 0, true, true);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = View.inflate(getActivity(),R.layout.dialog_first_time,null);

            final YouTubePlayerSupportFragment youTubePlayerSupportFragment = (YouTubePlayerSupportFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);

            youTubePlayerSupportFragment.initialize(Config.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                    if (!wasRestored) {
                        //I assume the below String value is your video id
                        youTubePlayer.loadVideo("GJZ45KiWLV4");
                    }
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    if (youTubeInitializationResult.isUserRecoverableError()) {
                        youTubeInitializationResult.getErrorDialog(getActivity(), 1).show();
                    } else {
                        String errorMessage = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });

            builder.setView(view)
                    .setCancelable(false)
                    .setPositiveButton("ENTENDIDO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            youTubePlayerSupportFragment.onDestroy();
                        }
                    })
                    .create()
                    .show();
        }

    }

    private void showShare(boolean show) {
//        shareRow.setVisibility(show ? View.VISIBLE : View.GONE);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutTrainings.getLayoutParams();
//        params.gravity = show ? Gravity.START : Gravity.END;
//        layoutTrainings.setLayoutParams(params);
    }

    class AsyncT extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL url = new URL("https://fcm.googleapis.com/fcm/send"); //Enter URL here
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Authorization", "key=AAAAJoiF3uY:APA91bFcQXSRcnKoPBiUk8MmBaRw_EQO55ekb_9WQzGDsia78SaPy3mgDAxwct3EjVY0GEXrs_4Z8qthZQrpqIv76fv9T-vLfQSf7Q-yzgqhnafwe562VV3--8Gg0dJQmyIVEW-BymC8");
                httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("to", "/topics/notifications");
                JSONObject jsonNotif = new JSONObject();
                jsonNotif.put("title", "ESTO ES EL TITULO");
                jsonNotif.put("text", "esto es eL CUERPO");
                jsonObject.put("notification", jsonNotif);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                final JSONObject jo = jsonObject;
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
//                        Toast.makeText(getContext(), jo.toString(), Toast.LENGTH_LONG).show();
                    }
                });

                wr.writeBytes("{ \"notification\": {\"title\": \"Portugal vs. Denmark\",\"text\": \"5 to 1\"},\"to\" : \"cEoIG3BAlJo:APA91bFKUoTIGcwMw9BorsCpH7DUtiXW6FC_-UkkXpDEAj8H0U_YAh5hyhSrHjkM6SXYp40g62Wz1LrylARSSZ1MFsWtev-HAgbyJ0P153-wKAr1WgHRcrs1mmMmDvvcWyCdH1BFyBnK\"}");
                wr.flush();
                wr.close();

            } catch (MalformedURLException e) {
                Log.e("ERRORR", "MainFragment - class AsyncT - Line 615\n    " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("ERRORR", "MainFragment - class AsyncT - Line 618\n    " + e.getMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e("ERRORR", "MainFragment - class AsyncT - Line 621\n    " + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }


    }
}
/*class MyBounceInterpolator implements android.view.animation.Interpolator {

    MyBounceInterpolator() {
    }

    public float getInterpolation(float time) {
//        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
//                Math.cos(mFrequency * time) + 1);
        return (float) Math.sin(Math.PI*time);
    }
}*/
