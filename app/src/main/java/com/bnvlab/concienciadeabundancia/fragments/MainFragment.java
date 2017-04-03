package com.bnvlab.concienciadeabundancia.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.LoginActivity;
import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.Notify;
import com.bnvlab.concienciadeabundancia.clases.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Marcos on 21/03/2017.
 */

public class MainFragment extends Fragment {
//    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    ImageButton buttonConference, buttonVideos, buttonProgress, buttonTest;
    ImageButton buttonMaillink, buttonTweeterLink, buttonWebLink, buttonFacebookLink, buttonPhoneLink, buttonInformation, buttonShare, buttonAttention, buttonLogout;

    public MainFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        buttonConference = (ImageButton) view.findViewById(R.id.button_main_conference);
        buttonVideos = (ImageButton) view.findViewById(R.id.button_main_videos);
        buttonProgress = (ImageButton) view.findViewById(R.id.button_main_progress);
        buttonTest = (ImageButton) view.findViewById(R.id.button_main_test);

        buttonFacebookLink = (ImageButton) view.findViewById(R.id.button_facebook_link);
        buttonMaillink = (ImageButton) view.findViewById(R.id.button_email_link);
        buttonTweeterLink = (ImageButton) view.findViewById(R.id.button_tweeter_link);
        buttonWebLink = (ImageButton) view.findViewById(R.id.button_web_link);
        buttonPhoneLink = (ImageButton) view.findViewById(R.id.button_phone_link);
        buttonShare = (ImageButton) view.findViewById(R.id.button_share_main);
        buttonInformation = (ImageButton) view.findViewById(R.id.button_information_main);
        buttonAttention = (ImageButton) view.findViewById(R.id.button_attention_main);
        buttonLogout = (ImageButton) view.findViewById(R.id.button_logout_main);

        buttonConference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), ConferenceFragment.class);
            }
        });

        buttonVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), VideoFragment.class);
            }
        });

        buttonProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder
                        .setMessage("Seguro de cerrar sesión?")
                        .setTitle("SALIR")
                        .setIcon(R.drawable.attention_red)
                        .setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                showLogin();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();

                dialog.show();
            }
        });

        buttonAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder
                        .setMessage("Todavía no hemos podido verificar tu celular, si está mal escrito avisanos para corregirlo.")
                        .setTitle("VERIFICACIÓN")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                AlertDialog dialog = builder.create();

                dialog.show();
            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog();
            }
        });

        buttonMaillink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notify.email(getContext());
            }
        });

        checkReceive();

        checkPhone();

        return view;
    }

    private void showLogin() {
        Intent myIntent = new Intent(getActivity(), LoginActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.startActivity(myIntent);
        getActivity().finish();
    }

    private void checkReceive() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.RECEIVE_SMS)) {
                //THE USER NOT ACCEPT READ SMS

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }
    }

    private void shareDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder
                .setView(R.layout.dialog_share)
                .setNeutralButton("ENVIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog d = (Dialog) dialog;
                        EditText editText = (EditText) d.findViewById(R.id.edit_text_dialog_share);
                        if (editText.getText().toString().length() > 5) {
                            String message = "Conciencia de abundancia:\n" +
                                    editText.getText().toString() +
                                    "\n\nDescargá la App: \n\nhttps://play.google.com/store/apps/details?id=com.bnvlab.concienciadeabundancia";

                            Notify.share(message, getContext());
                            dialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Escibe tu experiencia!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Dialog dialog = builder.create();

        dialog.show();
    }

    private void checkPhone(){
        //String phone = MainActivity.user.getPhone();

        FirebaseUser userFB = FirebaseAuth.getInstance().getCurrentUser();
        final String phone = userFB.getEmail().split("@")[0];

        FirebaseDatabase.getInstance()
                .getReference(MainActivity.REFERENCE)
                .child("users")
                .child(phone)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            MainActivity.user = user;
                            buttonAttention.setVisibility(user.isVerified()?
                                    View.GONE
                                    :View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    /*protected void sendSMSMessage() {

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.SEND_SMS)) {
                Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
            sendSMS();
        }

        Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
    }*/

/*    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS();
                } else {
                    Toast.makeText(getContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        Toast.makeText(getContext(), "requestcode: " + requestCode, Toast.LENGTH_SHORT).show();

    }*/

/*    private void sendSMS() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("", null, "mensaje de prueba", null, null);
        Toast.makeText(getContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();
    }*/

}
