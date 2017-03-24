package com.bnvlab.concienciadeabundancia.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.VideoActivity;
import com.bnvlab.concienciadeabundancia.adapters.VideoItemAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.SimpleYouTubeHelper;
import com.bnvlab.concienciadeabundancia.clases.VideoItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Marcos on 24/03/2017.
 */

public class VideoFragment extends Fragment {
    static VideoItemAdapter adapter;
    ArrayList<VideoItem> list = new ArrayList<>();
    //    ArrayList<String> urlList = new ArrayList<>();
//    ArrayAdapter adapter;
    ListView listView;
    Context context;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        context = getContext();

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_video_fragment);
        listView = (ListView) view.findViewById(R.id.list_view_fragment_videos);
        adapter = new VideoItemAdapter(context, list);
//        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,urlList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                VideoItem videoItem = list.get(position);

                Intent myIntent = new Intent(getContext(), VideoActivity.class);
                myIntent.putExtra("video", list.get(position).getUrl()); //Optional parameters
                myIntent.putExtra("list", list);
                getActivity().startActivity(myIntent);
            }
        });

        getVideoList();
        return view;
    }


    private void getVideoList() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance()
                .getReference(MainActivity.REFERENCE)
                .child("videos")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data :
                                dataSnapshot.getChildren()) {

//                            urlList.add((String)data.getValue());

                            AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {
                                VideoItem vi;
                                String url;
                                SimpleYouTubeHelper syh;

                                @Override
                                protected Void doInBackground(String... params) {
                                    vi = new VideoItem();
                                    url = params[0];
                                    syh = new SimpleYouTubeHelper(url);

                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void s) {
                                    vi.setTitle(syh.getTitleQuietly());
                                    vi.setThumbnail(syh.getThumbnailUrl());
                                    vi.setUrl(url);
                                    list.add(vi);

                                    refresh();
                                }
                            };

                            task.execute((String) data.getValue());


                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void refresh()
    {
        adapter.notifyDataSetChanged();
    }
}
