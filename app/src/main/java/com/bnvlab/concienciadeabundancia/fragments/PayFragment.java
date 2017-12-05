package com.bnvlab.concienciadeabundancia.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.PayActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.Config;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.VideosURL;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marcos on 07/06/2017.
 */

public class PayFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
    //Animation bounce;
    String creditUrl, cashUrl, transferUrl, location, locationKey;
    View progressBar, payLayout, iUnavaliable;
    Button buttonTransfer, buttonCredit, buttonCash, buttonReport;
    TextView textViewDesciption;
//    private static String PUBLIC_KEY = "APP_USR-dd544c46-10ce-4901-9804-1ae3f99aac2b";
    SharedPreferences prefs;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_pay,container,false);

        prefs = getActivity().getSharedPreferences(MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(),
                MODE_PRIVATE);

        location = MainActivity.user.getLocale();
        locationKey = prefs.getString(References.SHARED_PREFERENCES_USER_LOCATION_KEY,"");

        progressBar = view.findViewById(R.id.layout_progress);
        payLayout = view.findViewById(R.id.layout_pay);
        iUnavaliable = view.findViewById(R.id.image_unavaliable);
        //showProgress(true);

        buttonTransfer = (Button) view.findViewById(R.id.button_pay_bank);
        buttonCredit = (Button) view.findViewById(R.id.button_pay_credit);
        buttonCash = (Button) view.findViewById(R.id.button_pay_cash);
        buttonReport = (Button) view.findViewById(R.id.button_pay_report);

        textViewDesciption = (TextView) view.findViewById(R.id.textViewDescription);
        textViewDesciption.setText(MainActivity.appText.getPayDesciption());

        Button buttonRecomendations = (Button) view.findViewById(R.id.button_pay_recomendations);
        ImageButton back = (ImageButton) view.findViewById(R.id.new_icon_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        if(locationKey.equals(References.LOCATION_KEY) ||
                location.toLowerCase().equals(References.LOCATION_ARGENTINA.toLowerCase())) {
            buttonCredit.setText(getString(R.string.pay_credit_cash));
        }

        buttonTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMan.changeFragment(getActivity(), TransferFragment.class);
            }
        });

        buttonCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=S2UBEFZFGUJJ6&email=" + FirebaseAuth.getInstance().getCurrentUser().getEmail()); // TEST
                Log.i(References.ERROR_LOG, "key: "+ locationKey + "\nValue: " + location);
                if(locationKey.equals(References.LOCATION_KEY) ||
                        location.toLowerCase().equals(References.LOCATION_ARGENTINA.toLowerCase())) {
//                    try {
//                        Intent intent = new Intent(getActivity(), PayActivity.class);
//                        startActivity(intent);
//                    } catch (Exception e) {
                        uri = Uri.parse("https://www.mercadopago.com/mla/checkout/start?pref_id=257115493-2df0c656-1c75-45ac-a500-74bc8708ceda"); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
//                    }
                }else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        buttonCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PayActivity.class);
                startActivity(intent);
            }
        });

        buttonRecomendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.mercadopago.com/mla/checkout/start?pref_id=257115493-62f04065-afa3-44a8-a355-f6dbd0804437"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        buttonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReport();
            }
        });

        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) this.getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(Config.YOUTUBE_API_KEY, this);

        return view;
    }

    private void showReport(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.dialog_report,null);

        final EditText code = (EditText) view.findViewById(R.id.edit_code);
        builder.setTitle("Informar un pago")
                .setView(view)
                .setNegativeButton("Volver", null)
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                                .child(References.PAY)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .push()
                                .setValue(code.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                            Toast.makeText(getActivity(), "Se envió el informe de pago", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(getActivity(), "Hubo un problema. Intente más tarde por favor", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).create().show();
    }


    private void getPayUrl(){
        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(References.USERS_CHILD_LOCALE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){
                            String location = dataSnapshot.getValue(String.class);
                            FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                                    .child(References.PAY_URL)
                                    .child(location)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            cashUrl = dataSnapshot.hasChild(References.PAY_URL_CASH) ? dataSnapshot.getValue(String.class) : "";
                                            creditUrl = dataSnapshot.hasChild(References.PAY_URL_CREDIT)? dataSnapshot.getValue(String.class) : "";
                                            transferUrl = dataSnapshot.hasChild(References.PAY_URL_TRANSFER)? dataSnapshot.getValue(String.class) : "";

                                            buttonCash.setVisibility(cashUrl.equals("")? View.GONE : View.VISIBLE);
                                            buttonCredit.setVisibility(creditUrl.equals("")? View.GONE : View.VISIBLE);
                                            buttonTransfer.setVisibility(transferUrl.equals("")? View.GONE : View.VISIBLE);

                                            if (cashUrl.equals("") && creditUrl.equals("") && transferUrl.equals("")){
                                                FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                                                        .child(References.PAY_URL)
                                                        .child(References.PAY_URL_DEFAULTS)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                cashUrl = dataSnapshot.hasChild(References.PAY_URL_CASH) ? dataSnapshot.getValue(String.class) : "";
                                                                creditUrl = dataSnapshot.hasChild(References.PAY_URL_CREDIT)? dataSnapshot.getValue(String.class) : "";
                                                                transferUrl = dataSnapshot.hasChild(References.PAY_URL_TRANSFER)? dataSnapshot.getValue(String.class) : "";

                                                                buttonCash.setVisibility(cashUrl.equals("")? View.GONE : View.VISIBLE);
                                                                buttonCredit.setVisibility(creditUrl.equals("")? View.GONE : View.VISIBLE);
                                                                buttonTransfer.setVisibility(transferUrl.equals("")? View.GONE : View.VISIBLE);

                                                                buttonReport.setVisibility(View.VISIBLE);

                                                                if (cashUrl.equals("") && creditUrl.equals("") && transferUrl.equals("")) {
                                                                    progressBar.setVisibility(View.GONE);
                                                                    payLayout.setVisibility(View.GONE);
                                                                    iUnavaliable.setVisibility(View.VISIBLE);
                                                                }else{
                                                                    showProgress(false);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                progressBar.setVisibility(View.GONE);
                                                                payLayout.setVisibility(View.GONE);
                                                                iUnavaliable.setVisibility(View.VISIBLE);
                                                            }
                                                        });

                                            }else{
                                                showProgress(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            progressBar.setVisibility(View.GONE);
                                            payLayout.setVisibility(View.GONE);
                                            iUnavaliable.setVisibility(View.VISIBLE);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void showProgress(boolean show){
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
        payLayout.setVisibility(show? View.GONE : View.VISIBLE);
        iUnavaliable.setVisibility(View.GONE);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            String video = "";
            try {
                video = MainActivity.videosURL.getPay();
            }catch (Exception e) {
                SharedPreferences prefs = getActivity().getSharedPreferences(
                        MainActivity.APP_SHARED_PREF_KEY + MainActivity.user.getuId(), Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String vjson = prefs.getString(References.SHARED_PREFERENCES_APP_VIDEOS_URL, "");
                VideosURL v = gson.fromJson(vjson, VideosURL.class);
                video = v.getPay();
            }

            youTubePlayer.cueVideo(video);
            youTubePlayer.setShowFullscreenButton(false);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        String errorMessage = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), 1).show();
        }
        Log.d(References.ERROR_LOG, "PayFragment - YOUTUBE ERROR:\n" + errorMessage);
    }
}
