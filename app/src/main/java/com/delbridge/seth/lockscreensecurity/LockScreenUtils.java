package com.delbridge.seth.lockscreensecurity;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Created by Seth on 10/29/15.
 */
public class LockScreenUtils {
    private OverlayDialog overlayDialog;
    private AlertDialog.Builder customAlertDialogBuilder;
    private AlertDialog customOverlayDialog;
    private OnLockStatusChangedListener onLockStatusChangedListener;

    public interface OnLockStatusChangedListener{
        void onLockStatusChanged(boolean isLocked);
    }

    public LockScreenUtils(){
        reset();
    }

    public void reset(){
        if(overlayDialog != null){
            overlayDialog.dismiss();
            overlayDialog = null;
        }
    }

    public void unlock(){
        if(overlayDialog != null){
            Log.i("locking","about to unlock");
            overlayDialog.dismiss();
            overlayDialog = null;
            if(onLockStatusChangedListener != null) {
                Log.i("locking", "closing lockscreen");
                onLockStatusChangedListener.onLockStatusChanged(false);
            }
        }
    }

    public void lock(Activity activity){
        if(overlayDialog == null){
            Log.i("locking", "opening lockscreen");
            //overlayDialog = new OverlayDialog(activity);
            customAlertDialogBuilder = new AlertDialog.Builder(activity);
            customOverlayDialog = customAlertDialogBuilder.create();

            WindowManager.LayoutParams layoutParams = customOverlayDialog.getWindow().getAttributes();
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            layoutParams.dimAmount = 0f;
            layoutParams.width = 0;
            layoutParams.height = 0;
            layoutParams.gravity = Gravity.BOTTOM;
            customOverlayDialog.getWindow().setAttributes(layoutParams);
            customOverlayDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    0xffffff);

            customOverlayDialog.setOwnerActivity(activity);
            customOverlayDialog.setCancelable(false);

            Log.i("locking", "failed1");
            customOverlayDialog.show();
            Log.i("locking", "failed2");
            onLockStatusChangedListener = (OnLockStatusChangedListener) activity;
            Log.i("locking", "failed3");
        }
    }

    private class OverlayDialog extends AlertDialog{
        public OverlayDialog(Activity activity){
            super(activity);
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            layoutParams.dimAmount = 0f;
            layoutParams.width = 0;
            layoutParams.height = 0;
            layoutParams.gravity = Gravity.BOTTOM;
            getWindow().setAttributes(layoutParams);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    0xffffff);
            setOwnerActivity(activity);
            setCancelable(false);
        }

        public final boolean dispatchTouchEvent(MotionEvent e){
            return true;
        }
    }
}
