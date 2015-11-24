package com.app.jobaloon.jobseeker;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.jobaloon.chat.ChatList;
import com.app.jobaloon.company.SearchWindow;
import com.app.jobaloon.gson.JobListGSON;
import com.app.jobaloon.gson.LikeJobGSON;
import com.app.jobaloon.gson.LogoutGSON;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by SICS-Dpc2 on 03-Apr-15.
 */
public class JobListNew extends FragmentActions implements VolleyOnResponseListener, Response {
    @InjectView(R.id.back_to_my_home)
    Button backButton;

    @InjectView(R.id.profileVisitorsButton)
    Button profileVisitorsButton;

    private ImageView chatIcon, settingsIcon;
    private View actionBarView;
    public boolean pageNavigated = false;

    @InjectView(R.id.seeker_searchBar)
    EditText searchBar;

    //    @InjectView(R.id.seeker_searchText)
//    TextView seeker_searchText;
    @InjectView(R.id.jobListName)
    TextView jobListName;
    @InjectView(R.id.jobList_CompanyName)
    TextView jobList_CompanyName;
    @InjectView(R.id.jobList_Direction)
    TextView jobList_Direction;
    @InjectView(R.id.jobList_workingTime)
    TextView jobList_workingTime;
    @InjectView(R.id.likeJobButton)
    ImageView likeJobButton;
    @InjectView(R.id.dislikeJobButton)
    ImageView dislikeJobButton;
    @InjectView(R.id.Inscribirme)
    TextView Inscribirme;
    @InjectView(R.id.Descartar)
    TextView Descartar;
    @InjectView(R.id.mondayCheck)
    CheckBox mondayCheck;
    @InjectView(R.id.tuesdayCheck)
    CheckBox tuesdayCheck;
    @InjectView(R.id.wednesdayCheck)
    CheckBox wednesdayCheck;
    @InjectView(R.id.thursdayCheck)
    CheckBox thursdayCheck;
    @InjectView(R.id.fridayCheck)
    CheckBox fridayCheck;
    @InjectView(R.id.saturdayCheck)
    CheckBox saturdayCheck;
    @InjectView(R.id.sundayCheck)
    CheckBox sundayCheck;
    @InjectView(R.id.profileVisitCountframeLayout)
    FrameLayout profileVisitCountframeLayout;
    @InjectView(R.id.profileVisitCountText)
    TextView profileVisitCountText;
    @InjectView(R.id.addInfoText)
    TextView addInfoText;


    @InjectView(R.id.withoutOfferImage)
    ImageView withoutOfferImage;
    @InjectView(R.id.refreshTextView)
    TextView refreshTextView;
    @InjectView(R.id.refresh_jobOffersButton)
    ImageView refresh_jobOffersButton;
    @InjectView(R.id.scrollLayout)
    ScrollView scrollLayout;

    private static final int JOB_LIST = 1, LIKE_JOB = 2, DISLIKE_JOB = 3, CHAT_COUNT = 4;
    private VolleyForAll volleyForAll;
    private MyCrouton myCrouton;
    private MyProgressDialog myProgressDialog;
    private AppPreferences preferences;
    private ArrayList<HashMap<String, String>> jobArrayList = new ArrayList<HashMap<String, String>>();
    private int position = 0;
    private String[] workingDaysArray;
    private String workingDays;
    private TextView countText;
    private CountDownTimer countDownTimer;
    private FrameLayout frameLayout;
    private Context context;
    private ImageView redCircle;
    FrameLayout ChatcountframeLayout;
    private FragmentActivity con;

    SaveSecurePreference preference;
    String userId, oAuthToken;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_list_seeker_new, container, false);

        actionBarView = getActivity().getActionBar().getCustomView();
        preferences = new AppPreferences(getActivity(), "JobBoxData");
        volleyForAll = new VolleyForAll(getActivity(), this);
        myCrouton = new MyCrouton();
        myProgressDialog = new MyProgressDialog(getActivity());
        view.setClickable(true);
        ButterKnife.inject(this, view);
        context = getActivity();
        con = getActivity();
//        preferences.saveBooleanData("video_upload_without_like",false);

        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                con));

        preference = new SaveSecurePreference(getActivity());
        userId = preference.getPrefValue(WelcomePage.USER_KEY);
        oAuthToken = preference.getPrefValue(WelcomePage.AUTH_KEY);

        settingsIcon = (ImageView) actionBarView.findViewById(R.id.leftActionImage);
        chatIcon = (ImageView) actionBarView.findViewById(R.id.rightActionImage);
        settingsIcon.setImageDrawable(getResources().getDrawable(R.drawable.settings));
        TextView leftText = (TextView) actionBarView.findViewById(R.id.upload_video_left_text);
        TextView titleText = (TextView) actionBarView.findViewById(R.id.titleText);
        TextView centerText = (TextView) actionBarView.findViewById(R.id.upload_video_center_text);
        TextView countVisitorsText = (TextView) actionBarView.findViewById(R.id.countVisitorsText);
        ChatcountframeLayout = (FrameLayout) actionBarView.findViewById(R.id.frameLayout);
        redCircle = (ImageView) actionBarView.findViewById(R.id.redCircle);
        //making the visitor count gone here for making sure it is not showing for company profile.
        countVisitorsText.setVisibility(View.GONE);
        leftText.setVisibility(View.GONE);
        centerText.setVisibility(View.GONE);
        titleText.setText(context.getResources().getString(R.string.app_name));
        titleText.setVisibility(View.VISIBLE);
        getActivity().getActionBar().show();
        frameLayout = (FrameLayout) actionBarView.findViewById(R.id.frameLayout);
        countText = (TextView) actionBarView.findViewById(R.id.countText);
        chatIcon.setImageDrawable(getResources().getDrawable(R.drawable.msg));
        chatIcon.setVisibility(View.VISIBLE);
        settingsIcon.setVisibility(View.VISIBLE);
        chatIcon.setClickable(true);
        settingsIcon.setClickable(true);
        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNavigated = true;
                ChatcountframeLayout.setVisibility(View.GONE);
                countDownTimer.cancel();
                addFragment(new ChatList(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "chat");
            }
        });
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragment(new EditJobSeekerProfile(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "chat");
            }
        });

        //---------------------------chat count for a candidate-----------------------------------------//

//        String chatCountLink = con.getResources().getString(R.string.url_domain);
//        HashMap<String, String> chatparams = new HashMap<String, String>();
//        chatparams.put("type", "ChatCount");
//        chatparams.put("userid", preferences.getData("user_id"));
//        volleyForAll.volleyToNetwork(chatCountLink, VolleyForAll.HttpRequestType.HTTP_POST, chatparams, CHAT_COUNT);

        String chatCountLink = con.getResources().getString(R.string.url_new_domain) + "chatCount.php/chatCount/";
        RequestParams chatparams = new RequestParams();
        chatparams.put("userid", preference.getPrefValue(WelcomePage.USER_KEY));
        chatparams.put("oauthToken", preference.getPrefValue(WelcomePage.AUTH_KEY));
        AsycHttpCall.getInstance().CallApiPost(chatCountLink, chatparams, this, CHAT_COUNT);

        //---------------------------chat count for a candidate-----------------------------------------//
        myProgressDialog.setProgress();
//        String jobListURL = context.getResources().getString(R.string.url_domain);
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("type", "viewAllJobs");
//        params.put("label", preferences.getData("Labels"));
//        params.put("userid", preferences.getData("user_id"));
//        params.put("fromaddress", preferences.getData("FromAddress"));
//        params.put("distanseLimit", "");
//        if (preferences.getData("SeekerDesc") != null && !preferences.getData("SeekerDesc").equals("")) {
//            params.put("description", preferences.getData("SeekerDesc"));
//        } else {
//            params.put("description", preferences.getData("description"));
//        }
//        volleyForAll.volleyToNetwork(jobListURL, VolleyForAll.HttpRequestType.HTTP_POST, params, JOB_LIST);

        String jobListURL = context.getResources().getString(R.string.url_new_domain) + "viewAllJobs.php/viewAllJobs/";
        RequestParams params = new RequestParams();
        params.put("oauthToken", oAuthToken);
        params.put("userid", userId);
        try {
            params.put("fromaddress", URLDecoder.decode(preferences.getData("FromAddress"), "UTF-8"));
            params.put("label", URLDecoder.decode(preferences.getData("Labels"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (preferences.getData("SeekerDesc") != null && !preferences.getData("SeekerDesc").equals("")) {
            params.put("description", preferences.getData("SeekerDesc"));
        } else {
            params.put("description", preferences.getData("description"));
        }
        params.put("distanceLimit", "10");
        System.out.println("====== params : " + params);
        AsycHttpCall.getInstance().CallApiPost(jobListURL, params, this, JOB_LIST);

        if (preferences.getIntData("UPLOAD_LIKE") != 0) {

        }

        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                String chatCountLink = context.getResources().getString(R.string.url_domain);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("type", "ChatCount");
                params.put("userid", preferences.getData("user_id"));
                volleyForAll.volleyToNetwork(chatCountLink, VolleyForAll.HttpRequestType.HTTP_POST, params, CHAT_COUNT);
                countDownTimer.start();
            }
        }.start();
        return view;
    }

    @OnClick(R.id.back_to_my_home)
    public void BackPage() {
        countDownTimer.cancel();
        preferences.saveBooleanData("video_upload_without_like", true);
        replaceFragment(new UploadVideoSeekerNew(), R.id.frame, true, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "UploadVideoSeekerNew");
    }

    @OnClick(R.id.likeJobButton)
    public void LikeJob() {
        if (position < jobArrayList.size()) {
            if (preferences.getIntData("UPLOAD_LIKE") == 0 && preferences.getData("video").equals("") && preference.getPrefValue(WelcomePage.USER_VIDEO).equals("")) {
//                preferences.saveBooleanData("video_upload_without_like", false);
                Bundle b = new Bundle();
                b.putString("userIdFromLike", preferences.getData("user_id"));
                b.putString("id", jobArrayList.get(position).get("job_id"));
                UploadVideoSeekerNew upload = new UploadVideoSeekerNew();
                upload.setArguments(b);
                addFragment(upload, R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "JobSearchPage");
            } else {
//                String jobListURL = context.getResources().getString(R.string.url_domain);
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("type", "LikeOffer");
//                params.put("userid", preferences.getData("user_id"));
//                params.put("id", jobArrayList.get(position).get("job_id"));
//                params.put("likestatus", "Like");
//                volleyForAll.volleyToNetwork(jobListURL, VolleyForAll.HttpRequestType.HTTP_POST, params, LIKE_JOB);

                String jobLikeURL = context.getResources().getString(R.string.url_new_domain) + "likeoffer";
                RequestParams params = new RequestParams();
                params.put("userid", userId);
                params.put("oauthToken", oAuthToken);
                params.put("offerid", jobArrayList.get(position).get("job_id"));
                params.put("status", "Like");
                params.put("type", "");
                AsycHttpCall.getInstance().CallApiPost(jobLikeURL, params, this, LIKE_JOB);
            }

        } else {
            setVisibility();
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (preferences.getIntData("UPLOAD_LIKE") != 0 && !preferences.getData("video").equals("")) {
//
//            String jobListURL = context.getResources().getString(R.string.url_domain);
//            HashMap<String, String> params = new HashMap<String, String>();
//            params.put("type", "LikeOffer");
//            params.put("userid", preferences.getData("user_id"));
//            params.put("id", jobArrayList.get(position).get("job_id"));
//            params.put("likestatus", "Like");
//            volleyForAll.volleyToNetwork(jobListURL, VolleyForAll.HttpRequestType.HTTP_POST, params, LIKE_JOB);
//        }
//    }

    @OnClick(R.id.dislikeJobButton)
    public void dislikeJob() {
        if (position < jobArrayList.size()) {
//            String jobListURL = context.getResources().getString(R.string.url_domain);
//            HashMap<String, String> params = new HashMap<String, String>();
//            params.put("type", "LikeOffer");
//            params.put("userid", preferences.getData("user_id"));
//            params.put("id", jobArrayList.get(position).get("job_id"));
//            params.put("likestatus", "Dislike");
//            volleyForAll.volleyToNetwork(jobListURL, VolleyForAll.HttpRequestType.HTTP_POST, params, DISLIKE_JOB);
            System.out.println("jsacjhc");
            String jobDislikeURL = context.getResources().getString(R.string.url_new_domain) + "likeoffer/";
            RequestParams params = new RequestParams();
            params.put("userid", userId);
            params.put("oauthToken", oAuthToken);
            params.put("offerid", jobArrayList.get(position).get("job_id"));
            params.put("status", "Dislike");
            params.put("type", "");
            System.out.println("jasjd" + params);
            AsycHttpCall.getInstance().CallApiPost(jobDislikeURL, params, this, DISLIKE_JOB);
        } else {
            setVisibility();
        }
    }

    @OnClick(R.id.profileVisitorsButton)
    public void profileVisitors() {
        profileVisitCountframeLayout.setVisibility(View.GONE);
        addFragment(new ProfileViewersFragment(), R.id.frame, true, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "ProfileViewers");
    }

    @OnClick(R.id.refresh_jobOffersButton)
    public void refresh_jobOffers() {
        myProgressDialog.setProgress();
        String jobListURL = context.getResources().getString(R.string.url_new_domain) + "viewAllJobs.php/viewAllJobs/";
        RequestParams params = new RequestParams();
        params.put("oauthToken", oAuthToken);
        params.put("userid", userId);
        try {
            params.put("fromaddress", URLDecoder.decode(preferences.getData("FromAddress"), "UTF-8"));
            params.put("label", URLDecoder.decode(preferences.getData("Labels"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (preferences.getData("SeekerDesc") != null && !preferences.getData("SeekerDesc").equals("")) {
            params.put("description", preferences.getData("SeekerDesc"));
        } else {
            params.put("description", preferences.getData("description"));
        }
        params.put("distanceLimit", "10");
        System.out.println("====== params : " + params);
        AsycHttpCall.getInstance().CallApiPost(jobListURL, params, this, JOB_LIST);
//        String jobListURL = context.getResources().getString(R.string.url_domain);
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("type", "viewAllJobs");
//        params.put("label", preferences.getData("Labels"));
//        params.put("userid", preferences.getData("user_id"));
//        params.put("fromaddress", preferences.getData("FromAddress"));
//        params.put("distanseLimit", "");
//        volleyForAll.volleyToNetwork(jobListURL, VolleyForAll.HttpRequestType.HTTP_POST, params, JOB_LIST);
    }


    @OnClick(R.id.myApplicationList)
    public void myApplicationList() {
        addFragment(new SeeMyApplications(), R.id.frame, true, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "seeMyApplications");
    }

    @OnClick(R.id.seeker_searchBar)
    public void setSearchBar() {
        pageNavigated = true;
        SearchWindow.searchGSON = null;
//        addFragmentFromUp(new JobSeekerHomeNew(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "SearchWindow");
        addFragmentFromUp(new JobSeekerHomeWithoutDesc(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "SearchWindow");
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        countDownTimer.cancel();
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        countDownTimer.cancel();
    }

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        myProgressDialog.dismissProgress();
        if (code == JOB_LIST) {
            if (result.optBoolean("Result")) {
                JSONArray jarray = result.optJSONArray("Details");
                if (jarray != null) {
                    scrollLayout.setVisibility(View.VISIBLE);
                    likeJobButton.setVisibility(View.VISIBLE);
                    Inscribirme.setVisibility(View.VISIBLE);
                    dislikeJobButton.setVisibility(View.VISIBLE);
                    Descartar.setVisibility(View.VISIBLE);
//                    seeker_searchText.setVisibility(View.VISIBLE);
                    withoutOfferImage.setVisibility(View.GONE);
                    refreshTextView.setVisibility(View.GONE);
                    refresh_jobOffersButton.setVisibility(View.GONE);

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject jobj = jarray.optJSONObject(i);
                        HashMap<String, String> hMaps = new HashMap<String, String>();
                        hMaps.put("job_id", jobj.optString("id"));
                        hMaps.put("Role", jobj.optString("Role"));
                        hMaps.put("start_date", jobj.optString("start_date"));
                        hMaps.put("end_date", jobj.optString("end_date"));
                        hMaps.put("working_days", jobj.optString("working_days"));
                        hMaps.put("location", jobj.optString("location"));
                        hMaps.put("distance", jobj.optString("distance"));
                        hMaps.put("jobDescription", jobj.optString("jobDescription"));
                        hMaps.put("compony_id", jobj.optString("compony_id"));
                        hMaps.put("user_name", jobj.optString("user_name"));
                        hMaps.put("add_Info", jobj.optString("additional_information"));
                        jobArrayList.add(hMaps);
                    }
                    if (!jobArrayList.isEmpty() || jobArrayList != null) {
//                        seeker_searchText.setText("Tipo de Trabajo : " + jobArrayList.get(0).get("jobDescription"));
                        if (!jobArrayList.get(0).get("jobDescription").equals(""))
                            jobListName.setText(jobArrayList.get(0).get("jobDescription"));
                        jobList_CompanyName.setText(jobArrayList.get(0).get("user_name"));
                        addInfoText.setText(jobArrayList.get(0).get("add_Info"));
                        jobList_Direction.setText(jobArrayList.get(0).get("location"));
                        jobList_workingTime.setText(jobArrayList.get(0).get("start_date").substring(0, jobArrayList.get(0).get("start_date").length() - 3) + " - " + jobArrayList.get(0).get("end_date").substring(0, jobArrayList.get(0).get("start_date").length() - 3));
                        workingDays = jobArrayList.get(0).get("working_days");
                        daysChecked(workingDays);
                    } else
                        myCrouton.showCrouton(getActivity(), context.getResources().getString(R.string.No_hay_más_ofertas_para_este_tipo_de_trabajo), 1);
                } else {
                    setVisibility();
                }
            } else {
                setVisibility();
            }
        } else if (code == LIKE_JOB) {
            if (result.optBoolean("Result")) {
                position++;
                if (position < jobArrayList.size()) {
//                    if (preferences.getIntData("UPLOAD_LIKE") == 0) {
//                        System.out.println("-- Entered condition");
//                        addFragment(new UploadVideoSeekerNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "JobSearchPage");
//                    } else {
//                        System.out.println("-- not Entered condition");
//                    }
//                    seeker_searchText.setText("Tipo de Trabajo : " + jobArrayList.get(position).get("jobDescription"));
                    if (!jobArrayList.get(position).get("jobDescription").equals(""))
                        jobListName.setText(jobArrayList.get(position).get("jobDescription"));
                    jobList_CompanyName.setText(jobArrayList.get(position).get("user_name"));
                    addInfoText.setText(jobArrayList.get(position).get("add_Info"));
                    jobList_Direction.setText(jobArrayList.get(position).get("location"));
                    jobList_workingTime.setText(jobArrayList.get(position).get("start_date").substring(0, jobArrayList.get(position).get("start_date").length() - 3) + " - " + jobArrayList.get(position).get("end_date").substring(0, jobArrayList.get(position).get("start_date").length() - 3));
                    workingDays = jobArrayList.get(position).get("working_days");
                    daysChecked(workingDays);
                } else {
                    setVisibility();
                }
            }
        } else if (code == DISLIKE_JOB) {
            if (result.optBoolean("Result")) {
                position++;
                if (position < jobArrayList.size()) {
//                    seeker_searchText.setText("Tipo de Trabajo : " + jobArrayList.get(position).get("jobDescription"));
                    if (!jobArrayList.get(position).get("jobDescription").equals(""))
                        jobListName.setText(jobArrayList.get(position).get("jobDescription"));
                    jobList_CompanyName.setText(jobArrayList.get(position).get("user_name"));
                    addInfoText.setText(jobArrayList.get(position).get("add_Info"));
                    jobList_Direction.setText(jobArrayList.get(position).get("location"));
                    jobList_workingTime.setText(jobArrayList.get(position).get("start_date").substring(0, jobArrayList.get(position).get("start_date").length() - 3) + " - " + jobArrayList.get(position).get("end_date").substring(0, jobArrayList.get(position).get("end_date").length() - 3));
                    workingDays = jobArrayList.get(position).get("working_days");
                    daysChecked(workingDays);
                } else {
                    setVisibility();
                }
            }
        } else if (code == CHAT_COUNT) {
            try {
                if (result.optBoolean("Result")) {
                    int count = result.optInt("Count");
                    int VisitorCount = result.optInt("VisitorCount");
                    if (VisitorCount != 0) {
                        profileVisitCountframeLayout.setVisibility(View.VISIBLE);
                        profileVisitCountText.setVisibility(View.VISIBLE);
                        redCircle.setVisibility(View.VISIBLE);
                        if (VisitorCount > 9) {
                            profileVisitCountText.setText("9+");
                        } else {
                            profileVisitCountText.setText("" + VisitorCount);
                        }
                    } else {
                        profileVisitCountframeLayout.setVisibility(View.GONE);
                        profileVisitCountText.setVisibility(View.GONE);
                    }
                    if (count != 0) {
                        frameLayout.setVisibility(View.VISIBLE);
                        if (count > 9) {
                            countText.setText("9+");
                        } else {
                            countText.setText("" + count);
                        }
                    } else {
                        frameLayout.setVisibility(View.GONE);
                    }
                }
                result = result.optJSONObject("Details");
                preferences.saveData("user_id", result.optString("user_id"));
                preferences.saveData("user_name", result.optString("user_name"));
                preferences.saveData("user_email", result.optString("user_email"));
                preferences.saveData("user_address", result.optString("user_address"));
                preferences.saveData("chat_notification", result.optString("chat_notification"));
                preferences.saveData("profile_visitor", result.optString("profile_visitor"));
                preferences.saveData("user_type", result.optString("user_type"));
                preferences.saveData("password", result.optString("password"));
                preferences.saveData("video", result.optString("video"));
                preferences.saveData("image", result.optString("image"));
                preferences.saveData("profile_notification", result.optString("profile_visitor"));
                preferences.saveData("description", result.optString("Notes"));
                preferences.saveBooleanData("loggedIn", true);
                preferences.saveData("video", result.optString("video"));
                preferences.saveData("profile_notification", result.optString("profile_visitor"));
                preferences.saveData("description", result.optString("Notes"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    @Override
    public void onVolleyError(String result, int code) {
        if (code == JOB_LIST) {
            setVisibility();
        }
    }

    public void setVisibility() {
        myCrouton.showCrouton(con, context.getResources().getString(R.string.No_hay_más_ofertas_para_este_tipo_de_trabajo), 1);
        withoutOfferImage.setVisibility(View.VISIBLE);
        refreshTextView.setVisibility(View.VISIBLE);
        refresh_jobOffersButton.setVisibility(View.VISIBLE);
        scrollLayout.setVisibility(View.GONE);
        likeJobButton.setVisibility(View.GONE);
        Inscribirme.setVisibility(View.GONE);
        dislikeJobButton.setVisibility(View.GONE);
        Descartar.setVisibility(View.GONE);
    }

    public void daysChecked(String workingDays) {
        if (workingDays.contains("L"))
            mondayCheck.setChecked(true);
        else
            mondayCheck.setChecked(false);
        if (workingDays.contains("M"))
            tuesdayCheck.setChecked(true);
        else
            tuesdayCheck.setChecked(false);
        if (workingDays.contains("X"))
            wednesdayCheck.setChecked(true);
        else
            wednesdayCheck.setChecked(false);
        if (workingDays.contains("J"))
            thursdayCheck.setChecked(true);
        else
            thursdayCheck.setChecked(false);
        if (workingDays.contains("V"))
            fridayCheck.setChecked(true);
        else
            fridayCheck.setChecked(false);
        if (workingDays.contains("S"))
            saturdayCheck.setChecked(true);
        else
            saturdayCheck.setChecked(false);
        if (workingDays.contains("D"))
            sundayCheck.setChecked(true);
        else
            sundayCheck.setChecked(false);
    }

    @Override
    public void onSuccess(String response, int code) {
        myProgressDialog.dismissProgress();
        switch (code) {
            case JOB_LIST:
                System.out.println("------ job : " + response);
                parseJobDetails(response);
                break;

            case LIKE_JOB:
                System.out.println("------ like : " + response);
                likeJob(response);
                break;

            case DISLIKE_JOB:
                System.out.println("------ Dislike : " + response);
                disLikeJob(response);
                break;
        }
    }

    @Override
    public void onFail(String response, int code) {
        if (code == JOB_LIST) {
            setVisibility();
        }
        if (code == DISLIKE_JOB)
            System.out.println("------ error" + response);
        if (code == LIKE_JOB)
            System.out.println("------ error" + response);
    }

    public void parseJobDetails(String response) {
        if (response != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();

            JobListGSON jobListGSON = gson.fromJson(response,
                    JobListGSON.class);
            if (jobListGSON != null) {
                if (jobListGSON.Result) {
                    if (jobListGSON.Details != null) {
                        scrollLayout.setVisibility(View.VISIBLE);
                        likeJobButton.setVisibility(View.VISIBLE);
                        Inscribirme.setVisibility(View.VISIBLE);
                        dislikeJobButton.setVisibility(View.VISIBLE);
                        Descartar.setVisibility(View.VISIBLE);
                        withoutOfferImage.setVisibility(View.GONE);
                        refreshTextView.setVisibility(View.GONE);
                        refresh_jobOffersButton.setVisibility(View.GONE);
                        for (int i = 0; i < jobListGSON.Details.size(); i++) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("job_id", jobListGSON.Details.get(i).id);
                            hashMap.put("Role", jobListGSON.Details.get(i).Role);
                            hashMap.put("end_date", jobListGSON.Details.get(i).end_date);
                            hashMap.put("start_date", jobListGSON.Details.get(i).start_date);
                            hashMap.put("location", jobListGSON.Details.get(i).location);
                            hashMap.put("distance", jobListGSON.Details.get(i).distance);
                            hashMap.put("jobDescription", jobListGSON.Details.get(i).jobDescription);
                            hashMap.put("compony_id", jobListGSON.Details.get(i).compony_id);
                            hashMap.put("user_name", jobListGSON.Details.get(i).user_name);
                            hashMap.put("add_Info", jobListGSON.Details.get(i).additional_information);
                            jobArrayList.add(hashMap);
                        }

                        if (!jobArrayList.isEmpty() || jobArrayList != null) {
//                        seeker_searchText.setText("Tipo de Trabajo : " + jobArrayList.get(0).get("jobDescription"));
                            if (!jobArrayList.get(0).get("jobDescription").equals(""))
                                jobListName.setText(jobArrayList.get(0).get("jobDescription"));
                            jobList_CompanyName.setText(jobArrayList.get(0).get("user_name"));
                            addInfoText.setText(jobArrayList.get(0).get("add_Info"));
                            jobList_Direction.setText(jobArrayList.get(0).get("location"));
                            jobList_workingTime.setText(jobArrayList.get(0).get("start_date").substring(0, jobArrayList.get(0).get("start_date").length() - 3) + " - " + jobArrayList.get(0).get("end_date").substring(0, jobArrayList.get(0).get("start_date").length() - 3));
                            workingDays = jobArrayList.get(0).get("working_days");
                            daysChecked(workingDays);
                        } else {
                        }

                    } else {
                        setVisibility();
                    }
                } else {
                    setVisibility();
                }
            }
        } else {
            setVisibility();
            myCrouton.showCrouton(getActivity(), context.getResources().getString(R.string.No_hay_más_ofertas_para_este_tipo_de_trabajo), 1);
        }
    }

    public void likeJob(String response) {
        if (response != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();

            LikeJobGSON likeJobGSON = gson.fromJson(response,
                    LikeJobGSON.class);
            if (likeJobGSON.Result) {
                position++;
                if (position < jobArrayList.size()) {
                    if (!jobArrayList.get(position).get("jobDescription").equals(""))
                        jobListName.setText(jobArrayList.get(position).get("jobDescription"));
                    jobList_CompanyName.setText(jobArrayList.get(position).get("user_name"));
                    addInfoText.setText(jobArrayList.get(position).get("add_Info"));
                    jobList_Direction.setText(jobArrayList.get(position).get("location"));
                    jobList_workingTime.setText(jobArrayList.get(position).get("start_date").substring(0, jobArrayList.get(position).get("start_date").length() - 3) + " - " + jobArrayList.get(position).get("end_date").substring(0, jobArrayList.get(position).get("start_date").length() - 3));
                    workingDays = jobArrayList.get(position).get("working_days");
                    daysChecked(workingDays);
                } else {
                    setVisibility();
                }
            }
        }
    }

    public void disLikeJob(String response) {
        if (response != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();

            LikeJobGSON dislikeJobGSON = gson.fromJson(response,
                    LikeJobGSON.class);
            System.out.println("jhsdbfjhd" + dislikeJobGSON.Result);
            if (dislikeJobGSON.Result) {

                position++;
                if (position < jobArrayList.size()) {
                    if (!jobArrayList.get(position).get("jobDescription").equals(""))
                        jobListName.setText(jobArrayList.get(position).get("jobDescription"));
                    jobList_CompanyName.setText(jobArrayList.get(position).get("user_name"));
                    addInfoText.setText(jobArrayList.get(position).get("add_Info"));
                    jobList_Direction.setText(jobArrayList.get(position).get("location"));
                    jobList_workingTime.setText(jobArrayList.get(position).get("start_date").substring(0, jobArrayList.get(position).get("start_date").length() - 3) + " - " + jobArrayList.get(position).get("end_date").substring(0, jobArrayList.get(position).get("end_date").length() - 3));
                    workingDays = jobArrayList.get(position).get("working_days");
                    daysChecked(workingDays);
                } else {
                    setVisibility();
                }
            }
        }
    }
}
