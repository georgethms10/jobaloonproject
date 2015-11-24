package com.app.jobaloon.main;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jobaloon.gson.ChatListGSON;
import com.app.jobaloon.gson.UserLoginGSON;
import com.app.jobaloon.utils.AsycHttpCall;
import com.app.jobaloon.utils.NetworkCall;
import com.app.jobaloon.utils.Response;
import com.app.jobaloon.utils.SaveSecurePreference;
import com.bumptech.glide.Glide;
import com.app.jobaloon.UtilsGcm;
import com.app.jobaloon.company.SearchPageNew;
import com.app.jobaloon.jobseeker.JobListNew;
import com.app.jobaloon.utils.AppPreferences;
import com.app.jobaloon.utils.FragmentActions;
import com.app.jobaloon.utils.MyCrouton;
import com.app.jobaloon.utils.MyProgressDialog;
import com.app.jobaloon.utils.UncaughtExceptionReporter;
import com.app.jobaloon.utils.Validation;
import com.app.jobaloon.utils.VolleyForAll;
import com.app.jobaloon.utils.VolleyOnResponseListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by SICS-Dpc2 on 23-Jan-15.
 */
public class WelcomePage extends FragmentActions implements VolleyOnResponseListener, Response {
    private static final int INIT = 1, LOGIN = 2, DEVICE_TOKEN = 3;
    @InjectView(R.id.title)
    TextView titleView;
    @InjectView(R.id.subTitle)
    TextView subTitle;
    @InjectView(R.id.homeImageView)
    ImageView homeImageView;
    private String image, title, message;
    private MyCrouton myCrouton;
    private FragmentActivity _activity;
    private MyProgressDialog myProgressDialog;
    private VolleyForAll volleyForAll;
    private Validation validation;
    private AppPreferences preferences;
    Dialog d = null;
    Context context;

    private SharedPreferences mSecurePrefs;
    public static final String PASS_KEY = "Foo";
    public static final String AUTH_KEY = "sec";
    public static final String EMAIL_KEY = "emailSec";
    public static final String USER_KEY = "userSec";
    public static final String USER_VIDEO = "videoSec";
    public static final String USER_IMAGE = "imageSec";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();

        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                _activity));

        if (preferences.getBooleantData("loggedIn")) {
            if (preferences.getData("user_type").equalsIgnoreCase("Company")) {
                addFragment(new SearchPageNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "SearchPage");
            } else {
                if (preferences.getBooleantData("UPLOAD")) {
                    replaceFragment(new JobListNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "JobSeekerHome");
                    preferences.saveBooleanData("UPLOAD", false);
                } else {
                    replaceFragment(new JobListNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "JobSeekerHome");
                }
            }
        } else {
            View view = inflater.inflate(R.layout.activity_welcome_page, container, false);
            view.setClickable(true);
            ButterKnife.inject(this, view);
            init();
            getActivity().getActionBar().hide();
            View actionBarView = getActivity().getActionBar().getCustomView();
            ImageView leftActionImage = (ImageView) actionBarView.findViewById(R.id.leftActionImage);
            ImageView rightActionImage = (ImageView) actionBarView.findViewById(R.id.rightActionImage);
            leftActionImage.setImageDrawable(getResources().getDrawable(R.drawable.action_bar_icon));
            rightActionImage.setVisibility(View.INVISIBLE);
            leftActionImage.setVisibility(View.VISIBLE);
            String initURL = getResources().getString(R.string.url_domain);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("type", "initial");
            volleyForAll.volleyToNetwork(initURL, VolleyForAll.HttpRequestType.HTTP_POST, params, INIT);
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Hero Light.otf");
            titleView.setTypeface(font);
            subTitle.setTypeface(font);
            return view;
        }

        return null;
    }

    private void init() {
        _activity = getActivity();
        myCrouton = new MyCrouton();
        myProgressDialog = new MyProgressDialog(_activity);
        volleyForAll = new VolleyForAll(getActivity(), this);
        validation = new Validation();
        preferences = new AppPreferences(_activity, "JobBoxData");
    }

    @OnClick(R.id.createAccountBtn)
    public void createAccount() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        CreateAccountMain createAccountMain = new CreateAccountMain();
        Bundle bundle = new Bundle();
        bundle.putString("image", image);
        bundle.putString("title", title);
        bundle.putString("message", message);
        createAccountMain.setArguments(bundle);
        addFragment(createAccountMain, R.id.frame, true, FragmentTransaction.TRANSIT_NONE, "createAccountMain");
    }

    @OnClick(R.id.accessMyAccountBtn)
    public void accessMyAccount() {
//        int i = 1 / 0;
        d = new Dialog(getActivity());
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.login);
        final EditText emailEdit = (EditText) d.findViewById(R.id.emailEdit);
        final EditText passwordEdit = (EditText) d.findViewById(R.id.passwordEdit);
        passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    boolean isValidEditText = validation.validateEditTexts(emailEdit, passwordEdit);
                    if (isValidEditText) {
                        String email = emailEdit.getText().toString().trim();
                        if (validation.isValidEmail(email)) {
                            String password = passwordEdit.getText().toString().trim();
                            loginNow(email, password);
                        } else
                            emailEdit.setError(getResources().getString(R.string.enter_valid_email));
                    } else {
                        emailEdit.setError(getResources().getString(R.string.this_field_is_requierd));
                        passwordEdit.setError(getResources().getString(R.string.this_field_is_requierd));
                    }
                }
                return true;
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        d.show();
        d.getWindow().setAttributes(lp);
    }


    private void loginNow(String emailId, String password) {
        myProgressDialog.setProgress();
        try {
            password = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String loginURL = getActivity().getResources().getString(R.string.url_new_domain) + "login.php/login/";
        RequestParams params = new RequestParams();
        params.put("type", "login");
        params.put("user_email", emailId);
        params.put("user_password", password);
        preferences.saveData("deviceToken", UtilsGcm.registrationId);
        AsycHttpCall.getInstance().CallApiPost(loginURL, params, this, LOGIN);


//        volleyForAll.volleyToNetwork(loginURL, VolleyForAll.HttpRequestType.HTTP_POST, params, LOGIN);
    }


    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        myProgressDialog.dismissProgress();
        switch (code) {
            case INIT:
                parseInitValues(result);
                break;
//            case LOGIN:
//                parseLogin(result);
//                break;
            case DEVICE_TOKEN:
                break;
            default:
                break;
        }
    }

    @Override
    public void onVolleyError(String result, int code) {
        myProgressDialog.dismissProgress();
        myCrouton.showCrouton(_activity, result, 1);
    }

    private void parseInitValues(JSONObject result) {


        result = result.optJSONObject("Details");
        if (result != null) {
            image = result.optString("image");
            title = result.optString("Title");
            message = result.optString("Message");
            if (image != null) {
                Glide.with(_activity).load(_activity.getResources().getString(R.string.url_samples) + image).placeholder(R.drawable.welcome_bg).into(homeImageView);
            }

        } else {
            title = _activity.getResources().getString(R.string.app_name);
            message = _activity.getResources().getString(R.string.La_primera_impresión_es_lo_que_cuenta);
        }
        titleView.setText(title);
        subTitle.setText(message);
    }


    // to get the version name
    private String getVersionName() {
        PackageInfo pInfo;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "Failed to get Version Name";
    }

    @Override
    public void onSuccess(String response, int code) {
        myProgressDialog.dismissProgress();
        switch (code) {
            case LOGIN:
                saveUserDetails(response);
                break;

            case DEVICE_TOKEN:
                break;
        }
    }

    @Override
    public void onFail(String response, int code) {
        myProgressDialog.dismissProgress();
    }

    public void saveUserDetails(String response) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Log.e("Response", response);
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        Gson gson = gsonBuilder.create();

        UserLoginGSON loginGSON = gson.fromJson(response,
                UserLoginGSON.class);
        if (loginGSON != null) {
            d.cancel();
            if (loginGSON.Result) {
                preferences.saveBooleanData("loggedIn", true);
                SaveSecurePreference preference = new SaveSecurePreference(getActivity());
                preference.setPrefValue(WelcomePage.PASS_KEY, loginGSON.Details.password);
                preference.setPrefValue(WelcomePage.AUTH_KEY, loginGSON.Details.oauthToken);
                preference.setPrefValue(WelcomePage.EMAIL_KEY, loginGSON.Details.user_email);
                preference.setPrefValue(WelcomePage.USER_KEY, loginGSON.Details.user_id);
                preference.setPrefValue(WelcomePage.USER_VIDEO, loginGSON.Details.video);
                preference.setPrefValue(WelcomePage.USER_IMAGE, loginGSON.Details.image);
                preferences.saveData("user_name", loginGSON.Details.user_name);
                preferences.saveData("user_address", loginGSON.Details.user_address);
                preferences.saveData("user_type", loginGSON.Details.user_type);
                preferences.saveData("experience1", loginGSON.Details.experience1);
                preferences.saveData("experience2", loginGSON.Details.experience2);
                preferences.saveData("experience3", loginGSON.Details.experience3);
                preferences.saveData("prevLabel", URLDecoder.decode(loginGSON.Details.JobLabel));
                preferences.saveData("Distance", loginGSON.Details.distance);
                preferences.saveBooleanData("Jobpost", loginGSON.Details.Jobpost);
                preferences.saveData("lastSearchid", loginGSON.Details.lastSearchid);
                preferences.saveData("jobName", loginGSON.Details.Jobdescription);
                preferences.saveData("Selected_offer_id", loginGSON.Details.JobId);
                preferences.saveData("Labels", URLDecoder.decode(loginGSON.Details.JobLabel));
                if (loginGSON.Details.user_type.equals("Jobseeker"))
                    preferences.saveData("FromAddress", loginGSON.Details.user_address);
                else
                    preferences.saveData("FromAddress", loginGSON.Details.location);

                if (loginGSON.Details.user_type.equalsIgnoreCase("Company"))
                    addFragment(new SearchPageNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN,
                            "SearchPage");
                else
                    replaceFragment(new JobListNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN,
                            "JobSeekerHome");
            } else
                myCrouton.showCrouton(getActivity(), _activity.getResources().getString(R.string.Usuario_o_contraseña_incorrecto), 1);
        }
    }
}
