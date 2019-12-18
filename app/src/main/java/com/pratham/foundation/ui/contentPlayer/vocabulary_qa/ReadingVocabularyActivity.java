package com.pratham.foundation.ui.contentPlayer.vocabulary_qa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.interfaces.MediaCallbacks;
import com.pratham.foundation.modalclasses.Message;
import com.pratham.foundation.modalclasses.ModalVocabulary;
import com.pratham.foundation.services.stt.ContinuousSpeechService_New;
import com.pratham.foundation.services.stt.STT_Result_New;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;
import com.pratham.foundation.utility.MediaPlayerUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.ui.contentPlayer.old_cos.conversation.ConversationActivity.mediaPlayerUtil;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Constants.isTest;


@EActivity(R.layout.activity_vocabulary_reading)
public class ReadingVocabularyActivity extends BaseActivity implements MediaCallbacks,
        STT_Result_New.sttView, ReadingVocabularyContract.ReadingVocabularyView {

    @Bean(ReadingVocabularyPresenter.class)
    ReadingVocabularyContract.ReadingVocabularyPresenter presenter;

    @ViewById(R.id.ll_convo_main)
    RelativeLayout ll_convo_main;
    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewById(R.id.vocabChatFlow)
    FlowLayout vocabChatFlow;
    @ViewById(R.id.scroll_ll)
    ScrollView scroll_ll;
    @ViewById(R.id.btn_reading)
    ImageButton btn_reading;
    @ViewById(R.id.btn_imgsend)
    ImageButton btn_imgsend;
    @ViewById(R.id.tv_title)
    TextView tv_title;
    @ViewById(R.id.lin_layout)
    LinearLayout lin_layout;
    @ViewById(R.id.content_image)
    ImageView content_image;
    @ViewById(R.id.this_image)
    ImageView this_image;
    @ViewById(R.id.that_image)
    ImageView that_image;
    @ViewById(R.id.btn_speaker)
    ImageButton btn_speaker;
    @ViewById(R.id.btn_prev)
    ImageButton btn_prev;
    @ViewById(R.id.btn_next)
    ImageButton btn_next;
    @ViewById(R.id.floating_back)
    FloatingActionButton floating_back;
    @ViewById(R.id.floating_info)
    FloatingActionButton floating_info;

    ContinuousSpeechService_New continuousSpeechService;

    List<Integer> readSounds = new ArrayList<>();
    public static MediaPlayer mp, correctSound;
    Context mContext;
    static int currentPageNo, currentQueNo;
    int vocabLevel;
    Dialog nextDialog;
    private RecyclerView.Adapter mAdapter;
    static boolean[] correctArr;
    static boolean readingFlg, allCorrect = false, dilogOpen = false;
    static boolean[] testCorrectArr;
    private List messageList = new ArrayList();
    boolean testFlg, playingFlg, onSdCard;
    List<ModalVocabulary> modalVocabularyList;
    static String readingContentPath;
    String contentPath, contentTitle, StudentID, resId, vocabCategory, sttLang,
            ques, quesAudio, ans, ansAudio, ansCheck, certiCode;
    AnimationDrawable animationDrawable;
    Handler setDataHandler, showDataHandler, setBackgroundHandler, sendMessageHandler, newSetDataHandler;


    @AfterViews
    public void initialize() {

        silence_outer_layout.setVisibility(View.GONE);
        mContext = ReadingVocabularyActivity.this;
        animationDrawable = (AnimationDrawable) ll_convo_main.getBackground();
        animationDrawable.setEnterFadeDuration(4500);
        animationDrawable.setExitFadeDuration(4500);
        animationDrawable.start();
        floating_back.setImageResource(R.drawable.ic_left_arrow_white);
        floating_info.setImageResource(R.drawable.ic_info_outline_white);
        testFlg = false;

        presenter.setView(ReadingVocabularyActivity.this);
/*        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout_ripplepulse_right.getLayoutParams();
        params.rightMargin = -(int) getResources().getDimension(R.dimen._25sdp);
        params.bottomMargin = -(int) getResources().getDimension(R.dimen._25sdp);
        params = (ViewGroup.MarginLayoutParams) layout_ripplepulse_left.getLayoutParams();
        params.leftMargin = -(int) getResources().getDimension(R.dimen._25sdp);
        params.bottomMargin = -(int) getResources().getDimension(R.dimen._25sdp);*/

        Intent intent = getIntent();
        contentTitle = intent.getStringExtra("contentTitle");
        contentPath = intent.getStringExtra("contentPath");
        StudentID = intent.getStringExtra("StudentID");
        resId = intent.getStringExtra("resId");
        vocabCategory = intent.getStringExtra("vocabCategory");
        certiCode = intent.getStringExtra("certiCode");
        sttLang = intent.getStringExtra("sttLang");
        onSdCard = getIntent().getBooleanExtra("onSdCard", false);

        tv_title.setText(""+contentTitle);

        continuousSpeechService = new ContinuousSpeechService_New(mContext, ReadingVocabularyActivity.this, sttLang);

        correctSound = MediaPlayer.create(this, R.raw.correct_ans);
        mediaPlayerUtil = new MediaPlayerUtil(ReadingVocabularyActivity.this);
        mediaPlayerUtil.initCallback(ReadingVocabularyActivity.this);
        sendClikChanger(0);

        currentPageNo = 0;
        currentQueNo = 0;
        vocabLevel = 0;

        presenter.setResIdAndStartTime(resId);

        readSounds.add(R.raw.tap_the_mic);
        readSounds.add(R.raw.your_turn_to_read);
        readSounds.add(R.raw.would_you_like_to_read);
        readSounds.add(R.raw.tap_the_mic_to_read_out);
        Collections.shuffle(readSounds);

        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";

        continuousSpeechService.resetSpeechRecognizer();
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new QA_Adapter(messageList, this);
        recyclerView.setAdapter(mAdapter);

        if (FC_Utility.isDataConnectionAvailable(this)) {
            presenter.fetchJsonData(readingContentPath, vocabCategory);
        } else {
            showReadFullDialog();
        }
    }

    @SuppressLint("SetTextI18n")
    private void showReadFullDialog() {
        final Dialog dialog = new CustomLodingDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText("Ok");
        dia_btn_red.setVisibility(View.GONE);
        dia_btn_yellow.setVisibility(View.GONE);
        dialog.show();
        dia_title.setText("Read the full sentence");
        dia_btn_green.setOnClickListener(v -> {
            dialog.dismiss();
            presenter.fetchJsonData(readingContentPath, vocabCategory);
        });
    }

    @UiThread
    @Override
    public void setListData(List<ModalVocabulary> wordsDataList) {
        modalVocabularyList = wordsDataList;
        testCorrectArr = new boolean[modalVocabularyList.size()];
        setData();
    }

    public void setData() {
        content_image.setImageResource(0);
        messageList.clear();
        mAdapter.notifyDataSetChanged();
        if (setDataHandler == null)
            setDataHandler = new Handler();
        setDataHandler.postDelayed(() -> {
            try {
                File f = new File(readingContentPath + "images/" + modalVocabularyList.get(currentPageNo).getImg());
                if (f.exists()) {
                    Bitmap bmImg = BitmapFactory.decodeFile(readingContentPath + "images/" + modalVocabularyList.get(currentPageNo).getImg());
                    BitmapFactory.decodeStream(new FileInputStream(readingContentPath + "images/" + modalVocabularyList.get(currentPageNo).getImg()));
                    content_image.setImageBitmap(bmImg);
                }
                if (showDataHandler == null)
                    showDataHandler = new Handler();
                showDataHandler.postDelayed(() -> displayNextQuestion(currentQueNo), (long) (800));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, (long) (800));
    }

    private void displayNextQuestion(int currQueNo) {
        try {
//            startTime = ApplicationClass.getCurrentDateTime();
            setMute(0);
            btn_imgsend.setClickable(true);
            btn_reading.setClickable(true);
            btn_next.setClickable(true);
            btn_prev.setClickable(true);
            if (currQueNo == 0) {
                ques = modalVocabularyList.get(currentPageNo).getQuestion1Text();
                quesAudio = modalVocabularyList.get(currentPageNo).getQuestion1Aud();
                ans = modalVocabularyList.get(currentPageNo).getAnswer1Text();
                ansAudio = modalVocabularyList.get(currentPageNo).getAnswer1Aud();
                ansCheck = modalVocabularyList.get(currentPageNo).getCheck1Text();
                addItemInConvo(ques, quesAudio, false);
                startAudioReading("" + quesAudio, ans);
                setImage(ques);
            } else if (currQueNo == 1) {
                ques = modalVocabularyList.get(currentPageNo).getQuestion2Tex();
                if (ques != null &&
                        !ques.equalsIgnoreCase("")) {
                    quesAudio = modalVocabularyList.get(currentPageNo).getQuestion2Aud();
                    ans = modalVocabularyList.get(currentPageNo).getAnswer2Text();
                    ansAudio = modalVocabularyList.get(currentPageNo).getAnswer2Aud();
                    ansCheck = modalVocabularyList.get(currentPageNo).getCheck2Text();
                    addItemInConvo(ques, quesAudio, false);
                    startAudioReading("" + quesAudio, ans);
                    setImage(ques);
                } else {
                    btn_imgsend.setClickable(false);
                    btn_reading.setClickable(false);
                    vocabChatFlow.removeAllViews();
                    if (newSetDataHandler == null)
                        newSetDataHandler = new Handler();
                    newSetDataHandler.postDelayed(() -> {
                        if (currentPageNo < modalVocabularyList.size() - 1) {
                            currentPageNo++;
                            currentQueNo = 0;
                            messageList.clear();
                            setData();
                        } else {
                            LoadNext();
                        }
                    }, (long) (1200));
                }
            } else {
                btn_imgsend.setClickable(false);
                btn_reading.setClickable(false);
                vocabChatFlow.removeAllViews();
                if (newSetDataHandler == null)
                    newSetDataHandler = new Handler();
                newSetDataHandler.postDelayed(() -> {
                    if (currentPageNo < modalVocabularyList.size() - 1) {
                        currentPageNo++;
                        currentQueNo = 0;
                        messageList.clear();
                        setData();
                    } else {
                        LoadNext();
                    }
                }, (long) (1200));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImage(String ques) {
        if (ques.toLowerCase().contains("this") || ques.toLowerCase().contains("these")
                || ques.toLowerCase().contains("यह") || ques.toLowerCase().contains("ये")) {
            this_image.setVisibility(View.VISIBLE);
            that_image.setVisibility(View.GONE);
        } else if (ques.toLowerCase().contains("that") || ques.toLowerCase().contains("those")
                || ques.toLowerCase().contains("वह") || ques.toLowerCase().contains("वे")) {
            that_image.setVisibility(View.VISIBLE);
            this_image.setVisibility(View.GONE);
        } else {
            this_image.setVisibility(View.GONE);
            that_image.setVisibility(View.GONE);
        }
    }

    private void addItemInConvo(String text, String audio, boolean user) {
        if (user)
            messageList.add(new Message(text, "user", audio));
        else
            messageList.add(new Message(text, "bot", audio));
        mAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        sendClikChanger(0);
    }

    @Click(R.id.btn_imgsend)
    public void sendMessage() {
        vocabChatFlow.removeAllViews();
        sendClikChanger(0);
        if (readingFlg) {
            startReading();
        }
        addItemInConvo(ans, ansAudio, true);
        currentQueNo += 1;
        if (sendMessageHandler == null)
            sendMessageHandler = new Handler();
        sendMessageHandler.postDelayed(() -> displayNextQuestion(currentQueNo), (long) (1000));
    }

    private void setAnswerText(String answerText) {
        vocabChatFlow.removeAllViews();
        String[] splittedAnswer = answerText.split(" ");
        correctArr = new boolean[splittedAnswer.length];
        for (String word : splittedAnswer) {
            final SansTextView myTextView = new SansTextView(this);
            myTextView.setText(word);
            myTextView.setOnClickListener(v -> startAudioReading("" + ansAudio));
            myTextView.setTextSize(25);
            myTextView.setTextColor(getResources().getColor(R.color.colorAccentDark));
            vocabChatFlow.addView(myTextView);
            if (testFlg || FC_Constants.isPractice || isTest)
                myTextView.setVisibility(View.INVISIBLE);
        }
        sendClikChanger(0);
    }

    public static void playChat(String audioPath) {
        try {
            if (!readingFlg) {
                setMute(0);
                mediaPlayerUtil.playMedia(readingContentPath + "sounds/" + audioPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.btn_reading)
    public void startReading() {
        if (!readingFlg) {
            readingFlg = true;
            showLoader();
            continuousSpeechService.startSpeechInput();
            btn_reading.setImageResource(R.drawable.ic_stop_black);
            btn_reading.setBackgroundResource(R.drawable.button_red);
        } else {
            readingFlg = false;
            continuousSpeechService.stopSpeechInput();
            btn_reading.setImageResource(R.drawable.ic_mic_black);
            btn_reading.setBackgroundResource(R.drawable.button_green);
        }
    }

    private void startAudioReading(String audioFilePath, String ansStr) {
        try {
            mp = new MediaPlayer();
            mp.setDataSource(readingContentPath + "sounds/" + audioFilePath);
            mp.prepare();
            mp.start();
            mp.setOnCompletionListener(mp -> {
                playingFlg = false;
                setAnswerText(ansStr);
                startReading();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAudioReading(String audioFilePath) {
        if (!testFlg && !isTest) {
            if (readingFlg)
                startReading();
            try {
                mp = new MediaPlayer();
                mp.setDataSource(readingContentPath + "sounds/" + audioFilePath);
                mp.prepare();
                mp.start();
                mp.setOnCompletionListener(mp -> {
                    playingFlg = false;
                    startReading();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Click(R.id.btn_next)
    public void nextBtnPressed() {
        if (readingFlg)
            startReading();
        if (playingFlg)
            mp.stop();
        if (currentPageNo < modalVocabularyList.size() - 1) {
            currentPageNo++;
            currentQueNo = 0;
            messageList.clear();
            try {
                vocabChatFlow.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setData();
        } else {
            LoadNext();
        }
    }

    private void LoadNext() {
        if (!dilogOpen) {
            dilogOpen = true;
            if (isTest)
                showStars(true);
            else
                showWordNextDialog(this);
        }
    }

    @Click(R.id.btn_prev)
    public void prevBtnPressed() {
        if (readingFlg)
            startReading();
        if (playingFlg)
            mp.stop();
        if (currentPageNo > 0) {
            currentQueNo = 0;
            currentPageNo--;
            messageList.clear();
            try {
                vocabChatFlow.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setData();
        }
    }

    @SuppressLint("SetTextI18n")
    public void showWordNextDialog(Context context) {

        nextDialog = new Dialog(context);
        nextDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nextDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nextDialog.setContentView(R.layout.fc_custom_dialog);
        nextDialog.setCancelable(false);
        nextDialog.setCanceledOnTouchOutside(false);
        nextDialog.show();

        Button dia_btn_yellow = nextDialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = nextDialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = nextDialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText("Next");
        dia_btn_red.setText("Test");
        dia_btn_yellow.setText("Revise");

        dia_btn_green.setOnClickListener(v -> {
            currentPageNo = 0;
            currentQueNo = 0;
            messageList.clear();
            testFlg = false;
            presenter.createWholeList(vocabCategory);
            dilogOpen = false;
            nextDialog.dismiss();
        });

        dia_btn_red.setOnClickListener(v -> {
            currentPageNo = 0;
            currentQueNo = 0;
            messageList.clear();
            testFlg = true;
            setData();
            dilogOpen = false;
            nextDialog.dismiss();
        });

        dia_btn_yellow.setOnClickListener(v -> {
            currentPageNo = 0;
            currentQueNo = 0;
            messageList.clear();
            setData();
            testFlg = false;
            dilogOpen = false;
            nextDialog.dismiss();
        });

    }

    public Dialog myLoadingDialog;
    boolean dialogFlg = false;

    @UiThread
    public void showLoader() {
        try {
            if (!dialogFlg) {
                dialogFlg = true;
                myLoadingDialog = new Dialog(this);
                myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myLoadingDialog.setContentView(R.layout.loading_dialog);
                myLoadingDialog.setCanceledOnTouchOutside(false);
                myLoadingDialog.show();
            }
        }catch (Exception e){e.printStackTrace();}
    }

    @UiThread
    public void dismissLoadingDialog() {
        if (dialogFlg) {
            dialogFlg = false;
            if (myLoadingDialog != null)
                myLoadingDialog.dismiss();
        }
    }

    @Override
    public void stoppedPressed() {
//        showLoader();
//        presenter.micStopped(splitWordsPunct, wordsResIdList);
    }

    @Override
    public void sttEngineReady() {
        dismissLoadingDialog();
    }

    @Override
    public void Stt_onResult(ArrayList<String> sttResults) {
        presenter.sttResultProcess(sttResults, modalVocabularyList.get(currentPageNo), ansCheck, ans);
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
        if (readingFlg) {
            continuousSpeechService.resetHandler(true);
            silence_outer_layout.setVisibility(View.VISIBLE);
            silenceViewHandler = new Handler();
            silence_iv.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_continuous_shake));
            AnimateTextView(this, silence_main_layout);
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
        if (silenceViewHandler == null)
            silenceViewHandler = new Handler();
        silenceViewHandler.postDelayed(() -> {
            silence_iv.clearAnimation();
            silence_outer_layout.setVisibility(View.GONE);
            continuousSpeechService.resetHandler(false);
        }, 10);
    }

    @UiThread
    @Override
    public void setCorrectViewColor() {
        try {
            for (int x = 0; x < correctArr.length; x++)
                if (correctArr[x]) {
                    vocabChatFlow.getChildAt(x).setVisibility(View.VISIBLE);
                    ((SansTextView) vocabChatFlow.getChildAt(x)).setTextColor(getResources().getColor(R.color.colorBtnGreenDark));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.btn_speaker)
    public void chatAnswer() {
        if (!isTest) {
            if (readingFlg)
                startReading();
            new Handler().postDelayed(() -> startAudioReading("" + ansAudio), 100);
        }
    }

    @UiThread
    @Override
    public void sendClikChanger(int clickOn) {
        if (!isTest) {
            if (clickOn == 0) {
                btn_imgsend.setVisibility(View.GONE);
                btn_speaker.setVisibility(View.VISIBLE);
            } else {
                btn_imgsend.setVisibility(View.VISIBLE);
                btn_imgsend.setClickable(true);
                btn_speaker.setVisibility(View.GONE);
            }
        } else {
            btn_speaker.setVisibility(View.GONE);
            btn_imgsend.setVisibility(View.VISIBLE);
            if (clickOn == 0) {
                btn_imgsend.setClickable(false);
                btn_imgsend.setBackgroundResource(R.drawable.convo_send_disable);
            } else {
                btn_imgsend.setClickable(true);
                btn_imgsend.setBackgroundResource(R.drawable.button_yellow);
            }
        }
    }

    @UiThread
    @Override
    public void allCorrectAnswer() {
        if (readingFlg)
            startReading();
        btn_imgsend.setClickable(false);
        btn_next.setClickable(false);
        btn_prev.setClickable(false);
        lin_layout.setBackgroundResource(R.drawable.convo_correct_bg);
        correctSound.start();
        if (setBackgroundHandler == null)
            setBackgroundHandler = new Handler();
        setBackgroundHandler.postDelayed(() -> {
            lin_layout.setBackgroundResource(R.drawable.dialog_bg);
            sendMessage();
        }, 1200);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (readingFlg)
            startReading();
        try {
            if (playingFlg)
                mp.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click (R.id.floating_back)
    public void pressedBackBtn(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (readingFlg)
            startReading();
        try {
            if (playingFlg)
                mp.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!dilogOpen) {
            dilogOpen = true;
            showExitDialog(this);
        }
    }

    int correctCnt = 0, total = 0;

    @SuppressLint("SetTextI18n")
    private void showStars(boolean diaComplete) {

        final Dialog dialog = new CustomLodingDialog(this);
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
            total = testCorrectArr.length;
            for (int x = 0; x < testCorrectArr.length; x++)
                if (testCorrectArr[x])
                    correctCnt += 1;
        } catch (Exception e) {
            e.printStackTrace();
            correctCnt = 0;
            total = 0;
        }

        float perc = 0f;
        perc = ((float) correctCnt / (float) total) * 100;

        float rating = getStarRating(perc);
        dia_ratingBar.setRating(rating);

        dia_btn_red.setVisibility(View.GONE);
        dia_btn_green.setText("Next");
        dia_btn_yellow.setText("" + dialog_btn_cancel);
        if (diaComplete)
            dia_btn_yellow.setVisibility(View.GONE);

        dialog.show();

        dia_btn_yellow.setOnClickListener(v -> {
            dilogOpen = false;
            dialog.dismiss();
        });
        dia_btn_red.setOnClickListener(v -> {
            dilogOpen = false;
            dialog.dismiss();
        });

        dia_btn_green.setOnClickListener(v -> {
            dialog.dismiss();
            dilogOpen = false;
            disableHandlers();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("cCode", certiCode);
            returnIntent.putExtra("sMarks", correctCnt);
            returnIntent.putExtra("tMarks", total);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
    }

    public void disableHandlers() {
        try {
            if (readingFlg)
                startReading();
            if (playingFlg)
                mp.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        try {
            if (setDataHandler != null)
                setDataHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (showDataHandler != null)
                showDataHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (setBackgroundHandler != null)
                setBackgroundHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (sendMessageHandler != null)
                sendMessageHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (newSetDataHandler != null)
                newSetDataHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    @SuppressLint("SetTextI18n")
    public void showExitDialog(Context context) {

        final Dialog dialog = new CustomLodingDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);

        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_title.setText("Exit game?");
        dia_btn_green.setText("Yes");
        dia_btn_red.setText("No");
        dia_btn_yellow.setText("" + dialog_btn_cancel);

        dialog.show();

        dia_btn_green.setOnClickListener(v -> {
            dilogOpen = false;
            presenter.setCompletionPercentage();
            dialog.dismiss();
            if (isTest) {
                int correctCnt = 0, total = 0;
                try {
                    total = correctArr.length;
                    for (int x = 0; x < correctArr.length; x++)
                        if (correctArr[x])
                            correctCnt += 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    correctCnt = 0;
                    total = 0;
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra("cCode", certiCode);
                returnIntent.putExtra("sMarks", correctCnt);
                returnIntent.putExtra("tMarks", total);
                setResult(Activity.RESULT_OK, returnIntent);
            }
            disableHandlers();
            finish();
        });

        dia_btn_red.setOnClickListener(v -> {
            dilogOpen = false;
            dialog.dismiss();
        });
        dia_btn_yellow.setOnClickListener(v -> {
            dilogOpen = false;
            dialog.dismiss();
        });
    }

    @Override
    public void onComplete() {
        if (readingFlg)
            startReading();
    }
}