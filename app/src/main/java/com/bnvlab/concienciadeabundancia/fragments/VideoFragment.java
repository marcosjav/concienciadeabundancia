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
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.VideoActivity;
import com.bnvlab.concienciadeabundancia.adapters.VideoItemAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.auxiliaries.SimpleYouTubeHelper;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
import com.bnvlab.concienciadeabundancia.clases.VideoItem;
import com.google.firebase.auth.FirebaseAuth;
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
    static boolean active;
    View progress, layoutList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        context = getContext();

        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setTypeface(Utils.getTypeface(getContext()));

        TextView textViewTitle = (TextView) view.findViewById(R.id.textView_title);
        textViewTitle.setTypeface(Utils.getTypeface(getContext()));

        progress = view.findViewById(R.id.layout_progress);
        layoutList = view.findViewById(R.id.layout_list);

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

        isActive();
        return view;
    }

    private void isActive(){
        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(References.USERS_CHILD_ACTIVE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        active = dataSnapshot.getValue(boolean.class);
                        getVideoList();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        active = false;
                        getVideoList();
                    }
                });
    }

    private void getVideoList() {
        showProgress(true);

        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.VIDEOS)
                .orderByChild(References.VIDEOS_INDEX)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data :
                                dataSnapshot.getChildren()) {

                            if (!data.child(References.VIDEOS_CHILD_HIDDEN).getValue(boolean.class)
                                    && (active || data.child(References.FREE_CONTENT).getValue(boolean.class)))
                                (new VideoPreview()).execute(data.child(References.VIDEOS_CHILD_URL).getValue(String.class));

                        }
                        showProgress(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void refresh() {
        adapter.notifyDataSetChanged();
    }

    private class VideoPreview extends AsyncTask<String, Void, Void> {
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
    }

    private void showProgress(boolean show){
        progress.setVisibility(show?View.VISIBLE:View.GONE);
        layoutList.setVisibility(show?View.GONE:View.VISIBLE);
    }
}
