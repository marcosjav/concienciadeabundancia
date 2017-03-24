package com.bnvlab.concienciadeabundancia.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.adapters.ConferenceAdapter;
import com.bnvlab.concienciadeabundancia.clases.ConferenceItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Marcos on 24/03/2017.
 */

public class ConferenceFragment extends Fragment {
    ArrayList<ConferenceItem> list;
    ConferenceAdapter adapter;
    ProgressBar  progressBar;
    View view;

    public ConferenceFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conference,container, false);
        
        list = new ArrayList<>();
        adapter = new ConferenceAdapter(getContext(), R.layout.item_congress_row, list);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_conference_fragment);

        ListView listView = (ListView) view.findViewById(R.id.list_view_fragment_congress);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setNeutralButton("Ver ubicación", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=-27.437141,-58.980994(Domo del centenario)");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        getContext().startActivity(intent);
                    }
                });

                ConferenceItem item = list.get(position);

                builder.setTitle( item.getPlace())
                        .setMessage( item.getInfo() )
                        .setIcon(R.mipmap.ic_app_round);

                AlertDialog dialog = builder.create();

                dialog.show();

            }
        });

        getConfereces();

        this.view = view;
        return view;
    }

    private void getConfereces(){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance()
                .getReference(MainActivity.REFERENCE)
                .child("conferences")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data:
                             dataSnapshot.getChildren()) {

                            ConferenceItem item = new ConferenceItem();

                            item.setDate( (String)data.child("date").getValue() );
                            item.setDuration( (String)data.child("duration").getValue() );
                            item.setGps( (String)data.child("gps").getValue() );
                            item.setInfo( (String)data.child("info").getValue() );
                            item.setLocation( (String)data.child("location").getValue() );
                            item.setPlace( (String)data.child("place").getValue() );


                            list.add(item);
                            adapter.notifyDataSetChanged();
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
