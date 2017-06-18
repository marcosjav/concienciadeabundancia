package com.bnvlab.concienciadeabundancia.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
import com.bnvlab.concienciadeabundancia.clases.QuizItem;

import java.util.ArrayList;

/**
 * Created by bort0 on 22/03/2017.
 */

public class QuizAdapter extends BaseAdapter {

    Context context;
    ArrayList<QuizItem> list;
    public boolean disable;

    public QuizAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<QuizItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        QuizHolder holder = null;

        if(convertView == null) {
            view = ((Activity) context).getLayoutInflater().inflate(R.layout.item_quiz_row, parent, false);

            holder = new QuizHolder();
            holder.quizText = (TextView) view.findViewById(R.id.quiz_item_string);
            holder.quizText.setTypeface(Utils.getTypeface(context));
            holder.quizSwitch = (SwitchCompat) view.findViewById(R.id.quiz_item_switch);

            holder.quizSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    list.get(position).setAnswer(isChecked);
                }
            });

            view.setTag(holder);
        }
        else
            holder = (QuizHolder) view.getTag();

        QuizItem item = list.get(position);

        holder.quizText.setText(item.getQuiz());
        holder.quizSwitch.setChecked(item.getAnswer());
        if (disable) {
            holder.quizSwitch.setClickable(false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Disponible para usuarios registrados\nContáctate con nosotros para saber más", Toast.LENGTH_LONG).show();
                }
            });
        }

        return view;
    }

    private class QuizHolder{
        TextView quizText;
        SwitchCompat quizSwitch;
    }
}
