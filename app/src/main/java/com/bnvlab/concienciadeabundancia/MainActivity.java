package com.bnvlab.concienciadeabundancia;

import android.app.ActivityManager;
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
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
import com.bnvlab.concienciadeabundancia.clases.User;
import com.bnvlab.concienciadeabundancia.fragments.LoginFragment;
import com.bnvlab.concienciadeabundancia.fragments.MainFragment;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Calendar;

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
    LoginFragment loginFragment = new LoginFragment();
    MainFragment mainFragment = new MainFragment();
    boolean firstTime = true;
    public static boolean databaseCalled;
    public static final String APP_SHARED_PREF_KEY = MainActivity.class.getSimpleName(), FIRST_TIME_PREF_KEY = APP_SHARED_PREF_KEY + ".firsTime", VERIFIED = APP_SHARED_PREF_KEY + ".verified";
    private String android_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences prefs = this.getSharedPreferences(
                this.APP_SHARED_PREF_KEY, Context.MODE_PRIVATE);

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

            if (getIntent().getBooleanExtra(References.SHARE_FROM_NOTIFICATION, false)) {
                setShareStartTime();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final Context context = this;
                builder.setTitle("FELICIDADES!")
                        .setMessage("Ya completamos todos los cambios, ahora puedes disfrutar de este magnífico Presente." +
                                " Este es el momento para comenzar a Dar, puedes compartir este presente a quien desees," +
                                " solo por 12hs.\nEmpieza ahora!!!")
                        .setPositiveButton("COMPARTIR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.shareDialog(context);
                            }
                        });
                builder.create().show();
            }
        }

//        if (prefs.getBoolean(References.SHARE_FROM_NOTIFICATION, false)) {
//            setShareStartTime();
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            final Context context = this;
//            builder.setTitle("FELICIDADES!")
//                    .setMessage("Ya completamos todos los cambios, ahora puedes disfrutar de este magnífico Presente." +
//                            " Este es el momento para comenzar a Dar, puedes compartir este presente a quien desees," +
//                            " solo por 12hs.\nEmpieza ahora!!!")
//                    .setPositiveButton("COMPARTIR", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Utils.shareDialog(context);
//                            prefs.edit().putBoolean(References.SHARE_FROM_NOTIFICATION, false);
//                        }
//                    });
//            builder.create().show();
//        }

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

            builder.setNeutralButton("VIDEO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent myIntent = new Intent(MainActivity.this, VideoActivity.class);
                    myIntent.putExtra("video", "https://youtu.be/e79CUFrvnK0"); //Optional parameters
                    MainActivity.this.startActivity(myIntent);
                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    prefs.edit().putBoolean(MainActivity.FIRST_TIME_PREF_KEY, false).apply();
                }
            })
                    .setMessage("Nuestra empresa esta dedicada a lograr la Felicidad en toda la humanidad.\n" +
                            "¿Cómo? Expandiendo la Conciencia, el Amor a todo, la Salud y el Disfrute de la Vida.")
                    .setTitle("ELEGIMOS CREAR UN MUNDO FELIZ Y UNIDO");

            AlertDialog dialog = builder.create();

            dialog.show();
        }
//
//        SecureRandom secureRandom = new SecureRandom();
//        Toast.makeText(this,         new BigInteger(40, secureRandom).toString(32)
//                , Toast.LENGTH_LONG).show();

        FragmentMan.changeFragment(this, MainFragment.class);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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

//            Toast.makeText(this, "this: " + thisVersion + "\nPlay: " +playVersion, Toast.LENGTH_SHORT).show();

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

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (getSystemService(QuizNotificationService.class) == null)
//                    startService(new Intent(this, QuizNotificationService.class));
//            }else{
//            if (!isMyServiceRunning(QuizNotificationService.class)) {
//                startService(new Intent(this, QuizNotificationService.class));
//            // use this to start and trigger a service
//            Intent i= new Intent(this, FirebaseBackgroundService.class);
//// potentially add data to the intent
//            i.putExtra("firstTime", true);
//            startService(i);
//                QuizNotificationService quizNotificationService = new QuizNotificationService();
//                Notification note = new Notification( 0, null, System.currentTimeMillis() );
//                note.flags |= Notification.FLAG_NO_CLEAR;
//                quizNotificationService.startForeground(33, note);
//            }

            //        startService(new Intent(FirebaseBackgroundService.class.getName()));
            startService(new Intent(this, FirebaseBackgroundService.class)); // para android 5
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null)
            if (intent.getExtras().getBoolean(References.SHARE_FROM_NOTIFICATION, false)) {
                setShareStartTime();
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("FELICIDADES!")
                        .setMessage("Ya completamos todos los cambios, ahora puedes disfrutar de este magnífico Presente." +
                                " Este es el momento para comenzar a Dar, puedes compartir este presente a quien desees," +
                                " solo por 12hs.\nEmpieza ahora!!!")
                        .setPositiveButton("COMPARTIR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.shareDialog(getBaseContext());
                            }
                        });
                builder.create().show();
            }
    }

    private void setShareStartTime() {
        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.SHARE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(Calendar.getInstance().getTime().getTime());
    }
}


