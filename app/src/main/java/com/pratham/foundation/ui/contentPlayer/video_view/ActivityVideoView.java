package com.pratham.foundation.ui.contentPlayer.video_view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.media_controller.PlayerControlView;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import static com.pratham.foundation.database.AppDatabase.appDatabase;


@EActivity(R.layout.fragment_video_view)
public class ActivityVideoView extends BaseActivity {

    @ViewById(R.id.videoView)
    VideoView videoView;
    @ViewById(R.id.player_control_view)
    PlayerControlView player_control_view;

    String videoPath,startTime,resId,contentName;
    boolean onSdCard;
    long videoDuration = 0;

    @AfterViews
    public void initialize() {
//        getActivity().setRequestedOrientation(
//                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        Intent intent = getIntent();
        videoPath = intent.getStringExtra("contentPath");
        resId = intent.getStringExtra("resId");
        contentName = intent.getStringExtra("contentName");
        onSdCard = intent.getBooleanExtra("onSdCard", false);

        if (onSdCard)
            videoPath = ApplicationClass.contentSDPath + "/.FCA/English/Game/Videos/" + videoPath;
        else
            videoPath = ApplicationClass.foundationPath + "/.FCA/English/Game/Videos/" + videoPath;

        initializePlayer(videoPath);
    }

    private void initializePlayer(String videoPath) {
//        MediaController mediaController= new MediaController(getActivity());
//        mediaController.setAnchorView(videoView);
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
                onBackPressed(),1500);
        });
//        new Handler().postDelayed(() -> videoView.start(),500);
    }

    @Override
    public void onBackPressed() {
        addScore();
        BackupDatabase.backup(this);
        super.onBackPressed();
    }

    @Background
    public void addScore() {
        String endTime = FC_Utility.getCurrentDateTime();
        Score score = new Score();
        score.setSessionID(FC_Constants.currentSession);
        score.setStudentID(""+FC_Constants.currentStudentID);
        score.setDeviceID(FC_Utility.getDeviceID());
        score.setResourceID(resId);
        score.setQuestionId(0);
        score.setScoredMarks((int) FC_Utility.getTimeDifference(startTime, endTime));
        score.setTotalMarks((int) videoDuration);
        score.setStartDateTime(startTime);
        score.setEndDateTime(endTime);
        score.setLevel(0);
        score.setLabel("video");
        score.setSentFlag(0);
        appDatabase.getScoreDao().insert(score);
    }
}
