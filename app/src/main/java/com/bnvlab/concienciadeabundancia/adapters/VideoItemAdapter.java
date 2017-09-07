package com.bnvlab.concienciadeabundancia.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.clases.VideoItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Marcos on 24/03/2017.
 */

public class VideoItemAdapter extends BaseAdapter {
    Context context;

    static class ViewHolder {
        TextView textViewTitle;
        ImageView imageViewTitle;
        ImageView imageViewThumbnail;
        TextView url;
    }

    private static final String TAG = "CustomAdapter";
    private static int convertViewCounter = 0;

    private ArrayList<VideoItem> data;
    private LayoutInflater inflater = null;

    public VideoItemAdapter(Context c, ArrayList<VideoItem> d) {
        Log.v(TAG, "Constructing CustomAdapter");

        this.data = d;
        inflater = LayoutInflater.from(c);
        this.context = c;
    }

    @Override
    public int getCount() {
        Log.v(TAG, "in getCount()");
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        Log.v(TAG, "in getItem() for position " + position);
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.v(TAG, "in getItemId() for position " + position);
        return position;
    }

    @Override
    public int getViewTypeCount() {
        Log.v(TAG, "in getViewTypeCount()");
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        Log.v(TAG, "in getItemViewType() for position " + position);
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        Log.v(TAG, "in getView for position " + position + ", convertView is "
                + ((convertView == null) ? "null" : "being recycled"));

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.new_item_video_row, parent, false);

            convertViewCounter++;
            Log.v(TAG, convertViewCounter + " convertViews have been created");

            holder = new ViewHolder();

            holder.textViewTitle = (TextView) convertView
                    .findViewById(R.id.text_view_video_item_title);
            holder.textViewTitle.setText(data.get(position).getUrl());
            holder.imageViewThumbnail = (ImageView) convertView
                    .findViewById(R.id.image_view_video_item_thumbnail);
            holder.imageViewTitle = (ImageView) convertView
                    .findViewById(R.id.image_view_video_item_title);
            holder.url = (TextView) convertView.findViewById(R.id.text_view_video_item_url);

            convertView.setTag(holder);

        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewTitle.setText(data.get(position).getTitle());
//        holder.textViewTitle.setTypeface(Utils.getTypeface(context));
//        holder.imageViewThumbnail.setImageBitmap(data.get(position).getThumbnail());
        String thumbnailURL = data.get(position).getThumbnail();
        if (!thumbnailURL.isEmpty() && !thumbnailURL.equals("")) {
            Picasso.with(context).load(thumbnailURL).placeholder(R.drawable.progress_animation).into(holder.imageViewThumbnail);
        }
        holder.url.setText(data.get(position).getUrl());

        return convertView;
    }

}
