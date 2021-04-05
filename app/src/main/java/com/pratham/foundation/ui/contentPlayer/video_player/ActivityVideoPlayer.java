package com.pratham.foundation.ui.contentPlayer.video_player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.video_player.CustomExoPlayerView;
import com.pratham.foundation.customView.video_player.ExoPlayerCallBack;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.services.youtube_extractor.VideoMeta;
import com.pratham.foundation.services.youtube_extractor.YouTubeExtractor;
import com.pratham.foundation.services.youtube_extractor.YtFile;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;


@EActivity(R.layout.fragment_video_player)
public class ActivityVideoPlayer extends BaseActivity {

    private static final int AAJ_KA_SAWAL_FOR_THIS_VIDEO = 1;
    private static final int SHOW_SAWAL = 2;
    private static final int SHOW_NEXT_CONTENT_OF_THE_COURSE = 3;
    private static final int CLOSE_CONTENT_PLAYER_ACTIVITY = 4;
    private static final int BACK_TO_COURSE_DETAIL = 5;
    @ViewById(R.id.videoView)
    CustomExoPlayerView videoView;
    @ViewById(R.id.close_video)
    ImageButton close_video;
//    @ViewById(R.id.player_control_view)
//    PlayerControlView player_control_view;

    Context context;
    private String contentType, contentPath, contentName, startTime = "no_resource", resId, endTime;
    private long videoDuration = 0;
//    private Modal_AajKaSawal videoSawal = null;
    private boolean initialized = false,onSdCard;
    private boolean isVideoEnded = false;

    @SuppressLint("StaticFieldLeak")
    @AfterViews
    public void initialize() {

//        context = getActivity();
//        Bundle bundle = getArguments();
        context = this;
//        Bundle bundle = getArguments();
//        contentType = bundle.getString("contentType");
//        contentPath = bundle.getString("contentPath");
//        resId = bundle.getString("resId");
//        contentName = bundle.getString("contentName");
//        onSdCard = bundle.getBoolean("onSdCard", false);

        Intent intent = getIntent();
        contentPath = intent.getStringExtra("contentPath");
        resId       = intent.getStringExtra("resId");
        contentName = intent.getStringExtra("contentName");
        contentType = intent.getStringExtra("contentType");
        onSdCard    = intent.getBooleanExtra("onSdCard", false);

//        contentType = FC_Constants.YOUTUBE_LINK;
//        contentPath = "https://youtu.be/XsJTztRwem4";
//        contentPath =  Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/fractions_with_shapes- video.m4v";
//        onSdCard = false;
        if(contentType!=null && contentType.equalsIgnoreCase(FC_Constants.YOUTUBE_LINK)) {
//            new YouTubeExtractor(Objects.requireNonNull(getActivity())) {
            new YouTubeExtractor(Objects.requireNonNull(context)) {
                @Override
                protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                    if (ytFiles != null && ytFiles.size() > 0) {
                        String url = null;
                        for (int i = 0; i < ytFiles.size(); i++) {
                            int m = ytFiles.keyAt(i);
                            if (ytFiles.get(m).getUrl() != null)
                                url = ytFiles.get(m).getUrl();
                            break;
                        }
                        initializePlayer(url);
                    } else {
//                        Toast.makeText(getActivity(), "Video cannot be played", Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "Video cannot be played", Toast.LENGTH_SHORT).show();
                    }
                }
            }.extract(contentPath, true, true);
        } else {
            if (onSdCard)
                contentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath;
            else
                contentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath;
//            contentPath =  Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/fractions_with_shapes- video.m4v";
            initializePlayer(contentPath);
        }
    }

    private void initializePlayer(String videoPath) {
        videoView.setSource(videoPath);
        videoView.setExoPlayerCallBack(new ExoPlayerCallBack() {
            @Override
            public void onError() {
//                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart() {
                if (!initialized) {
                    startTime = FC_Utility.getCurrentDateTime();
                    videoDuration = videoView.getPlayer().getDuration();
                    initialized = true;
                }
            }

            @Override
            public void onEnded() {
                if (!isVideoEnded) {
                    endTime = FC_Utility.getCurrentDateTime();
                    isVideoEnded = true;
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pausePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!videoView.getPlayer().isPlaying())
            videoView.getPlayer().setPlayWhenReady(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Subscribe
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(FC_Constants.CLOSE_CONTENT_PLAYER)) {
                addScoreToDB();
            }
        }
    }

    @Background
    public void addScoreToDB() {
        if(!isVideoEnded)
            endTime = FC_Utility.getCurrentDateTime();
        float scoredMarksInt = (float) FC_Utility.getTimeDifference(startTime, endTime);
        Score modalScore = new Score();
        modalScore.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
        modalScore.setStudentID("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
        modalScore.setDeviceID(FC_Utility.getDeviceID());
        modalScore.setResourceID(resId);
        modalScore.setQuestionId(0);
        modalScore.setScoredMarks((int) FC_Utility.getTimeDifference(startTime, endTime));
        modalScore.setTotalMarks((int) videoDuration);
        modalScore.setStartDateTime(startTime);
        modalScore.setEndDateTime(endTime);
        modalScore.setLevel(0);
        modalScore.setLabel(""+contentType);
        modalScore.setSentFlag(0);
        AppDatabase.getDatabaseInstance(context).getScoreDao().insert(modalScore);
        float perc = 0f;
        perc = (scoredMarksInt / (float) videoDuration) * 100;
        addContentProgress(perc, "resourceProgress");
    }

    private void addContentProgress(float perc, String label) {
        try {
            ContentProgress contentProgress = new ContentProgress();
            contentProgress.setProgressPercentage("" + perc);
            contentProgress.setResourceId("" + resId);
            contentProgress.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            contentProgress.setStudentId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("" + label);
            contentProgress.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.close_video)
    public void closeVide() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        try {
            addScoreToDB();
            BackupDatabase.backup(this);
            super.onBackPressed();
        } catch (Exception e) {
            super.onBackPressed();
            e.printStackTrace();
        }
    }
}
