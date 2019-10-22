package com.pratham.foundation.ui.contentPlayer.video_view;

import android.support.v4.app.Fragment;
import android.widget.VideoView;

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

    private String videoPath;
    private String startTime;
    private String resId;
    private long videoDuration = 0;

    @AfterViews
    public void initialize() {
        videoPath = Objects.requireNonNull(getArguments()).getString("videoPath");
        resId = getArguments().getString("resId");
        initializePlayer(videoPath);
    }

    private void initializePlayer(String videoPath) {
        videoView.setVideoPath(videoPath);
        videoView.start();
        videoView.setOnPreparedListener(mp -> {
            startTime = FC_Utility.getCurrentDateTime();
            videoDuration = videoView.getDuration();
        });
        videoView.setOnCompletionListener(mp -> {
            //TODO back press
        });
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
