package com.pratham.foundation.ui.contentPlayer.story_reading;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.RipplePulseLayout;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.modalclasses.ModalParaSubMenu;
import com.pratham.foundation.services.TTSService;
import com.pratham.foundation.services.stt.ContinuousSpeechService_New;
import com.pratham.foundation.services.stt.STT_Result_New;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.BaseActivity.setMute;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;
import static com.pratham.foundation.utility.SplashSupportActivity.ButtonClickSound;


@EFragment(R.layout.fragment_story_reading)
public class StoryReadingFragment extends Fragment implements
        /*RecognitionListener, */STT_Result_New.sttView,
        StoryReadingContract.StoryReadingView {

    @Bean(StoryReadingPresenter.class)
    StoryReadingContract.StoryReadingPresenter presenter;

    public static MediaPlayer mp, mPlayer;
    @ViewById(R.id.myflowlayout)
    FlowLayout wordFlowLayout;
    @ViewById(R.id.tv_story_title)
    TextView story_title;
    @ViewById(R.id.iv_image)
    ImageView pageImage;
    @ViewById(R.id.btn_prev)
    ImageButton btn_previouspage;
    @ViewById(R.id.btn_next)
    ImageButton btn_nextpage;
    @ViewById(R.id.btn_play)
    ImageButton btn_Play;
    @ViewById(R.id.btn_read_mic)
    ImageButton btn_Mic;
    @ViewById(R.id.myScrollView)
    ScrollView myScrollView;
    @ViewById(R.id.layout_ripplepulse_right)
    RipplePulseLayout layout_ripplepulse_right;
    @ViewById(R.id.layout_ripplepulse_left)
    RipplePulseLayout layout_ripplepulse_left;
    @ViewById(R.id.layout_mic_ripple)
    RipplePulseLayout layout_mic_ripple;
    @ViewById(R.id.ll_btn_play)
    LinearLayout ll_btn_play;
    @ViewById(R.id.ll_btn_submit)
    LinearLayout ll_btn_submit;
    @ViewById(R.id.btn_submit)
    Button btn_submit;

    ContinuousSpeechService_New continuousSpeechService;

    public static String storyId, StudentID = "", readingContentPath;
    TTSService ttsService;
    Context context;

    List<ModalParaSubMenu> modalPagesList;

    String contentType, storyPath, storyData, storyName, storyAudio, certiCode, storyBg;
    static int currentPage, lineBreakCounter = 0;

    public Handler handler, audioHandler, soundStopHandler, colorChangeHandler,
            startReadingHandler, quesReadHandler, endhandler;

    List<String> splitWords = new ArrayList<String>();
    List<String> splitWordsPunct = new ArrayList<String>();
    List<String> wordsDurationList = new ArrayList<String>();
    List<String> wordsResIdList = new ArrayList<String>();

    boolean fragFlg = false, lastPgFlag = false;
    boolean playFlg = false, mediaPauseFlag = false, pauseFlg = false;
    int wordCounter = 0, totalPages = 0, correctAnswerCount, pageNo = 1, quesNo = 0, quesPgNo = 0;
    float stopPlayBack = 0f, startPlayBack = 0f;
    List<Integer> readSounds = new ArrayList<>();
    private String LOG_TAG = "VoiceRecognitionActivity", /*myCurrentSentence,*/
            startTime;
    boolean voiceStart = false, flgPerMarked = false, onSdCard;
    static boolean[] correctArr;
    static boolean[] testCorrectArr;


    @AfterViews
    public void initialize() {
        silence_outer_layout.setVisibility(View.GONE);
        Bundle bundle = getArguments();
        contentType = bundle.getString("contentType");
        storyData = bundle.getString("storyData");
        storyPath = bundle.getString("storyPath");
        storyId = bundle.getString("storyId");
        StudentID = bundle.getString("StudentID");
        storyName = bundle.getString("storyTitle");
        certiCode = bundle.getString("certiCode");
        onSdCard = bundle.getBoolean("onSdCard", false);
        ttsService = BaseActivity.ttsService;
        contentType = "story";

        context = getActivity();
        presenter.setView(StoryReadingFragment.this);
        showLoader();
        modalPagesList = new ArrayList<>();

        continuousSpeechService = new ContinuousSpeechService_New(context, StoryReadingFragment.this);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout_ripplepulse_right.getLayoutParams();
        params.rightMargin = -(int) getResources().getDimension(R.dimen._25sdp);
        params.bottomMargin = -(int) getResources().getDimension(R.dimen._25sdp);
        params = (ViewGroup.MarginLayoutParams) layout_ripplepulse_left.getLayoutParams();
        params.leftMargin = -(int) getResources().getDimension(R.dimen._25sdp);
        params.bottomMargin = -(int) getResources().getDimension(R.dimen._25sdp);

        if (contentType.equalsIgnoreCase(FC_Constants.RHYME_RESOURCE))
            layout_mic_ripple.setVisibility(View.GONE);

        readSounds.add(R.raw.tap_the_mic);
        readSounds.add(R.raw.your_turn_to_read);
        readSounds.add(R.raw.would_you_like_to_read);
        readSounds.add(R.raw.tap_the_mic_to_read_out);
        Collections.shuffle(readSounds);
        startTime = FC_Utility.getCurrentDateTime();
        presenter.setResId(storyId);

        currentPage = 0;

        presenter.addScore(0, "", 0, 0, startTime, contentType + " start");
        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + "/.LLA/English/Game/" + storyPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + "/.LLA/English/Game/" + storyPath + "/";

        continuousSpeechService.resetSpeechRecognizer();

        try {
            story_title.setText(storyName);
            presenter.fetchJsonData(readingContentPath);
            //pageArray = presenter.fetchJsonData(storyName);
//            getWordsOfStoryOfPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListData(List<ModalParaSubMenu> paraDataList) {
        modalPagesList = paraDataList;
        totalPages = modalPagesList.size();
    }

    public Dialog myLoadingDialog;

    @UiThread
    @Override
    public void showLoader() {
        myLoadingDialog = new Dialog(context);
        myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myLoadingDialog.setContentView(R.layout.loading_dialog);
        myLoadingDialog.setCanceledOnTouchOutside(false);
//        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }

    @UiThread
    @Override
    public void dismissLoadingDialog() {
        if (myLoadingDialog != null) {
            myLoadingDialog.dismiss();
        }
    }

    @UiThread
    @Override
    public void setCategoryTitle(String title) {
        story_title.setText(storyName);
    }

    @Override
    public void setParaAudio(String paraAudio) {
        storyAudio = paraAudio;
    }

    @UiThread
    @Override
    public void initializeContent(int pageNo) {
        currentPage = pageNo;
        if (currentPage == totalPages - 1) {
            lastPgFlag = true;
            layout_ripplepulse_right.setVisibility(View.GONE);
        }
        if (currentPage == 0) {
            lastPgFlag = false;
            layout_ripplepulse_left.setVisibility(View.GONE);
        }
        if (currentPage < totalPages - 1 && currentPage > 0) {
            lastPgFlag = false;
            layout_ripplepulse_left.setVisibility(View.VISIBLE);
            layout_ripplepulse_right.setVisibility(View.VISIBLE);
        }

        wordFlowLayout.removeAllViews();
        storyAudio = modalPagesList.get(currentPage).getReadPageAudio();
        storyBg = modalPagesList.get(currentPage).getPageImage();

        try {
            File f = new File(readingContentPath + storyBg);
            if (f.exists()) {
                Bitmap bmImg = BitmapFactory.decodeFile(readingContentPath + storyBg);
                BitmapFactory.decodeStream(new FileInputStream(readingContentPath + storyBg));
                pageImage.setImageBitmap(bmImg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        correctArr = new boolean[modalPagesList.get(currentPage).getReadList().size()];
        splitWords = new ArrayList<>();
        splitWordsPunct = new ArrayList<>();
        wordsDurationList = new ArrayList<>();
        wordsResIdList = new ArrayList<>();
        lineBreakCounter = 0;

        for (int i = 0; i < modalPagesList.get(currentPage).getReadList().size(); i++) {
            splitWords.add(modalPagesList.get(currentPage).getReadList().get(i).getWord());
            if (modalPagesList.get(currentPage).getReadList().get(i).getWord().equalsIgnoreCase("#"))
                lineBreakCounter += 1;
            correctArr[i] = false;
            String regex = "[\\-+.\"^?!@#%&*,:]";
            splitWordsPunct.add(splitWords.get(i).replaceAll(regex, ""));
            wordsDurationList.add(modalPagesList.get(currentPage).getReadList().get(i).getWordDuration());
            wordsResIdList.add(modalPagesList.get(currentPage).getReadList().get(i).getWordId());
        }
        startPlayBack = Float.parseFloat(modalPagesList.get(currentPage).getReadList().get(0).getWordFrom());
        setWordsToLayout();
        startTime = FC_Utility.getCurrentDateTime();
        new Handler().postDelayed(() -> {
            if (!FC_Constants.isTest)
                btn_Play.performClick();
            else {
                btn_Mic.performClick();
                ll_btn_play.setVisibility(View.GONE);
                ll_btn_submit.setVisibility(View.VISIBLE);
            }
        }, 200);
    }

    @UiThread
    @Override
    public void allCorrectAnswer() {
        for (int i = 0; i < splitWordsPunct.size(); i++) {
            ((SansTextView) wordFlowLayout.getChildAt(i)).setTextColor(getResources().getColor(R.color.colorGreenDark));
            correctArr[i] = true;
        }
        new Handler().postDelayed(() -> {
            //setMute(0);
            if (lastPgFlag) {
                if (FC_Constants.isTest)
                    showStars(true);
                else
                    showAcknowledgeDialog(true);
            } else {
                btn_nextpage.performClick();
            }
        }, 1000);
    }

    private void setWordsToLayout() {

        for (int i = 0; i < splitWords.size(); i++) {
            if (splitWords.get(i).equalsIgnoreCase("#")) {
                final SansTextView myTextView = new SansTextView(context);
                myTextView.setWidth(2000);
                wordFlowLayout.addView(myTextView);
            } else {
                final SansTextView myTextView = new SansTextView(context);
                myTextView.setText(splitWords.get(i));
                myTextView.setId(i);
                myTextView.setTextSize(35);
                myTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
                final int finalI = i;
                myTextView.setOnClickListener(v -> {
                    if ((!playFlg || pauseFlg) && !voiceStart) {
                        setMute(0);
                        myTextView.setTextColor(getResources().getColor(R.color.colorRedDark));
                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.reading_zoom_in);
                        myTextView.startAnimation(animation);
                        colorChangeHandler.postDelayed(() -> {
                            myTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
                            Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.reading_zoom_out);
                            myTextView.startAnimation(animation1);
                        }, 350);
                        playClickedWord(finalI);
//                        ttsService.play("" + linesStringList[finalI]);
                    }
                });
                wordFlowLayout.addView(myTextView);
            }
        }
        dismissLoadingDialog();
    }

    Boolean clickFlag = false;

    private void playClickedWord(int id) {
        try {
            if (!FC_Constants.isTest && !clickFlag) {
                clickFlag = true;
                Log.d("ReadingPara", "wordCounter : " + wordCounter);
                float end = Float.parseFloat(modalPagesList.get(currentPage).getReadList().get(id).getWordDuration());
                end = end * 1000;
                float seekTime = Float.parseFloat(modalPagesList.get(currentPage).getReadList().get(id).getWordFrom());
                MediaPlayer clickMP = new MediaPlayer();
                clickMP.setDataSource(readingContentPath + storyAudio);
                clickMP.prepare();
                clickMP.seekTo((int) (seekTime * 1000));
                clickMP.start();
                endhandler = new Handler();
                endhandler.postDelayed(() -> {
                    clickMP.stop();
                    final SansTextView myView = (SansTextView) wordFlowLayout.getChildAt(id);
                    myView.setTextColor(getResources().getColor(R.color.colorPrimary));
                    clickFlag = false;
                }, (long) end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAudioReading() {
        try {
            mp = new MediaPlayer();
            mp.setDataSource(readingContentPath + storyAudio);
            mp.prepare();
            mp.seekTo((int) (startPlayBack * 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        startStoryReading(wordCounter);
    }

    public void startStoryReading(final int index) {
        float wordDuration = 1;
        handler = new Handler();
        colorChangeHandler = new Handler();
        mp.start();
        SansTextView myNextView = null;

        if (index < wordsDurationList.size()) {
            wordDuration = Float.parseFloat(wordsDurationList.get(index));
            final SansTextView myView = (SansTextView) wordFlowLayout.getChildAt(index);
            if (index < wordFlowLayout.getChildCount())
                myNextView = (SansTextView) wordFlowLayout.getChildAt(index + 1);

            if (myNextView != null)
                isScrollBelowVisible(myNextView);
            myView.setTextColor(getResources().getColor(R.color.colorRedDark));
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.reading_zoom_in);
            myView.startAnimation(animation);
//            wordPopUp(this, myView);
            colorChangeHandler.postDelayed(() -> {
                myView.setTextColor(getResources().getColor(R.color.colorPrimary));
//                    wordPopDown(ReadingStoryActivity.this, myView);
                Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.reading_zoom_out);
                myView.startAnimation(animation1);
            }, 350);
            if (index == wordsDurationList.size() - 1) {
                try {
                    handler.postDelayed(() -> {
                        try {
                            playFlg = false;
                            pauseFlg = true;
                            if (!contentType.equalsIgnoreCase(FC_Constants.RHYME_RESOURCE)) {
                                layout_mic_ripple.setVisibility(View.VISIBLE);
                                quesReadHandler = new Handler();
                                quesReadHandler.postDelayed(() -> {
                                    Collections.shuffle(readSounds);
                                    mPlayer = MediaPlayer.create(context, readSounds.get(0));
                                    mPlayer.start();
                                }, (long) (5000));
                            }
                            if (mp != null && mp.isPlaying())
                                mp.stop();
                            btn_Play.setImageResource(R.drawable.ic_play_arrow);
                            layout_mic_ripple.startRippleAnimation();
                            layout_ripplepulse_right.startRippleAnimation();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, (long) (wordDuration * 1000));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPauseFlag = true;
            }
        } else
            wordDuration = 1;

        handler.postDelayed(() -> {
            if (index < wordFlowLayout.getChildCount()) {
                wordCounter += 1;
                if (!pauseFlg)
                    startStoryReading(wordCounter);
            } else {
                for (int i = 0; i < wordsDurationList.size(); i++) {
                    SansTextView myView = (SansTextView) wordFlowLayout.getChildAt(i);
                    myView.setBackgroundColor(Color.TRANSPARENT);
                    myView.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                wordCounter = 0;
            }
        }, (long) (wordDuration * 1000));

    }

    @UiThread
    @Override
    public void setCorrectViewColor() {
        for (int x = 0; x < correctArr.length; x++) {
            if (correctArr[x]) {
                ((SansTextView) wordFlowLayout.getChildAt(x)).setTextColor(getResources().getColor(R.color.colorGreenDark));
            }
        }
//        if (voiceStart)
//            sttMethod();
    }

    //If you want to detect that the view is FULLY visible:
    private void isScrollBelowVisible(View view) {
        Rect scrollBounds = new Rect();
        myScrollView.getDrawingRect(scrollBounds);

        float top = view.getY();
        float bottom = top + view.getHeight();

        if (!(scrollBounds.top < top) || !(scrollBounds.bottom > bottom))
            view.getParent().requestChildFocus(view, view);
    }

    @Click(R.id.btn_read_mic)
    void sttMethod() {
        if (!voiceStart) {
            voiceStart = true;
            flgPerMarked = false;
            btn_Mic.setImageResource(R.drawable.ic_stop_black_24dp);
            layout_mic_ripple.stopRippleAnimation();
            ll_btn_play.setVisibility(View.GONE);
            layout_ripplepulse_right.stopRippleAnimation();
            try {
                if (quesReadHandler != null) {
                    quesReadHandler.removeCallbacksAndMessages(null);
                    try {
                        if (mPlayer.isPlaying()) {
                            mPlayer.stop();
                            mPlayer.reset();
                            mPlayer.release();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            continuousSpeechService.startSpeechInput();
        } else {
            voiceStart = false;
            if (!FC_Constants.isTest)
                ll_btn_play.setVisibility(View.VISIBLE);
            btn_Mic.setImageResource(R.drawable.ic_mic_black_24dp);
            layout_mic_ripple.startRippleAnimation();
            layout_ripplepulse_right.startRippleAnimation();
            continuousSpeechService.stopSpeechInput();
        }
    }

    @Click(R.id.btn_play)
    void playReading() {
        if (!playFlg || pauseFlg) {
            layout_mic_ripple.setVisibility(View.GONE);
            playFlg = true;
            pauseFlg = false;
            try {
                if (quesReadHandler != null) {
                    quesReadHandler.removeCallbacksAndMessages(null);
                    try {
                        if (mPlayer.isPlaying() && mPlayer != null) {
                            mPlayer.stop();
                            mPlayer.reset();
                            mPlayer.release();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            btn_Play.setImageResource(R.drawable.ic_stop_black_24dp);
//            btn_Play.setText("Stop");
            if (audioHandler != null)
                audioHandler.removeCallbacksAndMessages(null);
            if (handler != null)
                handler.removeCallbacksAndMessages(null);
            if (colorChangeHandler != null)
                colorChangeHandler.removeCallbacksAndMessages(null);
            if (quesReadHandler != null) {
                quesReadHandler.removeCallbacksAndMessages(null);
                try {
                    if (mPlayer.isPlaying() && mPlayer != null) {
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayer.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (startReadingHandler != null)
                startReadingHandler.removeCallbacksAndMessages(null);
            if (soundStopHandler != null)
                soundStopHandler.removeCallbacksAndMessages(null);
            wordCounter = 0;
            layout_ripplepulse_right.stopRippleAnimation();
            setMute(0);
            startAudioReading();
        } else {
            btn_Play.setImageResource(R.drawable.ic_play_arrow);
            layout_mic_ripple.startRippleAnimation();
            layout_ripplepulse_right.startRippleAnimation();
//            btn_Play.setText("Read");
            if (!contentType.equalsIgnoreCase(FC_Constants.RHYME_RESOURCE))
                layout_mic_ripple.setVisibility(View.VISIBLE);
            wordCounter = 0;
            try {
                playFlg = false;
                pauseFlg = true;
                try {
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.reset();
                        mp.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (startReadingHandler != null)
                    startReadingHandler.removeCallbacksAndMessages(null);
                if (soundStopHandler != null)
                    soundStopHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int setBooleanGetCounter() {
        int counter = 0;
        for (int x = 0; x < correctArr.length; x++) {
            if (correctArr[x]) {
                ((SansTextView) wordFlowLayout.getChildAt(x)).setTextColor(getResources().getColor(R.color.colorGreenDark));
                counter++;
            }
        }
        return counter;
    }

    public float getPercentage() {
        int counter = 0;
        float perc = 0f;
        int totLen = correctArr.length - lineBreakCounter;
        try {
            for (int x = 0; x < correctArr.length; x++) {
                if (correctArr[x]) {
                    ((SansTextView) wordFlowLayout.getChildAt(x)).setTextColor(getResources().getColor(R.color.colorGreenDark));
                    counter++;
                }
            }
            if (counter > 0)
                perc = ((float) counter / (float) totLen) * 100;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return perc;
    }

    @Click(R.id.btn_prev)
    void gotoPrevPage() {
        if (currentPage > 0) {
            wordCounter = 0;
            ButtonClickSound.start();
            try {
                if (audioHandler != null)
                    audioHandler.removeCallbacksAndMessages(null);
                if (handler != null)
                    handler.removeCallbacksAndMessages(null);
                if (colorChangeHandler != null)
                    colorChangeHandler.removeCallbacksAndMessages(null);
                if (quesReadHandler != null) {
                    quesReadHandler.removeCallbacksAndMessages(null);
                    try {
                        if (mPlayer.isPlaying() && mPlayer != null) {
                            mPlayer.stop();
                            mPlayer.reset();
                            mPlayer.release();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (soundStopHandler != null)
                    soundStopHandler.removeCallbacksAndMessages(null);
                if (startReadingHandler != null)
                    startReadingHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (voiceStart) {
                voiceStart = false;
                if (!FC_Constants.isTest)
                    ll_btn_play.setVisibility(View.VISIBLE);
                btn_Mic.setImageResource(R.drawable.ic_mic_black_24dp);
                continuousSpeechService.stopSpeechInput();
                setMute(0);
            }
            Log.d("click", "totalPages: PreviousBtn: " + totalPages + "  currentPage: " + currentPage);
            try {
                if (mp != null && mp.isPlaying()) {
                    mp.stop();
                    mp.reset();
                    mp.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!flgPerMarked) {
                flgPerMarked = true;
                correctAnswerCount = setBooleanGetCounter();
                float perc = getPercentage();
                presenter.addScore(0, "perc - " + perc, correctAnswerCount, correctArr.length, startTime, "" + contentType);
            }

            try {
                currentPage--;
                pageNo--;
                playFlg = false;
                pauseFlg = true;
                flgPerMarked = false;
                presenter.getPage(currentPage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Click(R.id.btn_submit)
    public void submitTest() {
        showStars(false);
    }

    @Click(R.id.btn_next)
    void gotoNextPage() {
        if (currentPage < totalPages - 1) {
            layout_ripplepulse_right.startRippleAnimation();
            wordCounter = 0;
            setMute(0);
            try {
                if (audioHandler != null)
                    audioHandler.removeCallbacksAndMessages(null);
                if (handler != null)
                    handler.removeCallbacksAndMessages(null);
                if (colorChangeHandler != null)
                    colorChangeHandler.removeCallbacksAndMessages(null);
                if (quesReadHandler != null) {
                    quesReadHandler.removeCallbacksAndMessages(null);
                    try {
                        if (mPlayer.isPlaying() && mPlayer != null) {
                            mPlayer.stop();
                            mPlayer.reset();
                            mPlayer.release();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (soundStopHandler != null)
                    soundStopHandler.removeCallbacksAndMessages(null);
                if (startReadingHandler != null)
                    startReadingHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (voiceStart) {
                voiceStart = false;
                if (!FC_Constants.isTest)
                    ll_btn_play.setVisibility(View.VISIBLE);
                btn_Mic.setImageResource(R.drawable.ic_mic_black_24dp);
                continuousSpeechService.stopSpeechInput();
                setMute(0);
            }
            ButtonClickSound.start();
            Log.d("click", "totalPages: NextBtn: " + totalPages + "  currentPage: " + currentPage);
            try {
                if (mp != null && mp.isPlaying()) {
                    mp.stop();
                    mp.reset();
                    mp.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!flgPerMarked) {
                flgPerMarked = true;
                correctAnswerCount = setBooleanGetCounter();
                float perc = getPercentage();
                presenter.addScore(0, "perc - " + perc, correctAnswerCount, correctArr.length, startTime, "" + contentType);
            }

            currentPage++;
            pageNo++;
            flgPerMarked = true;
            playFlg = false;
            pauseFlg = true;
            presenter.getPage(currentPage);
            Log.d("click", "NextBtn - totalPages: " + totalPages + "  currentPage: " + currentPage);
        }
    }

    @SuppressLint("SetTextI18n")
    public void showAcknowledgeDialog(boolean diaComplete) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText("Yes");
        dia_btn_red.setText("No");
        dia_btn_yellow.setText("" + dialog_btn_cancel);
        dialog.show();

        if (!diaComplete)
            dia_title.setText("Exit\n'" + storyName + "'");
        else {
            dia_title.setText("Good Job\nRead another one???");
        }
        dia_btn_red.setOnClickListener(v -> dialog.dismiss());
        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
        dia_btn_green.setOnClickListener(v -> {
            dialog.dismiss();
            if (FC_Constants.isTest) {
                float correctCnt = getPercentage();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("cCode", certiCode);
                returnIntent.putExtra("sMarks", correctCnt);
                returnIntent.putExtra("tMarks", correctArr.length);
//                setResult(Activity.RESULT_OK, returnIntent);
            }
            exitDBEntry();
            //TODO Back Press Here
//            ReadingStoryActivity.super.onBackPressed();
        });
    }

    public void exitDBEntry() {
        try {
            if (mp != null && mp.isPlaying()) {
                mp.stop();
                mp.reset();
                mp.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (colorChangeHandler != null)
                colorChangeHandler.removeCallbacksAndMessages(null);
            if (quesReadHandler != null) {
                quesReadHandler.removeCallbacksAndMessages(null);
                try {
                    if (mPlayer.isPlaying() && mPlayer != null) {
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayer.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (soundStopHandler != null)
                soundStopHandler.removeCallbacksAndMessages(null);
            if (handler != null)
                handler.removeCallbacksAndMessages(null);
            if (mp != null && mp.isPlaying()) mp.stop();
            if (startReadingHandler != null)
                startReadingHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!flgPerMarked) {
                flgPerMarked = true;
                float correctCnt = getPercentage();
                correctAnswerCount = setBooleanGetCounter();
                presenter.addScore(0, "perc - " + correctCnt, correctAnswerCount, correctArr.length, startTime, "" + contentType);
            }
            presenter.addScore(0, "", 0, 0, startTime, contentType + " End");
        } catch (Exception e) {
            e.printStackTrace();
        }
        presenter.addProgress();
    }

/*
    @Override
    public void onBackPressed() {

        if (voiceStart) {
            btn_Mic.performClick();
            voiceStart = false;
            if (!FC_Constants.isTest)
                ll_btn_play.setVisibility(View.VISIBLE);
            setMute(0);
        }
        try {
            if (playFlg)
                btn_Play.performClick();
            if (audioHandler != null)
                audioHandler.removeCallbacksAndMessages(null);
            if (handler != null)
                handler.removeCallbacksAndMessages(null);
            if (colorChangeHandler != null)
                colorChangeHandler.removeCallbacksAndMessages(null);
            if (quesReadHandler != null) {
                quesReadHandler.removeCallbacksAndMessages(null);
            }
            if (soundStopHandler != null)
                soundStopHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!fragFlg) {
            showAcknowledgeDialog(false);
        } else {
            fragFlg = false;
//            getSupportFragmentManager().popBackStack();
        }
    }
*/

    int correctCnt = 0, total = 0;

    @SuppressLint("SetTextI18n")
    private void showStars(boolean diaComplete) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_test_star_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        RatingBar dia_ratingBar = dialog.findViewById(R.id.dia_ratingBar);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);
        correctCnt = 0;
        total = 0;

        try {
            if (testCorrectArr != null) {
                total = testCorrectArr.length;
                for (int i = 0; i < testCorrectArr.length; i++)
                    if (testCorrectArr[i])
                        correctCnt += 1;
            }
        } catch (Exception e) {
            correctCnt = 0;
            total = 0;
        }

        float perc = 0f;
        if (total > 0)
            perc = ((float) correctCnt / (float) total) * 100;

        float rating = getStarRating(perc);
        dia_ratingBar.setRating(rating);

        dia_btn_red.setVisibility(View.GONE);
        dia_btn_green.setText("SUBMIT");
        dia_btn_yellow.setText("" + dialog_btn_cancel);
        if (diaComplete)
            dia_btn_yellow.setVisibility(View.GONE);

        dialog.show();

        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
        dia_btn_green.setOnClickListener(v -> {
            dialog.dismiss();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("cCode", certiCode);
            returnIntent.putExtra("sMarks", correctCnt);
            returnIntent.putExtra("tMarks", total);
//            setResult(Activity.RESULT_OK, returnIntent);
//            finish();
        });
    }

    public float getStarRating(float perc) {
        float ratings=0;
        if (perc < 21)
            ratings = (float) 1;
        else if (perc >= 21 && perc < 41)
            ratings = (float) 2;
        else if (perc >= 41 && perc < 61)
            ratings = (float) 3;
        else if (perc >= 61 && perc < 81)
            ratings = (float) 4;
        else if (perc >= 81)
            ratings = (float) 5;

        return ratings;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (voiceStart) {
            btn_Mic.performClick();
            voiceStart = false;
            if (!FC_Constants.isTest)
                ll_btn_play.setVisibility(View.VISIBLE);
            setMute(0);
        }
        try {
            if (playFlg)
                btn_Play.performClick();
            if (audioHandler != null)
                audioHandler.removeCallbacksAndMessages(null);
            if (handler != null)
                handler.removeCallbacksAndMessages(null);
            if (colorChangeHandler != null)
                colorChangeHandler.removeCallbacksAndMessages(null);
            if (quesReadHandler != null) {
                quesReadHandler.removeCallbacksAndMessages(null);
/*                try {
                    if (mPlayer.isPlaying() && mPlayer != null) {
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayer.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
            if (soundStopHandler != null)
                soundStopHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Stt_onResult(ArrayList<String> sttResult) {

        flgPerMarked = false;
        presenter.sttResultProcess(sttResult, splitWordsPunct, wordsResIdList);

/*        if (!voiceStart) {
            resetSpeechRecognizer();
            btn_Play.setVisibility(View.VISIBLE);
            btn_Mic.setImageResource(R.drawable.ic_mic_black_24dp);
            setMute(0);
        } else
            speech.startListening(recognizerIntent);*/
    }

    @ViewById(R.id.silence_outer)
    RelativeLayout silence_outer_layout;
    @ViewById(R.id.silence_main_layout)
    RelativeLayout silence_main_layout;
    @ViewById(R.id.silence_iv)
    ImageView silence_iv;
    Handler silenceViewHandler;

    @Override
    public void silenceDetected() {
        if (voiceStart) {
            continuousSpeechService.resetHandler(true);
            silence_outer_layout.setVisibility(View.VISIBLE);
            silenceViewHandler = new Handler();
            silence_iv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_continuous_shake));
            AnimateTextView(context, silence_main_layout);
        }
    }

    public void AnimateTextView(Context c, final RelativeLayout silence_layout) {
        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.zoom_in_new);
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out_full);
        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                silence_layout.setAnimation(anim_out);
            }
        });
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                resetSilence();
            }
        });
        //(holder.mTextView).setAnimation(anim_in);
        silence_layout.setAnimation(anim_in);
    }

    private void resetSilence() {
        silenceViewHandler.postDelayed(() -> {
            silence_iv.clearAnimation();
            silence_outer_layout.setVisibility(View.GONE);
            continuousSpeechService.resetHandler(false);
        }, 10);
    }

/*    public void loadFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("contentType", contentType);
        FC_Utility.showFragment(ReadingStoryActivity.this, new fragment_acknowledge(), R.id.story_ll,
                bundle, fragment_acknowledge.class.getSimpleName());
    }*/

}