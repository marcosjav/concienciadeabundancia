package com.bnvlab.concienciadeabundancia.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.Quiz;
import com.bnvlab.concienciadeabundancia.R;

/**
 * Created by bort0 on 22/03/2017.
 */

public class QuizAdapter extends ArrayAdapter {

    Context context;
    int layoutResourceId;
    Quiz quiz[] = null;

    public QuizAdapter(Context context, int layoutResourceId, Quiz[] quiz) {
        super(context, layoutResourceId, quiz);

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.quiz = quiz;
    }

    public View getView(int posicion, View convertView, ViewGroup parent){
        View row = convertView;
        QuizHolder holder = null;
        if(row==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new QuizHolder();
            holder.answer = (Switch) row.findViewById(R.id.switch_quiz);
            holder.quiz = (TextView) row.findViewById(R.id.row_text);
            row.setTag(holder);
        }
        else
            holder = (QuizHolder) row.getTag();

        Quiz item = quiz[posicion];
        holder.quiz.setText(item.getQuiz());
        holder.answer.setChecked(item.getAnswer());

        return row;
    }

    static class QuizHolder {
        private TextView quiz;
        private Switch answer;
    }
}
