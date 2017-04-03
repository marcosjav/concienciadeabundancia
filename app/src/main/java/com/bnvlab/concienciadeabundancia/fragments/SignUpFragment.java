package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.IHidable;
import com.bnvlab.concienciadeabundancia.clases.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

/**
 * Created by Marcos on 21/03/2017.
 */

public class SignUpFragment extends Fragment implements IHidable{
    static String TAG = "fragment_sign_up";
    Button buttonSignUp;
    Spinner spinnerLocation;
    EditText editTextName,
            editTextLastName,
            editTextPhone,
            editTextMail,
            editTextPassword,
            editTextRePassword;
    boolean isReady = false;
    ViewSwitcher viewSwitcherOK;
    
    ArrayList<String> locationList = new ArrayList<String>() {{
//        add("Resistencia");
//        add("Corrientes");
//        add("Córdoba");
    }};

    public SignUpFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        editTextName = (EditText) view.findViewById(R.id.edittext_sign_up_name);
        editTextLastName = (EditText) view.findViewById(R.id.edittext_sign_up_lastname);
        editTextPhone = (EditText) view.findViewById(R.id.edittext_sign_up_phone);
        editTextMail = (EditText) view.findViewById(R.id.edittext_sign_up_mail);
        editTextPassword = (EditText) view.findViewById(R.id.edittext_sign_up_password);
        editTextRePassword = (EditText) view.findViewById(R.id.edittext_sign_up_repassword);
        viewSwitcherOK = (ViewSwitcher) view.findViewById(R.id.view_switcher_sign_up_ok);

        spinnerLocation = (Spinner) view.findViewById(R.id.spinner_sign_up_location);
        buttonSignUp = (Button) view.findViewById(R.id.button_sign_up_ok);

        FirebaseDatabase.getInstance()
                .getReference(MainActivity.REFERENCE)
                .child("locations")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getChildrenCount();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    String location = (String)postSnapshot.getValue();
                    locationList.add(location);
                }

                //THE 'me.srodrigo:androidhintspinner:1.0.0' LIBRARY ALLOWS US TO PUT A HINT TEXT IN THE SPINNER  https://github.com/srodrigo/Android-Hint-Spinner
                HintSpinner<String> hintSpinner = new HintSpinner<>(
                        spinnerLocation,
                        // Default layout - You don't need to pass in any layout id, just your hint text and
                        // your list data
                        new HintAdapter(getContext(), R.string.login_edittext_hint_location, locationList),
                        new HintSpinner.Callback<String>() {
                            @Override
                            public void onItemSelected(int position, String itemAtPosition) {
                                // Here you handle the on item selected event (this skips the hint selected event)
                            }
                        });
                hintSpinner.init();

                ((ViewSwitcher)view.findViewById(R.id.view_switcher_sign_up_location)).showNext();
                isReady = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "onCancelled", Toast.LENGTH_SHORT).show();
            }
        });



        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReady) {
                    signUp();
                }
                else
                {
                    Toast.makeText(getActivity(), "Espere a que carguen las localidades", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void signUp() {
        String name = editTextName.getText().toString(),
                lastName = editTextLastName.getText().toString(),
                phone = editTextPhone.getText().toString(),
                email = editTextMail.getText().toString(),
                password = editTextPassword.getText().toString(),
                repassword = editTextRePassword.getText().toString();

        if (name.length() > 2) {
            if (lastName.length() > 2) {
                if (phone.length() == 10) {
                    if (isValidEmail(email)) {
                        if (password.length() > 5) {
                            if (password.equals(repassword)) {
                                viewSwitcherOK.showNext();
                                registerUser(phone + "@cda.com", password);
                            } else {
                                Toast.makeText(getContext(), "Las claves no coinciden", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Clave muy corta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Correo incorrecto", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Teléfono incorrecto.\nIngresalo sin el 0 (cero)\ny sin el 15", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Apellido corto", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Nombre corto", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(String email, String password) {
        String name = editTextName.getText().toString(), lastName = editTextLastName.getText().toString(), locale = spinnerLocation.getSelectedItem().toString(), phone = editTextPhone.getText().toString();

        final User user = new User(name, lastName, locale, phone, email);

        MainActivity.user = user;
        MainActivity.newUser = true;

        //REGISTRO DE UN NUEVO USUARIO
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getContext(), "falló la autenticación" + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "se autenticó correctamente",
                                    Toast.LENGTH_SHORT).show();

                            FragmentMan.removeFragment(getActivity(), SignUpFragment.class);
                        }

                        viewSwitcherOK.showPrevious();
                        // ...
                    }
                });
    }

    @Override
    public void setVisibility(boolean visible) {
        getView().findViewById(R.id.fragment_sign_up).setVisibility(visible? View.VISIBLE : View.GONE);
    }
}
