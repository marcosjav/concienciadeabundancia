package com.bnvlab.concienciadeabundancia.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.adapters.ConferenceAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.ConferenceItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Marcos on 24/03/2017.
 */

public class ConferenceFragment extends Fragment {
    private static final int REQUEST_WRITE_PERMISSION = 20;
    ArrayList<ConferenceItem> list;
    ConferenceAdapter adapter;
    View progressBar;
    View view;
    ListView listView;
    View iUnavaliable;
//    Animation bounce;
    @Override
    public void onStart() {
        super.onStart();
/*        bounce = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
//        MyBounceInterpolator interpolator = new MyBounceInterpolator();

        bounce.setInterpolator(interpolator);*/
    }

    public ConferenceFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_conference, container, false);

        list = new ArrayList<>();
        adapter = new ConferenceAdapter(getContext(), R.layout.item_congress_row, list);

        progressBar = view.findViewById(R.id.layout_progress);
        iUnavaliable = view.findViewById(R.id.image_unavaliable);

//        ((TextView)view.findViewById(R.id.text_back)).setTypeface(Utils.getTypeface(getContext()));
        view.findViewById(R.id.new_icon_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(bounce);
                getActivity().onBackPressed();
            }
        });

        listView = (ListView) view.findViewById(R.id.list_view_fragment_congress);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final ConferenceItem item = list.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setNeutralButton("Ver enlace", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button

//                        String uri = String.format(Locale.ENGLISH, "geo:0,0?q="
//                                + item.getGps()
//                                + "("
//                                + item.getPlace()
//                                + ")");
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                        getContext().startActivity(intent);
                        Uri uri = Uri.parse("https://www.facebook.com/cdainternacional"); // missing 'http://' will cause crashed
                        try {
                            int versionCode = getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                            boolean activated = getContext().getPackageManager().getApplicationInfo("com.facebook.katana", 0).enabled;
                            if (activated) {
                                if ((versionCode >= 3002850)) {
                                    uri = Uri.parse("fb://facewebmodal/f?href=" + "https://www.facebook.com/cdainternacional");
                                } else {
                                    uri = Uri.parse("fb://page/" + "cdainternacional");
                                }
                            }
                        } catch (PackageManager.NameNotFoundException ignored) {
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

                builder.setTitle(item.getPlace())
                        .setMessage(item.getInfo())
                        .setIcon(R.mipmap.ic_cda_icon);

                AlertDialog dialog = builder.create();

                dialog.show();

            }
        });

        getConfereces();

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_PERMISSION);

        this.view = view;
        return view;
    }

    private void getConfereces() {
        showProrgess(true);
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.CONFERENCES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isEmpty = true;
                        for (DataSnapshot data :
                                dataSnapshot.getChildren()) {

                            ConferenceItem item = new ConferenceItem();

                            if (data.child(References.CONFERENCES_CHILD_TITLE).getValue() != null)
                                item.setTitle(data.child(References.CONFERENCES_CHILD_TITLE).getValue(String.class));
                            if (data.child(References.CONFERENCES_CHILD_DATE).getValue() != null)
                                item.setDate(data.child(References.CONFERENCES_CHILD_DATE).getValue(String.class));
                            if (data.child(References.CONFERENCES_CHILD_DURATION).getValue() != null)
                                item.setDuration(data.child(References.CONFERENCES_CHILD_DURATION).getValue(String.class));
                            if (data.child(References.CONFERENCES_CHILD_GPS).getValue() != null)
                                item.setGps(data.child(References.CONFERENCES_CHILD_GPS).getValue(String.class));
                            if (data.child(References.CONFERENCES_CHILD_INFO).getValue() != null)
                                item.setInfo(data.child(References.CONFERENCES_CHILD_INFO).getValue(String.class));
                            if (data.child(References.CONFERENCES_CHILD_LOCATION).getValue() != null)
                                item.setLocation(data.child(References.CONFERENCES_CHILD_LOCATION).getValue(String.class));
                            if (data.child(References.CONFERENCES_CHILD_PLACE).getValue() != null)
                                item.setPlace(data.child(References.CONFERENCES_CHILD_PLACE).getValue(String.class));
                            if (data.child(References.CONFERENCES_CHILD_LOGO).getValue() != null)
                                item.setImage(data.child(References.CONFERENCES_CHILD_LOGO).getValue(String.class));

                            isEmpty = false;

                            list.add(item);
                            adapter.notifyDataSetChanged();
                        }
                        if (isEmpty) {
                            progressBar.setVisibility(View.GONE);
                            listView.setVisibility(View.GONE);
                            iUnavaliable.setVisibility(View.VISIBLE);
                        }else
                            showProrgess(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void showProrgess(boolean show){
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
        listView.setVisibility(show? View.GONE : View.VISIBLE);
        iUnavaliable.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
