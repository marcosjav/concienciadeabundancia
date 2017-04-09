package com.bnvlab.concienciadeabundancia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.clases.TrainingItem;

import java.util.List;

public class TrainingAdapter extends ArrayAdapter<TrainingItem> {

    public TrainingAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public TrainingAdapter(Context context, int resource, List<TrainingItem> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_training_row, null);
        }

        TrainingItem p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.text_view_training_row_title);
            TextView tt2 = (TextView) v.findViewById(R.id.text_view_training_row_state);
            ImageView iv = (ImageView) v.findViewById(R.id.image_view_training_row);

            if (tt1 != null) {
                tt1.setText(p.getTitle());
            }

            if (tt2 != null) {
                tt2.setText(p.isComplete()? "Enviado" : "Pendiente");
            }

            if (iv != null) {
                iv.setImageResource(p.isComplete()? R.drawable.complete : R.drawable.incomplete);
            }
        }

        return v;
    }


}