package com.pratham.foundation.ui.contentPlayer.audio_player;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.services.stt.ContinuousSpeechService_New;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EFragment(R.layout.fragment_audio_player)
public class AudioPlayerFragment extends Fragment implements OnGameClose {

    @ViewById(R.id.tv_title)
    TextView tv_title;
    @ViewById(R.id.start_time)
    TextView start_time;
    @ViewById(R.id.end_time)
    TextView end_time;
    @ViewById(R.id.ib_play)
    ImageButton ib_play;
    @ViewById(R.id.imageView)
    SimpleDraweeView imageView;
    @ViewById(R.id.seekBar)
    SeekBar seekbar;
    @ViewById(R.id.rl_no_data)
    RelativeLayout rl_no_data;
    @ViewById(R.id.main_layout)
    RelativeLayout main_layout;

    private int index = 0;
    private float perc = 0;
    private float percScore = 0;
    public static Intent intent;
    private String readingContentPath, contentPath, contentTitle, StudentID, resId;
    private boolean onSdCard;
    private Context context;
    private List<ScienceQuestion> dataList;
    String resStartTime;
    private ContinuousSpeechService_New continuousSpeechService;
    public Dialog myLoadingDialog;
    boolean dialogFlg = false;
    private String jsonName, nodeImage, imagePath;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private MediaPlayer mediaPlayer;
    private int oneTimeOnly = 0;

    //private String speechStartTime;
    public AudioPlayerFragment() {
    }

    @SuppressLint("DefaultLocale")
    @AfterViews
    public void initiate() {
        // super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            context = getActivity();
            contentPath = getArguments().getString("contentPath");
            StudentID = getArguments().getString("StudentID");
            resId = getArguments().getString("resId");
            contentTitle = getArguments().getString("contentName");
            onSdCard = getArguments().getBoolean("onSdCard", false);
            jsonName = getArguments().getString("jsonName");
            nodeImage = getArguments().getString("nodeImage");

            oneTimeOnly = 0;

            if (onSdCard) {
                readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath;
            } else {
                readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath;
            }
            if(!contentPath.equalsIgnoreCase(" ") || !contentPath.equalsIgnoreCase("") || contentPath!=null) {
                try {
                    if (onSdCard) {
                        imagePath = ApplicationClass.contentSDPath + App_Thumbs_Path + "/" + nodeImage;
                    } else {
                        imagePath = ApplicationClass.foundationPath + App_Thumbs_Path + "/" + nodeImage;
                    }
                    File f = new File(imagePath);
                    if (f.exists()) {
                        ImageRequest imageRequest = ImageRequestBuilder
                                .newBuilderWithSource(Uri.fromFile(f))
                                .setLocalThumbnailPreviewsEnabled(true)
                                .build();
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setImageRequest(imageRequest)
                                .setAutoPlayAnimations(true)// if gif, it will play.
                                .setOldController(Objects.requireNonNull(imageView).getController())
                                .build();
                        imageView.setController(controller);
                    } else {
                        imageView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                resStartTime = FC_Utility.getCurrentDateTime();
                addScore(0, "", 0, 0, resStartTime, jsonName + " " + GameConstatnts.START);
                tv_title.setText(contentTitle);
                try {
                    myHandler = new Handler();
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(readingContentPath);
                    mediaPlayer.prepare();
                    finalTime = mediaPlayer.getDuration();
                    startTime = mediaPlayer.getCurrentPosition();
                    if (oneTimeOnly == 0) {
                        seekbar.setMax((int) finalTime);
                        oneTimeOnly = 1;
                    }

                    mediaPlayer.setOnCompletionListener(mp1 -> {
                        ib_play.setImageResource(R.drawable.ic_play_arrow_black);
                        isPlaying = false;
                        try {
                            if (mp1.isPlaying())
                                mp1.stop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    end_time.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                            finalTime)))
                    );

                    start_time.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                            startTime)))
                    );
                    seekbar.setClickable(false);

                } catch (IOException e) {
                    showNoData();
                    e.printStackTrace();
                }
            }else {
                showNoData();
            }
        }
    }

    @UiThread
    public void showNoData() {
        main_layout.setVisibility(View.GONE);
        rl_no_data.setVisibility(View.VISIBLE);
    }

    private Runnable UpdateSongTime = new Runnable() {
        @SuppressLint("DefaultLocale")
        @UiThread
        public void run() {
            try {
                startTime = mediaPlayer.getCurrentPosition();
                start_time.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime)))
                );
                Log.d("AUDIO ACT", "run: " + startTime);
                seekbar.setProgress((int) startTime);
                myHandler.postDelayed(this, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    boolean isPlaying = false;

    @UiThread
    @SuppressLint("DefaultLocale")
    @Click(R.id.ib_play)
    public void onPlayClicked() {
        seekbar.setClickable(false);
        if (!isPlaying) {
            isPlaying = true;
            try {
                ib_play.setImageResource(R.drawable.ic_pause_black);
                mediaPlayer.start();
//                seekbar.setProgress((int)startTime);
                myHandler.postDelayed(UpdateSongTime, 100);
            } catch (Exception e) {
                ib_play.setImageResource(R.drawable.ic_play_arrow_black);
                isPlaying = false;
                mediaPlayer.stop();
                e.printStackTrace();
            }
        } else {
            ib_play.setImageResource(R.drawable.ic_play_arrow_black);
            isPlaying = false;
            mediaPlayer.pause();
        }
    }

    @UiThread
    @SuppressLint("DefaultLocale")
    @Click(R.id.ib_rewind)
    public void onRewindClicked() {
        seekbar.setClickable(false);

        if (isPlaying) {
            int temp = (int) startTime;
            if ((temp - backwardTime) > 0) {
                startTime = startTime - backwardTime;
                mediaPlayer.seekTo((int) startTime);
            }
        }
    }

    @UiThread
    @SuppressLint("DefaultLocale")
    @Click(R.id.ib_fastforward)
    public void onFastForwardClicked() {
        seekbar.setClickable(false);

        if (isPlaying) {
            int temp = (int) startTime;
            if ((temp + forwardTime) <= finalTime) {
                startTime = startTime + forwardTime;
                mediaPlayer.seekTo((int) startTime);
            }
        }
    }

    @UiThread
    @SuppressLint("DefaultLocale")
    @Click(R.id.ib_stop)
    public void onStopClicked() {
        seekbar.setClickable(false);
        if (isPlaying) {
            isPlaying = false;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String
            resStartTime, String Label) {
        try {
            String deviceId = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(resId);
            score.setQuestionId(wID);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setStartDateTime(resStartTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(FC_Constants.currentLevel);
            score.setLabel(Word + " - " + Label);
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(isPlaying)
            ib_play.performClick();

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    public void dismissLoadingDialog() {
        try {
            dialogFlg = false;
            new Handler().postDelayed(() -> {
                if (myLoadingDialog != null && myLoadingDialog.isShowing())
                    myLoadingDialog.dismiss();
            }, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Click(R.id.submit)
    public void submitClick() {
    }

    @Click(R.id.btn_next)
    public void onNextClick() {
    }

    @Click(R.id.btn_prev)
    public void onPreviousClick() {
    }

    @Override
    public void gameClose() {
        if (isPlaying)
            ib_play.performClick();
        addScore(0, "", 0, 0, resStartTime, jsonName + " " + GameConstatnts.END);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
    }
}