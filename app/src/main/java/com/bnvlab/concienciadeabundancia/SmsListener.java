package com.bnvlab.concienciadeabundancia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.auxiliaries.Notify;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Marcos on 22/03/2017.
 */

public class SmsListener extends BroadcastReceiver {
    private SharedPreferences preferences;
    Context context;

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        this.context = context;
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
//                        Toast.makeText(context, msgBody, Toast.LENGTH_SHORT).show();
                        if (msgBody.startsWith("cda code:"))
                        {
                            final String code = msgBody.split("code:")[1];

                            FirebaseDatabase.getInstance()
                                    .getReference(References.REFERENCE)
                                    .child(References.VERIFICATION_CODES)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String dbCode = (String) dataSnapshot.getValue();

                                            Toast.makeText(context, dbCode.toString(), Toast.LENGTH_LONG).show();
                                            if (dbCode.equals(code))
                                            {
                                                setPhoneVerified();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                }catch(Exception e){
//                            Log.d("Exception caught",e.getMessage());
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void setPhoneVerified(){
        final String phone = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];

        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(References.USERS_CHILD_VERIFIED)
                .setValue(true);

        Notify.message(context, "Listo!", "Hemos verificado tu teléfono con éxito.");

        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.VERIFICATION_CODES)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue();
    }
}
