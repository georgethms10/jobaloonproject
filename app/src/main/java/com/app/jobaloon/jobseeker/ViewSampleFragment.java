package com.app.jobaloon.jobseeker;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.app.jobaloon.main.R;
import com.app.jobaloon.utils.FragmentActions;
import com.app.jobaloon.utils.MyCrouton;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created on 06 Feb 2015
 */
public class ViewSampleFragment extends FragmentActions {

    @InjectView(R.id.feedVideoView)
    VideoView feedVideoView;
    @InjectView(R.id.videoFrame)
    RelativeLayout videoFrame;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    private boolean isPlaying = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_sample, container, false);
        view.setClickable(true);
        init(view);
        String videoName = this.getArguments().getString("sampleVideo");
        if (!videoName.equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            feedVideoView.setVideoPath(getResources().getString(R.string.url_samples) + videoName);
            prepare();
        } else progressBar.setVisibility(View.GONE);
        feedVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                new MyCrouton().showCrouton(getActivity(), getResources().getString(R.string.can_not_play_the_video), 1);
                progressBar.setVisibility(View.GONE);
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
        return view;
    }

    private void init(View view) {
        ButterKnife.inject(this, view);
        progressBar.setVisibility(View.GONE);
    }


    private void prepare() {
        feedVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                feedVideoView.start();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (feedVideoView != null) {
            feedVideoView.pause();
            videoFrame.setVisibility(View.GONE);
            feedVideoView.setVisibility(View.GONE);
            feedVideoView = null;
        }
        ButterKnife.reset(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().hide();
        feedVideoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (feedVideoView != null)
            feedVideoView.pause();
    }

}
