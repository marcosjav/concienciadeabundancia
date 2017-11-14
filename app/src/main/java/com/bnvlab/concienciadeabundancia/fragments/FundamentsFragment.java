package com.bnvlab.concienciadeabundancia.fragments;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Marcos on 18/04/2017.
 */

public class FundamentsFragment extends Fragment {

    TextView textView;
    ViewSwitcher viewSwitcher ;
//    Animation bounce;
    @Override
    public void onStart() {
        super.onStart();
        /*bounce = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator();

        bounce.setInterpolator(interpolator);*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_fundaments, container, false);

        final float scale = 1f;//0.8f;

        final SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)view.findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.evolution));
        imageView.setScaleAndCenter(scale,new PointF(0,0));
        imageView.setMinScale(scale);
        imageView.setMaxScale(scale);
        imageView.setDoubleTapZoomScale(scale);

        view.findViewById(R.id.new_icon_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(bounce);
                getActivity().onBackPressed();
            }
        });
        return view;
    }

    private void getFundaments(){
        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.FUNDAMENTS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String text = dataSnapshot.getValue(String.class);
                        textView.setText(Utils.fromHtml(text));
                        viewSwitcher.showNext();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        viewSwitcher.showNext();
                        Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}
