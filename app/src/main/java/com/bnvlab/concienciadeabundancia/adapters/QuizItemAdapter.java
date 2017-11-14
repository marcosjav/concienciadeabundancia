package com.bnvlab.concienciadeabundancia.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.clases.QuizItem;

import java.util.ArrayList;

/**
 * Created by Marcos on 07/11/2017.
 */

public class QuizItemAdapter extends RecyclerView.Adapter<QuizItemAdapter.ViewHolder> {
    private ArrayList<QuizItem> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public SwitchCompat mSwitchView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.quiz_item_string);
            mSwitchView = (SwitchCompat) v.findViewById(R.id.quiz_item_switch);
        }
    }

    public QuizItemAdapter(ArrayList<QuizItem> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public QuizItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final QuizItem quizItem = mDataset.get(position);

        holder.mSwitchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                quizItem.setAnswer(isChecked);
            }
        });

        holder.mTextView.setText(quizItem.getQuiz());
        holder.mSwitchView.setChecked(quizItem.getAnswer());

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
