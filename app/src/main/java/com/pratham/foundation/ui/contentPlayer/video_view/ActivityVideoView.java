package com.pratham.foundation.ui.contentPlayer.video_view;

import android.content.Intent;
import android.os.Handler;
import android.widget.VideoView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.media_controller.PlayerControlView;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;


@EActivity(R.layout.fragment_video_view)
public class ActivityVideoView extends BaseActivity {

    @ViewById(R.id.videoView)
    VideoView videoView;
    @ViewById(R.id.player_control_view)
    PlayerControlView player_control_view;

    String videoPath, startTime, resId, contentName;
    boolean onSdCard;
    long videoDuration = 0;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//        getActivity().setRequestedOrientation(
//                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        Intent intent = getIntent();
        videoPath = intent.getStringExtra("contentPath");
        resId = intent.getStringExtra("resId");
        contentName = intent.getStringExtra("contentName");
        onSdCard = intent.getBooleanExtra("onSdCard", false);

        if (onSdCard)
            videoPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + videoPath;
        else
            videoPath = ApplicationClass.foundationPath + gameFolderPath + "/" + videoPath;

        initializePlayer(videoPath);
    }

    @Click(R.id.close_video)
    public void closeVide() {
        onBackPressed();
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
            try {
                new Handler().postDelayed(this::onBackPressed, 1500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
//        new Handler().postDelayed(() -> videoView.start(),500);
    }

    @Override
    public void onBackPressed() {
        try {
            addScore();
            BackupDatabase.backup(this);
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    public void addScore() {
        try {
            String endTime = FC_Utility.getCurrentDateTime();
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setStudentID("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
