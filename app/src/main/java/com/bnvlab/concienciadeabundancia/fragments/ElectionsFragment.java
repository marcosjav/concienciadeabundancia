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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.Config;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.VideosURL;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.Gson;

/**
 * Created by Marcos on 18/04/2017.
 */

public class ElectionsFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
    private Button buttonSend;
    private EditText election00, election01, election02, election03, election04, election05, election06, election07
            , election08, election09, election10, election11, election12, election13, election14, election15, election16, election17
            , election18, election19, election20, election21, election22, election23, election24, election25;
    private TextView textViewDescription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_elections, container, false);

        buttonSend = (Button) view.findViewById(R.id.button_send_elections);
        election00 = (EditText) view.findViewById(R.id.edit_text_election_01);
        election01 = (EditText) view.findViewById(R.id.edit_text_election_02);
        election02 = (EditText) view.findViewById(R.id.edit_text_election_03);
        election03 = (EditText) view.findViewById(R.id.edit_text_election_04);
        election04 = (EditText) view.findViewById(R.id.edit_text_election_05);
        election05 = (EditText) view.findViewById(R.id.edit_text_election_06);
        election06 = (EditText) view.findViewById(R.id.edit_text_election_07);
        election07 = (EditText) view.findViewById(R.id.edit_text_election_08);
        election08 = (EditText) view.findViewById(R.id.edit_text_election_09);
        election09 = (EditText) view.findViewById(R.id.edit_text_election_10);
        election10 = (EditText) view.findViewById(R.id.edit_text_election_11);
        election11 = (EditText) view.findViewById(R.id.edit_text_election_12);
        election12 = (EditText) view.findViewById(R.id.edit_text_election_13);
        election13 = (EditText) view.findViewById(R.id.edit_text_election_14);
        election14 = (EditText) view.findViewById(R.id.edit_text_election_15);
        election15 = (EditText) view.findViewById(R.id.edit_text_election_16);
        election16 = (EditText) view.findViewById(R.id.edit_text_election_17);
        election17 = (EditText) view.findViewById(R.id.edit_text_election_18);
        election18 = (EditText) view.findViewById(R.id.edit_text_election_19);
        election19 = (EditText) view.findViewById(R.id.edit_text_election_20);
        election20 = (EditText) view.findViewById(R.id.edit_text_election_20);
        election21 = (EditText) view.findViewById(R.id.edit_text_election_21);
        election22 = (EditText) view.findViewById(R.id.edit_text_election_22);
        election23 = (EditText) view.findViewById(R.id.edit_text_election_23);
        election24 = (EditText) view.findViewById(R.id.edit_text_election_24);
        election25 = (EditText) view.findViewById(R.id.edit_text_election_25);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                election00.setText("");
                election01.setText("");
                election02.setText("");
                election03.setText("");
                election04.setText("");
                election05.setText("");
                election06.setText("");
                election07.setText("");
                election08.setText("");
                election09.setText("");
                election10.setText("");
                election11.setText("");
                election12.setText("");
                election13.setText("");
                election14.setText("");
                election15.setText("");
                election16.setText("");
                election17.setText("");
                election18.setText("");
                election19.setText("");
                election20.setText("");
                election21.setText("");
                election22.setText("");
                election23.setText("");
                election24.setText("");
                election25.setText("");
            }
        });

        textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
        textViewDescription.setText(MainActivity.appText.getElectionsDescription());

        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) this.getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(Config.YOUTUBE_API_KEY, this);

        ImageButton back = (ImageButton) view.findViewById(R.id.new_icon_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            String video = "";
            try {
                video = MainActivity.videosURL.getElections();
            }catch (Exception e) {
                SharedPreferences prefs = getActivity().getSharedPreferences(
                        MainActivity.APP_SHARED_PREF_KEY + MainActivity.user.getuId(), Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String vjson = prefs.getString(References.SHARED_PREFERENCES_APP_VIDEOS_URL, "");
                VideosURL v = gson.fromJson(vjson, VideosURL.class);
                video = v.getElections();
            }

            youTubePlayer.cueVideo(video);
            youTubePlayer.setShowFullscreenButton(false);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        String errorMessage = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), 1).show();
        }
        Log.d(References.ERROR_LOG, "ElectionsFragment - YOUTUBE ERROR:\n" + errorMessage);
    }
}
