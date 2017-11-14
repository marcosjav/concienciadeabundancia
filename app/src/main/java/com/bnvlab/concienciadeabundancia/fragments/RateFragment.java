package com.bnvlab.concienciadeabundancia.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.bnvlab.concienciadeabundancia.FragmentMan;
import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Marcos on 18/04/2017.
 */

public class RateFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_rate, container, false);

        final SharedPreferences preferences = getActivity().getSharedPreferences(
                MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(), Context.MODE_PRIVATE);

        view.findViewById(R.id.button_ok)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preferences.edit().putBoolean(References.SHARED_PREFERENCES_DONT_ASK_RATE, true).apply();
                        launchMarket();
                        rated(true);
                        onDestroy();
                    }
                });

        view.findViewById(R.id.chekbox_dont_ask)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        preferences.edit().putBoolean(References.SHARED_PREFERENCES_DONT_ASK_RATE, checkBox.isChecked()).apply();
                        rated(checkBox.isChecked());
//                        Log.d("DEBUG-RATE", checkBox.isChecked() + "");
                    }
                });

        return view;
    }

    private void rated(boolean isRated){
        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.RATE_APP)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(isRated);
    }

    private void launchMarket() {
//        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
//        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
//        try {
//            startActivity(myAppLinkToMarket);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(getActivity(), " unable to find market app", Toast.LENGTH_LONG).show();
//        }
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }

    public static void rateApp(FragmentActivity activity){
        SharedPreferences preferences = activity.getSharedPreferences(
                MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(), Context.MODE_PRIVATE);

        if (!preferences.getBoolean(References.SHARED_PREFERENCES_DONT_ASK_RATE, false)){
            FragmentMan.changeFragment(activity, RateFragment.class);
        }
    }
}
