package com.app.jobaloon.jobseeker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.app.jobaloon.gson.EditJobSeekerGSON;
import com.app.jobaloon.gson.SeekerRegGSON;
import com.app.jobaloon.main.R;
import com.app.jobaloon.main.WelcomePage;
import com.app.jobaloon.utils.AppPreferences;
import com.app.jobaloon.utils.AsycHttpCall;
import com.app.jobaloon.utils.ClearBackStack;
import com.app.jobaloon.utils.FragmentActions;
import com.app.jobaloon.utils.HideKeyBoard;
import com.app.jobaloon.utils.MyCrouton;
import com.app.jobaloon.utils.MyProgressDialog;
import com.app.jobaloon.utils.PlacesAutoCompleteAdapter;
import com.app.jobaloon.utils.Response;
import com.app.jobaloon.utils.SaveSecurePreference;
import com.app.jobaloon.utils.UncaughtExceptionReporter;
import com.app.jobaloon.utils.Validation;
import com.app.jobaloon.utils.VolleyForAll;
import com.app.jobaloon.utils.VolleyOnResponseListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Shadila U on 23-Jan-15.
 */
public class CreateProfileForJobSeeker extends FragmentActions implements VolleyOnResponseListener, Response {
    @InjectView(R.id.nameEdit)
    EditText nameEdit;
    @InjectView(R.id.emailEdit)
    EditText emailEdit;
    @InjectView(R.id.passwordEdit)
    EditText passwordEdit;
    //    @InjectView(R.id.exp_one_Edit)
//    EditText exp_one_Edit;
//    @InjectView(R.id.exp_two_Edit)
//    EditText exp_two_Edit;
//    @InjectView(R.id.exp_three_Edit)
//    EditText exp_three_Edit;
    @InjectView(R.id.addressAuto)
    AutoCompleteTextView addressAuto;
    String name, email, password, address;
    private VolleyForAll volleyForAll;
    private MyCrouton myCrouton;
    private MyProgressDialog myProgressDialog;
    private Validation validation;
    private AppPreferences preferences;
    Context con;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.seeker_registration, container, false);
        view.setClickable(true);
        ButterKnife.inject(this, view);
        init();

        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                con));

        addressAuto.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.auto_list_item));
        addressAuto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    new HideKeyBoard(getActivity());
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    private void init() {
        con = getActivity();
        volleyForAll = new VolleyForAll(con, this);
        myCrouton = new MyCrouton();
        myProgressDialog = new MyProgressDialog(getActivity());
        validation = new Validation();
        preferences = new AppPreferences(getActivity(), "JobBoxData");
    }


    @OnClick(R.id.nextButton)
    public void nextButton() {
        boolean editTextValid = validation.validateEditTexts(nameEdit, emailEdit, passwordEdit, addressAuto);
        if (editTextValid) {
            email = emailEdit.getText().toString().trim();
            boolean validEmail = validation.isValidEmail(email);
            if (validEmail) {
                name = nameEdit.getText().toString().trim();
                password = passwordEdit.getText().toString().trim();
                address = addressAuto.getText().toString().trim();
//                exp1 = exp_one_Edit.getText().toString().trim();
//                exp2 = exp_two_Edit.getText().toString().trim();
//                exp3 = exp_three_Edit.getText().toString().trim();
                myProgressDialog.setProgress();
                try {
                    name = URLEncoder.encode(name, "UTF-8");
                    address = URLEncoder.encode(address, "UTF-8");
                    password = URLEncoder.encode(password, "UTF-8");
//                    exp1 = URLEncoder.encode(exp1, "UTF-8");
//                    exp2 = URLEncoder.encode(exp2, "UTF-8");
//                    exp3 = URLEncoder.encode(exp3, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String createProfileURL = con.getResources().getString(R.string.url_new_domain) + "register.php/userRegistration?";
                RequestParams params = new RequestParams();
                params.put("user_name", name);
                params.put("user_mail", email);
                params.put("user_password", password);
                params.put("chat_notification", "ON");
                params.put("profile_visitor", "ON");
                params.put("user_address", address);
                params.put("user_type", "Jobseeker");
                params.put("deviceToken", "");
                params.put("userStatus", "Normal Users");
                params.put("video", "");
                params.put("image", "");
                params.put("experience1", "");
                params.put("experience2", "");
                params.put("experience3", "");
                params.put("distance", "");
                params.put("job_type", "");
                AsycHttpCall.getInstance().CallApiPost(createProfileURL, params, this, 1);

//                String createProfileURL = con.getResources().getString(R.string.url_domain);
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("type", "signup");
//                params.put("user_name", name);
//                params.put("user_email", email);
//                params.put("user_password", password);
//                params.put("user_address", address);
//                params.put("chat_notification", "");
//                params.put("profile_visitor", "");
//                params.put("user_type", "Jobseeker");
//                params.put("deviceToken", "");
//                params.put("experience1", "");
//                params.put("experience2", "");
//                params.put("experience3", "");
//                volleyForAll.volleyToNetwork(createProfileURL, VolleyForAll.HttpRequestType.HTTP_POST, params, 1);
            } else
                myCrouton.showCrouton(getActivity(), con.getResources().getString(R.string.enter_valid_email), 1);
        } else
            myCrouton.showCrouton(getActivity(), con.getResources().getString(R.string.enter_all_fields), 1);
    }

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        myProgressDialog.dismissProgress();
        boolean status = result.optBoolean("Result");
//        if (status) {
//            result = result.optJSONObject("Details");
//            preferences.saveData("user_id", result.optString("user_id"));
//            preferences.saveData("user_name", result.optString("user_name"));
//            preferences.saveData("user_email", result.optString("user_email"));
//            preferences.saveData("user_address", result.optString("user_address"));
//            preferences.saveData("chat_notification", result.optString("chat_notification"));
//            preferences.saveData("profile_visitor", result.optString("profile_visitor"));
//            preferences.saveData("user_type", result.optString("user_type"));
//            preferences.saveData("password", result.optString("password"));
//            preferences.saveData("video", result.optString("video"));
//            preferences.saveData("image", result.optString("image"));
//            preferences.saveData("profile_notification", result.optString("profile_visitor"));
//            preferences.saveData("description", result.optString("Notes"));
//            preferences.saveBooleanData("loggedIn", true);
//            preferences.saveData("video", result.optString("video"));
//            preferences.saveData("profile_notification", result.optString("profile_visitor"));
//            preferences.saveData("description", result.optString("Notes"));
//            preferences.saveData("code", result.optString("code"));
//            preferences.saveData("Labels", result.optString("Labels"));
//            preferences.saveBooleanData("Jobpost", result.optBoolean("Jobpost"));
//            preferences.saveData("lastSearchid", result.optString("lastSearchid"));
//            preferences.saveData("jobName", result.optString("Jobdescription"));
//            preferences.saveData("Selected_offer_id", result.optString("JobId"));
//            preferences.saveData("prevLabel", result.optString("JobLabel"));
//            preferences.saveData("Distance", result.optString("distance"));
//            preferences.saveData("FromAddress", result.optString("user_address"));
////            preferences.saveData("exp1", result.optString("experience1"));
////            preferences.saveData("exp2", result.optString("experience2"));
////            preferences.saveData("exp3", result.optString("experience3"));
//            new ClearBackStack(getActivity());
//            myCrouton.showCrouton(getActivity(), con.getResources().getString(R.string.success), 2);
////            replaceFragment(new JobSeekerHomeNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "JobSeekerHome");
//            replaceFragment(new JobSeekerHomeWithoutDesc(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "JobSeekerHome");
//        } else
        myCrouton.showCrouton(getActivity(), con.getResources().getString(R.string.Este_correo_ya_ha_sido_dado_de_alta), 1);
    }

    @Override
    public void onVolleyError(String result, int code) {
//        myProgressDialog.dismissProgress();
//        myCrouton.showCrouton(getActivity(), result, 1);
    }

    @Override
    public void onSuccess(String response, int code) {
        switch (code) {
            case 1:
                if (response != null)
                    parseDetails(response);
                break;
        }
    }

    @Override
    public void onFail(String response, int code) {

    }

    public void parseDetails(String response) {
        myProgressDialog.dismissProgress();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Log.e("Response", response);
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        Gson gson = gsonBuilder.create();

        SeekerRegGSON createGSON = gson.fromJson(response,
                SeekerRegGSON.class);
        if (createGSON != null) {
            if (createGSON.Result) {
                preferences.saveBooleanData("loggedIn", true);
                SaveSecurePreference preference = new SaveSecurePreference(getActivity());
                preference.setPrefValue(WelcomePage.USER_KEY, createGSON.Details.user_id);
                preference.setPrefValue(WelcomePage.EMAIL_KEY, createGSON.Details.user_email);
                preference.setPrefValue(WelcomePage.PASS_KEY, createGSON.Details.password);
                preference.setPrefValue(WelcomePage.AUTH_KEY, createGSON.Details.oauthToken);
                preference.setPrefValue(WelcomePage.USER_VIDEO, "");
                preferences.saveData("user_name", createGSON.Details.user_name);
                preferences.saveData("user_address", createGSON.Details.user_address);
                preferences.saveData("user_type", createGSON.Details.user_type);
                preferences.saveData("FromAddress", createGSON.Details.user_address);
                new ClearBackStack(getActivity());
                myCrouton.showCrouton(getActivity(), con.getResources().getString(R.string.success), 2);
                replaceFragment(new JobSeekerHomeWithoutDesc(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "JobSeekerHome");
            } else {
                myCrouton.showCrouton(getActivity(), con.getResources().getString(R.string.Este_correo_ya_ha_sido_dado_de_alta), 1);
            }
        }
    }
}
