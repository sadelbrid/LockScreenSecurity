package com.delbridge.seth.lockscreensecurity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import android.app.Notification;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.provider.SyncStateContract.Constants;
import android.support.v4.app.NotificationCompat;

public class LockScreenService extends Service {
    private BroadcastReceiver broadcastReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // Register for Lockscreen event intents
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        broadcastReceiver = new LockScreenIntentReceiver();
        registerReceiver(broadcastReceiver, filter);
        startForeground();
        return START_STICKY;
    }

    // Run service in foreground so it is less likely to be killed by system
    private void startForeground() {
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentText("Running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(null)
                .setOngoing(true)
                .build();
        startForeground(9999,notification);
    }

    // Unregister receiver
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}
