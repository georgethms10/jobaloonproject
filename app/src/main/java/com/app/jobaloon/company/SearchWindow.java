package com.app.jobaloon.company;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.jobaloon.gson.LabelsGSON;
import com.app.jobaloon.gson.PostOfferGSON;
import com.app.jobaloon.gson.SearchGSON;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by SICS-Dpc2 on 29-Jan-15.
 */
public class SearchWindow extends FragmentActions implements VolleyOnResponseListener, Response {
    private static final int LABELS = 1, SEARCH = 2;
    public static SearchGSON searchGSON = null;
    @InjectView(R.id.startTimeEdit)
    EditText starttimeEdit;
    @InjectView(R.id.endTimeEdit)
    EditText endtimeEdit;
    @InjectView(R.id.NameOfOffer)
    EditText NameOfOffer;
    @InjectView(R.id.additionalInfo)
    EditText additionalInfo;

    @InjectView(R.id.candidateRadiusSeek)
    SeekBar candidateRadiusSeek;
    @InjectView(R.id.candidateRadiusText)
    TextView candidateRadiusText;
    @InjectView(R.id.searchBar)
    AutoCompleteTextView searchBar;
    @InjectView(R.id.sundayCheck)
    CheckBox sundayCheck;
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

    private ArrayList<String> labelsList, codesList;
    private VolleyForAll volleyForAll;
    private MyCrouton myCrouton;
    private MyProgressDialog myProgressDialog;
    private AppPreferences preferences;
    private Validation validation;
    Spinner chooseCategory;
    String fromTime = "10:00:00", toTime = "18:00:00";
    AutoCompleteTextView companyLabelAuto;
    private double distance = 0.0;
    FragmentActivity act;

    SaveSecurePreference preference;
    String userId, authToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_window, container, false);
        ButterKnife.inject(this, view);
        init();
        act = getActivity();
        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                act));

        //link to get all labels
//        String labelsURL = getResources().getString(R.string.url_domain);
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("type", "Label");
//        volleyForAll.volleyToNetwork(labelsURL, VolleyForAll.HttpRequestType.HTTP_POST, params, LABELS);

        preference = new SaveSecurePreference(getActivity());
        userId = preference.getPrefValue(WelcomePage.USER_KEY);
        authToken = preference.getPrefValue(WelcomePage.AUTH_KEY);
        String labelsURL = getActivity().getResources().getString(R.string.url_new_domain) + "labels.php/labels";
        RequestParams params = new RequestParams();
        params.put("userid", userId);
        params.put("oauthToken", authToken);
        AsycHttpCall.getInstance().CallApiPost(labelsURL, params, this, LABELS);

        chooseCategory = (Spinner) view.findViewById(R.id.searchCriteriaSpinner);
        companyLabelAuto = (AutoCompleteTextView) view.findViewById(R.id.companyLabelAuto);

        searchBar.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.auto_list_item));
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    new HideKeyBoard(getActivity());
                    return true;
                }
                return false;
            }
        });

        //setting values
        if (!preferences.getData("prevLabel").equals("")) {
            searchBar.setText(preferences.getData("FromAddress"));
            //seek bar auto set
            candidateRadiusSeek.setProgress(preferences.getIntData("prevDistanceLimit"));
            candidateRadiusText.setText(preferences.getIntData("prevDistanceLimit") + " Km");

            //check boxes auto selection
            if (preferences.getData("fromTime").length() != 0)
                starttimeEdit.setText(preferences.getData("fromTime").substring(0, preferences.getData("fromTime").length() - 3));
            if (preferences.getData("toTime").length() != 0)
                endtimeEdit.setText(preferences.getData("toTime").substring(0, preferences.getData("toTime").length() - 3));
            if (!preferences.getData("fromTime").equals(""))
                fromTime = preferences.getData("fromTime");
            if (!preferences.getData("toTime").equals(""))
                toTime = preferences.getData("toTime");

            if (preferences.getBooleantData("prevMondayCheck"))
                mondayCheck.setChecked(true);
            else mondayCheck.setChecked(false);
            if (preferences.getBooleantData("prevTuesdayCheck"))
                tuesdayCheck.setChecked(true);
            else tuesdayCheck.setChecked(false);
            if (preferences.getBooleantData("prevWednesdayCheck"))
                wednesdayCheck.setChecked(true);
            else wednesdayCheck.setChecked(false);
            if (preferences.getBooleantData("prevThursdayCheck"))
                thursdayCheck.setChecked(true);
            else thursdayCheck.setChecked(false);
            if (preferences.getBooleantData("prevFridayCheck"))
                fridayCheck.setChecked(true);
            else fridayCheck.setChecked(false);
            if (preferences.getBooleantData("prevSaturdayCheck"))
                saturdayCheck.setChecked(true);
            else saturdayCheck.setChecked(false);
            if (preferences.getBooleantData("prevSundayCheck"))
                sundayCheck.setChecked(true);
            else sundayCheck.setChecked(false);

        } else {
            if (preferences.getData("user_address") != null && !preferences.getData("user_address").equals(""))
                searchBar.setText(preferences.getData("user_address"));
            candidateRadiusSeek.setProgress(1);
            candidateRadiusText.setText(getResources().getString(R.string.default_radius));
        }


        candidateRadiusSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                seekBar.setProgress(progress);
                preferences.saveData("Distance", String.valueOf(progress * 0.6214));
                candidateRadiusText.setText(progress + " Km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

    private void init() {
        labelsList = new ArrayList<String>();
        codesList = new ArrayList<String>();
        preferences = new AppPreferences(getActivity(), "JobBoxData");
        volleyForAll = new VolleyForAll(getActivity(), this);
        myCrouton = new MyCrouton();
        myProgressDialog = new MyProgressDialog(getActivity());
        validation = new Validation();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getActionBar().show();
        if (!preferences.getBooleantData("searchOk")) {
            new ClearBackStack(getActivity());
            addFragment(new SearchPageNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "SearchPage");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().hide();
    }

    @OnClick(R.id.closeLayout)
    public void closeIt1() {
        new HideKeyBoard(getActivity());
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.closeButton)
    public void closeIt2() {
        new HideKeyBoard(getActivity());
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.startTimeEdit)
    public void fromTimeSet() {
        showStartTimePicker(getActivity());
    }

    @OnClick(R.id.endTimeEdit)
    public void toTimeSet() {
        showEndTimePicker(getActivity());
    }

    public void showStartTimePicker(Context context) {
        final Dialog d = new Dialog(context);
        d.setTitle(getResources().getString(R.string.set_time));
        d.setContentView(R.layout.time_picker_dialog);
        Button okBtn = (Button) d.findViewById(R.id.buttonOk);
        final NumberPicker fromHourPick = (NumberPicker) d.findViewById(R.id.fromHourPick);
        final NumberPicker fromMinPick = (NumberPicker) d.findViewById(R.id.fromMinPick);
        fromHourPick.setMaxValue(24);
        fromHourPick.setMinValue(1);
        fromHourPick.setWrapSelectorWheel(false);
        fromMinPick.setWrapSelectorWheel(false);
        fromMinPick.setMaxValue(59);
        fromMinPick.setMinValue(00);

        setTwoDigitPicker(fromHourPick, fromMinPick);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fromHour = String.format("%02d", fromHourPick.getValue());
                String fromMinute = String.format("%02d", fromMinPick.getValue());
                fromTime = fromHour + ":" + fromMinute + ":00";
                System.out.println("fromTime : " + fromTime + " ," + preferences.getData("fromTime"));
                starttimeEdit.setText(fromHour + ":" + fromMinute);
                d.dismiss();
            }
        });
        d.show();
    }

    public void showEndTimePicker(Context context) {
        final Dialog d = new Dialog(context);
        d.setTitle(getResources().getString(R.string.set_time));
        d.setContentView(R.layout.end_time_picker_dialog);
        Button okBtn = (Button) d.findViewById(R.id.buttonOk);
        final NumberPicker toHourPick = (NumberPicker) d.findViewById(R.id.toHourPick);
        final NumberPicker toMinPick = (NumberPicker) d.findViewById(R.id.toMinPick);
        toHourPick.setMaxValue(24);
        toHourPick.setMinValue(1);
        toMinPick.setWrapSelectorWheel(false);
        toMinPick.setMaxValue(59);
        toMinPick.setMinValue(00);
        toHourPick.setWrapSelectorWheel(false);

        setTwoDigitPicker(toHourPick, toMinPick);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toHour = String.format("%02d", toHourPick.getValue());
                String toMinute = String.format("%02d", toMinPick.getValue());
                toTime = toHour + ":" + toMinute + ":00";
                endtimeEdit.setText(toHour + ":" + toMinute);
                d.dismiss();
            }
        });
        d.show();
    }

    private void setTwoDigitPicker(NumberPicker... picker) {
        for (int i = 0; i < picker.length; i++) {
            picker[i].setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int i) {
                    return String.format("%02d", i);
                }
            });
        }
    }

    @OnClick(R.id.searchButton)
    public void search() {
        String desc = NameOfOffer.getText().toString().trim();
        String additionalInfo_ = additionalInfo.getText().toString().trim();
        if (!desc.equals("") && !additionalInfo_.equals("")) {
            if (validation.validateAnyCheckBoxes(sundayCheck, mondayCheck, tuesdayCheck, wednesdayCheck, thursdayCheck, fridayCheck,
                    saturdayCheck)) {
                myProgressDialog.setProgress();
                String distanceLimit = "", label = "";
                label = companyLabelAuto.getText().toString().trim();

                if (label != null && !label.equals("")) {
                    if (candidateRadiusSeek.getProgress() != 0) {
                        double kilometers = candidateRadiusSeek.getProgress();
                        double miles = kilometers * .6214;
                        distanceLimit = String.valueOf(miles);
                    }
                    String fromAddress = searchBar.getText().toString().trim();

                    String weekdays = "";
                    if (sundayCheck.isChecked())
                        weekdays = weekdays + "D, ";
                    if (mondayCheck.isChecked())
                        weekdays = weekdays + "L, ";
                    if (tuesdayCheck.isChecked())
                        weekdays = weekdays + "M, ";
                    if (wednesdayCheck.isChecked())
                        weekdays = weekdays + "X, ";
                    if (thursdayCheck.isChecked())
                        weekdays = weekdays + "J, ";
                    if (fridayCheck.isChecked())
                        weekdays = weekdays + "V, ";
                    if (saturdayCheck.isChecked())
                        weekdays = weekdays + "S, ";
                    if (weekdays.length() > 0 && weekdays.charAt(weekdays.length() - 1) == ',')
                        weekdays = weekdays.substring(0, weekdays.length() - 1);

                    if (!weekdays.equals("")) {

                        String searchURL = getResources().getString(R.string.url_new_domain) + "insert_post.php/insertPost";
                        RequestParams params = new RequestParams();
                        params.put("Role", label);
                        params.put("start_date", fromTime);
                        params.put("end_date", toTime);
                        params.put("working_days", weekdays);
                        params.put("location", fromAddress);
                        params.put("distance", distanceLimit);
                        params.put("jobDescription", desc);
                        params.put("compony_id", userId);
                        params.put("additional_information", additionalInfo_);
                        params.put("oauthToken", authToken);
                        System.out.println("------ params : " + params);
                        AsycHttpCall.getInstance().CallApiPost(searchURL, params, this, SEARCH);
//                        String searchURL = getResources().getString(R.string.url_domain);
//                        HashMap<String, String> params = new HashMap<String, String>();
//                        params.put("type", "inserPost");
//                        params.put("role", label);
//                        params.put("start_date", fromTime);
//                        params.put("end_date", toTime);
//                        params.put("working_days", weekdays);
//                        params.put("distanseLimit", distanceLimit);
//                        params.put("fromaddress", fromAddress);
//                        params.put("compony_id", preferences.getData("user_id"));
//                        params.put("description", desc);
//                        params.put("additional_information", additionalInfo_);
//                        System.out.println("------ params : " + params);
//                        volleyForAll.volleyToNetwork(searchURL, VolleyForAll.HttpRequestType.HTTP_POST, params, SEARCH);
                        //saving all data for next search auto fill
                        //string values
                        preferences.saveData("fromTime", fromTime);
                        preferences.saveData("toTime", toTime);
                        preferences.saveData("prevLabel", label);
                        preferences.saveData("prevFromAddress", fromAddress);
                        preferences.saveData("jobName", desc);
                        preferences.saveData("jobName", desc);

                        //boolean values
                        preferences.saveBooleanData("prevMondayCheck", mondayCheck.isChecked());
                        preferences.saveBooleanData("prevTuesdayCheck", tuesdayCheck.isChecked());
                        preferences.saveBooleanData("prevWednesdayCheck", wednesdayCheck.isChecked());
                        preferences.saveBooleanData("prevThursdayCheck", thursdayCheck.isChecked());
                        preferences.saveBooleanData("prevFridayCheck", fridayCheck.isChecked());
                        preferences.saveBooleanData("prevSaturdayCheck", saturdayCheck.isChecked());
                        preferences.saveBooleanData("prevSundayCheck", sundayCheck.isChecked());
                        preferences.saveIntData("prevDistanceLimit", candidateRadiusSeek.getProgress());
                    } else
                        myCrouton.showCrouton(getActivity(), "Please select Working days!", 1);

                } else {
                    myProgressDialog.dismissProgress();
                    myCrouton.showCrouton(getActivity(), getResources().getString(R.string.all_fields_are_mandatory), 1);
                }
            }
        } else
            myCrouton.showCrouton(getActivity(), getResources().getString(R.string.all_fields_are_mandatory), 1);
    }

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        myProgressDialog.dismissProgress();
        switch (code) {
            case LABELS:
                parseLabels(result);
                break;
            case SEARCH:
                myProgressDialog.dismissProgress();
                parseSearch(result);
                break;
            default:
                break;
        }
    }

    @Override
    public void onVolleyError(String result, int code) {
        myProgressDialog.dismissProgress();
        myCrouton.showCrouton(getActivity(), result, 1);
    }

    private void parseLabels(JSONObject result) {
        if (result.optBoolean("Result")) {
            JSONArray jsonArray = result.optJSONArray("Label");
            int size = jsonArray.length();
            for (int i = 0; i < size; i++)
                labelsList.add(jsonArray.optString(i));
            jsonArray = result.optJSONArray("Code");
            size = jsonArray.length();
            for (int i = 0; i < size; i++)
                codesList.add(jsonArray.optString(i));
        } else {
            labelsList.add(getResources().getString(R.string.failed));
            codesList.add(getResources().getString(R.string.failed));
        }
        try {
            ArrayAdapter<String> label_adapter = new ArrayAdapter<String>(
                    getActivity(), R.layout.auto_list_item, labelsList);
            label_adapter.setDropDownViewResource(R.layout.auto_list_item);
            companyLabelAuto.setThreshold(1);

            //Set adapter to AutoCompleteTextView
            companyLabelAuto.setAdapter(label_adapter);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void parseSearch(JSONObject result) {
        if (result != null) {
            if (result.optBoolean("Result")) {
                myCrouton.showCrouton(getActivity(), getResources().getString(R.string.Tu_oferta_ha_sido_creada_con_éxito), 2);
                preferences.saveBooleanData("afterPost", true);
                String selected = result.optString("id");
                preferences.saveData("Selected_offer_id", selected);
//                preferences.saveData("prevLabel",label);
                new HideKeyBoard(getActivity());
                getFragmentManager().popBackStack();
            } else {
                preferences.saveBooleanData("afterPost", false);
                myCrouton.showCrouton(getActivity(), getResources().getString(R.string.no_result_to_show), 1);
            }
        } else
            myCrouton.showCrouton(getActivity(), getResources().getString(R.string.failed_to_get_data_from_server), 1);
    }

    @Override
    public void onSuccess(String response, int code) {
        switch (code) {
            case LABELS:
                parseLabel(response);
                break;

            case SEARCH:
                myProgressDialog.dismissProgress();
                parseSearchDetails(response);
                break;

        }
    }

    public void parseSearchDetails(String response) {
        if (response != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();
            PostOfferGSON postGSON = gson.fromJson(response,
                    PostOfferGSON.class);

            if (postGSON.Result) {
                myCrouton.showCrouton(getActivity(), getResources().getString(R.string.Tu_oferta_ha_sido_creada_con_éxito), 2);
                preferences.saveBooleanData("afterPost", true);
                String selected = postGSON.id;
                preferences.saveData("Selected_offer_id", selected);
//                preferences.saveData("prevLabel",label);
                new HideKeyBoard(getActivity());
                getFragmentManager().popBackStack();
            } else {
                preferences.saveBooleanData("afterPost", false);
                myCrouton.showCrouton(getActivity(), getResources().getString(R.string.no_result_to_show), 1);
            }

        } else
            myCrouton.showCrouton(getActivity(), getResources().getString(R.string.failed_to_get_data_from_server), 1);
    }

    public void parseLabel(String response) {
        if (response != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();
            LabelsGSON labelGSON = gson.fromJson(response,
                    LabelsGSON.class);
            if (labelGSON != null) {
                String labels = labelGSON.Label.toString();
                String[] separated = labels.split(",");
                for (int i = 0; i < separated.length; i++) {
                    if (i == 0)
                        separated[i] = separated[i].replace("[", "");

                    if (i == separated.length - 1)
                        separated[i] = separated[i].replace("]", "");
                    labelsList.add(separated[i]);
//                    HashMap<String, String> hashMap = new HashMap<>();
//                    hashMap.put("labelName", separated[i]);
////                    hashMap.put("selectedStatus", "false");
//                    labelsList.add(hashMap);
                }
            }
            try {
                ArrayAdapter<String> label_adapter = new ArrayAdapter<String>(
                        getActivity(), R.layout.auto_list_item, labelsList);
                label_adapter.setDropDownViewResource(R.layout.auto_list_item);
                companyLabelAuto.setThreshold(1);

                //Set adapter to AutoCompleteTextView
                companyLabelAuto.setAdapter(label_adapter);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFail(String response, int code) {

    }
}
