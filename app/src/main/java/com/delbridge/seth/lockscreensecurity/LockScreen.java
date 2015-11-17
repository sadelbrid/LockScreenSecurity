package com.delbridge.seth.lockscreensecurity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


public class LockScreen extends Activity implements LockScreenUtils.OnLockStatusChangedListener {
    // User-interface
    private Button btnUnlock;
    // Member variables
    private LockScreenUtils lockScreenUtils;

    // Set appropriate flags to make the screen appear over the keyguard
    @Override
    public void onAttachedToWindow() {
        //this.getWindow().setType(
                //WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        this.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        );

        super.onAttachedToWindow();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        lockScreenUtils = new LockScreenUtils();
        btnUnlock = (Button) findViewById(R.id.btnUnlock);
        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // unlock home button and then screen on button press
                lockScreenUtils.unlock();
            }
        });

        // unlock screen in case of app get killed by system
        if (getIntent() != null && getIntent().hasExtra("kill")
                && getIntent().getExtras().getInt("kill") == 1) {
            enableKeyguard();
            lockScreenUtils.unlock();
        } else {
            try {
                // disable keyguard
                disableKeyguard();
                // lock home button
                Log.i("locking", "locking screen");
                lockScreenUtils.lock(LockScreen.this);
                // start service for observing intents
                Log.i("locking", "starting service");
                startService(new Intent(this, LockScreenService.class));
                // listen the events get fired during the call

                StateListener phoneStateListener = new StateListener();
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                telephonyManager.listen(phoneStateListener,
                        PhoneStateListener.LISTEN_CALL_STATE);
                Log.i("locking", "state listener created");
            } catch (Exception e) {
            }
        }
    }

    // Handle events of calls and unlock screen if necessary
    private class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if(state == TelephonyManager.CALL_STATE_RINGING)
                lockScreenUtils.unlock();
        }
    };

    // Don't finish Activity on Back press
    @Override
    public void onBackPressed() {
        return;
    }

    // Handle button clicks
    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (keyCode == KeyEvent.KEYCODE_POWER)
                || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                || (keyCode == KeyEvent.KEYCODE_CAMERA)
                || (keyCode) == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return false;
    }

    // handle the key press events here itself
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
            return false;
        }
        return event.getKeyCode() == KeyEvent.KEYCODE_HOME;
    }

    // Simply unlock device when home button is successfully unlocked
    @Override
    public void onLockStatusChanged(boolean isLocked) {
        if (!isLocked) {
            //Unlock device by finishing activity
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        lockScreenUtils.unlock();
    }

    @SuppressWarnings("deprecation")
    private void disableKeyguard() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("IN");
        keyguardLock.disableKeyguard();
    }

    @SuppressWarnings("deprecation")
    private void enableKeyguard() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("IN");
        keyguardLock.reenableKeyguard();
    }
}
