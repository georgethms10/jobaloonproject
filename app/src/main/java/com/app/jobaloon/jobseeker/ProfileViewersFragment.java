package com.app.jobaloon.jobseeker;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.app.jobaloon.chat.ChatList;
import com.app.jobaloon.gson.ProfileVistorGSON;
import com.app.jobaloon.main.R;
import com.app.jobaloon.utils.AppPreferences;
import com.app.jobaloon.utils.FragmentActions;
import com.app.jobaloon.utils.MyCrouton;
import com.app.jobaloon.utils.MyProgressDialog;
import com.app.jobaloon.utils.UncaughtExceptionReporter;
import com.app.jobaloon.utils.VolleyForAll;
import com.app.jobaloon.utils.VolleyOnResponseListener;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created on 06 Feb 2015
 */
public class ProfileViewersFragment extends FragmentActions implements VolleyOnResponseListener {

    @InjectView(R.id.visitorList)
    ListView visitorList;
    ImageView glassesIcon;
    private VolleyForAll volleyForAll;
    private MyCrouton myCrouton;
    private MyProgressDialog myProgressDialog;
    private AppPreferences preferences;
    FragmentActivity ContextActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_viewers, container, false);
        view.setClickable(true);
        init(view);

        TimeZone tz = TimeZone.getDefault();
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        ContextActivity = getActivity();

        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                ContextActivity));

        View actionBarView = getActivity().getActionBar().getCustomView();
        glassesIcon = (ImageView) actionBarView.findViewById(R.id.leftActionImage);
        ImageView chatIcon = (ImageView) actionBarView.findViewById(R.id.rightActionImage);
        TextView countVisitorsText = (TextView) actionBarView.findViewById(R.id.countVisitorsText);
        countVisitorsText.setVisibility(View.GONE);
        glassesIcon.setClickable(true);
        getActivity().getActionBar().show();
        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragmentNew(new ChatList(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "chat", ContextActivity);
            }
        });

        glassesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragmentNew(new EditJobSeekerProfile(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "EditJobSeekerProfile", ContextActivity);
            }
        });

        myProgressDialog.setProgress();
        String sampleVideoURL = getResources().getString(R.string.url_domain);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "viewProfileVisitors");
        params.put("userid", preferences.getData("user_id"));
        params.put("index", "0");
        params.put("timezone", mTimeZone.getID());
        volleyForAll.volleyToNetwork(sampleVideoURL, VolleyForAll.HttpRequestType.HTTP_POST, params, 1);
        return view;
    }

    private void init(View view) {
        ButterKnife.inject(this, view);
        preferences = new AppPreferences(getActivity(), "JobBoxData");
        volleyForAll = new VolleyForAll(getActivity(), this);
        myCrouton = new MyCrouton();
        myProgressDialog = new MyProgressDialog(getActivity());
    }

    @OnClick(R.id.backButton)
    public void backClick() {
        glassesIcon.setClickable(true);
        getFragmentManager().popBackStack();
    }

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        myProgressDialog.dismissProgress();
        if (result != null) {
            if (result.optBoolean("Result")) {
                ProfileVistorGSON gsonDetails = new Gson().fromJson(result.toString(), ProfileVistorGSON.class);
                ProfileVisitorAdapter adapter = new ProfileVisitorAdapter(getActivity(),
                        gsonDetails.Details);
                adapter.notifyDataSetChanged();
                visitorList.setAdapter(adapter);
            } else
                myCrouton.showCrouton(getActivity(), getString(R.string.no_result_to_show), 1);
        }
    }

    @Override
    public void onVolleyError(String result, int code) {
        myProgressDialog.dismissProgress();
        myCrouton.showCrouton(getActivity(), result, 1);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        glassesIcon.setClickable(true);
    }
}
