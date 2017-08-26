package com.bnvlab.concienciadeabundancia.auxiliaries;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;

/**
 * Created by Marcos on 20/03/2017.
 */

public class Notify {

    public static final int ACTION_NONE = 0, ACTION_SHARE = 1, ACTION_TRAININGS = 2, ACTION_TRAINING_RESULT = 3, ACTION_MESSAGE = 4;

    public static void message(Context mContext, String title, String text, int id) {
        message(mContext, title, text, id, ACTION_NONE);
    }

    public static void message(Context mContext, String title, String text, int id, int action) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_notification_white)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setVibrate(new long[]{1000, 1000});;
        if (action != 0)
            mBuilder.setPriority(NotificationCompat.PRIORITY_MAX|NotificationCompat.PRIORITY_HIGH);

        // Creats an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
//        if (action == ACTION_SHARE)
//            resultIntent.putExtra(References.SHARE_FROM_NOTIFICATION, true);
//        else if (action == ACTION_TRAININGS)
//            resultIntent.putExtra(References.TRAININGS_FROM_NOTIFICATION, true);

        resultIntent.putExtra("launchedBy",action);

        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder.build());

    }

    public static void message(Context mContext, String title, String text) {
        message(mContext, title, text, 1);
    }

    public static void share(String message, Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
//        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.app_name)));
    }

//    public static void shareTo(String phone, String message, Context context) {
////        Intent sendIntent = new Intent();
////        sendIntent.setAction(Intent.ACTION_SENDTO);
////        sendIntent.putExtra(Intent.EXTRA_PHONE_NUMBER, phone);
////        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
////        sendIntent.setType("text/plain");
//////        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
////        context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.app_name)));
//        Intent sendIntent = new Intent("android.intent.action.MAIN");
//        sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
//        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(phone) + "@s.whatsapp.net");//phone number without "+" prefix
//
//        context.startActivity(sendIntent);
//    }

    public static void email(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@concienciadeabundancia.com"});
        context.startActivity(Intent.createChooser(intent, ""));
    }

//    public static void sendSMS(final String phoneNumber, final String message, final Context context) {
//        String SENT = "SMS_SENT";
//        String DELIVERED = "SMS_DELIVERED";
//
//        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(
//                SENT), 0);
//
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
//                new Intent(DELIVERED), 0);
//
//        // ---when the SMS has been sent---
//        context.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        ContentValues values = new ContentValues();
////                        for (int i = 0; i < MobNumber.size() - 1; i++) {
////                            values.put("address", MobNumber.get(i).toString());// txtPhoneNo.getText().toString());
////                            values.put("body", MessageText.getText().toString());
////                        }
//                        values.put("address", phoneNumber);
//                        values.put("body", message);
//                        context.getContentResolver().insert(
//                                Uri.parse("content://sms/sent"), values);
//                        Toast.makeText(context, "SMS sent",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        Toast.makeText(context, "Generic failure",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(context, "No service",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(context, "Null PDU",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(context, "Radio off",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(SENT));
//
//        // ---when the SMS has been delivered---
//        context.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(context, "SMS delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Toast.makeText(context, "SMS not delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(DELIVERED));
//
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
//    }
//
//    public static void sendSms(String phonenumber, String message, Context context) {
//        try {
//            PendingIntent pi = PendingIntent.getActivity(context, 0,
//                    new Intent(context, MainActivity.class), 0);
//            SmsManager sms = SmsManager.getDefault();
//            // this is the function that does all the magic
//            sms.sendTextMessage(phonenumber, null, message, pi, null);
//        } catch (Exception e) {
//            Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }

//    public static void enviarSMS(final String phoneNumber, final String message, final Context context) {
//        String SENT = "SMS_SENT";
//        String DELIVERED = "SMS_DELIVERED";
//
//        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(
//                SENT), 0);
//
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
//                new Intent(DELIVERED), 0);
//
//        // ---when the SMS has been sent---
//        context.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        ContentValues values = new ContentValues();
////                        for (int i = 0; i < MobNumber.size() - 1; i++) {
////                            values.put("address", MobNumber.get(i).toString());// txtPhoneNo.getText().toString());
////                            values.put("body", MessageText.getText().toString());
////                        }
//                        values.put("address", phoneNumber);
//                        values.put("body", message);
//                        context.getContentResolver().insert(
//                                Uri.parse("content://sms/sent"), values);
//                        Toast.makeText(context, "SMS sent",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        Toast.makeText(context, "Generic failure",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(context, "No service",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(context, "Null PDU",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(context, "Radio off",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(SENT));
//
//        // ---when the SMS has been delivered---
//        context.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(context, "SMS delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Toast.makeText(context, "SMS not delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(DELIVERED));
//
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
//    }

    public void notification(Context mContext, String title, String text, int id, int action) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.ico_app)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true);
        if (action != 0)
            mBuilder.setPriority(NotificationCompat.PRIORITY_MAX|NotificationCompat.PRIORITY_HIGH);

        // Creats an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
//        if (action == ACTION_SHARE)
//            resultIntent.putExtra(References.SHARE_FROM_NOTIFICATION, true);
//        else if (action == ACTION_TRAININGS)
//            resultIntent.putExtra(References.TRAININGS_FROM_NOTIFICATION, true);

        resultIntent.putExtra("launchedBy",action);

        stackBuilder.addNextIntent(resultIntent);

//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(action,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, action, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder.build());

    }
}