package com.bnvlab.concienciadeabundancia.auxiliaries;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bnvlab.concienciadeabundancia.clases.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Marcos on 04/06/2017.
 */

public class Conn {
    private static final String BASE_URL = "http://192.168.1.148:5000/";

    public static void getJSONArray(Context context, UrlType url, Response.Listener<JSONArray> onResponse, Response.ErrorListener onError) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, BASE_URL + url.toString(), null, onResponse, onError);
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    public static void getJSONObject(Context context, String url, Response.Listener<JSONObject> onResponse, Response.ErrorListener onError) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, onResponse, onError);
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    public static ArrayList<String> jsonToArray(JSONArray jsonArray) {
        ArrayList<String> list = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    list.add(jsonArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public enum UrlType {
        LOCATIONS("get-locations", 0);

        private String stringValue;
        private int intValue;
        private UrlType(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }
}
