package com.bnvlab.concienciadeabundancia.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.Notify;

/**
 * Created by Marcos on 17/03/2017.
 * Esta clase maneja atodo lo referido al login
 */

public class LoginFragment extends Fragment {
    Button buttonSignIn, buttonSignUp;
    ImageButton buttonMaillink, buttonTweeterLink, buttonWebLink, buttonFacebookLink, buttonPhoneLink;

    public LoginFragment() {
        // REQUIRED EMPTY PUBLIC CONSTRUCTOR
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        buttonSignIn = (Button) view.findViewById(R.id.button_login_sign_in);
        buttonSignUp = (Button) view.findViewById(R.id.button_login_sign_up);
        buttonFacebookLink = (ImageButton) view.findViewById(R.id.button_facebook_link);
        buttonMaillink = (ImageButton) view.findViewById(R.id.button_email_link);
        buttonTweeterLink = (ImageButton) view.findViewById(R.id.button_tweeter_link);
        buttonWebLink = (ImageButton) view.findViewById(R.id.button_web_link);
        buttonPhoneLink = (ImageButton) view.findViewById(R.id.button_phone_link);


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), SettingsFragment.class);
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), SignInFragment.class);

            }
        });

        buttonFacebookLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.facebook.com/concienciaabundancia"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        buttonWebLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.concienciadeabundancia.com"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        buttonPhoneLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.fromParts("tel", "3624376536", null); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });

        buttonMaillink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notify.email(getContext());
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



}
