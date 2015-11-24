package com.app.jobaloon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.app.jobaloon.main.BaseFragmentActivity;
import com.app.jobaloon.main.R;

public class GCMIntentService extends GCMBaseIntentService {

    public GCMIntentService() {
        super(UtilsGcm.GCMSenderId);
    }

    @Override
    protected void onError(Context context, String regId) {
        Log.e("", "error registration id : " + regId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        handleMessage(context, intent);
    }

    @Override
    protected void onRegistered(Context context, String regId) {
        UtilsGcm.registrationId = regId;
        handleRegistration(context, regId);
    }

    @Override
    protected void onUnregistered(Context arg0, String arg1) {

    }

    private void handleMessage(Context context, Intent intent) {
        UtilsGcm.notificationReceived = true;
        UtilsGcm.notiTitle = intent.getStringExtra("Title");
        UtilsGcm.notiType = intent.getStringExtra("type");
        UtilsGcm.notiUserId = intent.getStringExtra("userid");
        int icon = R.drawable.action_bar_icon; // icon from resources
        CharSequence tickerText = UtilsGcm.notiTitle;
        long when = System.currentTimeMillis(); // notification time
        CharSequence contentTitle = "" + UtilsGcm.notiTitle;
        // // message title
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, tickerText, when);
        Intent notificationIntent = new Intent(context, BaseFragmentActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, "",
                pendingIntent);
        notification.flags |= notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND
                | Notification.DEFAULT_LIGHTS;
        notification.vibrate = new long[]{100L, 100L, 200L, 500L};
        notificationManager.notify(1, notification);
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wl.acquire();
    }

    private void handleRegistration(Context context, String regId) {
        UtilsGcm.registrationId = regId;
        // SavePreferences("PUSHNOT", regId);
        Log.e("", "registration id : " + regId);
    }
}
