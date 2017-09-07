package com.bnvlab.concienciadeabundancia.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.clases.ZoomOutPageTransformer;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import github.chenupt.multiplemodel.viewpager.ModelPagerAdapter;
import github.chenupt.multiplemodel.viewpager.PagerModelManager;
import github.chenupt.springindicator.SpringIndicator;
import github.chenupt.springindicator.viewpager.ScrollerViewPager;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marcos on 15/07/2017.
 */

public class MessageFragment extends Fragment {
    private ScrollerViewPager mPager;
    SharedPreferences prefs;
    private List<String> titles = new ArrayList<>();
    private List<String> msgs = new ArrayList<>();
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
        View view = inflater.inflate(R.layout.message_slide, container, false);

        prefs = getActivity().getSharedPreferences(MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(),
                MODE_PRIVATE);

        try {
            HashSet<String> notifications = (HashSet<String>) prefs.getStringSet("notifications", new HashSet<String>());
            HashSet<String> newNotifications = new HashSet<>();

            int i = 1;

            for (String n : notifications) {
                JSONObject object = new JSONObject(n);

                if (!object.getBoolean("read")){
                    titles.add( i++ + "" );
                    msgs.add( object.getString("title") + ";" + object.getString("message") );
                    object.put("read", true);
                }
                newNotifications.add(object.toString());
            }

            prefs.edit().putStringSet("notifications", newNotifications).apply();

        }catch (Exception e){
            Log.e("ERRORR", e.getMessage());
            e.printStackTrace();
            titles.add("1");
            msgs.add("Ups!;Sin mensajes nuevos");
        }

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ScrollerViewPager) view.findViewById(R.id.pager);
        SpringIndicator springIndicator = (SpringIndicator) view.findViewById(R.id.indicator);

        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        PagerModelManager manager = new PagerModelManager();
        manager.addCommonFragment(MessageSlidePageFragment.class, msgs, titles);

        ModelPagerAdapter adapter = new ModelPagerAdapter(getActivity().getSupportFragmentManager(), manager);
        mPager.setAdapter(adapter);
        mPager.fixScrollSpeed();

        springIndicator.setViewPager(mPager);

        return view;
    }

}
