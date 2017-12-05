package com.bnvlab.concienciadeabundancia.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.adapters.MessageAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.MessageItem;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marcos on 16/07/2017.
 */

public class NotificationsFragment extends Fragment {

    ExpandableListView epView;
    ArrayList<MessageItem> list;
    ExpandableListAdapter mAdapter;
    LayoutInflater inflater;
    ViewSwitcher viewSwitcher;
//    Animation bounce;

    @Override
    public void onStart() {
        super.onStart();
        /*bounce = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator();

        bounce.setInterpolator(interpolator);*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_notifications, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(
                MainActivity.APP_SHARED_PREF_KEY + MainActivity.user.getuId(), Context.MODE_PRIVATE);

        this.inflater = inflater;
        epView = (ExpandableListView) view.findViewById(R.id.expandable_list_faq);
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);
        list = new ArrayList<>();

        getNotifications();

        view.findViewById(R.id.new_icon_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(bounce);
                getActivity().onBackPressed();
            }
        });

        prefs.edit().putBoolean(References.NOTIFICATION_NEW_MESSAGES, false).apply();
        return view;
    }

    private void getNotifications(){
        list = new ArrayList<>();
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(),
                MODE_PRIVATE);

        try {
            HashSet<String> notifications = (HashSet<String>) prefs.getStringSet("notifications", new HashSet<String>());

            for (String n : notifications) {
                JSONObject object = new JSONObject(n);
                list.add(new MessageItem(object.getString("title"), object.getString("message"), object.getLong("time")));
            }

            Collections.sort(list,new MessageComparator());

            mAdapter = new MessageAdapter(getContext(), inflater, list);
            epView.setAdapter(mAdapter);

        }catch (Exception e){
            Log.e("ERRORR", e.getMessage());
            e.printStackTrace();
        }
        viewSwitcher.showNext();
    }

    private class MessageComparator implements Comparator<MessageItem>{

        @Override
        public int compare(MessageItem o1, MessageItem o2) {
            return (int)(o2.getTime() - o1.getTime());
        }
    }
}
