package com.bnvlab.concienciadeabundancia.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.ImageHelper;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;

/**
 * Created by Marcos on 15/07/2017.
 */

public class MessageSlidePageFragment extends Fragment {
    private String msg, title;
    TextView tvMessage, tvTitle;
    Animation bounce;

    @Override
    public void onStart() {
        super.onStart();
        bounce = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator();

        bounce.setInterpolator(interpolator);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String[] data = getArguments().getString("data").split(";");
            msg = data[1];
            title = data[0];
        }catch (Exception e){
            Log.e("ERRORR", e.getMessage());
            e.printStackTrace();
            getActivity().onBackPressed();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(
                R.layout.fragment_message_slide_page, container, false);

        tvMessage = (TextView) view.findViewById(R.id.message);
        tvTitle = (TextView) view.findViewById(R.id.title);
        View layout = view.findViewById(R.id.content);

        Bitmap back = ImageHelper.getRoundedCornerBitmap(
                ((BitmapDrawable)getResources().getDrawable(R.drawable.fodo10).getCurrent()).getBitmap(),
                30);
        Drawable d = new BitmapDrawable(getResources(), back);

        layout.setBackground(d);

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        tvMessage.setText(msg);
        tvTitle.setText(title);
    }
}
