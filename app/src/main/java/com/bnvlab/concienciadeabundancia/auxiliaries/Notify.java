package com.bnvlab.concienciadeabundancia.auxiliaries;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;

/**
 * Created by Marcos on 20/03/2017.
 */

public class Notify {

    public static void message(Context mContext, String title, String text) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_cda_icon)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true);

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
        mNotificationManager.notify(0, mBuilder.build());

    }

    public static void share(String message, Context context)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
//        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.app_name)));
    }

    
}