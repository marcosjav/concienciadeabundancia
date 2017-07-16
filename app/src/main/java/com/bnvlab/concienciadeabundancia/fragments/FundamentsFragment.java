package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
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
    Animation bounce;
    @Override
    public void onStart() {
        super.onStart();
        bounce = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator();

        bounce.setInterpolator(interpolator);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fundaments, container, false);

        TextView title = (TextView) view.findViewById(R.id.textView);
        title.setTypeface(Utils.getTypeface(getContext()));

        textView = (TextView) view.findViewById(R.id.text_view_fundaments);

        textView.setMovementMethod(LinkMovementMethod.getInstance());  // THIS ALLOW US TO OPEN HTML LINKS IN TEXT

        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);

        ((TextView)view.findViewById(R.id.text_back)).setTypeface(Utils.getTypeface(getContext()));
        view.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(bounce);
                getActivity().onBackPressed();
            }
        });
//
//        message.setText(Html.fromHtml("<font color='red'>I like bigknol</font><br />" +
//                "<strong>Strong Data</strong><br />" +
//                    "<tt>Moving</tt><br /> \n" +
//                "<big>Hello Iam Big</big> \\n\n" +
//                "        <small>Iam Small</small>  \\n\n" +
//                "        <b>Iam bold</b> \\n\n" +
//                "        <strike>Vanish 100$</strike>\\n\n" +
//                "        <a href=\"http://bigknol.com\">Goto Bigknol</a>\n" +
//                "        X<sub>2</sub>Y \\n\n" +
//                "        X<sup>30</sup>Z \\n\n" +
//                "        <u>Underline</u> \\n\n" +
//                "        <i>Hi Five </i> \\n\n" +
//                "        <tt>Hi Six</tt>\\n"));
        getFundaments();

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
