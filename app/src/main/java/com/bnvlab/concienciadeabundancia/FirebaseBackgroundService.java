package com.bnvlab.concienciadeabundancia;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.bnvlab.concienciadeabundancia.auxiliaries.ICallback;
import com.bnvlab.concienciadeabundancia.auxiliaries.Notify;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Marcos on 01/05/2017.
 */

public class FirebaseBackgroundService extends Service {

    SharedPreferences prefs;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = this.getSharedPreferences(
                FirebaseBackgroundService.class.getSimpleName(), Context.MODE_PRIVATE);

        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);

        if (prefs.getBoolean(References.SHARED_PREFERENCES_FIRST_TIME + FirebaseAuth.getInstance().getCurrentUser().getUid(),true))
            getFirstTime(new ICallback() {
                @Override
                public void callback() {
                    checkTrainings();
                    checkConferences();
                    checkSentTraining();
                }
            });
    }

    private void getFirstTime(ICallback callback) {
        getFirstTimeTrainings(callback);
    }
    private void getFirstTimeTrainings(final ICallback callback) {
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.QUIZ)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren())
                            prefs.edit().putBoolean(References.SHARED_PREFERENCES_NOTIFICATION_TRAINING + data.getKey(), true).apply();
                        getFirstTimeConferences(callback);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    private void getFirstTimeConferences(final ICallback callback) {
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.CONFERENCES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren())
                            prefs.edit().putBoolean(References.SHARED_PREFERENCES_NOTIFICATION_CONFERENCE + data.getKey(), true).apply();
                        getFirstTimeSent(callback);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    private void getFirstTimeSent(final ICallback callback) {
        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.SENT)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot data : dataSnapshot.getChildren())
                            if (data.child(References.SENT_CHILD_CHECKED) != null && data.child(References.SENT_CHILD_CHECKED).getValue(boolean.class)
                                    && !prefs.getBoolean(References.SHARED_PREFERENCES_NOTIFICATION_RESULT + data.getKey(), false))
                                prefs.edit().putBoolean(References.SHARED_PREFERENCES_NOTIFICATION_RESULT + data.getKey(), true).apply();
                            callback.callback();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void checkTrainings() {
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.QUIZ)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        boolean active = prefs.getBoolean(References.USERS_CHILD_ACTIVE, false);
                        boolean freeContent = dataSnapshot.child(References.FREE_CONTENT).getValue(boolean.class);

                        if (FirebaseAuth.getInstance().getCurrentUser() != null
                                && !dataSnapshot.child(References.QUIZ_CHILD_HIDDEN).getValue(boolean.class)
                                && (freeContent || (!freeContent && active))) {
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

    private void checkSentTraining() {
        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.SENT)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot data : dataSnapshot.getChildren()) {
                            if (FirebaseAuth.getInstance().getCurrentUser() != null && data.child(References.SENT_CHILD_CHECKED).getValue(boolean.class) &&
                                    !prefs.getBoolean(References.SHARED_PREFERENCES_NOTIFICATION_RESULT + data.getKey(), false)){
                                Notify.message(getApplicationContext(), "Felicidades", "Entrá a la app y enterate", 3, true);
                                prefs.edit().putBoolean(References.SHARED_PREFERENCES_NOTIFICATION_RESULT + data.getKey(), true).apply();
                            }
//                            if (!data.child(References.SENT_CHILD_CHECKED).getValue(boolean.class))
//                                FirebaseDatabase.getInstance().getReference(References.REFERENCE)
//                                        .child(References.SENT)
//                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                        .child(data.getKey())
//                                        .child(References.SENT_CHILD_CHECKED)
//                                        .addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                                                    if (dataSnapshot.getValue(boolean.class)
//                                                            && !prefs.getBoolean(References.SHARED_PREFERENCES_NOTIFICATION_RESULT + data.getKey(), false)) {
//                                                        Notify.message(getApplicationContext(), "Felicidades", "Entrá a la app y enterate", 3, true);
//                                                        prefs.edit().putBoolean(References.SHARED_PREFERENCES_NOTIFICATION_RESULT + data.getKey(), true).apply();
//                                                    }
//
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void postNotif(String title, String text) {
        Notify.message(this,title, text);
    }

}