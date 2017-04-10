package com.bnvlab.concienciadeabundancia;

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

import com.bnvlab.concienciadeabundancia.clases.QuizNotificationService;
import com.bnvlab.concienciadeabundancia.clases.User;
import com.bnvlab.concienciadeabundancia.fragments.LoginFragment;
import com.bnvlab.concienciadeabundancia.fragments.MainFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;

import java.io.IOException;

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
    public static String REFERENCE = "CDA";
    public static boolean newUser = false;
    public static User user = null;
    FragmentManager fragmentManager;
    LoginFragment loginFragment = new LoginFragment();
    MainFragment mainFragment = new MainFragment();
    boolean firstTime = true;
    public static final String APP_SHARED_PREF_KEY = MainActivity.class.getSimpleName(), FIRST_TIME_PREF_KEY = APP_SHARED_PREF_KEY + ".firsTime", VERIFIED = APP_SHARED_PREF_KEY + ".verified";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // REVISO SI HAY UNA NUEVA VERSIÓN
        VersionChecker versionChecker = new VersionChecker();
        try {
            String latestVersion = versionChecker.execute().get();
            double playVersion = Double.valueOf(latestVersion);
            double thisVersion = Double.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

//            Toast.makeText(this, "this: " + thisVersion + "\nPlay: " +playVersion, Toast.LENGTH_SHORT).show();

            if (playVersion > thisVersion)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                    builder.setPositiveButton("IR A PLAYSTORE", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User clicked OK button
                                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                            } catch (android.content.ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
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

//        FirebaseDatabase.getInstance()
//                .getReference(MainActivity.REFERENCE)
//                .child("last_version")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.getValue() != null)
//                            try {
//                                long lastVersionCode = (long) dataSnapshot.getValue();
//                                long thisVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
//
//                                if (lastVersionCode > thisVersionCode) {
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//                                    builder.setPositiveButton("IR A PLAYSTORE", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            // User clicked OK button
//                                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
//                                            try {
//                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                                            } catch (android.content.ActivityNotFoundException anfe) {
//                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                                            }
//                                        }
//                                    })
//                                            .setMessage("Hay una nueva versión de esta aplicación disponible.\nPor favor descargala antes de seguir.")
//                                            .setTitle("Nueva actualización")
//                                            .setCancelable(false)
//                                            .setIcon(R.drawable.attention_yellow);
//
//                                    AlertDialog dialog = builder.create();
//
//                                    dialog.show();
//                                }
//                            } catch (PackageManager.NameNotFoundException e) {
//                                e.printStackTrace();
//                            }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            showLogin();
            stopService(new Intent(this, QuizNotificationService.class));
        } else {
            FirebaseDatabase.getInstance()
                    .getReference(REFERENCE)
                    .child(User.CHILD)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                User user = data.getValue(User.class);

                                if (user.getEmail().equals(fbUser.getEmail()) || user.getPhone().equals(fbUser.getEmail().split("@")[0]))
                                    MainActivity.user = user;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            startService(new Intent(this, QuizNotificationService.class));
        }

        final SharedPreferences prefs = this.getSharedPreferences(
                this.APP_SHARED_PREF_KEY, Context.MODE_PRIVATE);

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
                    .setMessage("Creemos que el amor y la unidad van a transformar positiva-mente al mundo. \n" +
                            "Creemos que al solucionar la escasez, crearemos un mundo igualitario para todos sus habitantes. \n" +
                            "Es nuestro compromiso principal generar abundancia y libertad absoluta en 10 Millones de personas para el 2030.")
                    .setTitle("ELEGIMOS crear un mundo UNIDO");

            AlertDialog dialog = builder.create();

            dialog.show();
        }
//
//        SecureRandom secureRandom = new SecureRandom();
//        Toast.makeText(this,         new BigInteger(40, secureRandom).toString(32)
//                , Toast.LENGTH_LONG).show();

        FragmentMan.changeFragment(this, MainFragment.class);
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
}


