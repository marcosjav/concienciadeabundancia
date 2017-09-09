package com.bnvlab.concienciadeabundancia.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Marcos on 07/06/2017.
 */

public class PayFragment extends Fragment {
    //Animation bounce;
    String creditUrl, cashUrl, transferUrl;
    View progressBar, payLayout, iUnavaliable;
    Button buttonTransfer, buttonCredit, buttonCash, buttonReport;

    @Override
    public void onStart() {
        super.onStart();
        /*bounce = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator();

        bounce.setInterpolator(interpolator);*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_pay,container,false);

        progressBar = view.findViewById(R.id.layout_progress);
        payLayout = view.findViewById(R.id.layout_pay);
        iUnavaliable = view.findViewById(R.id.image_unavaliable);
        //showProgress(true);

        buttonTransfer = (Button) view.findViewById(R.id.button_pay_bank);
        buttonCredit = (Button) view.findViewById(R.id.button_pay_credit);
        buttonCash = (Button) view.findViewById(R.id.button_pay_cash);
        buttonReport = (Button) view.findViewById(R.id.button_pay_report);

        Button buttonRecomendations = (Button) view.findViewById(R.id.button_pay_recomendations);
        ImageButton back = (ImageButton) view.findViewById(R.id.new_icon_back);
//        Button report = (Button) view.findViewById(R.id.button_report);
//        ((TextView)view.findViewById(R.id.text)).setTypeface(Utils.getTypeface(getContext()));

//        report.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.startAnimation(bounce);
//                showReport();
//            }
//        });
//        report.setTypeface(Utils.getTypeface(getContext()));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(bounce);
                getActivity().onBackPressed();
            }
        });
//        back.setTypeface(Utils.getTypeface(getContext()));

        buttonTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(bounce);
                Uri uri = Uri.parse("https://www.mercadopago.com/mla/checkout/start?pref_id=257115493-c3c511ef-9a0c-4c52-82fc-126e888adfbd");//"https://www.mercadopago.com/mla/checkout/start?pref_id=257115493-c3c511ef-9a0c-4c52-82fc-126e888adfbd"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
//        buttonTransfer.setTypeface(Utils.getTypeface(getContext()));

        buttonCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(bounce);
                /*Intent intent = new Intent(getActivity(), PayActivity.class);
                startActivity(intent);*/

                Uri uri = Uri.parse("https://www.mercadopago.com/mla/checkout/start?pref_id=257115493-2df0c656-1c75-45ac-a500-74bc8708ceda"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
//        buttonCredit.setTypeface(Utils.getTypeface(getContext()));

        buttonCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(bounce);
                //Uri uri = Uri.parse(cashUrl);//
                Uri uri = Uri.parse( "http://mpago.la/vyfh"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
//        buttonCash.setTypeface(Utils.getTypeface(getContext()));

        buttonRecomendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(bounce);
                Uri uri = Uri.parse("https://www.mercadopago.com/mla/checkout/start?pref_id=257115493-62f04065-afa3-44a8-a355-f6dbd0804437"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
//        buttonRecomendations.setTypeface(Utils.getTypeface(getContext()));

        buttonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReport();
            }
        });
        //getPayUrl();

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
}
