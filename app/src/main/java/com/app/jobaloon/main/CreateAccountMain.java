package com.app.jobaloon.main;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.app.jobaloon.company.CreateProfileForCompany;
import com.app.jobaloon.jobseeker.CreateProfileForJobSeeker;
import com.app.jobaloon.utils.FragmentActions;
import com.app.jobaloon.utils.UncaughtExceptionReporter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by SICS-Dpc2 on 23-Jan-15.
 */
public class CreateAccountMain extends FragmentActions {

    @InjectView(R.id.createAccountBtn)
    Button lookingForJob;
    @InjectView(R.id.accessMyAccountBtn)
    Button lookingForWorker;
    @InjectView(R.id.homeImageView)
    ImageView homeImageView;
    @InjectView(R.id.title)
    TextView titleView;
    @InjectView(R.id.subTitle)
    TextView subTitle;
    private FragmentActivity _activity;
    private String image, title, message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_welcome_page, container, false);
        view.setClickable(true);
        ButterKnife.inject(this, view);
        _activity = getActivity();

        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                _activity));

        lookingForWorker.setText(getResources().getString(R.string.looking_job));
        lookingForJob.setText(getResources().getString(R.string.looking_worker));
        Bundle bundle = getArguments();
        image = bundle.getString("image");
        title = bundle.getString("title");
        message = bundle.getString("message");
        Glide.with(getActivity()).load(getActivity().getString(R.string.url_samples) + image).placeholder(R.drawable.welcome_bg).into(homeImageView);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Hero Light.otf");
        titleView.setTypeface(font);
        subTitle.setTypeface(font);
        titleView.setText(title);
        subTitle.setText(message);
        return view;
    }

    @OnClick(R.id.accessMyAccountBtn)
    public void setLookingForJob() {
        addFragment(new CreateProfileForJobSeeker(), R.id.frame, true, FragmentTransaction.TRANSIT_FRAGMENT_OPEN,
                "createProfileForJobSeeker");
    }

    @OnClick(R.id.createAccountBtn)
    public void setLookingForWorker() {
        addFragment(new CreateProfileForCompany(), R.id.frame, true, FragmentTransaction.TRANSIT_FRAGMENT_OPEN,
                "createProfileForCompany");

    }
}
