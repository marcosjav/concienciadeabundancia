package com.bnvlab.concienciadeabundancia;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bnvlab.concienciadeabundancia.auxiliaries.Notify;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Marcos on 15/05/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static SharedPreferences prefs;
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            RemoteMessage.Notification notification = remoteMessage.getNotification();
                Map<String, String> data = remoteMessage.getData();
                String title = data.get("title");
                String message = data.get("message");
                String uid = data.get("uid");
                int clickAction = Integer.parseInt(data.get("action"));

                if (uid != null) {
                    String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (uid.equals("all") || uid.equals(currentUID)) {

                        try {
                            final SharedPreferences prefs = this.getSharedPreferences(
                                    MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(), Context.MODE_PRIVATE);

                            if (prefs.getBoolean(References.NOTIFICATION_NEW_MESSAGES, false)) {
                                Notify.message(getApplicationContext(), "Mensajes nuevos!", "Haz click aquí para leerlos", clickAction, clickAction);
                            } else {
                                Notify.message(getApplicationContext(), title, message, clickAction, clickAction);
                            }

                            Set<String> notifications = prefs.getStringSet("notifications", new HashSet<String>());

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("message", message);
                            jsonObject.put("title", title);
                            jsonObject.put("read", false);
                            jsonObject.put("time", System.currentTimeMillis());

                            notifications.add(jsonObject.toString());

                            prefs.edit().putStringSet(References.NOTIFICATIONS, notifications).apply();
                            prefs.edit().putBoolean(References.NOTIFICATION_NEW_MESSAGES, true).apply();

                        } catch (Exception e) {
                            Log.d("MyFirebaseMessage", e.getMessage());
                        }
                    }
                }
            }
        }catch (Exception e){

        }
    }


}
