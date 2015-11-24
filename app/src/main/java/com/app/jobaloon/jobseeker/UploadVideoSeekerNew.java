package com.app.jobaloon.jobseeker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.app.jobaloon.gson.JobListGSON;
import com.app.jobaloon.gson.SampleVideoGSON;
import com.app.jobaloon.main.R;
import com.app.jobaloon.main.WelcomePage;
import com.app.jobaloon.utils.AppPreferences;
import com.app.jobaloon.utils.AsycHttpCall;
import com.app.jobaloon.utils.FragmentActions;
import com.app.jobaloon.utils.MyCrouton;
import com.app.jobaloon.utils.MyProgressDialog;
import com.app.jobaloon.utils.Response;
import com.app.jobaloon.utils.SaveSecurePreference;
import com.app.jobaloon.utils.UncaughtExceptionReporter;
import com.app.jobaloon.utils.VolleyForAll;
import com.app.jobaloon.utils.VolleyOnResponseListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by SICS-Dpc2 on 03-Apr-15.
 */
public class UploadVideoSeekerNew extends FragmentActions implements VolleyOnResponseListener, Response {

    @InjectView(R.id.videoSaveButton)
    ImageView jobSearchButton;

    @InjectView(R.id.recordNewVideo)
    ImageView recordNewVideo;

    @InjectView(R.id.questionMark)
    ImageView QuestionButton;

    @InjectView(R.id.feedVideoView)
    VideoView feedVideoView;
    @InjectView(R.id.videoFrame)
    RelativeLayout videoFrame;
    @InjectView((R.id.palceholder_video))
    ImageView placeHolderImage;
    private AppPreferences preferences;
    private boolean isPlaying = false;
    private VolleyForAll volleyForAll;
    private CountDownTimer countDownTimer;
    private static final int CHAT_COUNT = 1;
    private String imageName = "", videoName = "", videoFilePath = "", sampleVideo = "",
            imageFilePath = "", editProfileURL;
    private static final int SAVE_CHANGES = 1, LOGOUT = 2, LABELS = 3, ERASE_VIDEO = 100, LIKE_JOB = 300;
    private MyCrouton myCrouton;
    private MyProgressDialog myProgressDialog;
    Context con;
    private ImageView saveVideoButton;

    SaveSecurePreference preference;
    String userId, emailId, passwrd, authToken;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_seeker_video_new, container, false);
        ButterKnife.inject(this, view);
        init(view);
        View actionBarView = getActivity().getActionBar().getCustomView();

        IntentFilter filter = new IntentFilter("uploadSuccessful");
        getActivity().registerReceiver(new Receiver(), filter);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        TextView leftText = (TextView) actionBarView.findViewById(R.id.upload_video_left_text);
        TextView centerText = (TextView) actionBarView.findViewById(R.id.upload_video_center_text);
        ImageView rightImage = (ImageView) actionBarView.findViewById(R.id.rightActionImage);
        ImageView leftImage = (ImageView) actionBarView.findViewById(R.id.leftActionImage);
        TextView titleText = (TextView) actionBarView.findViewById(R.id.titleText);
        FrameLayout frameLayout = (FrameLayout) actionBarView.findViewById(R.id.frameLayout);

        leftText.setVisibility(View.VISIBLE);
        centerText.setVisibility(View.VISIBLE);
        rightImage.setVisibility(View.INVISIBLE);
        titleText.setVisibility(View.GONE);
        leftImage.setVisibility(View.INVISIBLE);
        frameLayout.setVisibility(View.INVISIBLE);
        con = getActivity();

        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                con));

        getActivity().getActionBar().show();

        preference = new SaveSecurePreference(getActivity());
        userId = preference.getPrefValue(WelcomePage.USER_KEY);
        passwrd = preference.getPrefValue(WelcomePage.PASS_KEY);
        emailId = preference.getPrefValue(WelcomePage.EMAIL_KEY);
        authToken = preference.getPrefValue(WelcomePage.AUTH_KEY);

        getActivity().registerReceiver(CancellSession,
                new IntentFilter("uploadSuccessful"));
        myCrouton = new MyCrouton();
        myProgressDialog = new MyProgressDialog(getActivity());
        if (preferences.getBooleantData("UploadOnProgress")) {
            placeHolderImage.setVisibility(View.GONE);
            feedVideoView.setVisibility(View.VISIBLE);
            Uri vidFile = Uri.parse(
                    preferences.getData("myCurrentVideoPath"));
            feedVideoView.setVideoURI(vidFile);
            preferences.saveBooleanData("UploadOnProgress", false);
            prepare();
        } else if (!preference.getPrefValue(WelcomePage.USER_VIDEO).equals("")) {
            placeHolderImage.setVisibility(View.GONE);
            feedVideoView.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(getResources().getString(R.string.url_new_video) + preference.getPrefValue(WelcomePage.USER_VIDEO));
            System.out.println("video name : " + preference.getPrefValue(WelcomePage.USER_VIDEO));
            feedVideoView.setVideoURI(uri);
            prepare();
        } else {
            placeHolderImage.setVisibility(View.VISIBLE);
            feedVideoView.setVisibility(View.GONE);
        }

        feedVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (what == 100) {
                    feedVideoView.stopPlayback();
                }
                return true;
            }
        });
        videoFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    feedVideoView.pause();
                    isPlaying = false;
                } else {
                    feedVideoView.start();
                    isPlaying = true;
                }
            }
        });

        String sampleVideoURL = getResources().getString(R.string.url_new_domain) + "samplevideo.php/samplevideo/";
        RequestParams params = new RequestParams();
        params.put("userid", preference.getPrefValue(WelcomePage.USER_KEY));
        params.put("oauthToken", preference.getPrefValue(WelcomePage.AUTH_KEY));
        AsycHttpCall.getInstance().CallApiPost(sampleVideoURL, params, this, SAVE_CHANGES);
//        String sampleVideoURL = getResources().getString(R.string.url_domain);
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("type", "SampleVideo");
//        volleyForAll.volleyToNetwork(sampleVideoURL, VolleyForAll.HttpRequestType.HTTP_POST, params, SAVE_CHANGES);
        editProfileURL = getResources().getString(R.string.url_new_domain) + "JobseekerEdit.php/jobseekerEdit";
        return view;
    }

    @OnClick(R.id.videoSaveButton)
    public void JobList() {
        saveVideoButton.setClickable(false);
        feedVideoView.pause();
        feedVideoView.stopPlayback();
        if (videoFilePath != null && !videoFilePath.equals("") && feedVideoView != null || !preferences.getData("video").equals("")||!preference.getPrefValue(WelcomePage.USER_VIDEO).equals("")) {
            Intent uploadService = new Intent(getActivity(), UploadService.class);
//            uploadService.putExtra("fullName", preferences.getData("user_name"));
//            uploadService.putExtra("emailId", preferences.getData("user_email"));
//            uploadService.putExtra("address", preferences.getData("user_address"));
//            uploadService.putExtra("chatStatus", "");
//            uploadService.putExtra("pushStatus", "");
//            uploadService.putExtra("description", preferences.getData("SeekerDesc"));
//            uploadService.putExtra("password", preferences.getData("password"));
//            uploadService.putExtra("videoName", videoName);
//            uploadService.putExtra("imageName", imageName);
//            uploadService.putExtra("videoFilePath", videoFilePath);
//            uploadService.putExtra("editProfileURL", editProfileURL);
//            uploadService.putExtra("imageFilePath", imageFilePath);
//            uploadService.putExtra("code", "");
//            uploadService.putExtra("label", preferences.getData("Labels"));
//            getActivity().startService(uploadService);

            uploadService.putExtra("fullName", preferences.getData("user_name"));
            uploadService.putExtra("emailId", emailId);
            uploadService.putExtra("oAuthToken", authToken);
            uploadService.putExtra("userId", userId);
            uploadService.putExtra("address", preferences.getData("user_address"));
            uploadService.putExtra("chatStatus", "");
            uploadService.putExtra("pushStatus", "");
            uploadService.putExtra("description", preferences.getData("SeekerDesc"));
            uploadService.putExtra("password", passwrd);
            uploadService.putExtra("videoName", videoName);
            uploadService.putExtra("imageName", imageName);
            uploadService.putExtra("videoFilePath", videoFilePath);
            uploadService.putExtra("editProfileURL", editProfileURL);
            uploadService.putExtra("imageFilePath", imageFilePath);
            uploadService.putExtra("code", "");
            uploadService.putExtra("label", preferences.getData("Labels"));
            getActivity().startService(uploadService);

            preferences.saveIntData("UPLOAD_LIKE", 1);//Upload video for first time

            preferences.saveData("myCurrentVideoPath", videoFilePath);
            if (!videoFilePath.equalsIgnoreCase(""))
                preferences.saveBooleanData("UploadOnProgress", true);
            preferences.saveData("description", preferences.getData("SeekerDesc"));

            if (preferences.getBooleantData("video_upload_without_like")) {
                saveVideoButton.setClickable(true);
                replaceFragment(new JobListNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "JobListNew");
//                preferences.saveBooleanData("video_upload_without_like",false);
            }

        } else {
            myCrouton.showCrouton(getActivity(), con.getResources().getString(R.string.Por_favor_graba_un_vídeo_antes_de_seguir), 1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        feedVideoView.pause();
        feedVideoView.stopPlayback();
    }


    @OnClick(R.id.questionMark)
    public void Question() {
        feedVideoView.setVisibility(View.GONE);
        Bundle bundle = new Bundle();
        bundle.putString("sampleVideo", sampleVideo);
        ViewSampleFragment sampleFragment = new ViewSampleFragment();
        sampleFragment.setArguments(bundle);
        replaceFragment(sampleFragment, R.id.frame, true, FragmentTransaction.TRANSIT_NONE, "ViewSample");
    }

    @OnClick(R.id.recordNewVideo)
    public void recordNewVideo() {
        Intent intent = new Intent(
                android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
        Calendar calendar = Calendar.getInstance();
        videoName = "Video_" + preferences.getData("user_id") + calendar.get(Calendar.YEAR)
                + calendar.get(Calendar.MONTH)
                + calendar.get(Calendar.DAY_OF_MONTH)
                + calendar.get(Calendar.HOUR_OF_DAY)
                + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) + ".mp4";
        File jobBoxFolder = new File(Environment.getExternalStorageDirectory(), "JobBox");
        if (!jobBoxFolder.exists())
            jobBoxFolder.mkdir();
        else {
            String[] children = jobBoxFolder.list();
            for (String aChildren : children) {
                new File(jobBoxFolder, aChildren).delete();
            }
        }
        File file = new File(jobBoxFolder, videoName);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(file));
        intent.putExtra(
                android.provider.MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        startActivityForResult(intent, 100);
    }


    private void init(View view) {
        view.setClickable(true);
        preferences = new AppPreferences(getActivity(), "JobBoxData");
        volleyForAll = new VolleyForAll(getActivity(), this);
        saveVideoButton = (ImageView) view.findViewById(R.id.videoSaveButton);
    }

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        myProgressDialog.dismissProgress();
        if (code == SAVE_CHANGES) {
            if (result != null) {
                if (result.optBoolean("Result")) {
                    JSONObject jsonObject = result.optJSONObject("Details");
                    sampleVideo = jsonObject.optString("sampleAppvideo");
                }
            }
        } else if (code == LIKE_JOB) {
            if (result.optBoolean("Result"))
                replaceFragment(new JobListNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "JobListNew");
        }
    }

    @Override
    public void onVolleyError(String result, int code) {

    }

    private void prepare() {
        feedVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                feedVideoView.start();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == getActivity().RESULT_OK) {
            try {
                Runtime.getRuntime().freeMemory();
                File jobBoxFolder = new File(Environment.getExternalStorageDirectory(), "JobBox");
                File file = new File(jobBoxFolder, videoName);
                if (file != null) {
                    videoFilePath = file.getAbsolutePath();

                    Uri vidFile = Uri.parse(videoFilePath);
                    feedVideoView.setVideoURI(vidFile);
                    feedVideoView.setVisibility(View.VISIBLE);
                    prepare();

                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(
                            videoFilePath,
                            MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    if (bMap != null) {
                        placeHolderImage.setVisibility(View.GONE);
                    }
                    Calendar calendar = Calendar.getInstance();
                    imageName = "Image_" + preferences.getData("user_id") + calendar.get(Calendar.YEAR)
                            + calendar.get(Calendar.MONTH)
                            + calendar.get(Calendar.DAY_OF_MONTH)
                            + calendar.get(Calendar.HOUR_OF_DAY)
                            + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) + ".jpg";
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bMap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    File f = new File(jobBoxFolder, imageName);
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.close();

                    imageFilePath = f.getAbsolutePath();
                } else {
                    videoFilePath = "";
                    imageFilePath = "";
                    imageName = "";
                    videoName = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Log.d("JobBox", "Cancelled");
        }
    }

    BroadcastReceiver CancellSession = new BroadcastReceiver() {

        @Override
        public void onReceive(Context c, Intent arg1) {
            if (arg1 != null) {
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(CancellSession);
    }

    @Override
    public void onSuccess(String response, int code) {
        myProgressDialog.dismissProgress();
        switch (code) {
            case SAVE_CHANGES:
                parseSampleVideo(response);
                break;
        }
    }

    public void parseSampleVideo(String response) {
        if (response != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();

            SampleVideoGSON sampleGSON = gson.fromJson(response,
                    SampleVideoGSON.class);
            if (sampleGSON.Result) {
                sampleVideo = sampleGSON.Details.sampleAppvideo;
            }
        }
    }

    @Override
    public void onFail(String response, int code) {
        myProgressDialog.dismissProgress();
    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (!preferences.getBooleantData("video_upload_without_like")) {
                System.out.println("Entered condition" + preferences.getBooleantData("video_upload_without_like"));
                Bundle b = getArguments();
                if (b != null) {
                    String userId = b.getString("userIdFromLike");
                    String id = b.getString("id");

                    String jobListURL = getActivity().getResources().getString(R.string.url_domain);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("type", "LikeOffer");
                    params.put("userid", preferences.getData("user_id"));
                    params.put("id", id);
                    params.put("likestatus", "Like");
                    volleyForAll.volleyToNetwork(jobListURL, VolleyForAll.HttpRequestType.HTTP_POST, params, LIKE_JOB);
                }
            }
        }
    }


}
