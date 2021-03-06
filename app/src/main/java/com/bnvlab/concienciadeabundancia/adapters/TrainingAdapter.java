package com.bnvlab.concienciadeabundancia.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.clases.TrainingItem;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class TrainingAdapter extends ArrayAdapter<TrainingItem> {
    static SharedPreferences prefs;

    public TrainingAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        prefs = context.getSharedPreferences(
                MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(), Context.MODE_PRIVATE);
    }

    public TrainingAdapter(Context context, int resource, List<TrainingItem> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (prefs == null)
            prefs =  getContext().getSharedPreferences(
                    MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(), Context.MODE_PRIVATE);

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_training_row, null);
        }

        TrainingItem p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.text_view_training_row_title);

            TextView tt2 = (TextView) v.findViewById(R.id.text_view_training_row_state);

            ImageView imageView = (ImageView) v.findViewById(R.id.image_view_training_row_state);

            if (tt1 != null) {
                tt1.setText(p.getTitle());
            }

            if (tt2 != null) {
                if (p.isFinished()) {
                    tt2.setText("Finalizado");
                    imageView.setImageResource(R.drawable.new_icon_finished);
                }
                else {
                    boolean active = MainActivity.user.isActive();
                    tt2.setText(p.isComplete() ? "Enviado" : "Pendiente");
                    if (active || p.isFree()) {
                        imageView.setImageResource(p.isComplete() ? R.drawable.new_icon_sent : R.drawable.new_icon_available);
                    }else{
                        imageView.setImageResource(R.drawable.new_icon_padlock);
                    }

                }
            }

        }

        return v;
    }


}