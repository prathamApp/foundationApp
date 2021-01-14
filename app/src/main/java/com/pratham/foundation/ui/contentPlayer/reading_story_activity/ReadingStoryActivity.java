package com.pratham.foundation.ui.contentPlayer.reading_story_activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nex3z.flowlayout.FlowLayout;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.fontsview.SansTextView;
import com.pratham.foundation.customView.shape_of_view.ShadowLayout;
import com.pratham.foundation.modalclasses.ModalParaSubMenu;
import com.pratham.foundation.services.TTSService;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.services.stt.ContinuousSpeechService_New;
import com.pratham.foundation.services.stt.STT_Result_New;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_FOLDER_NAME;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

//@EActivity(R.layout.activity_story_reading)
@EActivity(R.layout.story_reading_activity)
public class ReadingStoryActivity extends BaseActivity implements
        /*RecognitionListener, */STT_Result_New.sttView,
        ReadingStoryActivityContract.ReadingStoryView {

    @Bean(ReadingStoryActivityPresenter.class)
    ReadingStoryActivityContract.ReadingStoryActivityPresenter presenter;

    public static MediaPlayer mp, mPlayer;
    @ViewById(R.id.myflowlayout)
    FlowLayout wordFlowLayout;
    @ViewById(R.id.tv_story_title)
    TextView story_title;
    @ViewById(R.id.stt_result_tv)
    TextView stt_result_tv;
    @ViewById(R.id.iv_image)
    SimpleDraweeView pageImage;
    @ViewById(R.id.clean_stt)
    ImageView clean_stt;
    @ViewById(R.id.btn_prev)
    ImageButton btn_previouspage;
    @ViewById(R.id.btn_next)
    ImageButton btn_next;
    @ViewById(R.id.btn_play)
    ImageButton btn_Play;
    @ViewById(R.id.btn_read_mic)
    ImageButton btn_Mic;
    @ViewById(R.id.myScrollView)
    ScrollView myScrollView;
    @ViewById(R.id.myScrollView2)
    ScrollView myScrollView2;
    @ViewById(R.id.btn_submit)
    Button btn_submit;
    @ViewById(R.id.story_ll)
    RelativeLayout story_ll;
    @ViewById(R.id.image_container)
    ShadowLayout image_container;
    @ViewById(R.id.btn_camera)
    ImageButton btn_camera;
    @ViewById(R.id.btn_Stop)
    ImageButton btn_Stop;
    @ViewById(R.id.bottom_bar2)
    LinearLayout bottom_bar2;
    @ViewById(R.id.floating_back)
    FloatingActionButton floating_back;
    /*    @ViewById(R.id.floating_info)
        FloatingActionButton floating_info;*/
    @ViewById(R.id.floating_img)
    FloatingActionButton floating_img;

    @ViewById(R.id.ll_edit_text)
    LinearLayout ll_edit_text;
    @ViewById(R.id.et_edit_ans)
    EditText et_edit_ans;
    @ViewById(R.id.bt_edit_ok)
    Button bt_edit_ok;

    ContinuousSpeechService_New continuousSpeechService;

    public static String storyId, StudentID = "", readingContentPath;
    TTSService ttsService;
    Context context;
    List<ModalParaSubMenu> modalPagesList;
    String contentType, storyPath, storyData, storyName, storyAudio, certiCode, storyBg, pageTitle, sttLang;
    static int currentPage, lineBreakCounter = 0;
    public Handler handler, audioHandler, soundStopHandler, colorChangeHandler,
            startReadingHandler, quesReadHandler, endhandler;
    List<String> splitWords = new ArrayList<String>();
    List<String> splitWordsPunct = new ArrayList<String>();
    List<String> wordsDurationList = new ArrayList<String>();
    List<String> wordsResIdList = new ArrayList<String>();
    boolean fragFlg = false, lastPgFlag = false, stopFlg = true;
    boolean playFlg = false, mediaPauseFlag = false, pauseFlg = false, playHideFlg = false;
    int wordCounter = 0, totalPages = 0, correctAnswerCount, pageNo = 1, quesNo = 0, quesPgNo = 0;
    float stopPlayBack = 0f, startPlayBack = 0f;
    List<Integer> readSounds = new ArrayList<>();
    private String LOG_TAG = "VoiceRecognitionActivity", /*myCurrentSentence,*/
            startTime;
    boolean voiceStart = false, flgPerMarked = false, onSdCard;
    static boolean[] correctArr;
    static boolean[] testCorrectArr;
    String [] attAnsList;
//    AnimationDrawable animationDrawable;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        silence_outer_layout.setVisibility(View.GONE);
        floating_back.setImageResource(R.drawable.ic_left_arrow_white);
//        floating_info.setImageResource(R.drawable.ic_info_outline_white);
//        floating_img.setImageResource(R.drawable.ic_image_white);
        Intent intent = getIntent();
        contentType = intent.getStringExtra("contentType");
        storyPath = intent.getStringExtra("storyPath");
        storyId = intent.getStringExtra("storyId");
        storyName = intent.getStringExtra("storyTitle");
        certiCode = intent.getStringExtra("certiCode");
        sttLang = intent.getStringExtra("sttLang");
        onSdCard = intent.getBooleanExtra("onSdCard", false);
        ttsService = ApplicationClass.ttsService;
//        contentType = "story";
        bottom_bar2.setVisibility(View.GONE);
        btn_camera.setVisibility(View.GONE);
/*        animationDrawable = (AnimationDrawable) story_ll.getBackground();
        animationDrawable.setEnterFadeDuration(4500);
        animationDrawable.setExitFadeDuration(4500);
        animationDrawable.start();*/
        context = ReadingStoryActivity.this;
        presenter.setView(ReadingStoryActivity.this);
        showLoader();
        modalPagesList = new ArrayList<>();

        if (sttLang == null)
            sttLang = "English";
        continuousSpeechService = new ContinuousSpeechService_New(context,
                ReadingStoryActivity.this, sttLang);
        if (contentType.equalsIgnoreCase(FC_Constants.RHYME_RESOURCE)) {
            btn_Mic.setVisibility(View.GONE);
            myScrollView2.setVisibility(View.GONE);
        }

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
            readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + storyPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + storyPath + "/";

        continuousSpeechService.resetSpeechRecognizer();
        stopFlg = true;

        try {
            story_title.setText(Html.fromHtml(storyName));
            story_title.setSelected(true);
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
        attAnsList = new String[totalPages];
        for(int a=0; a<totalPages;a++)
            attAnsList[a]="";
    }

    public CustomLodingDialog myLoadingDialog;
    boolean dialogFlg = false;

    @UiThread
    @Override
    public void showLoader() {
        if (!dialogFlg) {
            dialogFlg = true;
            myLoadingDialog = new CustomLodingDialog(context);
            myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myLoadingDialog.setContentView(R.layout.loading_dialog);
            myLoadingDialog.setCanceledOnTouchOutside(false);
            myLoadingDialog.show();
        }
    }

    @UiThread
    @Override
    public void dismissLoadingDialog() {
        try {
            new Handler().postDelayed(() -> {
                if (dialogFlg && myLoadingDialog != null) {
                    myLoadingDialog.dismiss();
                    dialogFlg = false;
                }
            }, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void setCategoryTitle(String title) {
        story_title.setText(Html.fromHtml(storyName));
    }

    @Override
    public void setParaAudio(String paraAudio) {
        storyAudio = paraAudio;
    }

    @UiThread
    @Override
    public void initializeContent(int pageNo) {
        currentPage = pageNo;
        if (currentPage == 0) {
            lastPgFlag = false;
            btn_previouspage.setVisibility(View.GONE);
        }
        if (currentPage < totalPages - 1 && currentPage > 0) {
            lastPgFlag = false;
            btn_previouspage.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.VISIBLE);
        }

        wordFlowLayout.removeAllViews();
        storyAudio = modalPagesList.get(currentPage).getReadPageAudio();
        storyBg = modalPagesList.get(currentPage).getPageImage();
        pageTitle = modalPagesList.get(currentPage).getPageTitle();
        if (pageTitle != null && !pageTitle.equalsIgnoreCase(""))
            story_title.setText(Html.fromHtml(pageTitle));

        playHideFlg = storyAudio.equalsIgnoreCase("NA");

        try {
            File f = new File(readingContentPath + storyBg);
            if (f.exists()) {
                ImageRequest imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(Uri.fromFile(f))
                        .setLocalThumbnailPreviewsEnabled(true)
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setAutoPlayAnimations(true)// if gif, it will play.
                        .setOldController(Objects.requireNonNull(pageImage).getController())
                        .build();
                pageImage.setController(controller);
            } else {
                pageImage.setVisibility(View.GONE);
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
            if (FastSave.getInstance().getString(CURRENT_FOLDER_NAME, "").equalsIgnoreCase("English"))
                splitWordsPunct.add(modalPagesList.get(currentPage).getReadList().get(i).getWord().replaceAll("[^a-zA-Z ]", "").toLowerCase());
            else
                splitWordsPunct.add(splitWords.get(i).replaceAll(STT_REGEX, ""));
            wordsDurationList.add(modalPagesList.get(currentPage).getReadList().get(i).getWordDuration());
            wordsResIdList.add(modalPagesList.get(currentPage).getReadList().get(i).getWordId());
        }
        startPlayBack = Float.parseFloat(modalPagesList.get(currentPage).getReadList().get(0).getWordFrom());
        setWordsToLayout();
        if (playHideFlg)
            btn_Play.setVisibility(View.GONE);
        startTime = FC_Utility.getCurrentDateTime();
        dismissLoadingDialog();
        new Handler().postDelayed(() -> {
            if (!FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test))
                btn_Play.performClick();
            else {
                btn_Mic.performClick();
                btn_Play.setVisibility(View.GONE);
                bottom_bar2.setVisibility(View.VISIBLE);
                btn_submit.setVisibility(View.VISIBLE);
            }
        }, 200);
    }

    @UiThread
    @Override
    public void allCorrectAnswer() {
        for (int i = 0; i < splitWordsPunct.size(); i++) {
            ((SansTextView) wordFlowLayout.getChildAt(i)).setTextColor(getResources().getColor(R.color.colorBtnGreenDark));
            correctArr[i] = true;
        }
        new Handler().postDelayed(() -> {
            //setMute(0);
            if (lastPgFlag) {
                if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test))
                    showStars(true);
                else
                    showAcknowledgeDialog(true);
            } /*else {
                btn_nextpage.performClick();
            }*/
        }, 1000);
        dismissLoadingDialog();
    }

    private void setWordsToLayout() {
        for (int i = 0; i < splitWords.size(); i++) {
            if (splitWords.get(i).equalsIgnoreCase("#")) {
                final SansTextView myTextView = new SansTextView(context);
                myTextView.setWidth(2000);
                wordFlowLayout.addView(myTextView);
            } else {
                final SansTextView myTextView = new SansTextView(context);
                myTextView.setText(Html.fromHtml(splitWords.get(i)));
                myTextView.setId(i);
                myTextView.setTextSize(30);
                myTextView.setTextColor(getResources().getColor(R.color.colorText));
                final int finalI = i;
                myTextView.setOnClickListener(v -> {
                    if (!FastSave.getInstance().getString(FC_Constants.CURRENT_FOLDER_NAME, "")
                            .equalsIgnoreCase("maths"))
                        if ((!playFlg || pauseFlg) && !voiceStart) {
                            setMute(0);
                            myTextView.setTextColor(getResources().getColor(R.color.colorRedDark));
                            Animation animation = AnimationUtils.loadAnimation(context, R.anim.reading_zoom_in);
                            myTextView.startAnimation(animation);
                            if (colorChangeHandler == null)
                                colorChangeHandler = new Handler();
                            colorChangeHandler.postDelayed(() -> {
                                myTextView.setTextColor(getResources().getColor(R.color.colorText));
                                Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.reading_zoom_out);
                                myTextView.startAnimation(animation1);
                            }, 350);
                            if (!storyAudio.equalsIgnoreCase("NA"))
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
            if (!FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test) && !clickFlag) {
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
                    myView.setTextColor(getResources().getColor(R.color.colorText));
                    clickFlag = false;
                }, (long) end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAudioReading(int wordCounter) {
        try {
            mp = new MediaPlayer();
            float seekTime = Float.parseFloat(modalPagesList.get(currentPage).getReadList().get(wordCounter).getWordFrom());
            mp.setDataSource(readingContentPath + storyAudio);
            mp.prepare();
            mp.seekTo((int) (seekTime * 1000));
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
            myView.setBackgroundColor(getResources().getColor(R.color.yellow_text_bg));
            colorChangeHandler.postDelayed(() -> {
                myView.setTextColor(getResources().getColor(R.color.colorText));
                myView.setBackgroundColor(getResources().getColor(R.color.full_transparent));
            }, 350);
            if (index == wordsDurationList.size() - 1) {
                try {
                    handler.postDelayed(() -> {
                        try {
                            playFlg = false;
                            pauseFlg = true;
                            if (!contentType.equalsIgnoreCase(FC_Constants.RHYME_RESOURCE)) {
                                btn_Mic.setVisibility(View.VISIBLE);
                                btn_Stop.setVisibility(View.GONE);
                                wordCounter = 0;
                                quesReadHandler = new Handler();
                                quesReadHandler.postDelayed(() -> {
                                    Collections.shuffle(readSounds);
                                    mPlayer = MediaPlayer.create(context, readSounds.get(0));
                                    mPlayer.start();
                                }, 5000);
                            }
                            btn_Stop.performClick();
//                            layout_mic_ripple.startRippleAnimation();
//                            layout_ripplepulse_right.startRippleAnimation();
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

        if (playFlg && !pauseFlg) {
            handler.postDelayed(() -> {
                if (index < wordFlowLayout.getChildCount()) {
                    wordCounter += 1;
                    if (!pauseFlg)
                        startStoryReading(wordCounter);
                } else {
                    for (int i = 0; i < wordsDurationList.size(); i++) {
                        SansTextView myView = (SansTextView) wordFlowLayout.getChildAt(i);
                        myView.setBackgroundColor(Color.TRANSPARENT);
                        myView.setTextColor(getResources().getColor(R.color.colorText));
                    }
                    wordCounter = 0;
                }
            }, (long) (wordDuration * 1000));
        }

    }

    @UiThread
    @Override
    public void setCorrectViewColor() {
        try {
            for (int x = 0; x < correctArr.length; x++) {
                if (correctArr[x]) {
                    ((SansTextView) wordFlowLayout.getChildAt(x)).setTextColor(getResources().getColor(R.color.colorBtnGreenDark));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @Click(R.id.btn_Stop)
    void stopBtn() {
        stopFlg = true;
        if (voiceStart) {
            continuousSpeechService.stopSpeechInput();
            voiceStart = false;
            btn_Stop.setVisibility(View.GONE);
            if (!FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test) && !playHideFlg)
                btn_Play.setVisibility(View.VISIBLE);
            btn_Mic.setVisibility(View.VISIBLE);
        } else if (playFlg || pauseFlg) {
            btn_Stop.setVisibility(View.GONE);
            btn_Play.setVisibility(View.VISIBLE);
            if (!contentType.equalsIgnoreCase(FC_Constants.RHYME_RESOURCE))
                btn_Mic.setVisibility(View.VISIBLE);
            btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
            startPlayBack = Float.parseFloat(modalPagesList.get(currentPage).getReadList().get(0).getWordFrom());
            wordCounter = 0;
            playFlg = false;
            pauseFlg = true;
            try {
                if (mp.isPlaying()) {
                    mp.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (startReadingHandler != null)
                    startReadingHandler.removeCallbacksAndMessages(null);
                if (soundStopHandler != null)
                    soundStopHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new Handler().postDelayed(() -> wordCounter = 0, 200);
    }

    @Click(R.id.btn_read_mic)
    void sttMethod() {
//        if (!voiceStart) {
        showLoader();
        voiceStart = true;
        flgPerMarked = false;
        btn_Mic.setVisibility(View.GONE);
        btn_Play.setVisibility(View.GONE);
        btn_Stop.setVisibility(View.VISIBLE);
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
//        } else {
//            voiceStart = false;
//            if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test) && !playHideFlg)
//                btn_Play.setVisibility(View.VISIBLE);
//            btn_Mic.setImageResource(R.drawable.ic_mic_black);
////            layout_mic_ripple.startRippleAnimation();
////            layout_ripplepulse_right.startRippleAnimation();
//            continuousSpeechService.stopSpeechInput();
//        }
    }

    @Click(R.id.btn_play)
    void playReading() {
        try {
            if (!storyAudio.equalsIgnoreCase("NA")) {
                if (!playFlg) {
                    btn_Mic.setVisibility(View.GONE);
                    btn_Play.setVisibility(View.VISIBLE);
                    btn_Stop.setVisibility(View.VISIBLE);
                    btn_Play.setImageResource(R.drawable.ic_pause_black);
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
                    //            btn_Play.setImageResource(R.drawable.ic_stop_black_24dp);
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
                    //            layout_ripplepulse_right.stopRippleAnimation();
                    setMute(0);
                    if (stopFlg) {
                        wordCounter = 0;
                    }
                    stopFlg = false;
                    startAudioReading(wordCounter);
                } else {
                    playFlg = false;
                    pauseFlg = true;
                    btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
                    if (wordCounter > 1)
                        wordCounter--;
                    try {
                        if (mp.isPlaying()) {
                            mp.pause();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int setBooleanGetCounter() {
        int counter = 0;
        for (int x = 0; x < correctArr.length; x++) {
            if (correctArr[x]) {
                ((SansTextView) wordFlowLayout.getChildAt(x)).setTextColor(getResources().getColor(R.color.colorBtnGreenDark));
                counter++;
            }
        }
        return counter;
    }

    public float getPercentage() {
        int counter = 0;
        float perc = 0f;
        try {
            int totLen = correctArr.length - lineBreakCounter;
            for (int x = 0; x < correctArr.length; x++) {
                if (correctArr[x]) {
                    ((SansTextView) wordFlowLayout.getChildAt(x)).setTextColor(getResources().getColor(R.color.colorBtnGreenDark));
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
            try {
                ButtonClickSound.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

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
                if (!FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test) && !playHideFlg)
                    btn_Play.setVisibility(View.VISIBLE);
                btn_Mic.setImageResource(R.drawable.ic_mic_black);
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
                stt_result_tv.setText(""+attAnsList[currentPage]);
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

    boolean nextPressedFlg = false;

    @Click(R.id.btn_next)
    void gotoNextPage() {
        if (currentPage < totalPages - 1 && !nextPressedFlg) {
            nextPressedFlg = true;
//            layout_ripplepulse_right.startRippleAnimation();
            wordCounter = 0;
            try {
                ButtonClickSound.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            if (voiceStart) {
                btn_Stop.performClick();
                if (!FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test) && !playHideFlg)
                    btn_Play.setVisibility(View.VISIBLE);
                btn_Mic.setImageResource(R.drawable.ic_mic_black);
                setMute(0);
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
                stt_result_tv.setText(""+attAnsList[currentPage]);
                new Handler().postDelayed(() -> {
                    presenter.getPage(currentPage);
                    nextPressedFlg = false;
                }, 300);
                Log.d("click", "NextBtn - totalPages: " + totalPages + "  currentPage: " + currentPage);
            } else {
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
                stt_result_tv.setText(""+attAnsList[currentPage]);
                new Handler().postDelayed(() -> {
                    presenter.getPage(currentPage);
                    nextPressedFlg = false;
                }, 300);
                Log.d("click", "NextBtn - totalPages: " + totalPages + "  currentPage: " + currentPage);
            }

        }/* else {
            GameConstatnts.playGameNext(getActivity(), true, this);
        }*/
    }

    @SuppressLint("SetTextI18n")
    public void showAcknowledgeDialog(boolean diaComplete) {
        final CustomLodingDialog dialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText("" + getResources().getString(R.string.yes));
        dia_btn_red.setText("" + getResources().getString(R.string.no));
        dia_btn_yellow.setText("" + getResources().getString(R.string.Cancel));
        dia_btn_yellow.setVisibility(View.GONE);
        dialog.show();

        if (!diaComplete)
            dia_title.setText("Exit\n'" + storyName + "'");
        else {
            dia_title.setText("Good Job\nRead another one???");
        }
        dia_btn_red.setOnClickListener(v -> dialog.dismiss());
        dia_btn_green.setOnClickListener(v -> {
            dialog.dismiss();
            if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test)) {
                float correctCnt = getPercentage();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("cCode", certiCode);
                returnIntent.putExtra("sMarks", correctCnt);
                if (correctArr != null)
                    returnIntent.putExtra("tMarks", correctArr.length);
                returnIntent.putExtra("tMarks", 0);
//                setResult(Activity.RESULT_OK, returnIntent);
            }
            exitDBEntry();
            //TODO Back Press Here
            ReadingStoryActivity.super.onBackPressed();
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

    @Click(R.id.floating_back)
    public void pressedBackBtn() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        try {
            if (voiceStart || playFlg)
                btn_Stop.performClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!fragFlg)
            showAcknowledgeDialog(false);
        else
            fragFlg = false;
    }

    int correctCnt = 0, total = 0;

    @SuppressLint("SetTextI18n")
    private void showStars(boolean diaComplete) {
        final CustomLodingDialog dialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
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
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
    }

    public float getStarRating(float perc) {
        float ratings = 0;
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
            if (!FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test) && !playHideFlg)
                btn_Play.setVisibility(View.VISIBLE);
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
    public void stoppedPressed() {
        showLoader();
        presenter.micStopped(splitWordsPunct, wordsResIdList);
    }

    @Override
    public void sttEngineReady() {
        dismissLoadingDialog();
    }

    @Override
    public void Stt_onResult(ArrayList<String> sttResult) {

        flgPerMarked = false;
        presenter.sttResultProcess(sttResult, splitWordsPunct, wordsResIdList);

        if(sttResult.size()>0) {
            String txt = String.valueOf(stt_result_tv.getText());
            String atxt = txt + sttResult.get(0)+ " ";
            attAnsList[currentPage]=atxt;
            stt_result_tv.setText("");
            stt_result_tv.setTextSize(28);
            stt_result_tv.setText(attAnsList[currentPage]);
            stt_result_tv.setMovementMethod(new ScrollingMovementMethod());
        }
        /*        if (!voiceStart) {
            resetSpeechRecognizer();
            btn_Play.setVisibility(View.VISIBLE);
            btn_Mic.setImageResource(R.drawable.ic_mic_black);
            setMute(0);
        } else
            speech.startListening(recognizerIntent);*/
    }

    @Click(R.id.clean_stt)
    void sttClearClicked() {
        if(voiceStart)
            btn_Stop.performClick();
        et_edit_ans.setText(attAnsList[currentPage]);
        ll_edit_text.setVisibility(View.VISIBLE);
//        stt_result_tv.setText("");
//        attAnsList[currentPage]="";
    }

    @Click(R.id.bt_edit_ok)
    public void editOKClicked(){
        attAnsList[currentPage] = ""+et_edit_ans.getText();
        stt_result_tv.setText(attAnsList[currentPage]);
        ArrayList<String> sttResult = new ArrayList<>();
        sttResult.add(attAnsList[currentPage]);
        presenter.sttResultProcess(sttResult, splitWordsPunct, wordsResIdList);
//        presenter.checkSttAns(attAnsList[currentPage]);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(ll_edit_text.getWindowToken(), 0);
        ll_edit_text.setVisibility(View.GONE);
//        hideSystemUI();
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

}