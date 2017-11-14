package com.bnvlab.concienciadeabundancia.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.LoginActivity;
import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.VersionChecker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Marcos on 21/03/2017.
 */

public class MainFragment extends Fragment {
    //    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    ImageButton buttonWebLink, buttonFacebookLink, buttonInstaLink, buttonLogout, buttonSettings, buttonAddChilden, notificationButton, buttonSubscribe;
    ImageButton buttonFAQ, buttonAbout, buttonFundaments, buttonConference, buttonVideos;
    Button buttonTrainings, buttonShare, buttonAsk, buttonElections;
    //    View shareRow;
    private static final String fbUri0 = "https://www.facebook.com/";
    private static final String fbUri1 = "cda-internacional-256242691550706";
    ArrayList<JSONObject> notificationsList;
    View notificationsIndicator;
    //    LinearLayout layoutTrainings;
    SharedPreferences prefs;
    //    Animation bounce;
    final static String TAG = "ERRORR - MainFragment";
    private DatabaseReference reference;
    private FirebaseUser fbUser;

    public MainFragment() {
        reference = FirebaseDatabase.getInstance().getReference(References.REFERENCE);
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (fbUser == null) {
            Intent myIntent = new Intent(getActivity(), LoginActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            this.startActivity(myIntent);
            getActivity().finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.new_fragment_main, container, false);

        prefs = getActivity().getSharedPreferences(
                MainActivity.APP_SHARED_PREF_KEY + fbUser.getUid(), Context.MODE_PRIVATE);

        //################ show last date connection after change ############
//        reference.child(References.LAST_CONNECTION)
//                .child(fbUser.getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.getValue() != null && dataSnapshot.getValue(long.class) != null) {
//                            String dateString = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", getResources().getConfiguration().locale).format(new Date(dataSnapshot.getValue(long.class)));
//                            Log.d("DEBUG_LAST_CONNECTION", dateString);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

        checkUserType();
        reference
                .child(References.LAST_CONNECTION)
                .child(fbUser.getUid())
                .setValue(ServerValue.TIMESTAMP);

        notificationButton = (ImageButton) view.findViewById(R.id.notifications);
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
        buttonAsk = (Button) view.findViewById(R.id.button_main_ask);
        buttonElections = (Button) view.findViewById(R.id.button_main_elections);

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
                /*AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

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
                builder.create().show();*/
                FragmentMan.changeFragment(getActivity(), LogoutFragment.class);
            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                shareDialog();
                FragmentMan.changeFragment(getActivity(), ShareFragment.class);
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

        buttonAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), ChatFragment.class);
            }
        });

        buttonElections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), ElectionsFragment.class);
            }
        });

        return view;
    }


    private void checkVersion() {
        // REVISO SI HAY UNA NUEVA VERSIÓN
        VersionChecker versionChecker = new VersionChecker();
        try {
            String latestVersion = versionChecker.execute(getActivity().getPackageName()).get();
            double playVersion = Double.valueOf(latestVersion);
            double thisVersion = Double.valueOf(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);

            if (playVersion > thisVersion) {
                MainActivity.updateAvailable = true;
                FragmentMan.changeFragment(getActivity(), UpdateFragment.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkUserType() {
        reference
                .child(References.ADMINISTRATORS)
                .child(fbUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            prefs.edit().putBoolean(References.SHARED_PREFERENCES_CAN_SHARE, true).apply();
                        } else {
                            prefs.edit().putBoolean(References.SHARED_PREFERENCES_CAN_SHARE, false).apply();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("ERRORR", "MainFragment - OnCreate - Line 468\n    " + databaseError.getMessage());
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkVersion();

        //##### CALIFICAR APLICACIÓN
        boolean ask = prefs.getBoolean(References.SHARED_PREFERENCES_DONT_ASK_RATE, false);

        if (!ask)
            reference
                    .child(References.RATE_APP)
                    .child(fbUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean ask = false;
                            if (dataSnapshot.getValue() != null)
                                ask = dataSnapshot.getValue(boolean.class);

                            if (!ask)
                                rateApp();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        //############ LA PRIMERA VEZ QUE ENTRA A LA APLICACIÓN
        if (prefs.getBoolean(References.SHARED_PREFERENCES_FIRST_TIME, true)) {
            FragmentMan.changeFragment(getActivity(), WelcomeFragment.class);
        }
    }

    private void rateApp() {
        reference
                .child(References.SENT)
                .child(fbUser.getUid())
                .orderByChild(References.SENT_CHILD_CHECKED)
                .equalTo(true)
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null)
                            FragmentMan.changeFragment(getActivity(), RateFragment.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}

