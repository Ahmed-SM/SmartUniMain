package com.aurak.smartuni.smartuni.Notifcation;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notif extends Application {
    public static final String CHANNEL_1_ID = "Channel1";
    @Override
    public void onCreate() {
        super.onCreate();
        
        createNotificationChannels();
        
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,"Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel1);
            }

        }
    }
}
