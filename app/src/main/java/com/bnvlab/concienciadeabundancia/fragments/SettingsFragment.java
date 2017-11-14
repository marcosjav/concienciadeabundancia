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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
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

/**
 * Created by Marcos on 21/03/2017.
 */

public class SettingsFragment extends Fragment {
    static String TAG = "fragment_settings";
    Button buttonUpdate,
            buttonLocation,
//            buttonPassOk,
            buttonPersonalInfo,
            buttonChangePass;

    EditText editTextName,
            editTextSecondName,
            editTextLastName,
            editTextPhone,
            editTextMail,
            editTextPassword,
            editTextRePassword;
    boolean isReady = false, locationsReady = false, userReady = false, locList;
//    ViewSwitcher viewSwitcherOK;
    LinearLayout layoutLoading;
    TextView textViewLocation;
    ListView listViewLocation;
    View layoutSettings,
            layoutProgressBar,
//            layoutPass,
            layoutSettingsMenu;
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
//        viewSwitcherOK = (ViewSwitcher) view.findViewById(R.id.view_switcher_settings_ok);
//        layoutLoading = (LinearLayout) view.findViewById(R.id.settings_progressbar);
        textViewLocation = (TextView) view.findViewById(R.id.textView_location);
        buttonLocation = (Button) view.findViewById(R.id.button_location);
        listViewLocation = (ListView) view.findViewById(R.id.listview_location);

        layoutSettings = view.findViewById(R.id.layout_settings);
//        layoutPass     = view.findViewById(R.id.layout_change_pass);
        layoutSettingsMenu = view.findViewById(R.id.layout_settings_menu);
        layoutProgressBar = view.findViewById(R.id.settings_progressbar);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, locationList);
        listViewLocation.setAdapter(adapter);

//        buttonPassOk = (Button) view.findViewById(R.id.button_pass_ok);
        buttonPersonalInfo = (Button) view.findViewById(R.id.button_personal_info);
        buttonChangePass = (Button) view.findViewById(R.id.button_change_pass);
        buttonUpdate = (Button) view.findViewById(R.id.button_settings_ok);

        view.findViewById(R.id.new_icon_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(bounce);
                getActivity().onBackPressed();
            }
        });

//        buttonPassOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changePass();
//            }
//        });

//        buttonChangePass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPassword(true);
//            }
//        });
//
//        buttonPersonalInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPersonalInfo(true);
//            }
//        });

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewLocation.setText(getString(R.string.signup_locations_text));
                textViewLocation.setTextColor(Color.WHITE);
                showLocationList(true);
            }
        });

        listViewLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                buttonLocation.setText(locationList.get(position));
                showLocationList(false);
            }
        });

        user = MainActivity.user;

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

                        locationsReady = true;
                        isReady = true;
                        updateLocationButton();
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
        showProgress(true);

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
//                            String second = user.getSecondName() == null ? "" : user.getSecondName();
                            user = data.getValue(User.class);
                            userReady = true;
                            editTextName.setText(user.getName());
                            editTextSecondName.setText(user.getSecondName() == null ? "" : user.getSecondName());
                            editTextLastName.setText(user.getLastName());
                            editTextPhone.setText(user.getPhone());
                            editTextMail.setText(user.getEmail());
                            updateLocationButton();
//                            layoutLoading.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        //        FILLING USER DATA IN EDITTEXTS
    }

    private void updateLocationButton() {
        if (userReady && locationsReady) {
//            spinnerLocation.setSelection(locationList.indexOf(user.getLocale()));
            buttonLocation.setText(user.getLocale());
            showProgress(false);
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
        String  name = editTextName.getText().toString(),
                secondName = editTextSecondName.getText().toString(),
                lastName = editTextLastName.getText().toString(),
                phone = editTextPhone.getText().toString(),
                email = editTextMail.getText().toString(),
                password = editTextPassword.getText().toString(),
                repassword = editTextRePassword.getText().toString();

        showProgress(true);

        if (name.length() > 2) {
            if (lastName.length() > 2) {
                if (!buttonLocation.getText().toString().equals(getString(R.string.signup_button_locations))) {
                    if (phone.length() == 10) {
                        if (isValidEmail(email)) {
                            if (password.length() > 5 || password.equals("")) {
                                if (password.equals(repassword)) {
//                                    viewSwitcherOK.showNext();
                                    saveData();
                                } else {
//                                Toast.makeText(getContext(), "Las claves no coinciden", Toast.LENGTH_SHORT).show();
                                    editTextRePassword.setError("Las claves no coinciden");
                                    showProgress(false);
                                }
                            } else {
//                            Toast.makeText(getContext(), "Clave muy corta", Toast.LENGTH_SHORT).show();
                                editTextPassword.setError("Clave muy corta");
                                showProgress(false);
                            }
                        } else {
//                        Toast.makeText(getContext(), "Correo incorrecto", Toast.LENGTH_SHORT).show();
                            editTextMail.setError("Correo incorrecto");
                            showProgress(false);
                        }
                    } else {
//                    Toast.makeText(getContext(), "Teléfono incorrecto.\nIngresalo sin el 0 (cero)\ny sin el 15", Toast.LENGTH_SHORT).show();
                        editTextPhone.setError("Teléfono incorrecto.\nIngresalo sin el 0 (cero)\ny sin el 15");
                        showProgress(false);
                    }
                } else {
                    textViewLocation.setError("");
                    textViewLocation.setTextColor(Color.RED);//just to highlight that this is an error
                    textViewLocation.setText("Debe elegir una localidad");//changes the selected item text to this
                    textViewLocation.requestFocus();
                    showProgress(false);
                }
            } else {
//                Toast.makeText(getContext(), "Apellido corto", Toast.LENGTH_SHORT).show();
                editTextLastName.setError("Apellido corto");
                showProgress(false);
            }
        } else {
//            Toast.makeText(getContext(), "Nombre corto", Toast.LENGTH_SHORT).show();
            editTextName.setError("Nombre corto");
            showProgress(false);
        }
    }

    boolean finish1, finish2;
    private void saveData() {
        boolean isModified = false, emailUpdate = false;
        final String nName = editTextName.getText().toString(), nSecondName = editTextSecondName.getText().toString(), nLastName = editTextLastName.getText().toString(), nPhone = editTextPhone.getText().toString(), nEmail = editTextMail.getText().toString(), nLocation = buttonLocation.getText().toString(), nPassword = editTextPassword.getText().toString(), registredPhone = user.getPhone();


        finish1 = false;
        finish2 = false;

        String salida = "";

        if (!nPassword.equals("")) {
            FirebaseAuth.getInstance().getCurrentUser().updatePassword(nPassword);
        }

        if (!nName.equals(user.getName())) {
            user.setName(nName);
            isModified = true;
        }

        if (!nSecondName.equals(user.getSecondName())) {
            user.setSecondName(nSecondName);
            isModified = true;
        }

        if (!nLastName.equals(user.getLastName())) {
            user.setLastName(nLastName);
            isModified = true;
        }

        if (!nPhone.equals(user.getPhone())) {
            user.setLastNumber(user.getPhone());
            user.setPhone(nPhone);
            isModified = true;
        }

        if (!nEmail.equals(user.getEmail())) {
            emailUpdate = true;
            isModified = true;
        }

        if (!nLocation.equals(user.getLocale())) {
            user.setLocale(nLocation);
            isModified = true;
        }


        if  (emailUpdate){
            FirebaseAuth.getInstance().getCurrentUser()
                    .updateEmail(nEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                FirebaseDatabase.getInstance()
                                        .getReference(References.REFERENCE)
                                        .child(References.USERS)
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(References.USERS_CHILD_EMAIL)
                                        .setValue(nEmail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    showProgress(false);
                                                }
                                                finish1 = true;
                                                finish(finish1, finish2);
                                            }
                                        });
                            }else{
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            finish1 = true;
        }

        if (isModified) {
            FirebaseDatabase.getInstance()
                    .getReference(References.REFERENCE)
                    .child(References.USERS)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                showProgress(false);
                            }
                            finish2 = true;
                            finish(finish1, finish2);
                        }
                    });
        } else {
            finish2 = true;
        }

    }

    private void changePass() {
    }

    @Override
    public void onResume() {
        updateDataFields();
        super.onResume();
    }

    private void finish(boolean uno, boolean dos){
        if (uno && dos) {
//            viewSwitcherOK.showPrevious();
            Toast.makeText(getContext(), "LISTO!", Toast.LENGTH_SHORT).show();
//            getActivity().onBackPressed();

            showProgress(false);
        }
    }

    private void showLocationList(boolean show){
        listViewLocation.setVisibility(show?View.VISIBLE : View.GONE);
        layoutSettings.setVisibility(!show?View.VISIBLE:View.GONE);
        locList = show;
    }

    private void showProgress(boolean show){
        int visible = show?View.VISIBLE : View.GONE,
                gone = !show?View.VISIBLE:View.GONE;

        layoutSettings.setVisibility(gone);
        layoutProgressBar.setVisibility(visible);
//        layoutPass.setVisibility(View.GONE);
//        layoutSettingsMenu.setVisibility(gone);
    }

   /* private void showPassword(boolean show){
        int visible = show?View.VISIBLE : View.GONE,
            gone = !show?View.VISIBLE:View.GONE;

        layoutSettings.setVisibility(gone);
        layoutProgressBar.setVisibility(gone);
//        layoutPass.setVisibility(visible);
        layoutSettingsMenu.setVisibility(gone);
    }

    private void showPersonalInfo(boolean show){
        int visible = show?View.VISIBLE : View.GONE,
            gone = !show?View.VISIBLE:View.GONE;

        layoutSettings.setVisibility(visible);
        layoutProgressBar.setVisibility(gone);
        layoutPass.setVisibility(gone);
        layoutSettingsMenu.setVisibility(gone);
    }*/


}
