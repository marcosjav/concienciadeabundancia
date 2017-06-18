package com.bnvlab.concienciadeabundancia;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.auxiliaries.Config;
import com.bnvlab.concienciadeabundancia.auxiliaries.Notify;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
import com.bnvlab.concienciadeabundancia.clases.User;
import com.bnvlab.concienciadeabundancia.fragments.MainFragment;
import com.bnvlab.concienciadeabundancia.fragments.TrainingFragment;
import com.firebase.client.Firebase;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

/*
NORMAS:
    -DESCRIPCIÓN DE MÉTODOS, SUS PARÁMETROS Y DEMÁS
    -COMENTARIOS
    -NOMBRES DE FUNCIONES Y VARIABLES AUTODESCRIPTIVOS - PREFERENTEMENTE EN INGLÉS
    -CADA CLASE DEBE TENER SU COMENTARIO DESCRIBIENDO EL PROPÓSITO
    -EN MÉTODOS COMPLICADOS, ANOTAR EL PSEUDOCÓDICO EN UN COMENTARIO
    -CREAR CÓDIGO LO MÁS GENÉRICO POSIBLE, PARA PODER REUTILIZARLO
    -CADA COMMIT DEBE IR ACOMPAÑADO DE UNA DESCRIPCIÓN DE LO QUE SE HIZO Y DE LO QUE SE SE VA A SEGUIR EN EL PRÓXIMO
    -LOS TEXTOS, COLORES Y DEMÁS EN SU RESPECTIVO XML
*/
public class MainActivity extends FragmentActivity {
    //    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;
    static String TAG = "CDA_INFORMATION";
    //    public static String REFERENCE = "CDA";
    public static boolean newUser = false;
    public static User user = null;
    FragmentManager fragmentManager;
    MainFragment mainFragment = new MainFragment();
    boolean firstTime = true;
    public static boolean databaseCalled;
    public static final String APP_SHARED_PREF_KEY = "ConcienciaDeAbundancia", FIRST_TIME_PREF_KEY = APP_SHARED_PREF_KEY + ".firsTime", VERIFIED = APP_SHARED_PREF_KEY + ".verified";
    private String android_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean showTrainings = false;
        final SharedPreferences prefs;

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            showLogin();
        else {
            prefs = this.getSharedPreferences(
                    this.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(), Context.MODE_PRIVATE);

            if (!prefs.getBoolean("databaseCalled", false)) {
                try {
                    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                } catch (Exception e) {
                    Log.d("MAIN_ACTIVITY", e.getMessage());
                }
                prefs.edit().putBoolean("databaseCalled", true).apply();
            }

            checks();

            String value = null;
            if (getIntent() != null && getIntent().getExtras() != null) {
                String v = getIntent().getExtras().getString("android_id");
                if (v != null)
                    value = v;

//            if (getIntent().getBooleanExtra(References.SHARE_FROM_NOTIFICATION, false)) {
//                setShareStartTime();
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                final Context context = this;
//                builder.setTitle("FELICIDADES!")
//                        .setMessage("Ya completamos todos los cambios, ahora puedes disfrutar de este magnífico Presente." +
//                                " Este es el momento para comenzar a Dar, puedes compartir este presente a quien desees," +
//                                " solo por 12hs.\nEmpieza ahora!!!")
//                        .setPositiveButton("COMPARTIR", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Utils.shareDialog(context);
//                            }
//                        });
//                builder.create().show();
//            } if (getIntent().getBooleanExtra(References.TRAININGS_FROM_NOTIFICATION, false)) {
//                showTrainings = true;
//            }
                try {
                    HashSet<String> notifications = (HashSet<String>) prefs.getStringSet("notifications", new HashSet<String>());
                    ArrayList<JSONObject> list = new ArrayList<>();
                    String title = "";
                    String message = "";

                    for (String n : notifications) {
                        JSONObject object = new JSONObject(n);
                        list.add(object);
                        boolean read = object.getBoolean("read");
                        title = prefs.getString("title", "");
                        message = prefs.getString("message", "");
                        switch (getIntent().getIntExtra("launchedBy", 0)) {
                            case Notify.ACTION_SHARE:
                                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                                if (!title.equals("") || !message.equals("")) {
                                    builder.setTitle(title)
                                            .setMessage(message)
                                            .setPositiveButton("OK", null)
                                            .setCancelable(true)
                                            .create()
                                            .show();
                                }
                                break;
                            case Notify.ACTION_TRAININGS:
                                showTrainings = true;
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                                String title1 = prefs.getString("title", "");
                                String message1 = prefs.getString("message", "");
                                if (!title1.equals("") || !message1.equals("")) {
                                    builder1.setTitle(title1)
                                            .setMessage(message1)
                                            .setPositiveButton("OK", null)
                                            .setCancelable(true)
                                            .create()
                                            .show();
                                }
                                break;
                            case Notify.ACTION_TRAINING_RESULT:
                                setShareStartTime();
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                                final Context context = this;
                                builder2.setTitle("FELICIDADES!")
                                        .setMessage("Ya completamos todos los cambios, ahora puedes disfrutar de este magnífico Presente." +
                                                " Este es el momento para comenzar a Dar, puedes compartir este presente a quien desees," +
                                                " solo por 12hs.\nEmpieza ahora!!!")
                                        .setPositiveButton("COMPARTIR", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Utils.shareDialog(context);
                                            }
                                        });
                                builder2.create().show();
                                break;
                        }
                    }
                } catch (Exception e) {
                    Log.e("ERRORR", "MainActivity - OnCreate - Line 178\n    " + e.getMessage());
                    e.printStackTrace();
                }

                setIntent(null);
            }

            if (value != null) {
                android_id = value;
                prefs.edit().putString("android_id", android_id).apply();
            } else if (prefs.getString("android_id", "").equals("")) {
                prefs.edit().putString("android_id", android_id).apply();
            } else {
                android_id = prefs.getString("android_id", "");
            }


            Firebase.getDefaultConfig().setPersistenceEnabled(true);

            if (prefs.getBoolean("firstLogin", true)) {
                FirebaseAuth.getInstance().signOut();
                prefs.edit().putBoolean("firstLogin", false).apply();
            }

            ///////////////////////

            // use a default value using new Date()
            firstTime = prefs.getBoolean(this.FIRST_TIME_PREF_KEY, true);

            // PROPAGANDA DEL PRIMER INGRESO DEL USUSARIO
            if (firstTime) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                View view = View.inflate(this, R.layout.dialog_video, null);

                final YouTubePlayerSupportFragment frag =
                        (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
                frag.initialize(Config.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
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
                            youTubeInitializationResult.getErrorDialog(MainActivity.this, 1).show();
                        } else {
                            String errorMessage = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setView(view)
                        .setCancelable(false)
                        .setPositiveButton("ENTENDIDO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                prefs.edit().putBoolean(MainActivity.FIRST_TIME_PREF_KEY, false).apply();
                                frag.onDestroy();
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();

                dialog.show();
            }

            FragmentMan.changeFragment(this, MainFragment.class);
            if (showTrainings)
                FragmentMan.changeFragment(this, TrainingFragment.class);
        }
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
        checks();
    }

    private void checks() {
        // REVISO SI HAY UNA NUEVA VERSIÓN
        VersionChecker versionChecker = new VersionChecker();
        try {
            String latestVersion = versionChecker.execute().get();
            double playVersion = Double.valueOf(latestVersion);
            double thisVersion = Double.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

            if (playVersion > thisVersion) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            showLogin();
        } else {
            FirebaseDatabase.getInstance()
                    .getReference(References.REFERENCE)
                    .child(References.USERS)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
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
                            if (dataSnapshot.getValue() != null)
                                if (!dataSnapshot.getValue(String.class).equals(android_id)) {
                                    Toast.makeText(MainActivity.this, "Se inició sesión en otro dispositivo", Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                    try {
                                        FirebaseInstanceId.getInstance().deleteInstanceId();
                                    } catch (IOException e) {
                                        Log.d("ERROR_ADMIN", "MAIN_ACTIVITY_deleteInstanse: " + e.getMessage());
                                    }
                                    Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    MainActivity.this.startActivity(myIntent);
                                    MainActivity.this.finish();
                                }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void setShareStartTime() {
        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.SHARE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(Calendar.getInstance().getTime().getTime());
    }


}


