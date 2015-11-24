package com.app.jobaloon.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

public class UncaughtExceptionReporter implements
        Thread.UncaughtExceptionHandler {

    private final String SUBJECT = "Unexpected Error";
        private final String SEND_EMAIL = "jobaloonapp@gmail.com";
//    private final String SEND_EMAIL = "shadila1992@gmail.com";
    private final String CC_SEND_EMAIL = "binitha.sics@gmail.com";
    private Context mContext;
    String crashReportString = "";
    String deviceName = "";
    PackageInfo pInfo;

    public UncaughtExceptionReporter(Context context) {
        mContext = context;
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.d("Connected", throwable.toString());
        StringBuilder report = new StringBuilder();
        Date currentDate = new Date();
        report.append("\nUncaughtException collected on : "
                + currentDate.toString());

        report.append("\n\n");
//		report.append(DeviceInfo.getDeviceInformation());
        report.append("\n\n");
        report.append("\n____________________________________________________________________________");
        report.append("\nUncaughtException");
        report.append("\n____________________________________________________________________________");
        report.append("\n[STACK] : ");
        final Writer result = new StringWriter();

        String detail = "";
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
            String deviceName = android.os.Build.MODEL;
            String version = pInfo.versionName;
            detail = "version" + version;
            detail = detail + "\ndeviceName" + deviceName;
            detail = detail + "\nBRAND" + android.os.Build.BRAND;
            detail = detail + "\nRELEASE"
                    + android.os.Build.VERSION.RELEASE;
            detail = detail + "\nSDK_INT"
                    + android.os.Build.VERSION.SDK_INT;
            System.out.println("detail" + detail);
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            final PrintWriter printWriter = new PrintWriter(result);
            throwable.printStackTrace(printWriter);
            crashReportString = detail + "\n\n" + result.toString();
            Log.d("Jobaloon", "crashReportString " + crashReportString);
            report.append(detail + "\n\n" + result.toString());
            printWriter.close();
        } catch (Exception e) {
            report.append("\n\n");
        }


        report.append("\n\nCAUSE:");

        try {
            // If the exception was thrown in a background thread inside
            // AsyncTask, then the actual exception can be found with getCause
            Throwable cause = throwable.getCause();
            final PrintWriter printWriter = new PrintWriter(result);
            while (cause != null) {
                cause.printStackTrace(printWriter);
                report.append(detail + "\n\n" + result.toString());
                cause = cause.getCause();
            }
            printWriter.close();
        } catch (Exception e) {
            report.append("\n\n");
        }
        report.append("\n____________________________________________________________________________\n\n");
        // System.err.println("REPORT " + report.toString());
        // writeToFile(report.toString());


        sendMail(report);
    }

    public void sendMail(final StringBuilder errorContent) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                builder.setTitle("Sorry, an unexpected error occurred!");
                builder.create();
                builder.setNegativeButton(("Cancel"),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                System.exit(0);
                            }
                        });
                builder.setPositiveButton(("Report"),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent sendIntent = new Intent(
                                        Intent.ACTION_SEND);
                                StringBuilder body = new StringBuilder();
                                body.append('\n').append('\n');
                                body.append(errorContent).append('\n')
                                        .append('\n');
                                sendIntent.setType("message/rfc822");
                                sendIntent.putExtra(Intent.EXTRA_EMAIL,
                                        new String[]{SEND_EMAIL + "," + "binitha.sics@gmail.com"});
//                                System.out.println("------ crashReportString : "+crashReportString);
                                sendIntent.putExtra(Intent.EXTRA_TEXT,
                                        crashReportString);
                                sendIntent.putExtra(Intent.EXTRA_SUBJECT,
                                        SUBJECT);
                                sendIntent.putExtra(Intent.EXTRA_CC,
                                        CC_SEND_EMAIL);
                                mContext.startActivity(sendIntent);

                            }
                        });
                builder.setMessage("Please send us crash message");
                builder.show();
                Looper.loop();
            }
        }.start();
    }

}