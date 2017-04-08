package com.bnvlab.concienciadeabundancia.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.clases.QuizListItem;

import java.util.ArrayList;

/**
 * Created by Marcos on 08/04/2017.
 */

public class QuizListAdapter extends BaseAdapter {
    Context context;
    ArrayList<QuizListItem> list;

    public QuizListAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<QuizListItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        QuizListHolder holder = null;

        if (convertView == null) {
            view = ((Activity) context).getLayoutInflater().inflate(R.layout.item_quiz_list, parent, false);

            holder = new QuizListHolder();
            holder.title = (TextView) view.findViewById(R.id.text_view_quiz_list_title);
            holder.state = (TextView) view.findViewById(R.id.text_view_quiz_list_state);
            holder.image = (ImageView) view.findViewById(R.id.image_view_quiz_list);

            view.setTag(holder);
        } else
            holder = (QuizListHolder) view.getTag();

        QuizListItem item = list.get(position);

        holder.title.setText(item.getTitle());
        holder.state.setText(item.isSent() ? "ENVIADO" : "INCOMPLETO");
        holder.image.setImageResource(item.isSent()? R.drawable.complete : R.drawable.incomplete);

        return view;
    }

    private class QuizListHolder {
        TextView title;
        TextView state;
        ImageView image;
    }
}
