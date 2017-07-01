package com.bnvlab.concienciadeabundancia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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
import java.util.Collections;
import java.util.HashSet;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

import static com.bnvlab.concienciadeabundancia.fragments.SettingsFragment.isValidEmail;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends FragmentActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private String android_id;

    /**
     * Array of recent used phone numbers and adapter for autocomplete
     */
    ArrayList<String> used_numbers;
    ArrayAdapter<String> adapter;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private String invitationCode;
    private String invitationSenderUID;

    public static final String APP_SHARED_PREF_KEY = MainActivity.class.getSimpleName(), FIRST_TIME_PREF_KEY = APP_SHARED_PREF_KEY + ".firsTime", VERIFIED = APP_SHARED_PREF_KEY + ".verified";
    SharedPreferences prefs;
    private Button buttonPasswordRecovery;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            try {
                showProgress(true);
            }catch (Exception e){
                Log.e("ERRORR", e.getMessage());
                e.printStackTrace();
            }
        }

        setContentView(R.layout.activity_login);

        prefs = this.getSharedPreferences(
                this.APP_SHARED_PREF_KEY, Context.MODE_PRIVATE);

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


        mEmailView.setText("3794141560");
        mPasswordView.setText("asdasd");


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                attemptLogin();
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        final Button signUpButton = (Button) findViewById(R.id.email_sign_up_button);
        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewSwitcher viewSwitcher = (ViewSwitcher) findViewById(R.id.login_switcher);
                viewSwitcher.setInAnimation(LoginActivity.this, android.R.anim.fade_in);
                viewSwitcher.setOutAnimation(LoginActivity.this, android.R.anim.fade_out);
                viewSwitcher.showNext();
            }
        });

        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);

        Button buttonBack = (Button) findViewById(R.id.sign_up_back);
        buttonBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewSwitcher viewSwitcher = (ViewSwitcher) findViewById(R.id.login_switcher);
                viewSwitcher.showPrevious();
            }
        });

        Button buttonSignUpOK = (Button) findViewById(R.id.button_sign_up_ok);
        buttonSignUpOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (invitationCode != null) {
                    ViewSwitcher viewSwitcherOK = (ViewSwitcher) findViewById(R.id.switcher_sign_up_ok);
                    viewSwitcherOK.showPrevious();
                    signUp();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                    builder.setNegativeButton("Entiendo", null)
                            .setMessage("Necesitás ser invitado por alguien que ya nos conozca.")
                            .setTitle("Registro")
                            .setCancelable(true);

                    AlertDialog dialog = builder.create();

                    dialog.show();
                }
            }
        });

        loadLocations();

        buttonPasswordRecovery = (Button) findViewById(R.id.button_password_recovery);
        buttonPasswordRecovery.setOnClickListener(new OnClickListener() {
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

        final Uri data = getIntent().getData();
        if (data != null) {
            invitationCode = data.toString()
                    .replaceAll("http://concienciadeabundancia.com/code=", "");

            FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                    .child(References.INVITATION_CODES)
                    .child(invitationCode)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null){
                                ViewSwitcher viewSwitcher = (ViewSwitcher) findViewById(R.id.login_switcher);
                                viewSwitcher.setInAnimation(LoginActivity.this, android.R.anim.fade_in);
                                viewSwitcher.setOutAnimation(LoginActivity.this, android.R.anim.fade_out);
                                viewSwitcher.showNext();

                                FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                                        .child(References.INVITATION_CODES)
                                        .child(invitationCode)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                invitationSenderUID = dataSnapshot.getValue(String.class);
                                                ((TextView) findViewById(R.id.sign_up_invitation_code)).setText(invitationSenderUID);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }
                            else
                                Toast.makeText(LoginActivity.this, "Código de invitación erroneo o no encontrado\n Por favor solicítelo nuevamente", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });




        }
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

    ArrayList<String> locationList = new ArrayList<String>();

    private void loadLocations() {
        final Spinner spinnerLocation = (Spinner) findViewById(R.id.spinner_sign_up_location);
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.LOCATIONS)
                .orderByValue()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getChildrenCount();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            // TODO: handle the post
                            String location = (String) postSnapshot.getValue();
                            locationList.add(location);
                        }
                        Collections.sort(locationList);
                        //THE 'me.srodrigo:androidhintspinner:1.0.0' LIBRARY ALLOWS US TO PUT A HINT TEXT IN THE SPINNER  https://github.com/srodrigo/Android-Hint-Spinner
                        HintSpinner<String> hintSpinner = new HintSpinner<>(
                                spinnerLocation,
                                // Default layout - You don't need to pass in any layout id, just your hint text and
                                // your list data
                                new HintAdapter(LoginActivity.this, "LOCALIDAD", locationList),
                                new HintSpinner.Callback<String>() {
                                    @Override
                                    public void onItemSelected(int position, String itemAtPosition) {
                                        // Here you handle the on item selected event (this skips the hint selected event)
//                                        ((TextView) spinnerLocation.getSelectedView()).setTextColor(Color.BLACK);
                                    }
                                });
                        hintSpinner.init();

                        ((ViewSwitcher) findViewById(R.id.view_switcher_sign_up_location)).showNext();
//                        isReady = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Callback received when a permissions request has been completed.
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
//    }


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
//        if (isValidEmail(editTextEmail)) {
//            email = editTextEmail;
//            isSignedWhitEmail = true;
//        }
//        else {
//            email = editTextEmail;
//        }
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
                                Toast.makeText(LoginActivity.this, ((FirebaseAuthException) task.getException()).getErrorCode(), Toast.LENGTH_LONG).show();
                            mPasswordView.requestFocus();
                        }
                    }
                });
    }

    private void closeLogin(final ICallback callback, final String uId) {
        String email = ((AutoCompleteTextView) findViewById(R.id.email)).getText().toString();
        String email_sign_up = ((EditText) findViewById(R.id.sign_up_mail)).getText().toString();
        String password = mPasswordView.getText().toString();

        if (email.isEmpty() && !email_sign_up.isEmpty())
            email = email_sign_up;

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


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        return new CursorLoader(this,
//                // Retrieve data rows for the device user's 'profile' contact.
//                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
//                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
//
//                // Select only email addresses.
//                ContactsContract.Contacts.Data.MIMETYPE +
//                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
//                .CONTENT_ITEM_TYPE},
//
//                // Show primary email addresses first. Note that there won't be
//                // a primary email address if the user hasn't specified one.
//                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
//    }

//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        List<String> emails = new ArrayList<>();
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            emails.add(cursor.getString(ProfileQuery.ADDRESS));
//            cursor.moveToNext();
//        }
//
////        addEmailsToAutoComplete(emails);
//    }

//    @Override
//    public void onLoaderReset(Loader<Cursor> cursorLoader) {
//
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("used_numbers", used_numbers);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        ViewSwitcher viewSwitcher = (ViewSwitcher) findViewById(R.id.login_switcher);
        if (viewSwitcher.getDisplayedChild() > 0)
            viewSwitcher.showPrevious();
        else
            super.onBackPressed();
    }

    private void signUp() {
        EditText edName = ((EditText) findViewById(R.id.sign_up_first_name)),
                edSecondName = (EditText) findViewById(R.id.sign_up_second_name),
                edLastName = ((EditText) findViewById(R.id.sign_up_lastname)),
                edPhone = ((EditText) findViewById(R.id.sign_up_phone)),
                edEmail = ((EditText) findViewById(R.id.sign_up_mail)),
                edPassword = ((EditText) findViewById(R.id.sign_up_password)),
                edRePassword = ((EditText) findViewById(R.id.sign_up_repassword));

        String name = edName.getText().toString(),
                secondName = edSecondName.getText().toString(),
                lastName = edLastName.getText().toString(),
                phone = edPhone.getText().toString(),
                email = edEmail.getText().toString().toLowerCase(),
                password = edPassword.getText().toString(),
                repassword = edRePassword.getText().toString();

        Spinner spinnerLocation = (Spinner) findViewById(R.id.spinner_sign_up_location);

        if (name.length() > 2) {
            if (lastName.length() > 2) {
                if (spinnerLocation.getSelectedItemPosition() < spinnerLocation.getCount()) {
                    if (phone.length() > 5) {
                        if (isValidEmail(email)) {
                            if (password.length() > 5) {
                                if (password.equals(repassword)) {
                                    ((ViewSwitcher) findViewById(R.id.switcher_sign_up_ok)).showNext();
                                    registerUser(name, secondName, lastName, phone, email, password);
                                } else {
                                    edRePassword.setError("Las claves no coinciden");
                                }
                            } else {
                                edPassword.setError("Clave muy corta");
                            }
                        } else {
                            edEmail.setError("Correo incorrecto");
                        }
                    } else {
                        edPhone.setError("Ingresá tu teléfono");
                        edPhone.requestFocus();
                    }
                } else {
                    TextView errorText = (TextView) spinnerLocation.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Debe elegir una localidad");//changes the selected item text to this
                    errorText.requestFocus();
                }
            } else {
                edLastName.setError("Apellido muy corto");
            }
        } else {
            edName.setError("Nombre muy corto");
        }
        ViewSwitcher viewSwitcherOK = (ViewSwitcher) findViewById(R.id.switcher_sign_up_ok);
        viewSwitcherOK.showPrevious();
    }

    private void registerUser(String name, String secondName, String lastName, String phone, String email, String password) {
//        String name = editTextName.getText().toString(), lastName = editTextLastName.getText().toString(), locale = spinnerLocation.getSelectedItem().toString(), phone = editTextPhone.getText().toString();

        Spinner spinnerLocation = (Spinner) findViewById(R.id.spinner_sign_up_location);

        String locale = (String) spinnerLocation.getSelectedItem();

        final User user = new User(name, secondName, lastName, locale, phone, email, invitationSenderUID);

        MainActivity.newUser = true;
        MainActivity.user = user;

        //REGISTRO DE UN NUEVO USUARIO
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "falló la autenticación\n" + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseDatabase
                                    .getInstance()
                                    .getReference(References.REFERENCE)
                                    .child(References.USERS)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                                FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                                                        .child(References.INVITATION_CODES)
                                                        .child(invitationCode)
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful())
//                                                            close_login();
//                                                        else
                                                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                            else
                                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            SecureRandom secureRandom = new SecureRandom();

                            FirebaseDatabase.getInstance()
                                    .getReference(References.REFERENCE)
                                    .child("verification_codes")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("code")
                                    .setValue(new BigInteger(40, secureRandom).toString(32));
                        }

                        ViewSwitcher viewSwitcherOK = (ViewSwitcher) findViewById(R.id.switcher_sign_up_ok);
                        viewSwitcherOK.showPrevious();
                        // ...
                    }
                });
    }

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
}

