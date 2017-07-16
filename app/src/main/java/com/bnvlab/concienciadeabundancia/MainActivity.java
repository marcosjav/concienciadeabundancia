package com.bnvlab.concienciadeabundancia;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.User;
import com.bnvlab.concienciadeabundancia.fragments.MainFragment;
import com.bnvlab.concienciadeabundancia.fragments.TrainingFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Marcos on 25/06/2017.
 */

public class MainActivity extends FragmentActivity {
    public static boolean newUser = false;
    public static User user = null;
    public static final String APP_SHARED_PREF_KEY = "ConcienciaDeAbundancia", FIRST_TIME_PREF_KEY = APP_SHARED_PREF_KEY + ".firsTime", VERIFIED = APP_SHARED_PREF_KEY + ".verified";
    private String android_id;
    SharedPreferences prefs;
    boolean showTrainings = false;
    private FirebaseAuth mAuth;
    final static String TAG = "ERRORR - MainActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            prefs = getSharedPreferences(MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    MODE_PRIVATE);
            begin();
        } else {
            showLogin();
        }
    }


    private void begin() {
        checks();
        String value = null;

        if (value != null) {
            android_id = value;
            prefs.edit().putString("android_id", android_id).apply();
        } else if (prefs.getString("android_id", "").equals("")) {
            prefs.edit().putString("android_id", android_id).apply();
        } else {
            android_id = prefs.getString("android_id", "");
        }

        if (prefs.getBoolean("firstLogin", true)) {
            prefs.edit().putBoolean("firstLogin", false).apply();
        }

        FragmentMan.changeFragment(this, MainFragment.class);
        if (showTrainings)
            FragmentMan.changeFragment(this, TrainingFragment.class);

    }


    private void showLogin() {
        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.startActivity(myIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
            System.exit(0);
        }
    }

    public class VersionChecker extends AsyncTask<String, String, String> {

        String newVersion;

        @Override
        protected String doInBackground(String... params) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return newVersion;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


//    private void dialogFirstTime(){
//        // use a default value using new Date()
//        boolean firstTime = prefs.getBoolean(MainActivity.FIRST_TIME_PREF_KEY, true);
//
//        // PROPAGANDA DEL PRIMER INGRESO DEL USUSARIO
//        if (firstTime) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//            View view1 = View.inflate(this, R.layout.dialog_video, null);
//
//            final YouTubePlayerSupportFragment frag =
//                    (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_frame);
//
//            frag.initialize(Config.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
//                @Override
//                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
//                    if (!wasRestored) {
//                        //I assume the below String value is your video id
//                        youTubePlayer.loadVideo("GJZ45KiWLV4");
//                    }
//                }
//
//                @Override
//                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//                    if (youTubeInitializationResult.isUserRecoverableError()) {
//                        youTubeInitializationResult.getErrorDialog(MainActivity.this, 1).show();
//                    } else {
//                        String errorMessage = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
//                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//
//            builder.setView(view1)
//                    .setCancelable(false)
//                    .setPositiveButton("ENTENDIDO", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            prefs.edit().putBoolean(MainActivity.FIRST_TIME_PREF_KEY, false).apply();
//                            frag.onDestroy();
//                            dialog.dismiss();
//                        }
//                    });
//
//            AlertDialog dialog = builder.create();
//
//            dialog.show();
//        }
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private void checks() {
        // REVISO SI HAY UNA NUEVA VERSIÓN
        VersionChecker versionChecker = new VersionChecker();
        try {
            String latestVersion = versionChecker.execute().get();
            double playVersion = Double.valueOf(latestVersion);
            double thisVersion = Double.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

            if (playVersion > thisVersion) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setPositiveButton("IR A PLAYSTORE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        } finally {
                            finish();
                        }
                    }
                })
                        .setMessage("Hay una nueva versión de esta aplicación disponible.\nPor favor descargala antes de seguir.")
                        .setTitle("Nueva actualización")
                        .setCancelable(false)
                        .setIcon(R.drawable.attention_yellow);

                AlertDialog dialog = builder.create();

                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
//                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
//                                if (user.getEmail().equals(fbUser.getEmail()) || user.getPhone().equals(fbUser.getEmail().split("@")[0]))
                        MainActivity.user = user;
//                            }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(References.USERS_CHILD_DEVICEID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            SharedPreferences prefs = MainActivity.this.getSharedPreferences(MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    MODE_PRIVATE);
                            if (!dataSnapshot.getValue(String.class).equals(android_id) && !dataSnapshot.getValue(String.class).equals(prefs.getString("android_id", ""))) {
                                Toast.makeText(getApplicationContext(), "Se inició sesión en otro dispositivo", Toast.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                                try {
                                    FirebaseInstanceId.getInstance().deleteInstanceId();
                                } catch (IOException e) {
                                    Log.d("ERROR_ADMIN", "MAIN_ACTIVITY_deleteInstanse: " + e.getMessage());
                                }
//                                    Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
//                                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                    MainActivity.this.startActivity(myIntent);
//                                    MainActivity.this.finish();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void setShareStartTime() {
        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.SHARE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(Calendar.getInstance().getTime().getTime());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
