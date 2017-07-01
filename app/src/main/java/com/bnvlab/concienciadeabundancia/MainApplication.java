package com.bnvlab.concienciadeabundancia;

import android.app.Application;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Marcos on 07/06/2017.
 */

public class MainApplication extends Application {
//    public static FirebaseApp firebaseApp;
//    private FirebaseAuth.AuthStateListener mAuthListener;
//    public static User user;

    @Override
    public void onCreate() {
        super.onCreate();

//        firebaseApp = FirebaseApp.initializeApp(this);
        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().subscribeToTopic("notifications");

        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }
}
