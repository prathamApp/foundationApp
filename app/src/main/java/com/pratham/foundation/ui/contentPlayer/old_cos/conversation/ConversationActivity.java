package com.pratham.foundation.ui.contentPlayer.old_cos.conversation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.fontsview.SansTextViewBold;
import com.pratham.foundation.interfaces.MediaCallbacks;
import com.pratham.foundation.modalclasses.Message;
import com.pratham.foundation.services.shared_preferences.FastSave;
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
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;


@EActivity(R.layout.activity_conversation)
public class ConversationActivity extends BaseActivity
        implements ConversationContract.ConversationView, STT_Result_New.sttView, MediaCallbacks {

    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewById(R.id.ll_convo_main)
    RelativeLayout ll_convo_mainw;
    @ViewById(R.id.readChatFlow)
    FlowLayout readChatFlow;
    @ViewById(R.id.btn_reading)
    ImageButton btn_reading;
    @ViewById(R.id.btn_imgsend)
    ImageButton btn_imgsend;
    @ViewById(R.id.btn_speaker)
    ImageButton btn_speaker;
    @ViewById(R.id.tv_title)
    TextView tv_title;
    @ViewById(R.id.scroll_ll)
    ScrollView lin_layout;
    @ViewById(R.id.iv_monk)
    ImageView iv_monk;
    @ViewById(R.id.floating_back)
    FloatingActionButton floating_back;
    @ViewById(R.id.floating_info)
    FloatingActionButton floating_info;

    @Bean(ConversationPresenter.class)
    ConversationContract.ConversationPresenter presenter;

    JSONArray conversation;
    private RecyclerView.Adapter mAdapter;
    private List messageList = new ArrayList();
    String question;
    String answer, ansForCheck;
    String questionAudio;
    String answerAudio, startTime;
    MediaPlayer correctSound;
    int currentQueNos = 0, randomNumA, randomNumB, currentMsgNo = 0;
    static boolean voiceStart = false;
    public RecognitionListener listener;
    String selectedLanguage, contentData, contentId, studentID, contentName, contentPath, certiCode;
    public static String convoMode, convoPath;
    private String LOG_TAG = "VoiceRecognitionActivity";
    boolean[] correctArr;
    boolean myMsg, onSdCard;
    float[] msgPercentage;
    Context context;
    ContinuousSpeechService_New continuousSpeechService;
//    AnimationDrawable animationDrawable;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        iv_monk.setVisibility(View.GONE);
        silence_outer_layout.setVisibility(View.GONE);
        floating_back.setImageResource(R.drawable.ic_left_arrow_white);
        floating_info.setImageResource(R.drawable.ic_info_outline_white);
//        animationDrawable = (AnimationDrawable) ll_convo_mainw.getBackground();
//        animationDrawable.setEnterFadeDuration(4500);
//        animationDrawable.setExitFadeDuration(4500);
//        animationDrawable.start();

        context = ConversationActivity.this;
        continuousSpeechService = new ContinuousSpeechService_New(context, ConversationActivity.this, FC_Constants.ENGLISH);
        correctSound = MediaPlayer.create(context, R.raw.correct_ans);

        presenter.setView(ConversationActivity.this);

        sendClikChanger(0);
        mediaPlayerUtil = new MediaPlayerUtil(context);
        mediaPlayerUtil.initCallback(ConversationActivity.this);

        selectedLanguage = "english";
        contentId = getIntent().getStringExtra("storyId");
        studentID = getIntent().getStringExtra("StudentID");
        contentName = getIntent().getStringExtra("contentName");
        convoMode = getIntent().getStringExtra("convoMode");
        contentPath = getIntent().getStringExtra("contentPath");
        certiCode = getIntent().getStringExtra("certiCode");
        onSdCard = getIntent().getBooleanExtra("onSdCard", false);

        presenter.setContentId(contentId);
        convoMode = "A";
        voiceStart = false;

        if (onSdCard)
            convoPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
        else
            convoPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";
        //        if (convoMode.equals("A"))
//            iv_ConvoMode.setImageResource(R.drawable.mode_a);
//        else if (convoMode.equals("B"))
//            iv_ConvoMode.setImageResource(R.drawable.mode_b);
//        if (convoMode.equals("C"))
//            iv_ConvoMode.setImageResource(R.drawable.mode_c);

        tv_title.setText(contentName);
        presenter.fetchStory(convoPath);
    }

    @UiThread
    @Override
    public void setConvoJson(JSONArray returnStoryNavigate) {
        conversation = returnStoryNavigate;
        myMsg = true;
        if (conversation != null) {
            msgPercentage = new float[conversation.length()];
            recyclerView.setHasFixedSize(true);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            mAdapter = new MessageAdapter(messageList, context);
            recyclerView.setAdapter(mAdapter);
            for(int i =0; i<msgPercentage.length; i++)
                msgPercentage[1] = 0;
            new Handler().postDelayed(() -> displayNextQuestion(currentQueNos), 800);
        }
    }

    @UiThread
    @Override
    public void sendClikChanger(int clickOn) {
        if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
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

    @Click(R.id.btn_reading)
    public void startRecognition() {
        if (!voiceStart) {
            showLoader();
            // ButtonClickSound.start();
            voiceStart = true;
            btn_reading.setImageResource(R.drawable.ic_stop_black);
            btn_reading.setBackgroundResource(R.drawable.button_red);
            try {
                continuousSpeechService.startSpeechInput();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            voiceStart = false;
            btn_reading.setImageResource(R.drawable.ic_mic_black);
            btn_reading.setBackgroundResource(R.drawable.button_green);
            try {
                continuousSpeechService.stopSpeechInput();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //ButtonClickSound.start();
        }
    }

    @Click(R.id.btn_imgsend)
    public void sendMessage() {
        sendClikChanger(0);
        int correctAnswerCount = setBooleanGetCounter();
        float perc = presenter.getPercentage();
        presenter.addScore(0, "perc - " + perc, correctAnswerCount, correctArr.length, " Convo ");
        ButtonClickSound.start();
        btn_reading.setImageResource(R.drawable.ic_mic_black);
        if (voiceStart)
            btn_reading.performClick();
        readChatFlow.removeAllViews();

        addItemInConvo(answer, answerAudio, true);
        currentQueNos += 1;
        new Handler().postDelayed(() -> displayNextQuestion(currentQueNos), 1000);

/*        switch (convoMode) {
            case "A":
//            playChat("" + answerAudio);
                iv_ConvoMode.setImageResource(R.drawable.mode_a);
                addItemInConvo(answer, answerAudio, true);
                currentQueNos += 1;
                new Handler().postDelayed(() -> displayNextQuestion(currentQueNos), (long) (1000));
                break;
            case "B":
                playChat("" + questionAudio);
                iv_ConvoMode.setImageResource(R.drawable.mode_b);
                addItemInConvo(answer, answerAudio, true);
                addItemInConvo(question, questionAudio, false);
                currentQueNos += 1;
                new Handler().postDelayed(() -> displayNextQuestion(currentQueNos), (long) (1200));
                break;
            case "C":
                //if(cntr<1) {
                iv_ConvoMode.setImageResource(R.drawable.mode_c);
                if (myMsg) {
                    myMsg = false;
                    playChat("" + answerAudio);
                    addItemInConvo(answer, answerAudio, true);
                    new Handler().postDelayed(() -> setAnswerText(question), (long) (1200));
                } else {
                    myMsg = true;
                    playChat("" + questionAudio);
                    addItemInConvo(question, questionAudio, false);
                    currentQueNos += 1;
                    new Handler().postDelayed(() -> displayNextQuestion(currentQueNos), (long) (1200));
                }
                break;
*//*            }else{
                cntr=0;
                currentQueNos += 1;
                displayNextQuestion(currentQueNos);
            }*//*
        }*/
    }

    public void displayNextQuestion(int currentQueNo) {
        try {
            presenter.setStartTime(FC_Utility.getCurrentDateTime());
            if (currentQueNo < conversation.length()) {
                iv_monk.clearAnimation();
                iv_monk.setVisibility(View.GONE);
                currentMsgNo = currentQueNo;
                randomNumA = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").length());
                randomNumB = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").length());
                question = conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").getJSONObject(randomNumA).getString("data");
                answer = conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").getJSONObject(randomNumB).getString("data");
                questionAudio = conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").getJSONObject(randomNumA).getString("audio");
                answerAudio = conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").getJSONObject(randomNumB).getString("audio");
                addItemInConvo(question, questionAudio, false);
                playChat("" + questionAudio);
                setAnswerText(answer);
/*                switch (convoMode) {
                    case "A":
                        randomNumA = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").length());
                        randomNumB = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").length());
                        question = conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").getJSONObject(randomNumA).getString("data");
                        answer = conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").getJSONObject(randomNumB).getString("data");
                        questionAudio = conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").getJSONObject(randomNumA).getString("audio");
                        answerAudio = conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").getJSONObject(randomNumB).getString("audio");
                        addItemInConvo(question, questionAudio, false);
                        playChat("" + questionAudio);
                        //ttsService.play(question);
                        setAnswerText(answer);
                        break;
                    case "B":
                        randomNumA = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").length());
                        randomNumB = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").length());
                        question = conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").getJSONObject(randomNumB).getString("data");
                        answer = conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").getJSONObject(randomNumA).getString("data");
                        questionAudio = conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").getJSONObject(randomNumB).getString("audio");
                        answerAudio = conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").getJSONObject(randomNumA).getString("audio");
                        //addItemInConvo(question, true);
                        setAnswerText(answer);
                        break;
                    case "C":
                        randomNumA = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").length());
                        randomNumB = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").length());
                        question = conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").getJSONObject(randomNumB).getString("data");
                        answer = conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").getJSONObject(randomNumA).getString("data");
                        questionAudio = conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").getJSONObject(randomNumB).getString("audio");
                        answerAudio = conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").getJSONObject(randomNumA).getString("audio");
                        //addItemInConvo(question, true);
                        setAnswerText(answer);
                        String temp;
*//*                    if(myMsg) {
                        setAnswerText(answer);
                    }else{
                        temp = answer;
                        answer=question;
                        question=temp;
                        setAnswerText(question);
                    }*//*
                        break;
                }*/
            } else {
                btn_imgsend.setClickable(false);
                btn_reading.setClickable(false);
                readChatFlow.removeAllViews();
                new Handler().postDelayed(() -> {
                    if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test))
                        showStars(true);
                    else
                        ConvoEndDialog();
                }, 1200);
                /*                currentQueNos = 0;
                displayNextQuestion(currentQueNos);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getRandomNum(int max) {
        try {
            return new Random().nextInt(max);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void addItemInConvo(String text, String audio, boolean user) {
        if (user)
            messageList.add(new Message(text, "user", audio));
        else
            messageList.add(new Message(text, "bot", audio));
        mAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private void setAnswerText(String answerText) {
        answer = answerText;
        String[] splitArr = answerText.split(" ");
        correctArr = new boolean[splitArr.length];
        presenter.setCorrectArray(splitArr.length);
        //readChatFlow.removeAllViews();
        String[] splittedAnswer = answerText.split(" ");
        for (String word : splittedAnswer) {
            final SansTextViewBold myTextView = new SansTextViewBold(context);
            myTextView.setText(word);
            myTextView.setOnClickListener(v -> {
                if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                    if (voiceStart)
                        btn_reading.performClick();
                    playChat("" + answerAudio);
                }
            });
            myTextView.setTextSize(15);
            myTextView.setTextColor(getResources().getColor(R.color.colorAccentDark));
            readChatFlow.addView(myTextView);
        }
    }

    @Click(R.id.btn_speaker)
    public void chatAnswer() {
        if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
            if (voiceStart)
                startRecognition();
            new Handler().postDelayed(() -> playChat("" + answerAudio), 100);
        }
    }

    public static void playChat(String fileName) {
        try {
            mediaPlayerUtil.playMedia(convoPath + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "resume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "pause");
        super.onPause();
        if (voiceStart)
            btn_reading.performClick();
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "stop");
        super.onStop();
    }

    boolean submitPressed = false;
    @UiThread
    @Override
    public void submitAns(String[] splitQues) {
        try {
            if(!submitPressed) {
                submitPressed = true;
                if (voiceStart)
                    btn_reading.performClick();
                sendClikChanger(0);
                for (int i = 0; i < splitQues.length; i++) {
                    ((SansTextViewBold) readChatFlow.getChildAt(i)).setTextColor(getResources().getColor(R.color.readingGreen));
                    correctArr[i] = true;
                }
                lin_layout.setBackgroundResource(R.drawable.convo_correct_bg);
                correctSound.start();
                new Handler().postDelayed(() -> {
                    lin_layout.setBackgroundResource(R.drawable.dialog_bg);
                    sendMessage();
                    submitPressed = false;
                }, 1200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void clearMonkAnimation() {
        iv_monk.clearAnimation();
        iv_monk.setVisibility(View.GONE);
    }

    @UiThread
    @Override
    public void setAnsCorrect(boolean[] ansCorrect) {
        try {
            correctArr = ansCorrect;
            for (int x = 0; x < correctArr.length; x++)
                if (correctArr[x])
                    ((SansTextViewBold) readChatFlow.getChildAt(x)).setTextColor(getResources().getColor(R.color.colorBtnGreenDark));

            float perc = presenter.getPercentage();
            if (msgPercentage[currentMsgNo] < perc)
                msgPercentage[currentMsgNo] = perc;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int setBooleanGetCounter() {
        int counter = 0;
        try {
            for (int x = 0; x < correctArr.length; x++)
                if (correctArr[x]) {
                    ((SansTextViewBold) readChatFlow.getChildAt(x)).setTextColor(getResources().getColor(R.color.colorBtnGreenDark));
                    counter++;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return counter;
    }

    public CustomLodingDialog myLoadingDialog;
    boolean dialogFlg = false;

    @UiThread
    public void showLoader() {
        try {
            if (!dialogFlg) {
                dialogFlg = true;
                myLoadingDialog = new CustomLodingDialog(context);
                myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Objects.requireNonNull(myLoadingDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myLoadingDialog.setContentView(R.layout.loading_dialog);
                myLoadingDialog.setCanceledOnTouchOutside(false);
                myLoadingDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void dismissLoadingDialog() {
        try {
            dialogFlg = false;
            new Handler().postDelayed(() -> {
                if (myLoadingDialog != null && myLoadingDialog.isShowing() && !desFlag)
                    myLoadingDialog.dismiss();
            }, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean desFlag = false;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        desFlag = true;
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
    public void Stt_onResult(ArrayList<String> sttServerResult) {
        iv_monk.setVisibility(View.VISIBLE);
        iv_monk.startAnimation(AnimationUtils.loadAnimation(context, R.anim.float_anim));
        presenter.sttResultProcess(sttServerResult, answer);
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

    @Override
    public void onComplete() {
        try {
            btn_reading.performClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void ConvoEndDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_title.setText("Nice chatting with you.\nBye-bye!");
        dia_btn_green.setText(""+getResources().getString(R.string.Okay));
        dia_btn_red.setVisibility(View.GONE);
        dia_btn_yellow.setVisibility(View.GONE);
        dialog.show();

        dia_btn_green.setOnClickListener(v -> {
            presenter.addScore(0, "", 0, 0, "Convo End");
            float perc = getCompletionPercentage();
            presenter.addCompletion(perc);
            dialog.dismiss();
            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                int pages = getCompletionPages();
                int msgPercLength = msgPercentage.length;

                Intent returnIntent = new Intent();
                returnIntent.putExtra("cCode", certiCode);
                returnIntent.putExtra("sMarks", pages);
                returnIntent.putExtra("tMarks", msgPercLength);
                setResult(Activity.RESULT_OK, returnIntent);
            }
            closeConvo();
        });
    }

    private float getCompletionPercentage() {
        float tot = 0f, perc = 0f;
        try {
            for (float v : msgPercentage) tot += v;
            if (tot > 0)
                perc = (tot / msgPercentage.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return perc;
    }

    private int getCompletionPages() {
        int tot = 0;
        try {
            for (float v : msgPercentage)
                if (v > 50)
                    tot++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tot;
    }

    @Click (R.id.floating_back)
    public void pressedBackBtn(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (voiceStart)
            btn_reading.performClick();
        try {
            mediaPlayerUtil.stopMedia();
        } catch (Exception e) {
            e.printStackTrace();
        }
        showExitDialog(context);
    }

    int correctCnt = 0, total = 0;

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

        correctCnt = getCompletionPages();
        try {
            if (msgPercentage != null)
                total = msgPercentage.length;
        } catch (Exception e) {
            total = 0;
        }

        float perc = 0f;
        perc = ((float) correctCnt / (float) total) * 100;

        float rating = getStarRating(perc);
        dia_ratingBar.setRating(rating);

        dia_btn_red.setVisibility(View.GONE);
        dia_btn_green.setText(""+getResources().getString(R.string.Next));
        dia_btn_yellow.setText(""+getResources().getString(R.string.Cancel));
        if (diaComplete)
            dia_btn_yellow.setVisibility(View.GONE);

        dialog.show();

        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());

        dia_btn_red.setOnClickListener(v -> dialog.dismiss());

        dia_btn_green.setOnClickListener(v -> {
            dialog.dismiss();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("cCode", certiCode);
            returnIntent.putExtra("sMarks", correctCnt);
            returnIntent.putExtra("tMarks", total);
            setResult(Activity.RESULT_OK, returnIntent);
            closeConvo();
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

    @UiThread
    public void closeConvo() {
        finish();
    }

    @SuppressLint("SetTextI18n")
    public void showExitDialog(Context context) {

        final CustomLodingDialog dialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);

        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_title.setText(""+getResources().getString(R.string.Exit));
        dia_btn_green.setText(""+getResources().getString(R.string.yes));
        dia_btn_red.setText(""+getResources().getString(R.string.no));
        dia_btn_yellow.setText(""+getResources().getString(R.string.Cancel));
        dialog.show();

        dia_btn_red.setOnClickListener(v -> dialog.dismiss());
        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
        dia_btn_green.setOnClickListener(v -> {
            float perc = getCompletionPercentage();
            presenter.addCompletion(perc);
            dialog.dismiss();
            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                int pages = getCompletionPages();
                int msgPercLength = 0;
                try {
                    if (msgPercentage != null)
                        msgPercLength = msgPercentage.length;
                } catch (Exception e) {
                    msgPercLength = 0;
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra("cCode", certiCode);
                returnIntent.putExtra("sMarks", pages);
                returnIntent.putExtra("tMarks", msgPercLength);
                setResult(Activity.RESULT_OK, returnIntent);
            }
            closeConvo();
        });
    }
}