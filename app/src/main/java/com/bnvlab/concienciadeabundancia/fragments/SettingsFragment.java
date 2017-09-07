package com.bnvlab.concienciadeabundancia.fragments;

import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
import com.bnvlab.concienciadeabundancia.clases.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

/**
 * Created by Marcos on 21/03/2017.
 */

public class SettingsFragment extends Fragment {
    static String TAG = "fragment_settings";
    Button buttonUpdate;
    Spinner spinnerLocation;
    EditText editTextName,
            editTextSecondName,
            editTextLastName,
            editTextPhone,
            editTextMail,
            editTextPassword,
            editTextRePassword;
    boolean isReady = false, locationsReady = false, userReady = false, nameModify, lastNameModify, phoneModify, mailModify, passModify;
    ViewSwitcher viewSwitcherOK;
    LinearLayout layoutLoading;
    User user;
//    Animation bounce;
    @Override
    public void onStart() {
        super.onStart();
        /*bounce = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator();

        bounce.setInterpolator(interpolator);*/
    }

    ArrayList<String> locationList = new ArrayList<String>() {{
//        add("Resistencia");
//        add("Corrientes");
//        add("Córdoba");
    }};

    public SettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);

        editTextName = (EditText) view.findViewById(R.id.edittext_settings_name);
        editTextSecondName = (EditText) view.findViewById(R.id.edittext_settings_second_name);
        editTextLastName = (EditText) view.findViewById(R.id.edittext_settings_lastname);
        editTextPhone = (EditText) view.findViewById(R.id.edittext_settings_phone);
        editTextMail = (EditText) view.findViewById(R.id.edittext_settings_mail);
        editTextPassword = (EditText) view.findViewById(R.id.edittext_settings_password);
        editTextRePassword = (EditText) view.findViewById(R.id.edittext_settings_repassword);
        viewSwitcherOK = (ViewSwitcher) view.findViewById(R.id.view_switcher_settings_ok);
        layoutLoading = (LinearLayout) view.findViewById(R.id.layout_loading);

        spinnerLocation = (Spinner) view.findViewById(R.id.spinner_settings_location);
        buttonUpdate = (Button) view.findViewById(R.id.button_settings_ok);

        ((TextView)view.findViewById(R.id.text_back)).setTypeface(Utils.getTypeface(getContext()));
        view.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(bounce);
                getActivity().onBackPressed();
            }
        });

        user = MainActivity.user;
        /*String url = "https://polar-tor-72537.herokuapp.com/get-locations";
        Conn.getJSONArray(getActivity(), Conn.UrlType.LOCATIONS, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        locationList = Conn.jsonToArray(response);
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
                                        ((TextView) spinnerLocation.getSelectedView()).setTextColor(Color.BLACK);
                                    }
                                });
                        hintSpinner.init();

                        locationsReady = true;

                        updateSpinner();

                        layoutProgress.showNext();
                        isReady = true;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );*/

//        layoutProgress.showNext();
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.LOCATIONS)
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
                                new HintAdapter(getContext(), R.string.login_edittext_hint_location, locationList),
                                new HintSpinner.Callback<String>() {
                                    @Override
                                    public void onItemSelected(int position, String itemAtPosition) {
                                        // Here you handle the on item selected event (this skips the hint selected event)
                                        ((TextView) spinnerLocation.getSelectedView()).setTextColor(Color.WHITE);
                                    }
                                });
                        hintSpinner.init();

                        locationsReady = true;
                        isReady = true;
                        updateSpinner();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "onCancelled", Toast.LENGTH_SHORT).show();
                    }
                });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReady) {
                    modify();
                } else {
                    Toast.makeText(getActivity(), "Espere a que carguen las localidades", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateDataFields();

        return view;
    }

    private void updateDataFields() {
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.USERS)
                .orderByChild(References.USERS_CHILD_EMAIL)
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            user = data.getValue(User.class);
                            userReady = true;
                            editTextName.setText(user.getName());
                            editTextSecondName.setText(user.getSecondName() == null ? "" : user.getSecondName());
                            editTextLastName.setText(user.getLastName());
                            editTextPhone.setText(user.getPhone());
                            editTextMail.setText(user.getEmail());
                            updateSpinner();
                            layoutLoading.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        //        FILLING USER DATA IN EDITTEXTS
    }

    private void updateSpinner() {
        if (userReady && locationsReady) {
            spinnerLocation.setSelection(locationList.indexOf(user.getLocale()));
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void modify() {
        String name = editTextName.getText().toString(),
                lastName = editTextLastName.getText().toString(),
                phone = editTextPhone.getText().toString(),
                email = editTextMail.getText().toString(),
                password = editTextPassword.getText().toString(),
                repassword = editTextRePassword.getText().toString();

        if (name.length() > 2) {
            if (lastName.length() > 2) {
                if (spinnerLocation.getSelectedItemPosition() < spinnerLocation.getCount()) {
                    if (phone.length() == 10) {
                        if (isValidEmail(email)) {
                            if (password.length() > 5 || password.equals("")) {
                                if (password.equals(repassword)) {
                                    viewSwitcherOK.showNext();
                                    saveData();
                                } else {
//                                Toast.makeText(getContext(), "Las claves no coinciden", Toast.LENGTH_SHORT).show();
                                    editTextRePassword.setError("Las claves no coinciden");
                                }
                            } else {
//                            Toast.makeText(getContext(), "Clave muy corta", Toast.LENGTH_SHORT).show();
                                editTextPassword.setError("Clave muy corta");
                            }
                        } else {
//                        Toast.makeText(getContext(), "Correo incorrecto", Toast.LENGTH_SHORT).show();
                            editTextMail.setError("Correo incorrecto");
                        }
                    } else {
//                    Toast.makeText(getContext(), "Teléfono incorrecto.\nIngresalo sin el 0 (cero)\ny sin el 15", Toast.LENGTH_SHORT).show();
                        editTextPhone.setError("Teléfono incorrecto.\nIngresalo sin el 0 (cero)\ny sin el 15");
                    }
                } else {
                    TextView errorText = (TextView) spinnerLocation.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Debe elegir una localidad");//changes the selected item text to this
                    errorText.requestFocus();
                }
            } else {
//                Toast.makeText(getContext(), "Apellido corto", Toast.LENGTH_SHORT).show();
                editTextLastName.setError("Apellido corto");
            }
        } else {
//            Toast.makeText(getContext(), "Nombre corto", Toast.LENGTH_SHORT).show();
            editTextName.setError("Nombre corto");
        }
    }

    private void saveData() {
        boolean isModified = false;
        String nName = editTextName.getText().toString(), nLastName = editTextLastName.getText().toString(), nPhone = editTextPhone.getText().toString(), nEmail = editTextMail.getText().toString(), nLocation = locationList.get(spinnerLocation.getSelectedItemPosition()), nPassword = editTextPassword.getText().toString(), registredPhone = user.getPhone();

        String salida = "";

        if (!nPassword.equals("")) {
//            salida += "no password";
            FirebaseAuth.getInstance().getCurrentUser().updatePassword(nPassword);
        }
//        }else {
//            salida += "new pass: " + nPassword;
//        }

        if (!nName.equals(user.getName())) {
            user.setName(nName);
            isModified = true;
        }
//            salida += "\nMismo nombre";
//        else
//            salida += "\nNuevo nombre: " + nName;

        if (!nLastName.equals(user.getLastName())) {
            user.setLastName(nLastName);
            isModified = true;
        }
//            salida += "\nMismo apellido";
//        else
//            salida += "\nNuevo apellido: " + nLastName;

        if (!nPhone.equals(user.getPhone())) {
            user.setLastNumber(user.getPhone());
            registredPhone = user.getPhone();
            user.setPhone(nPhone);
            isModified = true;
//            FirebaseAuth.getInstance().getCurrentUser().updateEmail(nPhone + "@cda.com");
        }
//            salida += "\nMismo telefono";
//        else
//            salida += "\nNuevo telefoni: " + nPhone;

        if (!nEmail.equals(user.getEmail())) {
            user.setEmail(nEmail);
            isModified = true;
        }
//            salida += "\nMismo correo";
//        else
//            salida += "\nNuevo correo: " + nEmail;

        if (!nLocation.equals(user.getLocale())) {
            user.setLocale(nLocation);
            isModified = true;
        }
//            salida += "\nMisma localidad";
//        else
//            salida += "\nNueva localidad: " + nLocation;


        if (isModified) {
            FirebaseDatabase.getInstance()
                    .getReference(References.REFERENCE)
                    .child(References.USERS)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                viewSwitcherOK.showPrevious();
                                Toast.makeText(getContext(), "LISTO!", Toast.LENGTH_SHORT).show();

                            } else
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }

    @Override
    public void onResume() {
        updateDataFields();
        super.onResume();
    }


}
