package com.bnvlab.concienciadeabundancia;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

/**
 * Created by Marcos on 17/03/2017.
 * Esta clase maneja atodo lo referido al login
 */

public class LoginFragment extends Fragment {
    Button buttonLogin, buttonRegister;
    Spinner spinnerLocation;
    ArrayList<String> locationList = new ArrayList<String>() {{
        add("Resistencia");
        add("Corrientes");
        add("Córdoba");
    }};

    public LoginFragment() {
        // REQUIRED EMPTY PUBLIC CONSTRUCTOR
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        buttonLogin     = (Button) view.findViewById(R.id.button_login_login);
        buttonRegister  = (Button) view.findViewById(R.id.button_login_register);
        spinnerLocation = (Spinner) view.findViewById(R.id.spinner_login_location);

        //CHANGING VIEW TEST
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(R.id.layout_login_register);
            }
        });

        //NOTIFICATION TEST
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notify.message(getContext(), "Conciencia de abundancia", "Hay un nuevo test disponible!");
            }
        });


        //THE 'me.srodrigo:androidhintspinner:1.0.0' LIBRARY ALLOWS US TO PUT A HINT TEXT IN THE SPINNER
        HintSpinner<String> hintSpinner = new HintSpinner<>(
                spinnerLocation,
                // Default layout - You don't need to pass in any layout id, just your hint text and
                // your list data
                new HintAdapter(getContext(), R.string.login_edittext_hint_locale, locationList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        // Here you handle the on item selected event (this skips the hint selected event)
                    }
                });
        hintSpinner.init();

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
