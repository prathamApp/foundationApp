package com.pratham.foundation.ui.contentPlayer.video_view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.MediaController;
import android.widget.VideoView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import java.util.Objects;
import static com.pratham.foundation.database.AppDatabase.appDatabase;


@EFragment(R.layout.fragment_video_view)
public class FragmentVideoView extends Fragment {

    @ViewById(R.id.videoView)
    VideoView videoView;

    String videoPath,startTime,resId;
    boolean onSdCard;
    long videoDuration = 0;

    @AfterViews
    public void initialize() {
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        Bundle bundle = getArguments();
        videoPath = bundle.getString("contentPath");
        resId = bundle.getString("resId");
        onSdCard = bundle.getBoolean("onSdCard", false);

        if (onSdCard)
            videoPath = ApplicationClass.contentSDPath + "/.FCA/English/Game/" + videoPath;
        else
            videoPath = ApplicationClass.foundationPath + "/.FCA/English/Game/" + videoPath;

        initializePlayer(videoPath);
    }

    private void initializePlayer(String videoPath) {
        MediaController mediaController= new MediaController(getActivity());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath(videoPath);
        videoView.setOnPreparedListener(mp -> {
            startTime = FC_Utility.getCurrentDateTime();
            videoDuration = videoView.getDuration();
        });
        videoView.setOnCompletionListener(mp -> {
            //TODO back press)
        });

        new Handler().postDelayed(() -> videoView.start(),500);
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
