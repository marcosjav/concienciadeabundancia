package com.bnvlab.concienciadeabundancia;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import java.util.ArrayList;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

/**
 * Created by Marcos on 21/03/2017.
 */

public class SignUpFragment extends Fragment {
    Spinner spinnerLocation;
    ArrayList<String> locationList = new ArrayList<String>() {{
        add("Resistencia");
        add("Corrientes");
        add("Córdoba");
    }};

    public SignUpFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        
        spinnerLocation = (Spinner) view.findViewById(R.id.spinner_login_location);

        //THE 'me.srodrigo:androidhintspinner:1.0.0' LIBRARY ALLOWS US TO PUT A HINT TEXT IN THE SPINNER  https://github.com/srodrigo/Android-Hint-Spinner
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
}
