package com.bnvlab.concienciadeabundancia.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.adapters.MessageAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
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
    Animation bounce;

    @Override
    public void onStart() {
        super.onStart();
        bounce = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator();

        bounce.setInterpolator(interpolator);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        TextView title = (TextView) view.findViewById(R.id.textView);
        title.setTypeface(Utils.getTypeface(getContext()));

        this.inflater = inflater;
        epView = (ExpandableListView) view.findViewById(R.id.expandable_list_faq);
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);
        list = new ArrayList<>();

        getNotifications();

        ((TextView)view.findViewById(R.id.text_back)).setTypeface(Utils.getTypeface(getContext()));
        view.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(bounce);
                getActivity().onBackPressed();
            }
        });

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
