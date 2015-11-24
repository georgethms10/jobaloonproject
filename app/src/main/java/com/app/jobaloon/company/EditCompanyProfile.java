package com.app.jobaloon.company;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.jobaloon.chat.ChatList;
import com.app.jobaloon.gson.EditCompanyGSON;
import com.app.jobaloon.gson.EditJobSeekerGSON;
import com.app.jobaloon.gson.LogoutGSON;
import com.app.jobaloon.main.R;
import com.app.jobaloon.main.WelcomePage;
import com.app.jobaloon.utils.AppPreferences;
import com.app.jobaloon.utils.AsycHttpCall;
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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.me.lewisdeane.ldialogs.BaseDialog;
import uk.me.lewisdeane.ldialogs.CustomDialog;

/**
 * Created by SICS-Dpc2 on 29-Jan-15.
 */
public class EditCompanyProfile extends FragmentActions implements VolleyOnResponseListener, Response {
    private static final int SAVE_CHANGES = 1, LOGOUT = 2;
    @InjectView(R.id.companyNameEdit)
    EditText companyNameEdit;
    @InjectView(R.id.companyEmailEdit)
    EditText companyEmailEdit;
    @InjectView(R.id.passwordEdit)
    EditText passwordEdit;
    @InjectView(R.id.companyAddressAuto)
    AutoCompleteTextView companyAddressAuto;
    ImageView leftActionImage, rightActionImage;
    private String companyName, companyAddress, password, companyEmailId, chatStatus = "";
    private VolleyForAll volleyForAll;
    private MyCrouton myCrouton;
    private MyProgressDialog myProgressDialog;
    private Validation validation;
    private AppPreferences preferences;
    private TextView countVisitorsText, upload_video_center_text;
    private FrameLayout frameLayout;
    String companyNameEncoded = "", companyAddressEncoded = "", passwordEncoded = "", companyEmailEncoded = "";
    Context con;

    SaveSecurePreference preference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_company_profile, container, false);
        view.setClickable(true);
        ButterKnife.inject(this, view);
        init();
        con = getActivity();
        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                con));

        View actionBarView = getActivity().getActionBar().getCustomView();
        leftActionImage = (ImageView) actionBarView.findViewById(R.id.leftActionImage);
        rightActionImage = (ImageView) actionBarView.findViewById(R.id.rightActionImage);
        countVisitorsText = (TextView) actionBarView.findViewById(R.id.countVisitorsText);
        frameLayout = (FrameLayout) actionBarView.findViewById(R.id.frameLayout);
        upload_video_center_text = (TextView) actionBarView.findViewById(R.id.upload_video_center_text);
        leftActionImage.setClickable(false);
        leftActionImage.setVisibility(View.GONE);
        upload_video_center_text.setVisibility(View.GONE);
        rightActionImage.setClickable(true);
        rightActionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    replaceFragment(new ChatList(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "chat");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //setting values
        doSetValues();

        companyAddressAuto.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.auto_list_item));
        companyAddressAuto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

    @Override
    public void onPause() {
        super.onPause();
        leftActionImage.setClickable(true);
        leftActionImage.setVisibility(View.VISIBLE);
        rightActionImage.setClickable(true);
        // calling broadcast
        Intent intent = new Intent("backFromChatList");
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void init() {
        preferences = new AppPreferences(getActivity(), "JobBoxData");
        volleyForAll = new VolleyForAll(getActivity(), this);
        myCrouton = new MyCrouton();
        myProgressDialog = new MyProgressDialog(getActivity());
        validation = new Validation();
    }

    //set values on all fields
    private void doSetValues() {
        preference = new SaveSecurePreference(getActivity());
        companyNameEdit.setText(preferences.getData("user_name"));
        companyEmailEdit.setText(preference.getPrefValue(WelcomePage.EMAIL_KEY));
        passwordEdit.setText(preference.getPrefValue(WelcomePage.PASS_KEY));
        try {
            companyAddressAuto.setText(URLDecoder.decode(preferences.getData("user_address"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.logoutButton)
    public void logOut() {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity(), getResources().getString(R.string.confirm), getResources().getString(R.string.yes));

// Now we can use any of the following methods.
        builder.content(getResources().getString(R.string.do_you_really_want_to_logout));
        builder.negativeText(getResources().getString(R.string.no));
        builder.darkTheme(false);
        builder.typeface(Typeface.DEFAULT_BOLD);
        builder.titleTextSize(17);
        builder.contentTextSize(15);
        builder.buttonTextSize(15);
        builder.titleAlignment(BaseDialog.Alignment.LEFT);
        builder.contentAlignment(BaseDialog.Alignment.LEFT);
        builder.buttonAlignment(BaseDialog.Alignment.CENTER);
        builder.titleColor(getResources().getColor(R.color.blue)); // int res, or int colorRes parameter versions available as well.
        builder.contentColor(Color.BLACK); // int res, or int colorRes parameter versions available as well.
        builder.positiveColor(getResources().getColor(R.color.blue)); // int res, or int colorRes parameter versions available as well.
        builder.negativeColor(getResources().getColor(R.color.grey)); // int res, or int colorRes parameter versions available as well.

// Now we can build the dialog.
        final CustomDialog customDialog = builder.build();

// Show the dialog.
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {

                // calling the logout url
                myProgressDialog.setProgress();
//                String logoutURL = getResources().getString(R.string.url_domain);
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("type", "logout");
//                params.put("userid", preferences.getData("user_id"));
//                volleyForAll.volleyToNetwork(logoutURL, VolleyForAll.HttpRequestType.HTTP_POST, params, LOGOUT);

                logout();
            }

            @Override
            public void onCancelClick() {
                customDialog.cancel();
            }
        });
    }

    public void logout() {
        String logoutURL = getActivity().getResources().getString(R.string.url_new_domain) + "logout.php/logout";
        RequestParams params = new RequestParams();
        params.put("userid", preference.getPrefValue(WelcomePage.USER_KEY));
        params.put("oauthToken", preference.getPrefValue(WelcomePage.AUTH_KEY));
        AsycHttpCall.getInstance().CallApiPost(logoutURL, params, this, LOGOUT);
    }

    @OnClick(R.id.saveButton)
    public void saveChanges() {
        boolean editTextValid = validation.validateEditTexts(companyAddressAuto, companyEmailEdit, passwordEdit, companyNameEdit);
        if (editTextValid) {

            companyEmailId = companyEmailEdit.getText().toString().trim();
            boolean validEmail = validation.isValidEmail(companyEmailId);
            if (validEmail) {

                companyAddress = companyAddressAuto.getText().toString().trim();
                password = passwordEdit.getText().toString().trim();
                companyName = companyNameEdit.getText().toString().trim();
                myProgressDialog.setProgress();
                try {
                    companyNameEncoded = URLEncoder.encode(companyName, "UTF-8");
                    companyAddressEncoded = URLEncoder.encode(companyAddress, "UTF-8");
                    passwordEncoded = URLEncoder.encode(password, "UTF-8");
                    companyEmailEncoded = URLEncoder.encode(companyEmailId, "UTF-8");

                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                    companyAddressEncoded = companyAddress;
                    companyNameEncoded = companyName;
                    passwordEncoded = password;
                    companyEmailEncoded = companyEmailId;
                }
//oauthToken/&userid/&user_name/&user_email/&password/&user_address/&chat_notification
                String editCompanyProfileURL = getResources().getString(R.string.url_new_domain) + "edit_profile.php/editProfile/";
                RequestParams params = new RequestParams();
                params.put("oauthToken", preference.getPrefValue(WelcomePage.AUTH_KEY));
                params.put("userid", preference.getPrefValue(WelcomePage.USER_KEY));
                params.put("user_name", companyNameEncoded);
                params.put("user_email", companyEmailId);
                params.put("password", passwordEncoded);
                params.put("user_address", companyAddressEncoded);
                params.put("chat_notification", "ON");
                AsycHttpCall.getInstance().CallApiPost(editCompanyProfileURL, params, this, SAVE_CHANGES);
//                volleyForAll.volleyToNetwork(editCompanyProfileURL, VolleyForAll.HttpRequestType.HTTP_POST, params, SAVE_CHANGES);

//                String editCompanyProfileURL = getResources().getString(R.string.url_domain);
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("type", "editCompony");
//                params.put("user_name", companyNameEncoded);
//                params.put("user_email", companyEmailId);
//                params.put("user_password", passwordEncoded);
//                params.put("user_address", companyAddressEncoded);
//                params.put("chat_notification", chatStatus);
//                params.put("userid", preferences.getData("user_id"));
//                volleyForAll.volleyToNetwork(editCompanyProfileURL, VolleyForAll.HttpRequestType.HTTP_POST, params, SAVE_CHANGES);

            } else
                myCrouton.showCrouton(getActivity(), getResources().getString(R.string.enter_valid_email), 1);
        } else
            myCrouton.showCrouton(getActivity(), getResources().getString(R.string.enter_all_fields), 1);
    }

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        myProgressDialog.dismissProgress();
        if (code == SAVE_CHANGES) {
            if (result.optBoolean("Result")) {
                preferences.saveData("user_name", URLDecoder.decode(companyNameEncoded));
                preferences.saveData("user_email", URLDecoder.decode(companyEmailEncoded));
                preferences.saveData("user_address", URLDecoder.decode(companyAddressEncoded));
                preferences.saveData("password", password);
                myCrouton.showCrouton(getActivity(), getString(R.string.success_2), 2);
//                getFragmentManager().popBackStack();
                addFragment(new SearchPageNew(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "SearchPageNew");
            } else myCrouton.showCrouton(getActivity(), getString(R.string.failed), 1);

        } else if (code == LOGOUT) {

            if (result.optBoolean("Result")) {
                getActivity().getActionBar().hide();
//                new ClearBackStack(getActivity());
                addFragment(new WelcomePage(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_CLOSE, "WelcomePages");
                preferences.deleteAll();
                countVisitorsText.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);

            } else myCrouton.showCrouton(getActivity(), getString(R.string.failed), 1);
        }
    }

    @Override
    public void onVolleyError(String result, int code) {
        myProgressDialog.dismissProgress();
        myCrouton.showCrouton(getActivity(), result, 1);
    }

    @Override
    public void onSuccess(String response, int code) {
        myProgressDialog.dismissProgress();
        switch (code) {
            case SAVE_CHANGES:
                if (response != null)
                    parseUserDetails(response);
                break;

            case LOGOUT:
                if (response != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                    Gson gson = gsonBuilder.create();

                    LogoutGSON logoutGSON = gson.fromJson(response,
                            LogoutGSON.class);
                    if (logoutGSON != null) {
                        if (logoutGSON.Result) {
                            preferences.deleteAll();
                            getActivity().getActionBar().hide();
                            replaceFragment(new WelcomePage(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_CLOSE, "WelcomePages");
                        } else
                            myCrouton.showCrouton(getActivity(), getString(R.string.failed), 1);
                    }
                }
        }
    }

    @Override
    public void onFail(String response, int code) {
        myProgressDialog.dismissProgress();
        myCrouton.showCrouton(getActivity(), getString(R.string.failed), 1);
    }

    public void parseUserDetails(String response) {
        if (response != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Log.e("Response", response);
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();

            EditCompanyGSON editGSON = gson.fromJson(response,
                    EditCompanyGSON.class);
            if (editGSON != null) {
                if (editGSON.Result) {
                    myCrouton.showCrouton(getActivity(), getString(R.string.success_2), 2);
                    preferences.saveData("user_name", URLDecoder.decode(companyNameEncoded));
                    preferences.saveData("user_address", URLDecoder.decode(companyAddressEncoded));
                    preference.setPrefValue(WelcomePage.USER_KEY, editGSON.Details.user_id);
                    preference.setPrefValue(WelcomePage.AUTH_KEY, editGSON.Details.oauthToken);
                    addFragment(new SearchPageNew(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "SearchPageNew");
                } else
                    myCrouton.showCrouton(getActivity(), getString(R.string.failed), 1);
            }
        }
    }
}
