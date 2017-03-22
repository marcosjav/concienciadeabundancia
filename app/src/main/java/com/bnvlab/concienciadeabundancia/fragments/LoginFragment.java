package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.Notify;
import com.bnvlab.concienciadeabundancia.R;

/**
 * Created by Marcos on 17/03/2017.
 * Esta clase maneja atodo lo referido al login
 */

public class LoginFragment extends Fragment {
    Button buttonLogin, buttonRegister;

    public LoginFragment() {
        // REQUIRED EMPTY PUBLIC CONSTRUCTOR
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        buttonLogin     = (Button) view.findViewById(R.id.button_login_login);
        buttonRegister  = (Button) view.findViewById(R.id.button_login_register);


        //CHANGING VIEW TEST
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)  //VA ANTES DEL ADD
                        .add(R.id.fragment_main, new SignUpFragment(), "sign_up_fragment")
                        // Add this transaction to the back stack
                        .addToBackStack("login")
                        .commit();
            }
        });

        //NOTIFICATION TEST
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notify.message(getContext(), "Conciencia de abundancia", "Hay un nuevo test disponible!");
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)  //VA ANTES DEL ADD
                        .add(R.id.fragment_main, new SignInFragment(), "sign_in_fragment")
                        // Add this transaction to the back stack
                        .addToBackStack("login")
                        .commit();
            }
        });


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*SHOWS SPECIFICS LAYOUT*/
    private static final int    LAYOUT_MAIN     = 0,
                                LAYOUT_LOGIN    = 1,
                                LAYOUT_REGISTER = 2;

    /**
     * Shows specific layout, hidding the rest
     * @param layout the id of the layout to show
     */
    private void show(int layout)
    {
        int[] layout_id = { R.id.layout_login_main,
                            R.id.layout_login_register};

        Toast.makeText(getActivity(), "SHOW", Toast.LENGTH_SHORT).show();

        for (int id : layout_id )
        {
            getView().findViewById(id).setVisibility( id==layout ? View.VISIBLE : View.GONE);
        }
    }


}
