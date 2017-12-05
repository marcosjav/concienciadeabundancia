package com.bnvlab.concienciadeabundancia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.AppText;
import com.bnvlab.concienciadeabundancia.clases.AppValues;
import com.bnvlab.concienciadeabundancia.clases.SentUser;
import com.bnvlab.concienciadeabundancia.clases.User;
import com.bnvlab.concienciadeabundancia.clases.VideosURL;
import com.bnvlab.concienciadeabundancia.fragments.MainFragment;
import com.bnvlab.concienciadeabundancia.fragments.TrainingFragment;
import com.bnvlab.concienciadeabundancia.fragments.WelcomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Marcos on 25/06/2017.
 */

public class MainActivity extends FragmentActivity {
    //##    PARTE COMPARTIDA
    public static boolean newUser = false;
    public static User user = null;
    public static ArrayList<SentUser> sentUser = null;
    public static VideosURL videosURL = null;
    public static AppValues appValues = null;
    public static AppText appText = null;
    public static final String APP_SHARED_PREF_KEY = "ConcienciaDeAbundancia";//, FIRST_TIME_PREF_KEY = APP_SHARED_PREF_KEY + ".firsTime", VERIFIED = APP_SHARED_PREF_KEY + ".verified";
    public static boolean updateAvailable;
    FirebaseUser fbUser;

    private String android_id;
    SharedPreferences prefs;
    boolean showTrainings = false;
    private FirebaseAuth mAuth;
    final static String TAG = "ERRORR - MainActivity";
    DatabaseReference reference;
    private Gson gson;

    FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
                //Toast.makeText(MainActivity.this, "Se cerró la sesión", Toast.LENGTH_SHORT).show();
                showLogin();
            }
            // ...
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        gson = new Gson();
        reference = FirebaseDatabase.getInstance().getReference(References.REFERENCE);
        sentUser = new ArrayList<>();

        try {
            Uri data = getIntent().getData();
            if (data != null) {
                String invitationCode = data.toString();

                if (invitationCode != null && invitationCode.contains("http://cdainter.com/?code=")) {
                    Intent intent = new Intent();
                    intent.putExtra("uri", "http://cdainter.com/?code=");
                    startActivity(intent);
                    finish();
                }
            }
        } catch (Exception e) {
            Log.d(References.ERROR_LOG, e.getMessage());
        }
        getSupportFragmentManager().addOnBackStackChangedListener(getListener());
    }



    private void getUserJSON() {
        Log.d(References.ERROR_LOG, "getUserJSON()");
        reference.child(References.USERS)
                .child(fbUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        user.setuId(dataSnapshot.getKey());
                        Log.d(References.ERROR_LOG, gson.toJson(user));
                        prefs.edit().putString(References.SHARED_PREFERENCES_USER_JSON, gson.toJson(user)).apply();
                        getUserSent();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getUserSent() {
        Log.d(References.ERROR_LOG, "getUserSent()");
        reference.child(References.SENT)
                .child(fbUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            sentUser.add(new SentUser(data.getKey(), data.child(References.SENT_CHILD_CHECKED).getValue(boolean.class)));
                        }
                        prefs.edit().putString(References.SHARED_PREFERENCES_USER_SENT_JSON, gson.toJson(sentUser)).apply();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void begin() {
        Log.d(References.ERROR_LOG, "begin()");
        checks();
        //########### ANULO PORQUE LO HAGO EN CHECKS
//        String value = null;
//
//        if (value != null) {
//            android_id = value;
//            prefs.edit().putString(References.DEVICE_ID, android_id).apply();
//        } else if (prefs.getString(References.DEVICE_ID, "").equals("")) {
//            prefs.edit().putString(References.DEVICE_ID, android_id).apply();
//        } else {
//            android_id = prefs.getString(References.DEVICE_ID, "");
//        }

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
        List<Fragment> fl = fm.getFragments();
        String fs = "";
        if (fl != null)
            for (Fragment f : fl){
                if (f != null)
                    fs += "-" + f.getTag() + "\n";
            }
        Log.i(References.ERROR_LOG, "fragments:\n" + fs + "BackStackEntryCount: " + fm.getBackStackEntryCount());

        if (!updateAvailable && !WelcomeFragment.active) {
            if (fm.getBackStackEntryCount() > 0) {
//            if (fl.size() > 1) {
                Log.i(References.ERROR_LOG, "MainActivity - popping backstack");
                Log.i(References.ERROR_LOG, "MainActivity - updateAvailable: " + updateAvailable);
                Log.i(References.ERROR_LOG, "MainActivity - Welcome active: " + WelcomeFragment.active);
                fm.popBackStack();
            } else {
                Log.i(References.ERROR_LOG, "nothing on backstack, calling super");
                super.onBackPressed();
                System.exit(0);
            }
        } else if (WelcomeFragment.button) {
            Log.i(References.ERROR_LOG, "MainActivity - WelcomFragmentButton");
            fm.popBackStack();
            if (MainActivity.sentUser.size() < 1)
                FragmentMan.changeFragment(this, TrainingFragment.class);
            else
                prefs.edit().putBoolean(References.SHARED_PREFERENCES_FIRST_TIME, false).apply();

            WelcomeFragment.active = false;
            WelcomeFragment.button = false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser currentUser = mAuth.getCurrentUser();  //######    CONTROLO SI ESTÁ LOGUEADO Y GUARDO EL JSON DEL USUARIO Y DE LAS GUÍAS
        Log.d(References.ERROR_LOG, "onCreate()");
        if (currentUser != null) {
            fbUser = mAuth.getCurrentUser();
            prefs = getSharedPreferences(MainActivity.APP_SHARED_PREF_KEY + fbUser.getUid(),
                    MODE_PRIVATE);

            Log.d(References.ERROR_LOG, "currentUser != null");
            if (videosURL == null)
                getVideos(true);
            else {
                getVideos(false);
//                begin();
            }
        } else {
            showLogin();
            Log.d(References.ERROR_LOG, "showLogin()");
        }

        FragmentManager fm = getSupportFragmentManager();

        List<Fragment> list = fm.getFragments();
        Log.d(References.ERROR_LOG, list == null? "list: null" : list.size()+"");
        if (list==null || list.size() == 0)
            begin();
        /*if (list != null)
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getClass().equals(RateFragment.class))
                    onBackPressed();
            }*/
    }

    private void getVideos(final boolean first) {
        FirebaseDatabase.getInstance().getReference(References.APP_REFERENCE)
                //.child(References.APP_VIDEOS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            SharedPreferences appPrefs = MainActivity.this.getSharedPreferences(MainActivity.APP_SHARED_PREF_KEY, MODE_PRIVATE);
                            if (data.getKey().equals(References.APP_VIDEOS)) {
                                videosURL = data.getValue(VideosURL.class);
                                appPrefs.edit().putString(References.SHARED_PREFERENCES_APP_VIDEOS_URL, gson.toJson(videosURL)).apply();
                            } else if (data.getKey().equals(References.APP_VALUES)){
                                appValues = data.getValue(AppValues.class);
                                appPrefs.edit().putString(References.SHARED_PREFERENCES_APP_VALUES, gson.toJson(appValues)).apply();
                            } else if (data.getKey().equals(References.APP_TEXTS)){
                                appText = data.getValue(AppText.class);
                                appPrefs.edit().putString(References.SHARED_PREFERENCES_APP_TEXTS, gson.toJson(appText)).apply();
                            }
                        }
                        if (first)
                            begin();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private void checks() {

        Log.d(References.ERROR_LOG, "checks()");
        getUserJSON();

        reference
                .child(References.USERS)
                .child(fbUser.getUid())
                .child(References.USERS_CHILD_DEVICEID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if (fbUser != null) {
                                SharedPreferences prefs = MainActivity.this.getSharedPreferences(MainActivity.APP_SHARED_PREF_KEY + fbUser.getUid(),
                                        MODE_PRIVATE);
                                if (!dataSnapshot.getValue(String.class).equals(android_id) &&
                                        !dataSnapshot.getValue(String.class).equals(prefs.getString(References.DEVICE_ID, ""))) {
                                    Toast.makeText(getApplicationContext(), "Se inició sesión en otro dispositivo", Toast.LENGTH_LONG).show();
                                    mAuth.signOut();
                                    try {
                                        FirebaseInstanceId.getInstance().deleteInstanceId();
                                    } catch (IOException e) {
                                        Log.d("ERROR_ADMIN", "MAIN_ACTIVITY_deleteInstanse: " + e.getMessage());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void setShareStartTime() {
        reference
                .child(References.SHARE)
                .child(fbUser.getUid())
                .setValue(Calendar.getInstance().getTime().getTime());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);

    }

    private FragmentManager.OnBackStackChangedListener getListener()
    {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener()
        {
            public void onBackStackChanged()
            {
                FragmentManager manager = getSupportFragmentManager();

                if (manager != null)
                {
                    MainFragment currFrag = (MainFragment) manager.findFragmentByTag(MainFragment.class.getSimpleName());

                    currFrag.onFragmentResume();
                }
            }
        };

        return result;
    }
}
