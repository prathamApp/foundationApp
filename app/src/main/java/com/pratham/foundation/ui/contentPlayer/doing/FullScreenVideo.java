package com.pratham.foundation.ui.contentPlayer.doing;

import android.content.Intent;
import android.os.Handler;
import android.widget.VideoView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.media_controller.PlayerControlView;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.fragment_video_view)
public class FullScreenVideo extends BaseActivity {

    @ViewById(R.id.videoView)
    VideoView videoView;
    @ViewById(R.id.player_control_view)
    PlayerControlView player_control_view;

    String videoPath, startTime;
    long videoDuration = 0;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Intent intent = getIntent();
        videoPath = intent.getStringExtra("questionPath");
        initializePlayer(videoPath);
    }

    private void initializePlayer(String videoPath) {

        videoView.setMediaController(player_control_view.getMediaControllerWrapper());
        videoView.setVideoPath(videoPath);
        videoView.start();
        videoView.setOnPreparedListener(mp -> {
            startTime = FC_Utility.getCurrentDateTime();
            player_control_view.show();
            videoDuration = videoView.getDuration();
        });
        videoView.setOnCompletionListener(mp -> {
            new Handler().postDelayed(() ->
                    onBackPressed(), 1500);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Click(R.id.close_video)
    public void onCloseClick(){
        onBackPressed();
    }

}
