package com.bnvlab.concienciadeabundancia.adapters;

/**
 * Created by Marcos on 11/11/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.Config;
import com.bnvlab.concienciadeabundancia.clases.VideoEntry;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for the video list. Manages a set of YouTubeThumbnailViews, including initializing each
 * of them only once and keeping track of the loader of each one. When the ListFragment gets
 * destroyed it releases all the loaders.
 */
public final class PageAdapter extends BaseAdapter {

    private final List<VideoEntry> entries;
    private final List<View> entryViews;
    private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
    private final LayoutInflater inflater;
    private final ThumbnailListener thumbnailListener;

    private boolean labelsVisible;

    public PageAdapter(Context context, List<VideoEntry> entries) {
        this.entries = entries;

        entryViews = new ArrayList<View>();
        thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
        inflater = LayoutInflater.from(context);
        thumbnailListener = new ThumbnailListener();

        labelsVisible = true;
    }

    public void releaseLoaders() {
        for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
            loader.release();
        }
    }

    public void setLabelVisibility(boolean visible) {
        labelsVisible = visible;
        for (View view : entryViews) {
            view.findViewById(R.id.text).setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public VideoEntry getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        VideoEntry entry = entries.get(position);

        // There are three cases here
        if (view == null) {
            // 1) The view has not yet been created - we need to initialize the YouTubeThumbnailView.
            view = inflater.inflate(R.layout.video_list_item, parent, false);
            YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
            thumbnail.setTag(entry.videoId);
            thumbnail.initialize(Config.YOUTUBE_API_KEY, thumbnailListener);
        } else {
            YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
            YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(thumbnail);
            if (loader == null) {
                // 2) The view is already created, and is currently being initialized. We store the
                //    current videoId in the tag.
                thumbnail.setTag(entry.videoId);
            } else {
                // 3) The view is already created and already initialized. Simply set the right videoId
                //    on the loader.
                thumbnail.setImageResource(R.drawable.loading_thumbnail);
                loader.setVideo(entry.videoId);
            }
        }
        TextView label = ((TextView) view.findViewById(R.id.text));
        label.setText(entry.text);
        label.setVisibility(labelsVisible ? View.VISIBLE : View.GONE);
        return view;
    }

    public final class ThumbnailListener implements
            YouTubeThumbnailView.OnInitializedListener,
            YouTubeThumbnailLoader.OnThumbnailLoadedListener {

        @Override
        public void onInitializationSuccess(
                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            thumbnailViewToLoaderMap.put(view, loader);
            view.setImageResource(R.drawable.loading_thumbnail);
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