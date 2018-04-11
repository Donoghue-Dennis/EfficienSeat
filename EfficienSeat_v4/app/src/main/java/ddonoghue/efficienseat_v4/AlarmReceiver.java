package ddonoghue.efficienseat_v4;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Initiate Channel for notifications
        initChannel(context);

        //Create and Send Notification
        createRenewalNotif(context);
    }

    public static void createRenewalNotif(Context context){
        //This is the intent of PendingIntent
        Intent intentAction = new Intent(context, NotifActionReceiver.class);

        PendingIntent renewTable = PendingIntent.getBroadcast(MyContext.getContext(),1,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);
        if(Build.VERSION.SDK_INT >= 26){
            Notification mBuilder = new NotificationCompat.Builder(context,"reserve")
                    .setSmallIcon(R.drawable.alarm)
                    .setContentTitle("Would you like to renew?")
                    .setContentText("Reservation about to expire!")
                    .setContentInfo("30s")
                    .addAction(R.drawable.renew_check, "Renew", renewTable)
                    .build();
        }else{
            Notification mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.alarm)
                    .setContentTitle("Would you like to renew or cancel?")
                    .setContentText("Reservation about to expire!")
                    .setContentInfo("30s")
                    .addAction(R.drawable.renew_check, "Renew", renewTable)
                    .build();
        }
    }

    public void initChannel(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("reserve",
                    "reserveChannel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel for reservation expiration notification");
            notificationManager.createNotificationChannel(channel);
        }
    }
}
