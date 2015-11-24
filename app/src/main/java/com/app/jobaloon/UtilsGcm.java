package com.app.jobaloon;

import android.app.Application;

public class UtilsGcm extends Application {
    public static String GCMSenderId = "682569088189";// Old 458196796961
    // new 819874801482 // JobBox 682569088189
    public static boolean notificationReceived;
    public static String notiTitle = "", notiType = "", notiUserId = "", registrationId = "";
}