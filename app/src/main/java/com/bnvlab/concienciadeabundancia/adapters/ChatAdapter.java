package com.bnvlab.concienciadeabundancia.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.clases.ChatMessageItem;

import java.util.ArrayList;

/**
 * Created by Marcos on 07/11/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
        private ArrayList<ChatMessageItem> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTextView;
            public ViewHolder(TextView v) {
                super(v);
                mTextView = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ChatAdapter(ArrayList<ChatMessageItem> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.new_chat_bubble_ask, parent, false);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder((TextView) v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            ChatMessageItem messageItem = mDataset.get(position);
            if (messageItem.isAnswer())
                holder.mTextView.setBackgroundResource(R.drawable.new_chat_bubble_answer);
            else
                holder.mTextView.setBackgroundResource(R.drawable.new_chat_bubble_ask);

            holder.mTextView.setText(messageItem.getText());

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

}
