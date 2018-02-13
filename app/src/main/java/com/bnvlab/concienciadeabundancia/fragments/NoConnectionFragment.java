package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;

/**
 * Created by Marcos on 19/04/2017.
 */

public class NoConnectionFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_message, container, false);

        view.findViewById(R.id.button_negative).setVisibility(View.GONE);
        view.findViewById(R.id.button_neutral).setVisibility(View.GONE);

        Button buttonOk = (Button) view.findViewById(R.id.button_positive);
        buttonOk.setText(getString(R.string.message_no_connection_button));
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.exitApp();
            }
        });
        TextView textViewTitle = (TextView) view.findViewById(R.id.title);
        textViewTitle.setText(getString(R.string.message_no_connection_title));

        TextView textViewMessage = (TextView) view.findViewById(R.id.message);
        textViewMessage.setText(getString(R.string.message_no_connection_text));

        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        imageView.setImageResource(R.drawable.cloud_off);

        return view;
    }
}
