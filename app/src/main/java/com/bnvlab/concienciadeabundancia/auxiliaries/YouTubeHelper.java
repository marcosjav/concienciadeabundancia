package com.bnvlab.concienciadeabundancia.auxiliaries;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Marcos on 24/03/2017.
 */

public class YouTubeHelper {

    public static String getThumbnailURL(String videoId) {
        return "https://img.youtube.com/vi/" + videoId + "/0.jpg";
    }

    public static String getVideoTitle(String videoId) {
        URL url = null;
        String title = "";
        try {

            url = new URL("https://www.googleapis.com/youtube/v3/videos?id="
                + videoId
                + "&key=AIzaSyD3M0Z2RC2yWRWi3fYk2Pq9taY20Vdt1Co&fields=items(id,snippet(description,channelId,title,categoryId),statistics)&part=snippet,statistics");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
            title = total.toString();
            urlConnection.disconnect();

            return new JSONObject(title).getString("title");

        } catch (Exception e) {
            Log.d("CDA ERROR",e.getMessage());
        }

        return "No title";

//        return "https://www.googleapis.com/youtube/v3/videos?id="
//                + videoId
//                + "&key=AIzaSyD3M0Z2RC2yWRWi3fYk2Pq9taY20Vdt1Co&fields=items(id,snippet(description,channelId,title,categoryId),statistics)&part=snippet,statistics";
    }
}
