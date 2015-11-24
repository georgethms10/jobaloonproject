package com.app.jobaloon.company;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.jobaloon.main.R;
import com.app.jobaloon.utils.AppPreferences;
import com.app.jobaloon.utils.FragmentActions;

import java.net.URLDecoder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by SICS-Dpc2 on 14-Apr-15.
 */
public class SeeInfo extends FragmentActions {

    @InjectView(R.id.infoText)
    TextView infoText;

    @InjectView(R.id.infoButton)
    TextView cancelinfoButton;

    @InjectView(R.id.infoName)
    TextView infoName;

    AppPreferences apfs;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.desc_fragment,container,false);
        view.setClickable(true);
        ButterKnife.inject(this, view);
        apfs = new AppPreferences(getActivity(), "JobBoxData");
        infoText.setText(URLDecoder.decode(apfs.getData("jobDesc")));
        infoName.setText(apfs.getData("userName"));
        return view;
    }

    @OnClick(R.id.infoButton)
    public void infoButton() {
        getFragmentManager().popBackStack();
    }
}
