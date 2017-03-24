package com.bnvlab.concienciadeabundancia.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.clases.ConferenceItem;

import java.util.ArrayList;

/**
 * Created by Marcos on 24/03/2017.
 */

public class ConferenceAdapter extends BaseAdapter {
    Context context;
    ArrayList<ConferenceItem> list;

    public ConferenceAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<ConferenceItem> list) {
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
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ConferenceHolder holder = null;

        if(convertView == null) {
            view = ((Activity) context).getLayoutInflater().inflate(R.layout.item_congress_row, parent, false);

            holder = new ConferenceHolder();
            holder.textViewPlace = (TextView) view.findViewById(R.id.text_view_conference_item_place);
            holder.textViewLocation = (TextView) view.findViewById(R.id.text_view_conference_item_location);
            holder.textViewDate = (TextView) view.findViewById(R.id.text_view_conference_item_day);
            holder.textViewTime = (TextView) view.findViewById(R.id.text_view_conference_item_hour);

            view.setTag(holder);
        }
        else
            holder = (ConferenceHolder) view.getTag();

        ConferenceItem item = list.get(position);
        String[] date = item.getDate().split(" ");

        holder.textViewPlace.setText(item.getPlace());
        holder.textViewLocation.setText(item.getLocation());
        holder.textViewDate.setText(date[1]);
        holder.textViewTime.setText(date[0]);

        return view;
    }

    class ConferenceHolder {
        TextView textViewPlace, textViewLocation, textViewDate, textViewTime;
    }
}


