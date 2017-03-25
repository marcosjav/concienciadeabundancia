package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.IHidable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Marcos on 21/03/2017.
 */

public class SignInFragment extends Fragment implements IHidable{
    static String TAG = "fragment_sign_in";
    Button buttonSingIn;
    EditText editTextPhone, editTextPassword;
    ViewSwitcher viewSwitcher;

    public SignInFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        editTextPhone = (EditText) view.findViewById(R.id.edittext_sign_in_phone);
        editTextPassword = (EditText) view.findViewById(R.id.edittext_sign_in_password);
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher_sign_in);

        buttonSingIn = (Button) view.findViewById(R.id.button_sign_in_ok);
        buttonSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        return view;
    }

    private void signIn()
    {
        if (checkInfo()) {
            viewSwitcher.showNext();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(editTextPhone.getText().toString() + "@cda.com", editTextPassword.getText().toString())
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            viewSwitcher.showPrevious();

                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(getActivity(), "Error de inicio de sesion",
                                        Toast.LENGTH_SHORT).show();

                            } else {
//                                    FragmentMan.removeFragment(getActivity(),LoginFragment.class);
                                FragmentMan.eraseAll(getActivity());
                                FragmentMan.changeFragment(getActivity(), MainFragment.class, true);
                            }

                            // ...
                        }
                    });
        }
        else
        {
            Toast.makeText(getActivity(), "Datos incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkInfo()
    {
        String phone = editTextPhone.getText().toString();
        String pass  = editTextPassword.getText().toString();

        return phone.length() == 10
                && pass.length() > 5;
    }

    @Override
    public void setVisibility(boolean visible) {
        getView().findViewById(R.id.fragment_sign_in).setVisibility(visible? View.VISIBLE : View.GONE);
    }
}
