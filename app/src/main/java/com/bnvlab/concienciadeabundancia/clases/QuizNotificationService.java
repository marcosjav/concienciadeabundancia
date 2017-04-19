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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

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
//        super.onStartCommand(intent, flags, startId);
//        if (intent != null) {
//            Notification note = new Notification(0, null, System.currentTimeMillis());
//            note.flags |= Notification.FLAG_NO_CLEAR;
//            startForeground(0, note);
//            checkTrainings();
//            checkConferences();
//            Toast.makeText(this, "SERVICIO INICIADO", Toast.LENGTH_SHORT).show();
//        }
//        return START_STICKY;
//        showToast("onStart");

//        Intent intent2 = new Intent(this, MainActivity.class);
//        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent2, 0);

//        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();

        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .keepSynced(true);

        final SharedPreferences prefs = this.getSharedPreferences(
                MainActivity.APP_SHARED_PREF_KEY, Context.MODE_PRIVATE);

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
//        super.onDestroy();
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
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            final String title = dataSnapshot.child("title").getValue(String.class);
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
