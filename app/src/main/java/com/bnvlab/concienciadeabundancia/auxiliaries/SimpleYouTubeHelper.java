package com.bnvlab.concienciadeabundancia.auxiliaries;

import com.bnvlab.concienciadeabundancia.clases.VideoItem;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by Marcos on 24/03/2017.
 */

public class SimpleYouTubeHelper {
    private static JSONObject json;

    public SimpleYouTubeHelper(String youtubeUrl) {
        try {
            URL embededURL = new URL("http://www.youtube.com/oembed?url=" +
                    youtubeUrl + "&format=json"
            );

            json = new JSONObject(IOUtils.toString(embededURL));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTitleQuietly() {
        try {
            return json.getString("title");
        } catch (Exception e) {
            e.printStackTrace();
        }
        VideoItem.refresh();
        return "Sin nombre";
    }

    public String getThumbnailUrl() {
        try {
            return json.getString("thumbnail_url");

        } catch (Exception e) {
            e.printStackTrace();
        }
        VideoItem.refresh();
        return "";
    }

}
