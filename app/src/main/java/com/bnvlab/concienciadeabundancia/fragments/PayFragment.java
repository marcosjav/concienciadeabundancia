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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Marcos on 07/06/2017.
 */

public class PayFragment extends Fragment {
    Animation bounce;
    @Override
    public void onStart() {
        super.onStart();
        bounce = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator();

        bounce.setInterpolator(interpolator);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_subscribe,container,false);

        Button buttonMonth = (Button) view.findViewById(R.id.button_month);
        Button buttonDebit = (Button) view.findViewById(R.id.button_debit);
        Button buttonYear = (Button) view.findViewById(R.id.button_year);
        Button buttonComplete = (Button) view.findViewById(R.id.button_complete);
        Button back = (Button) view.findViewById(R.id.button_back);
        Button report = (Button) view.findViewById(R.id.button_report);
        ((TextView)view.findViewById(R.id.text)).setTypeface(Utils.getTypeface(getContext()));

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(bounce);
                showReport();
            }
        });
        report.setTypeface(Utils.getTypeface(getContext()));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(bounce);
                getActivity().onBackPressed();
            }
        });
        back.setTypeface(Utils.getTypeface(getContext()));

        buttonMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(bounce);
                Uri uri = Uri.parse("https://www.mercadopago.com/mla/checkout/start?pref_id=257115493-c3c511ef-9a0c-4c52-82fc-126e888adfbd"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        buttonMonth.setTypeface(Utils.getTypeface(getContext()));

        buttonDebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(bounce);
                Uri uri = Uri.parse("https://www.mercadopago.com/mla/checkout/start?pref_id=257115493-2df0c656-1c75-45ac-a500-74bc8708ceda"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        buttonDebit.setTypeface(Utils.getTypeface(getContext()));

        buttonYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(bounce);
                Uri uri = Uri.parse("http://mpago.la/vyfh"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        buttonYear.setTypeface(Utils.getTypeface(getContext()));

        buttonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(bounce);
                Uri uri = Uri.parse("https://www.mercadopago.com/mla/checkout/start?pref_id=257115493-62f04065-afa3-44a8-a355-f6dbd0804437"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        buttonComplete.setTypeface(Utils.getTypeface(getContext()));

        return view;
    }

    private void showReport(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.dialog_report,null);

        final EditText code = (EditText) view.findViewById(R.id.edit_code);
        ((TextView)view.findViewById(R.id.text)).setTypeface(Utils.getTypeface(getContext()));
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
}
