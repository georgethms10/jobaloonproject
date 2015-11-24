package com.app.jobaloon.jobseeker;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.app.jobaloon.gson.SampleVideoGSON;
import com.app.jobaloon.gson.UploadVideoGSON;
import com.app.jobaloon.main.R;
import com.app.jobaloon.main.WelcomePage;
import com.app.jobaloon.utils.AppPreferences;
import com.app.jobaloon.utils.AsycHttpCall;
import com.app.jobaloon.utils.ConnectionDetector;
import com.app.jobaloon.utils.MyCrouton;
import com.app.jobaloon.utils.Response;
import com.app.jobaloon.utils.SaveSecurePreference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class UploadService extends Service implements Response {
    private String fullName, emailId, address, chatStatus, pushStatus, description, password, videoName,
            imageName, videoFilePath, editProfileURL, imageFilePath, code, profile, oAuthToken, userId;
    private AppPreferences preferences;
    private MyCrouton myCrouton;
    SaveSecurePreference preference;
    public static final int VIDEO = 100;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            System.out.println("null check..");
            if (intent.getExtras() != null) {
                Bundle bundle = intent.getExtras();
                myCrouton = new MyCrouton();
                fullName = bundle.getString("fullName");
                emailId = bundle.getString("emailId");
                address = bundle.getString("address");
                chatStatus = bundle.getString("chatStatus");
                pushStatus = bundle.getString("pushStatus");
                description = bundle.getString("description");
                password = bundle.getString("password");
                videoName = bundle.getString("videoName");
                imageName = bundle.getString("imageName");
                videoFilePath = bundle.getString("videoFilePath");
                editProfileURL = bundle.getString("editProfileURL");
                imageFilePath = bundle.getString("imageFilePath");
                code = bundle.getString("code");
                profile = bundle.getString("label");
                oAuthToken = bundle.getString("oAuthToken");
                userId = bundle.getString("userId");
                preferences = new AppPreferences(getApplicationContext(), "JobBoxData");
                ConnectionDetector connectionDetector = new ConnectionDetector(
                        getApplicationContext());
                if (connectionDetector.isConnectingToInternet())
                    new AsyncSave().execute();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.no_connection),
                            Toast.LENGTH_SHORT).show();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private JSONObject doFileUpload() {
        String fullNameEncoded = "", addressEncoded = "", passwordEncoded = "", descriptionEncoded = "";
        try {
            fullNameEncoded = URLEncoder.encode(fullName, "UTF-8");
            addressEncoded = URLEncoder.encode(address, "UTF-8");
            passwordEncoded = URLEncoder.encode(password, "UTF-8");
            descriptionEncoded = URLEncoder.encode(description, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            fullNameEncoded = fullName;
            addressEncoded = address;
            passwordEncoded = password;
            descriptionEncoded = description;
        }

        JSONObject jObj = null;
        File fileVideo, fileImage;
        FileBody binVideo, binImage;

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(editProfileURL);
            MultipartEntity reqEntity = new MultipartEntity();
//            reqEntity.addPart("type", new StringBody("Jobseeker"));
//            reqEntity.addPart("user_name", new StringBody(fullNameEncoded));
//            reqEntity.addPart("user_email", new StringBody(emailId));
//            reqEntity.addPart("user_password", new StringBody(passwordEncoded));
//            reqEntity.addPart("user_address", new StringBody(addressEncoded));
//            reqEntity.addPart("chat_notification", new StringBody(chatStatus));
//            reqEntity.addPart("profile_visitor", new StringBody(pushStatus));
//            reqEntity.addPart("description", new StringBody(descriptionEncoded));
//            reqEntity.addPart("code", new StringBody(code));
//            reqEntity.addPart("label", new StringBody(profile));
//            reqEntity.addPart("userid", new StringBody(preferences.getData("user_id")));
            RequestParams params = new RequestParams();
            params.put("userid", userId);
            params.put("oauthToken", oAuthToken);
            params.put("user_name", fullNameEncoded);
            params.put("user_email", emailId);
            params.put("password", passwordEncoded);
            params.put("user_address", address);
            params.put("chat_notification", chatStatus);
            params.put("profile_visitor", "ON");
            params.put("Jobdescription", descriptionEncoded);
            params.put("code", code);
            params.put("Labels", profile);
            reqEntity.addPart("userid", new StringBody(userId));
            reqEntity.addPart("oauthToken", new StringBody(oAuthToken));
            reqEntity.addPart("user_name", new StringBody(fullNameEncoded));
            reqEntity.addPart("user_email", new StringBody(emailId));
            reqEntity.addPart("password", new StringBody(passwordEncoded));
            reqEntity.addPart("user_address", new StringBody(addressEncoded));
            reqEntity.addPart("chat_notification", new StringBody(chatStatus));
            reqEntity.addPart("profile_visitor", new StringBody(pushStatus));
            reqEntity.addPart("Jobdescription", new StringBody(descriptionEncoded));
            reqEntity.addPart("code", new StringBody(code));
            reqEntity.addPart("Labels", new StringBody(profile));
//            reqEntity.addPart("userid", new StringBody(preferences.getData("user_id")));

            System.out.println("reqEntity : " + reqEntity);

            if (!videoFilePath.equals("")) {
                fileVideo = new File(videoFilePath);
                binVideo = new FileBody(fileVideo);
                params.put("video", binVideo);
                reqEntity.addPart("video", binVideo);
            } else {
                reqEntity.addPart("video", new StringBody(""));
                params.put("video", "");
            }
            if (!imageFilePath.equals("")) {
                fileImage = new File(imageFilePath);
                binImage = new FileBody(fileImage);
                params.put("image", binImage);
                reqEntity.addPart("image", binImage);
            } else {
                reqEntity.addPart("image", new StringBody(""));
                params.put("image", "");
            }
            AsycHttpCall.getInstance().CallApiPost(editProfileURL, params, this, VIDEO);
//            post.setEntity(reqEntity);
//            HttpResponse response = client.execute(post);
//            HttpEntity resEntity = response.getEntity();
//            final String response_str = EntityUtils.toString(resEntity);
//            jObj = new JSONObject(response_str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return jObj;
    }

    @Override
    public void onSuccess(String response, int code) {
        switch (code) {
            case VIDEO:
                getUserDetails(response);
                break;
        }
    }

    public void getUserDetails(String response) {
        if (response != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();

            UploadVideoGSON videoGSON = gson.fromJson(response,
                    UploadVideoGSON.class);
            if (videoGSON.Result) {
                preference = new SaveSecurePreference(getApplication());
                preferences.saveData("user_name", fullName);
                preference.setPrefValue(WelcomePage.EMAIL_KEY, emailId);
//                    preferences.saveData("user_email", emailId);
                preferences.saveData("user_address", address);
                preferences.saveData("description", description);
                preference.setPrefValue(WelcomePage.PASS_KEY, password);
//                    preferences.saveData("password", password);
                if (!videoName.trim().equals(""))
                    preferences.saveData("video", videoName);
                if (!imageName.trim().equals(""))
                    preferences.saveData("image", imageName);
                preferences.saveData("code", code);
                preferences.saveData("Labels", profile);

                preferences.saveBooleanData("UPLOAD", true);
                preferences.saveBooleanData("UploadOnProgress", false);
                Toast.makeText(getApplicationContext(), getString(R.string.video_upload_is_successful), Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                i.setAction("uploadSuccessful");
                getApplicationContext().sendBroadcast(i);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.video_upload_is_failed), Toast.LENGTH_SHORT).show();
                preferences.saveIntData("UPLOAD_LIKE", 0);
            }
        }
    }

    @Override
    public void onFail(String response, int code) {

    }

    public class AsyncSave extends AsyncTask<Void, Void, Void> {

        JSONObject jObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), getString(R.string.video_started_uploading), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            jObject = doFileUpload();
            return null;
        }


        @Override
        protected void onPostExecute(Void resultValue) {
            super.onPostExecute(resultValue);
            if (jObject != null) {
                if (jObject.optBoolean("Result")) {
                    preference = new SaveSecurePreference(getApplication());
                    preferences.saveData("user_name", fullName);
                    preference.setPrefValue(WelcomePage.EMAIL_KEY, emailId);
//                    preferences.saveData("user_email", emailId);
                    preferences.saveData("user_address", address);
                    preferences.saveData("description", description);
                    preference.setPrefValue(WelcomePage.PASS_KEY, password);
//                    preferences.saveData("password", password);
                    if (!videoName.trim().equals(""))
                        preferences.saveData("video", videoName);
                    if (!imageName.trim().equals(""))
                        preferences.saveData("image", imageName);
                    preferences.saveData("code", code);
                    preferences.saveData("Labels", profile);

                    preferences.saveBooleanData("UPLOAD", true);
                    preferences.saveBooleanData("UploadOnProgress", false);
                    Toast.makeText(getApplicationContext(), getString(R.string.video_upload_is_successful), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent();
                    i.setAction("uploadSuccessful");
                    getApplicationContext().sendBroadcast(i);

//                    Intent broadCast=new Intent(getApplicationContext(),UploadVideoSeekerNew.class);

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.video_upload_is_failed), Toast.LENGTH_SHORT).show();
                    preferences.saveIntData("UPLOAD_LIKE", 0);
                }
            }
        }
    }
}
