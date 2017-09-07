package com.bnvlab.concienciadeabundancia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.clases.QuizItem;

import java.util.List;

public class ResumeAdapter extends ArrayAdapter<QuizItem> {

    public ResumeAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ResumeAdapter(Context context, int resource, List<QuizItem> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_resume_row, null);
        }

        QuizItem p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.text_view_resume_quiz);
//            tt1.setTypeface(Utils.getTypeface(getContext()));
            ImageView iv = (ImageView) v.findViewById(R.id.image_view_resume_check);

            if (tt1 != null) {
                tt1.setText(p.getQuiz());
            }

            if (iv != null) {
                iv.setImageResource(p.getAnswer()? R.drawable.new_icon_checked : R.drawable.new_icon_unchecked);
            }
        }

        return v;
    }


}