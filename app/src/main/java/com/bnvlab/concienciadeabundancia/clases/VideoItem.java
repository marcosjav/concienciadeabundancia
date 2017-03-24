package com.bnvlab.concienciadeabundancia.clases;

import com.bnvlab.concienciadeabundancia.auxiliaries.YouTubeHelper;
import com.bnvlab.concienciadeabundancia.fragments.VideoFragment;

import java.io.Serializable;

/**
 * Created by Marcos on 24/03/2017.
 */

public class VideoItem implements Serializable{
    private String url;
    private String thumbnail;
    private String title;
    private String videoId;

    public VideoItem() {
    }

    public VideoItem(String url, String thumbnail, String title) {
        this.url = url;
        this.thumbnail = thumbnail;
        this.title = title;
    }

    public VideoItem(String url) {
        this.url = url;
        this.videoId = url.split("be/")[1];
//        SimpleYouTubeHelper syh = new SimpleYouTubeHelper(url);
//        this.title = syh.getTitleQuietly();
//        this.thumbnail = syh.getThumbnailUrl();

        this.title = YouTubeHelper.getVideoTitle(this.videoId);
        this.thumbnail = YouTubeHelper.getThumbnailURL(this.videoId);
    }

    public static void refresh()
    {
        VideoFragment.refresh();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
