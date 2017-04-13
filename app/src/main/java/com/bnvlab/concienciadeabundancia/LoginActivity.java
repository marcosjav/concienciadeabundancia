package com.bnvlab.concienciadeabundancia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.clases.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

import static android.Manifest.permission.READ_CONTACTS;
import static android.os.Build.VERSION_CODES.M;
import static com.bnvlab.concienciadeabundancia.fragments.SettingsFragment.isValidEmail;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

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
    public static final String APP_SHARED_PREF_KEY = MainActivity.class.getSimpleName(), FIRST_TIME_PREF_KEY = APP_SHARED_PREF_KEY + ".firsTime", VERIFIED = APP_SHARED_PREF_KEY + ".verified";
    SharedPreferences prefs;
    private Button buttonPasswordRecovery;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

////It's enough to remove the line
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
////But if you want to display  full screen (without action bar) write too
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        prefs = this.getSharedPreferences(
                this.APP_SHARED_PREF_KEY, Context.MODE_PRIVATE);

        used_numbers = new ArrayList<>(prefs.getStringSet("used_numbers", new HashSet<String>()));

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, used_numbers);
        mEmailView.setAdapter(adapter);
        //populateAutoComplete();


        mPasswordView = (EditText) findViewById(R.id.password);
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
                ViewSwitcher viewSwitcherOK = (ViewSwitcher) findViewById(R.id.switcher_sign_up_ok);
                viewSwitcherOK.showPrevious();
                signUp();
            }
        });

        loadLocations();

        buttonPasswordRecovery = (Button) findViewById(R.id.button_password_recovery);
        buttonPasswordRecovery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

                builder
                        .setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        // User clicked OK button
                        final String email = mPasswordView.getText().toString();
                        if (isValidEmail(email))
                            sendRecoveryPass(email, dialog);
                        else if (isPhoneValid(email))
                        {
                            FirebaseDatabase.getInstance().getReference(MainActivity.REFERENCE)
                                    .child(User.CHILD)
                                    .endAt(email)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean isFinished = false;
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        User user = data.getValue(User.class);
                                        if(user.getEmail().equals(email) || user.getPhone().equals(email)) {
//                                    Toast.makeText(LoginActivity.this, "Usuario encontrado!", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance()
                                                    .sendPasswordResetEmail(user.getEmail())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(LoginActivity.this, "Se envió el correo!", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }
                                                    else{
                                                        Toast.makeText(LoginActivity.this, "Ocurrió un error\n"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }
                                                }
                                            });
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
                        else
                            Toast.makeText(LoginActivity.this, "No es un mail válido", Toast.LENGTH_SHORT).show();
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

    private void sendRecoveryPass(String email, final DialogInterface dialog)
    {
        FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Se envió el correo!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else
                            Toast.makeText(LoginActivity.this, "Hubo un inconveniente\n"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    private void populateAutoComplete() {
//        if (!mayRequestContacts()) {
//            return;
//        }
//
//        getLoaderManager().initLoader(0, null, this);
//    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    ArrayList<String> locationList = new ArrayList<String>();

    private void loadLocations() {
        final Spinner spinnerLocation = (Spinner) findViewById(R.id.spinner_sign_up_location);
        FirebaseDatabase.getInstance()
                .getReference(MainActivity.REFERENCE)
                .child("locations")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getChildrenCount();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            // TODO: handle the post
                            String location = (String) postSnapshot.getValue();
                            locationList.add(location);
                        }

                        //THE 'me.srodrigo:androidhintspinner:1.0.0' LIBRARY ALLOWS US TO PUT A HINT TEXT IN THE SPINNER  https://github.com/srodrigo/Android-Hint-Spinner
                        HintSpinner<String> hintSpinner = new HintSpinner<>(
                                spinnerLocation,
                                // Default layout - You don't need to pass in any layout id, just your hint text and
                                // your list data
                                new HintAdapter(LoginActivity.this, R.string.login_edittext_hint_location, locationList),
                                new HintSpinner.Callback<String>() {
                                    @Override
                                    public void onItemSelected(int position, String itemAtPosition) {
                                        // Here you handle the on item selected event (this skips the hint selected event)
                                        ((TextView) spinnerLocation.getSelectedView()).setTextColor(Color.BLACK);
                                    }
                                });
                        hintSpinner.init();

                        ((ViewSwitcher) findViewById(R.id.view_switcher_sign_up_location)).showNext();
//                        isReady = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
//                        Toast.makeText(LoginActivity.this, "onCancelled", Toast.LENGTH_SHORT).show();
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
//        Toast.makeText(this, "Teléfono: " + !isSignedWhitEmail, Toast.LENGTH_SHORT).show();
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
                    .getReference(MainActivity.REFERENCE)
                    .child(User.CHILD)
                    .endAt(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean isFinished = false;
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                User user = data.getValue(User.class);
                                if(user.getEmail().equals(email) || user.getPhone().equals(email)) {
//                                    Toast.makeText(LoginActivity.this, "Usuario encontrado!", Toast.LENGTH_SHORT).show();
                                    if (user.isSignInWithEmail()) {
                                        signInFireBase(user.getEmail(), password);
                                        isFinished = true;
                                        break;
                                    }
                                    else {
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
                        showProgress(false);

                        if (task.isSuccessful()) {

                            /*FirebaseDatabase.getInstance().getReference(MainActivity.REFERENCE)
                                    .child(User.CHILD)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            boolean isFinished = false;
                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                final User user = data.getValue(User.class);
                                                final FirebaseUser userFB = FirebaseAuth.getInstance().getCurrentUser();

                                                String wrongPhone = userFB.getEmail().split("@")[0];

                                                if (user.getEmail().equals(userFB.getEmail()))
                                                    MainActivity.user = user;

                                                if (!data.getKey().equals(userFB.getUid())) {

                                                    if (user.getPhone().equals(wrongPhone)) {
                                                        isFinished = true;
                                                        userFB.updateEmail(user.getEmail())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            if (!userFB.isEmailVerified())
                                                                                userFB.sendEmailVerification();

                                                                            user.setSignInWithEmail(true);

                                                                            MainActivity.user = user;

                                                                            FirebaseDatabase.getInstance()
                                                                                    .getReference(MainActivity.REFERENCE)
                                                                                    .child(User.CHILD)
                                                                                    .child(user.getPhone())
                                                                                    .child("signInWithEmail")
                                                                                    .setValue(true)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                close_login();
                                                                                            } else {
                                                                                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        }
                                                                                    });


                                                                        } else {
                                                                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                    }
                                                }
                                            }
                                            if (!isFinished)
                                                close_login();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });*/

                            close_login();

                        } else {

                            if (((FirebaseAuthException) task.getException()).getErrorCode().equals("ERROR_USER_NOT_FOUND")) {
                                mEmailView.setError("usuario no encontrado");
                                Toast.makeText(LoginActivity.this, email + "\n" + password, Toast.LENGTH_SHORT).show();
                            } else if (((FirebaseAuthException) task.getException()).getErrorCode().equals("ERROR_WRONG_PASSWORD")) {
                                mPasswordView.setError(getString(R.string.error_incorrect_password));
                            } else if (((FirebaseAuthException) task.getException()).getErrorCode().equals("ERROR_INVALID_EMAIL")) {
                                mPasswordView.setError(getString(R.string.error_invalid_email));
                            } else {
                                Toast.makeText(LoginActivity.this, ((FirebaseAuthException) task.getException()).getErrorCode(), Toast.LENGTH_LONG).show();
                            }
                            mPasswordView.requestFocus();
                        }
                    }
                });
    }

    private void close_login() {
        String email = ((AutoCompleteTextView) findViewById(R.id.email)).getText().toString();

        if (!used_numbers.contains(email))
            used_numbers.add(email);

        prefs.edit().putStringSet("used_numbers", new HashSet<String>(used_numbers)).apply();

        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(myIntent);
        finish();
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

    private void addEmailsTsoAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
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
        EditText edName = ((EditText) findViewById(R.id.sign_up_name)), edLastName = ((EditText) findViewById(R.id.sign_up_lastname)), edPhone = ((EditText) findViewById(R.id.sign_up_phone)), edEmail = ((EditText) findViewById(R.id.sign_up_mail)), edPassword = ((EditText) findViewById(R.id.sign_up_password)), edRePassword = ((EditText) findViewById(R.id.sign_up_repassword));

        String name = edName.getText().toString(),
                lastName = edLastName.getText().toString(),
                phone = edPhone.getText().toString(),
                email = edEmail.getText().toString().toLowerCase(),
                password = edPassword.getText().toString(),
                repassword = edRePassword.getText().toString();

        Spinner spinnerLocation = (Spinner) findViewById(R.id.spinner_sign_up_location);

        if (name.length() > 2) {
            if (lastName.length() > 2) {
                if (spinnerLocation.getSelectedItemPosition() < spinnerLocation.getCount()) {
//                    Toast.makeText(this, spinnerLocation.getSelectedItemPosition() + "", Toast.LENGTH_SHORT).show();
                    if (phone.length() == 10) {
                        if (isValidEmail(email)) {
                            if (password.length() > 5) {
                                if (password.equals(repassword)) {
                                    ((ViewSwitcher) findViewById(R.id.switcher_sign_up_ok)).showNext();
                                    registerUser(name, lastName, phone, email, password);
                                } else {
                                    edRePassword.setError("Las claves no coinciden");
//                                    Toast.makeText(getContext(), "Las claves no coinciden", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                edPassword.setError("Clave muy corta");
//                                Toast.makeText(getContext(), "Clave muy corta", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            edEmail.setError("Correo incorrecto");
//                            Toast.makeText(getContext(), "Correo incorrecto", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        edPhone.setError("Teléfono incorrecto.\nIngresalo sin el 0 (cero)\ny sin el 15");
//                        Toast.makeText(getContext(), "Teléfono incorrecto.\nIngresalo sin el 0 (cero)\ny sin el 15", Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(getContext(), "Apellido corto", Toast.LENGTH_SHORT).show();
            }
        } else {
            edName.setError("Nombre muy corto");
//            Toast.makeText(getContext(), "Nombre corto", Toast.LENGTH_SHORT).show();
        }
        ViewSwitcher viewSwitcherOK = (ViewSwitcher) findViewById(R.id.switcher_sign_up_ok);
        viewSwitcherOK.showPrevious();
    }

    private void registerUser(String name, String lastName, String phone, String email, String password) {
//        String name = editTextName.getText().toString(), lastName = editTextLastName.getText().toString(), locale = spinnerLocation.getSelectedItem().toString(), phone = editTextPhone.getText().toString();

        Spinner spinnerLocation = (Spinner) findViewById(R.id.spinner_sign_up_location);

        String locale = (String) spinnerLocation.getSelectedItem();

        final User user = new User(name, lastName, locale, phone, email);

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
                            Toast.makeText(LoginActivity.this, "falló la autenticación" + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseDatabase
                                    .getInstance()
                                    .getReference(MainActivity.REFERENCE)
                                    .child(User.CHILD)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                                close_login();
                                            else
                                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            SecureRandom secureRandom = new SecureRandom();

                            FirebaseDatabase.getInstance()
                                    .getReference(MainActivity.REFERENCE)
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
}

