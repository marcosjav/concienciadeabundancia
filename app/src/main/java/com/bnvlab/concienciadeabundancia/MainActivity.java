package com.bnvlab.concienciadeabundancia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.bnvlab.concienciadeabundancia.clases.User;
import com.bnvlab.concienciadeabundancia.fragments.LoginFragment;
import com.bnvlab.concienciadeabundancia.fragments.MainFragment;
import com.google.firebase.auth.FirebaseAuth;

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

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            showLogin();
        }

        final SharedPreferences prefs = this.getSharedPreferences(
                this.APP_SHARED_PREF_KEY, Context.MODE_PRIVATE);

        // use a default value using new Date()
        firstTime = prefs.getBoolean(this.FIRST_TIME_PREF_KEY, true);


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

        FragmentMan.changeFragment(this, MainFragment.class);

        //LOGIN SECTION
        /*mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser userFB = firebaseAuth.getCurrentUser();
                if (userFB != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + userFB.getUid());

                    if (newUser) {
                        user.setuId(userFB.getUid());

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest
                                .Builder()
                                .setDisplayName(user.getLastName() + ", " + user.getName())
                                .build();

                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);

                        //SAVE THE USER IN THE FIREBASE DATABASE
                        FirebaseDatabase
                                .getInstance()
                                .getReference(MainActivity.REFERENCE)
                                .child(User.CHILD)
                                .child(user.getPhone())
                                .setValue(user);
                    }

                    FragmentMan.eraseAll(MainActivity.this);

                    FragmentMan.changeFragment(MainActivity.this, MainFragment.class, true);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    FragmentMan.eraseAll(MainActivity.this);

                    FragmentMan.changeFragment(MainActivity.this, LoginFragment.class, true);
                    *//*Intent mStartActivity = new Intent(MainActivity.this, MainActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    System.exit(0);*//*
                }
            }
        };*/

        /*getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        if (getSupportFragmentManager().getBackStackEntryCount() < 1)
                            System.exit(0);


//                        setFragmentVisible();
                    }
                });
        getSupportFragmentManager().removeOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() < 1)
                    System.exit(0);
//                setFragmentVisible();
            }
        });*/
    }

    private void showLogin() {
        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.startActivity(myIntent);
        finish();
    }

/*    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }*/

/*    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

//        fragmentManager = getSupportFragmentManager();
    }


    /*    @Override
        public void onBackPressed() {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                Log.i("MainActivity", "popping backstack");
                fm.popBackStack();
            } else {
                Log.i("MainActivity", "nothing on backstack, calling super");
                super.onBackPressed();
            }
        }*/
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
}


