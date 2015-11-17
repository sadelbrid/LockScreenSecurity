package com.delbridge.seth.lockscreensecurity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
Broadcast Receiver that starts the lock screen when screen off, screen on, or boot complete
 */
public class LockScreenIntentReceiver extends BroadcastReceiver {
    // Handle actions and display Lockscreen
    //Display lockscreen if screen off, screen on, or boot complete
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)
                || intent.getAction().equals(Intent.ACTION_SCREEN_ON)
                || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent lockscreen = new Intent(context, LockScreen.class);
            lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(lockscreen);
        }
    }
}
