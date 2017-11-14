package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.Config;
import com.bnvlab.concienciadeabundancia.auxiliaries.Notify;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcos on 18/04/2017.
 */

public class ShareFragment extends Fragment {
    String video;
    private Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_share, container, false);
        thumbnailViewToLoaderMap = new HashMap<>();
        final ThumbnailListener thumbnailListener = new ThumbnailListener();

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

        YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
        thumbnail.setTag("juHUOeNT8n0");
        thumbnail.initialize(Config.YOUTUBE_API_KEY, thumbnailListener);

        return view;
    }

    private void shareDialog() {
        final String deepLink = "https://zw2gk.app.goo.gl/?link=http://cdainter.com/signup.php?code=" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        "&apn=com.bnvlab.concienciadeabundancia";

        String message = "Hola, te invito a vivir una experiencia que va a potenciar tu vida.\n " +
                "En 30 minutos lograrás potenciar tu confianza, valoración, seguridad, alegría y " +
                "reducirás tu estrés diario, de forma inmediata y permanente." +
                "\n" +
                "\nInstala la app desde el link:\n\n" + deepLink;

        Notify.share(message, getContext());
    }

    private final class ThumbnailListener implements
            YouTubeThumbnailView.OnInitializedListener,
            YouTubeThumbnailLoader.OnThumbnailLoadedListener {

        @Override
        public void onInitializationSuccess(
                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            thumbnailViewToLoaderMap.put(view, loader);
            view.setImageResource(R.drawable.progress_animation);
            String videoId = (String) view.getTag();
            loader.setVideo(videoId);
        }

        @Override
        public void onInitializationFailure(
                YouTubeThumbnailView view, YouTubeInitializationResult loader) {
            view.setImageResource(R.drawable.no_thumbnail);
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
        }

        @Override
        public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
            youTubeThumbnailView.setImageResource(R.drawable.no_thumbnail);
        }

    }

}

