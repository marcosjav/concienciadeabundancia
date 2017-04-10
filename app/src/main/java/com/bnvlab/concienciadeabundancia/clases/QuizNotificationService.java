package com.bnvlab.concienciadeabundancia.clases;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.auxiliaries.Notify;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Marcos on 10/04/2017.
 */

public class QuizNotificationService extends Service {
    private static boolean running;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!running) {
            running = true;
            checkTrainings();
            checkConferences();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }

    private void checkTrainings() {
        FirebaseDatabase.getInstance()
                .getReference(MainActivity.REFERENCE)
                .child(QuizItem.CHILD)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            final String title = dataSnapshot.child("title").getValue(String.class);
                            String message = "Se agregó: \"" + title + "\"";
                            Notify.message(getApplicationContext(), "Nuevo entrenamiento!", message);
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

    private void checkConferences(){
        FirebaseDatabase.getInstance()
                .getReference(MainActivity.REFERENCE)
                .child("conferences")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            final String title = dataSnapshot.child("title").getValue(String.class);
                            String message = title + " - Entrá para saber más";
                            Notify.message(getApplicationContext(), "Nuevo encuentro!", message);
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
