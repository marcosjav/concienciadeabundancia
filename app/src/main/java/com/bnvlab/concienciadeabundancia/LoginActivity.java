package com.bnvlab.concienciadeabundancia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.auxiliaries.ICallback;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;

import static com.bnvlab.concienciadeabundancia.fragments.SettingsFragment.isValidEmail;

/**
 * Created by Marcos on 31/08/2017.
 */

public class LoginActivity extends FragmentActivity {
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    public static final String APP_SHARED_PREF_KEY = MainActivity.class.getSimpleName(), FIRST_TIME_PREF_KEY = APP_SHARED_PREF_KEY + ".firsTime", VERIFIED = APP_SHARED_PREF_KEY + ".verified";
    SharedPreferences prefs;
    private String android_id;

    /**
     * Array of recent used phone numbers and adapter for autocomplete
     */
    ArrayList<String> used_numbers;
    ArrayAdapter<String> adapter;

    FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null){

                FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                        .child(References.USERS)
                        .child(user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User u = new User();
                                Gson gson = new Gson();
                                u.setuId(user.getUid());

                                if (dataSnapshot.hasChild(References.USERS_CHILD_ACTIVE))
                                    u.setActive(dataSnapshot.child(References.USERS_CHILD_ACTIVE).getValue(boolean.class));

                                if (dataSnapshot.hasChild(References.USERS_CHILD_NAME))
                                    u.setName(dataSnapshot.child(References.USERS_CHILD_NAME).getValue(String.class));

                                if (dataSnapshot.hasChild(References.USERS_CHILD_SECOND_NAME))
                                    u.setSecondName(dataSnapshot.child(References.USERS_CHILD_SECOND_NAME).getValue(String.class));

                                if (dataSnapshot.hasChild(References.USERS_CHILD_LASTNAME))
                                    u.setName(dataSnapshot.child(References.USERS_CHILD_LASTNAME).getValue(String.class));

                                if (dataSnapshot.hasChild(References.USERS_CHILD_LOCALE))
                                    u.setLocale(dataSnapshot.child(References.USERS_CHILD_LOCALE).getValue(String.class));

                                if (dataSnapshot.hasChild(References.USERS_CHILD_PHONE))
                                    u.setPhone(dataSnapshot.child(References.USERS_CHILD_PHONE).getValue(String.class));

                                if (dataSnapshot.hasChild(References.USERS_CHILD_EMAIL))
                                    u.setEmail(dataSnapshot.child(References.USERS_CHILD_EMAIL).getValue(String.class));

                                if (dataSnapshot.hasChild(References.USERS_CHILD_INVITED_FOR))
                                    u.setInvitationUID(dataSnapshot.child(References.USERS_CHILD_INVITED_FOR).getValue(String.class));

                                if (dataSnapshot.hasChild(References.USERS_CHILD_DEVICEID))
                                    u.setDeviceId(dataSnapshot.child(References.USERS_CHILD_DEVICEID).getValue(String.class));

                                if (dataSnapshot.hasChild(References.USERS_CHILD_ACTIVE))
                                    u.setActive(dataSnapshot.child(References.USERS_CHILD_ACTIVE).getValue(boolean.class));

                                if (dataSnapshot.hasChild(References.USERS_CHILD_COUNTER))
                                    u.setCounter(dataSnapshot.child(References.USERS_CHILD_COUNTER).getValue(int.class));

                                if (dataSnapshot.hasChild(References.USERS_CHILD_LAST_SENT))
                                    u.setLastSent(dataSnapshot.child(References.USERS_CHILD_LAST_SENT).getValue(long.class));

                                String us = gson.toJson(u);

                                SharedPreferences prefs = getSharedPreferences(
                                        MainActivity.APP_SHARED_PREF_KEY, Context.MODE_PRIVATE);

                                prefs.edit().putString("user", us).apply();

                                MainActivity.user = u;

                                closeLogin(new ICallback() {
                                    @Override
                                    public void callback() {
                                        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(myIntent);
                                        finish();
                                    }
                                }, user.getUid());


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("ERRORR", "LoginActivity - 881\n" + databaseError.getMessage());
                            }
                        });
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_fragment_login);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            try {
                showProgress(true);
            }catch (Exception e){
                Log.e("ERRORR", e.getMessage());
                e.printStackTrace();
            }
        }

        prefs = this.getSharedPreferences(
                this.APP_SHARED_PREF_KEY, Context.MODE_PRIVATE);

        // GENERO UN CÓDIGO ALEATORIO PARA LA SESIÓN
        SecureRandom secureRandom = new SecureRandom();
        String sessionCode = new BigInteger(40, secureRandom).toString(32);

        if (prefs.getString("android_id", "").equals("")) {
            prefs.edit().putString("android_id", sessionCode).apply();
        }

        android_id = prefs.getString("android_id", "");

        used_numbers = new ArrayList<>(prefs.getStringSet("used_numbers", new HashSet<String>()));

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, used_numbers);
        mEmailView.setAdapter(adapter);
        //populateAutoComplete();
        mEmailView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPasswordView.setFocusableInTouchMode(true);
                mPasswordView.requestFocus();
            }
        });


        mPasswordView = (EditText) findViewById(R.id.password);

//        mEmailView.setText("3794141560");
//        mPasswordView.setText("asdasd");


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                attemptLogin();
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

//        loadLocations();

        TextView buttonPasswordRecovery = (TextView) findViewById(R.id.button_password_recovery);
        buttonPasswordRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                builder
                        .setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                // User clicked OK button
                                final String email = mEmailView.getText().toString();
                                if (isValidEmail(email))
                                    sendRecoveryPass(email, dialog);
                                else if (isPhoneValid(email)) {
                                    FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                                            .child(References.USERS)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    boolean isFinished = false;
                                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                        User user = data.getValue(User.class);
                                                        if (user.getPhone().equals(email)) {
                                                            sendRecoveryPass(user.getEmail(), dialog);
                                                            isFinished = true;
                                                        }
                                                    }
                                                    if (!isFinished) {
                                                        Toast.makeText(LoginActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                                                        showProgress(false);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                } else
//                                    Toast.makeText(LoginActivity.this, "No es un mail válido", Toast.LENGTH_SHORT).show();
                                    mEmailView.setError("No es un email o teléfono válido");
                            }
                        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                        .setMessage("Te podemos mandar un correo con un link donde podés restablecer tu clave.")
                        .setTitle("RECUPERAR CLAVE");

                AlertDialog dialog = builder.create();

                dialog.show();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString().toLowerCase();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        final boolean isSignedWhitEmail = !isPhoneValid(email);
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email) && isSignedWhitEmail) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            /*mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);*/

//            signInFireBase(email,password);
            FirebaseDatabase.getInstance()
                    .getReference(References.REFERENCE)
                    .child(References.USERS)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean isFinished = false;
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                User user = data.getValue(User.class);
                                if (user.getEmail() == null)
                                    Log.d("CDA_ERROR", data.getKey() + " ");
                                else
                                if (user.getEmail().equals(email) || user.getPhone().equals(email)) {
                                    if (user.isSignInWithEmail()) {
                                        signInFireBase(user.getEmail(), password);
                                        isFinished = true;
                                        break;
                                    } else {
                                        signInFireBase(user.getPhone() + "@cda.com", password);
                                        isFinished = true;
                                        break;
                                    }
                                }
                            }
                            if (!isFinished) {
                                Toast.makeText(LoginActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                                showProgress(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        }
    }

    private void signInFireBase(final String email, final String password) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance()
                                    .signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
//                                                close_login();
                                            } else {
                                                showProgress(false);
                                                Toast.makeText(LoginActivity.this, ((FirebaseAuthException) task.getException()).getErrorCode(), Toast.LENGTH_LONG).show();
                                                mPasswordView.requestFocus();
                                            }
                                        }
                                    });
                        } else {
                            showProgress(false);
                            try {
                                Toast.makeText(LoginActivity.this, (task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }catch (Exception e){

                            }
                            mPasswordView.requestFocus();
                        }
                    }
                });
    }
    private void closeLogin(final ICallback callback, final String uId) {
        String email = ((AutoCompleteTextView) findViewById(R.id.email)).getText().toString();
//        String email_sign_up = ((EditText) findViewById(R.id.sign_up_mail)).getText().toString();
        String password = mPasswordView.getText().toString();

//        if (email.isEmpty() && !email_sign_up.isEmpty())
//            email = email_sign_up;

        if (!used_numbers.contains(email))
            used_numbers.add(email);

        prefs.edit().putStringSet("used_numbers", new HashSet<String>(used_numbers)).apply();

        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.USERS)
                .child(uId)
                .child(References.USERS_CHILD_DEVICEID)
                .setValue(android_id)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        SharedPreferences tPrefs = LoginActivity.this.getSharedPreferences(
                                MainActivity.APP_SHARED_PREF_KEY + uId, Context.MODE_PRIVATE);
                        tPrefs.edit().putString("android_id", android_id).apply();
                        callback.callback();

                    }
                });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("used_numbers", used_numbers);
        super.onSaveInstanceState(outState);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    private boolean isPhoneValid(String phone) {
        boolean isLongNumber;

        try {
            Long.parseLong(phone);
            isLongNumber = true;
        } catch (Exception e) {
            isLongNumber = false;
        }
        ;

        if (phone.length() == 10 && isLongNumber)
            return true;

        return false;
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
//       return email.contains("@") && email.contains(".");
//        return true;
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }
    private void showProgress(boolean isShowed){
        findViewById(R.id.progressBar).setVisibility(isShowed?View.VISIBLE : View.GONE);
        findViewById(R.id.login_form).setVisibility(isShowed?View.GONE : View.VISIBLE);
    }

    private void sendRecoveryPass(String email, final DialogInterface dialog) {
        FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Se envió el correo!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else
                            Toast.makeText(LoginActivity.this, "Hubo un inconveniente\n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
