package com.bnvlab.concienciadeabundancia.clases;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(intent != null)
        {
            checkTrainings();
            checkConferences();
            Toast.makeText(this, "SERVICIO INICIADO", Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
//        super.onDestroy();
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
                            Notify.message(getApplicationContext(), "Nuevo entrenamiento!", message, 1);
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
                            Notify.message(getApplicationContext(), "Nuevo encuentro!", message, 2);
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
