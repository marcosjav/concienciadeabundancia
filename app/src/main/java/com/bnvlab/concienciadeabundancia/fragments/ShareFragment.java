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
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.Config;
import com.bnvlab.concienciadeabundancia.auxiliaries.Notify;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.VideosURL;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

/**
 * Created by Marcos on 18/04/2017.
 */

public class ShareFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
    String video;
    TextView textViewDescription;
//    private Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_share, container, false);
//        thumbnailViewToLoaderMap = new HashMap<>();
//        final ThumbnailListener thumbnailListener = new ThumbnailListener();

        view.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog();
            }
        });

        view.findViewById(R.id.new_icon_back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });

//        YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
//        thumbnail.setTag("juHUOeNT8n0");
//        thumbnail.initialize(Config.YOUTUBE_API_KEY, thumbnailListener);

        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) this.getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(Config.YOUTUBE_API_KEY, this);

        textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
        textViewDescription.setText(MainActivity.appText.getShareDescription().replace("&","\n"));

        return view;
    }

    private void shareDialog() {
        final String deepLink = "https://zw2gk.app.goo.gl/?link=http://cdainter.com/signup.php?code=" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        "&apn=com.bnvlab.concienciadeabundancia";

        String message = MainActivity.appText.getSms().replace("&","\n");

        message += deepLink;

        Notify.share(message, getContext());
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            String video = "";
            try {
                video = MainActivity.videosURL.getShare();
            }catch (Exception e) {
                SharedPreferences prefs = getActivity().getSharedPreferences(
                        MainActivity.APP_SHARED_PREF_KEY + MainActivity.user.getuId(), Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String vjson = prefs.getString(References.SHARED_PREFERENCES_APP_VIDEOS_URL, "");
                VideosURL v = gson.fromJson(vjson, VideosURL.class);
                video = v.getShare();
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
        Log.d(References.ERROR_LOG, "ShareFragment - YOUTUBE ERROR:\n" + errorMessage);
    }

//    private final class ThumbnailListener implements
//            YouTubeThumbnailView.OnInitializedListener,
//            YouTubeThumbnailLoader.OnThumbnailLoadedListener {
//
//        @Override
//        public void onInitializationSuccess(
//                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
//            loader.setOnThumbnailLoadedListener(this);
//            thumbnailViewToLoaderMap.put(view, loader);
//            view.setImageResource(R.drawable.progress_animation);
//            String videoId = (String) view.getTag();
//            loader.setVideo(videoId);
//        }
//
//        @Override
//        public void onInitializationFailure(
//                YouTubeThumbnailView view, YouTubeInitializationResult loader) {
//            view.setImageResource(R.drawable.no_thumbnail);
//        }
//
//        @Override
//        public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
//        }
//
//        @Override
//        public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
//            youTubeThumbnailView.setImageResource(R.drawable.no_thumbnail);
//        }
//
//    }

}

