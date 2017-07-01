package com.bnvlab.concienciadeabundancia.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.clases.ConferenceItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
            holder.textViewDate = (TextView) view.findViewById(R.id.text_view_conference_item_date);
            holder.textViewTitle = (TextView) view.findViewById(R.id.text_view_conference_item_title);
            holder.logo = (ImageView) view.findViewById(R.id.logo);

            view.setTag(holder);
        }
        else
            holder = (ConferenceHolder) view.getTag();

        ConferenceItem item = list.get(position);

        try {
            String[] date = item.getDate().split(" ");
            holder.textViewDate.setText(date[1] + " - " + date[0] + " hs");
        }catch (Exception e){
            if (item.getDate().equals(""))
                holder.textViewDate.setVisibility(View.GONE);
            else
                holder.textViewDate.setText(item.getDate());
        }

        holder.textViewPlace.setText(item.getPlace());
        if (item.getPlace().equals(""))
            holder.textViewPlace.setVisibility(View.GONE);
        else
            holder.textViewPlace.setVisibility(View.VISIBLE);

        holder.textViewLocation.setText(item.getLocation());
        if (item.getLocation().equals(""))
            holder.textViewLocation.setVisibility(View.GONE);
        else
            holder.textViewLocation.setVisibility(View.VISIBLE);

        holder.textViewTitle.setText(item.getTitle());
        if (item.getTitle().equals(""))
            holder.textViewTitle.setVisibility(View.GONE);
        else
            holder.textViewTitle.setVisibility(View.VISIBLE);

        if (item.getImage().equals(""))
            holder.logo.setVisibility(View.GONE);
        else {
            holder.logo.setVisibility(View.VISIBLE);
            final ImageView imageLogo = holder.logo;
            StorageReference sr = FirebaseStorage.getInstance().getReference();
            sr.child(item.getImage()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        try {
                            int width = imageLogo.getWidth();
                            Picasso.with(context).load(task.getResult()).placeholder(R.drawable.progress_animation).resize(width, width).into(imageLogo);
                        }catch (Exception e){
                            Log.d("ERRORR", "ConferenceAdapter - line 118" + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    else{
                        Log.d("ERRORR", "ConferenceAdapter - line 118" + task.getException().getMessage());
                        task.getException().printStackTrace();
                    }
                }
            });
        }

        return view;
    }

    class ConferenceHolder {
        TextView textViewPlace, textViewLocation, textViewDate, textViewTitle;
        ImageView logo;
    }
}


