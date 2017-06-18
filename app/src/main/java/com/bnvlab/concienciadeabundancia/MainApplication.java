package com.bnvlab.concienciadeabundancia;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Marcos on 07/06/2017.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().subscribeToTopic("notifications");

    }
}
