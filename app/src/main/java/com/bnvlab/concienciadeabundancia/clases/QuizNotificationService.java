package com.bnvlab.concienciadeabundancia.clases;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.auxiliaries.Notify;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Marcos on 10/04/2017.
 */

public class QuizNotificationService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseApp.initializeApp(this);

        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .keepSynced(true);

        final SharedPreferences prefs = this.getSharedPreferences(
                MainActivity.APP_SHARED_PREF_KEY, Context.MODE_PRIVATE);

        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(References.USERS_CHILD_ACTIVE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        prefs.edit().putBoolean(References.USERS_CHILD_ACTIVE,dataSnapshot.getValue(boolean.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        if (prefs.getBoolean(References.SHARED_PREFERENCES_FIRST_TIME,true))
            prefs.edit().putBoolean(References.SHARED_PREFERENCES_FIRST_TIME, false).apply();
        else{
            checkConferences();
            checkTrainings();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
    }


    private void checkTrainings() {
        final SharedPreferences prefs = this.getSharedPreferences(
                MainActivity.APP_SHARED_PREF_KEY, Context.MODE_PRIVATE);

        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.QUIZ)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        boolean active = prefs.getBoolean(References.USERS_CHILD_ACTIVE,false);
                        boolean freeContent = dataSnapshot.child(References.FREE_CONTENT).getValue(boolean.class);

                        if (FirebaseAuth.getInstance().getCurrentUser() != null
                                && !dataSnapshot.child(References.QUIZ_CHILD_HIDDEN).getValue(boolean.class)
                                && ( freeContent || ( !freeContent && active))) {
                            final String title = dataSnapshot.child(References.QUIZ_CHILD_TITLE).getValue(String.class);
                            if (!prefs.getBoolean(References.SHARED_PREFERENCES_NOTIFICATION_TRAINING + dataSnapshot.getKey(), false)) {
                                String message = "Se agregó: \"" + title + "\"";
                                Notify.message(getApplicationContext(), "Nuevo entrenamiento!", message, 1);
                                prefs.edit().putBoolean(References.SHARED_PREFERENCES_NOTIFICATION_TRAINING + dataSnapshot.getKey(), true).apply();
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void checkConferences() {
        final SharedPreferences prefs = this.getSharedPreferences(
                MainActivity.APP_SHARED_PREF_KEY, Context.MODE_PRIVATE);

        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.CONFERENCES)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            final String title = dataSnapshot.child(References.CONFERENCES_CHILD_TITLE).getValue(String.class);
                            if (!prefs.getBoolean(References.SHARED_PREFERENCES_NOTIFICATION_CONFERENCE + dataSnapshot.getKey(), false)) {
                                String message = title + " - Entrá para saber más";
                                Notify.message(getApplicationContext(), "Nuevo encuentro!", message, 2);
                                prefs.edit().putBoolean(References.SHARED_PREFERENCES_NOTIFICATION_CONFERENCE + dataSnapshot.getKey(), true).apply();
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
