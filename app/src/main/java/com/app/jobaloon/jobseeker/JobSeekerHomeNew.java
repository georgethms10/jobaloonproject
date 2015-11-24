package com.app.jobaloon.jobseeker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.jobaloon.chat.ChatList;
import com.app.jobaloon.main.R;
import com.app.jobaloon.utils.AppPreferences;
import com.app.jobaloon.utils.FragmentActions;
import com.app.jobaloon.utils.HideKeyBoard;
import com.app.jobaloon.utils.MyCrouton;
import com.app.jobaloon.utils.PlacesAutoCompleteAdapter;
import com.app.jobaloon.utils.UncaughtExceptionReporter;
import com.app.jobaloon.utils.Validation;
import com.app.jobaloon.utils.VolleyForAll;
import com.app.jobaloon.utils.VolleyOnResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by SICS-Dpc2 on 03-Apr-15.
 */
public class JobSeekerHomeNew extends FragmentActions implements VolleyOnResponseListener {
    Spinner chooseCategory;
    private ImageView chatIcon, settingsIcon;
    private View actionBarView;

    @InjectView(R.id.seekerJobsearchButton)
    ImageView jobSearchButton;
    @InjectView(R.id.enterDescText)
    TextView enterDescText;


    private VolleyForAll volleyForAll;
    private static final int LABELS = 1;
    private ArrayList<String> labelsList;
    private MyCrouton myCrouton;
    private AppPreferences preferences;
    private Validation validation;

    @InjectView(R.id.jobSeekerLabelAuto)
    AutoCompleteTextView jobSeekerLabelAuto;
    @InjectView(R.id.seekerDescEdit)
    EditText seekerDescEdit;
    String desc = "";
    Context con;
    ArrayAdapter<String> myAdapter;
    FragmentActivity act;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_seeker_home_new, container, false);
        view.setClickable(true);
        ButterKnife.inject(this, view);
        volleyForAll = new VolleyForAll(getActivity(), this);
        labelsList = new ArrayList<String>();

        myCrouton = new MyCrouton();
        validation = new Validation();
        actionBarView = getActivity().getActionBar().getCustomView();
        con = getActivity();
        act = getActivity();
        preferences = new AppPreferences(con, "JobBoxData");

        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                con));


        enterDescText.setText(con.getResources().getString(R.string.Explain_in_140_characters_your_last_job_experience_and_education_1) + " 140 " + con.getResources().getString(R.string.Explain_in_140_characters_your_last_job_experience_and_education_2));


        settingsIcon = (ImageView) actionBarView.findViewById(R.id.leftActionImage);
        chatIcon = (ImageView) actionBarView.findViewById(R.id.rightActionImage);
        settingsIcon.setImageDrawable(con.getResources().getDrawable(R.drawable.settings));
        TextView countVisitorsText = (TextView) actionBarView.findViewById(R.id.countVisitorsText);
        //making the visitor count gone here for making sure it is not showing for company profile.
        countVisitorsText.setVisibility(View.GONE);
        getActivity().getActionBar().show();
        chatIcon.setImageDrawable(con.getResources().getDrawable(R.drawable.msg));
        chatIcon.setVisibility(View.VISIBLE);
        settingsIcon.setVisibility(View.VISIBLE);
        System.out.println("The label in preference : " + preferences.getData("Labels"));
        System.out.println("first  : " + preferences.getData("SeekerDesc"));

        jobSeekerLabelAuto.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.auto_list_item));
        jobSeekerLabelAuto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    new HideKeyBoard(getActivity());
                    return true;
                }
                return false;
            }
        });
        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentNew(new ChatList(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "ChatList", act);
            }
        });

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentNew(new EditJobSeekerProfile(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "EditJobSeekerProfile", act);
            }
        });

        //getting labels from the server
        String labelsURL = getResources().getString(R.string.url_domain);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "Label");
        volleyForAll.volleyToNetwork(labelsURL, VolleyForAll.HttpRequestType.HTTP_POST, params, LABELS);

        seekerDescEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                int length = 140 - seekerDescEdit.getText().length();
                enterDescText.setText(con.getResources().getString(R.string.Explain_in_140_characters_your_last_job_experience_and_education_1) + " " + length + " " + con.getResources().getString(R.string.Explain_in_140_characters_your_last_job_experience_and_education_2));
            }
        });

        return view;
    }

    @OnClick(R.id.seekerJobsearchButton)
    public void searchJob() {
        desc = seekerDescEdit.getText().toString().trim();
        String label = jobSeekerLabelAuto.getText().toString().trim();
        if (desc.equals("") || label.equals(""))
            myCrouton.showCrouton(getActivity(), con.getResources().getString(R.string.Todos_los_campos_son_obligatorios), 1);
        else {
            preferences.saveData("SeekerDesc", URLEncoder.encode(desc));
            preferences.saveData("Labels", URLEncoder.encode(label));
//            addFragment(new UploadVideoSeekerNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "JobSearchPage");
            addFragment(new JobListNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "JobListNew");
        }
    }

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        switch (code) {
            case LABELS:
                parseLabels(result);
                break;
            default:
                break;
        }
    }

    @Override
    public void onVolleyError(String result, int code) {

    }

    private void parseLabels(JSONObject result) {
        if (result.optBoolean("Result")) {
            JSONArray jsonArray = result.optJSONArray("Label");
            int size = jsonArray.length();
            for (int i = 0; i < size; i++)
                labelsList.add(jsonArray.optString(i));
            jsonArray = result.optJSONArray("Code");
            size = jsonArray.length();
        }
        try {
            ArrayAdapter<String> label_adapter = new ArrayAdapter<String>(
                    getActivity(), R.layout.auto_list_item, labelsList);
            label_adapter.setDropDownViewResource(R.layout.auto_list_item);
            jobSeekerLabelAuto.setThreshold(1);
            //Set adapter to AutoCompleteTextView
            jobSeekerLabelAuto.setAdapter(label_adapter);
            System.out.println("values : "+preferences.getData("SeekerDesc"));
            if (!preferences.getData("SeekerDesc").equals(""))
                seekerDescEdit.setText(URLDecoder.decode(preferences.getData("SeekerDesc")));
            else
                seekerDescEdit.setText(URLDecoder.decode(preferences.getData("SeekerDesc")));
            if (!preferences.getData("Labels").equals("")) {
                jobSeekerLabelAuto.setText(URLDecoder.decode(preferences.getData("Labels")));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
