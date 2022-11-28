package com.example.keepmynotes.Controller.AlarmForNote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.keepmynotes.R;
import com.example.keepmynotes.View.Alarm_Detail;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       int ID = intent.getIntExtra("ID_notification", 0);
       String Title = intent.getStringExtra("Title");
       String Content =  intent.getStringExtra("Content");

        Intent transfer = new Intent(context, Alarm_Detail.class);
        transfer.putExtra("ID", ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ID, transfer, 0);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"AlarmNote");
        builder.setSmallIcon(R.drawable.ic_access_alarm)
                .setContentTitle(Title)
                .setContentText(Content)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        notificationManagerCompat.notify(ID, builder.build());
        Log.e("Messagae","Alarm");

    }


}
