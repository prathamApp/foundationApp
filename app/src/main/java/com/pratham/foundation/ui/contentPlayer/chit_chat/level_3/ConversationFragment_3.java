package com.pratham.foundation.ui.contentPlayer.chit_chat.level_3;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.interfaces.MediaCallbacks;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.Message;
import com.pratham.foundation.services.stt.ContinuousSpeechService_New;
import com.pratham.foundation.services.stt.STT_Result_New;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;
import com.pratham.foundation.utility.MediaPlayerUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.SplashSupportActivity.ButtonClickSound;


@EFragment(R.layout.chitchat_level_3)
public class ConversationFragment_3 extends Fragment
        implements ConversationContract_3.ConversationView_3, STT_Result_New.sttView, MediaCallbacks, OnGameClose {

    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewById(R.id.ll_convo_main)
    RelativeLayout ll_convo_mainw;
    /* @ViewById(R.id.readChatFlow)
     FlowLayout readChatFlow;*/
    @ViewById(R.id.btn_reading)
    ImageButton btn_reading;
    @ViewById(R.id.readchat)
    TextView readchat;
    @ViewById(R.id.btn_imgsend)
    ImageButton btn_imgsend;
    /*  @ViewById(R.id.btn_speaker)
      ImageButton btn_speaker;*/
    @ViewById(R.id.clear)
    ImageView clear;
    @ViewById(R.id.tv_title)
    TextView tv_title;
    /*    @ViewById(R.id.lin_layout)
        RelativeLayout lin_layout;*/
    @ViewById(R.id.iv_monk)
    ImageView iv_monk;
    @ViewById(R.id.floating_back)
    FloatingActionButton floating_back;
    @ViewById(R.id.floating_info)
    FloatingActionButton floating_info;

    @Bean(ConversationPresenter_3.class)
    ConversationContract_3.ConversationPresenter_3 presenter;

    @ViewById(R.id.rl_personA)
    RelativeLayout person_A;
    @ViewById(R.id.rl_personB)
    RelativeLayout person_B;

    private RecyclerView.Adapter mAdapter;
    private List<Message> messageList = new ArrayList();
    public static MediaPlayerUtil mediaPlayerUtil;
    Context context;
    MediaPlayer correctSound;
    static boolean voiceStart = false;
    public RecognitionListener listener;
    String selectedLanguage, contentId, studentID, contentName, contentPath, certiCode;
    public static String convoMode, convoPath;
    private String LOG_TAG = "VoiceRecognitionActivity";
    boolean onSdCard;
    ContinuousSpeechService_New continuousSpeechService;
    String user = "";
    final String userA = "USER_A";
    final String userB = "USER_B";
    String userAnswer = "";
    public CustomLodingDialog myLoadingDialog;
    boolean dialogFlg = false;
    private String resStartTime, question;
    private int questionID;
    @AfterViews
    public void initialize() {
        iv_monk.setVisibility(View.GONE);
        btn_reading.setVisibility(View.INVISIBLE);
        btn_imgsend.setVisibility(View.GONE);
        clear.setVisibility(View.GONE);
        floating_back.setImageResource(R.drawable.ic_left_arrow_white);
        floating_info.setImageResource(R.drawable.ic_info_outline_white);
        context = getActivity();
        continuousSpeechService = new ContinuousSpeechService_New(context, ConversationFragment_3.this, FC_Constants.ENGLISH);
        correctSound = MediaPlayer.create(context, R.raw.correct_ans);

        sendClikChanger(0);
        mediaPlayerUtil = new MediaPlayerUtil(context);
        mediaPlayerUtil.initCallback(ConversationFragment_3.this);
        Bundle bundle = getArguments();
        selectedLanguage = "english";
        contentId = bundle.getString("resId");
        studentID = bundle.getString("StudentID");
        contentName = bundle.getString("contentName");
        convoMode = bundle.getString("convoMode");
        contentPath = bundle.getString("contentPath");
        certiCode = bundle.getString("certiCode");
        onSdCard = bundle.getBoolean("onSdCard", false);


        convoMode = "A";

        if (onSdCard)
            convoPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
        else
            convoPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";


        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.setView(ConversationFragment_3.this,contentId,contentName,resStartTime);
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.NEW_CHIT_CHAT_3 + " " + GameConstatnts.START, contentId, true);

        // setConvoJson();
        readchat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    btn_imgsend.setVisibility(View.GONE);
                    clear.setVisibility(View.GONE);
                } else {
                    btn_imgsend.setVisibility(View.VISIBLE);
                    clear.setVisibility(View.VISIBLE);
                }
            }
        });
        presenter.fetchStory(convoPath);
    }


    @Click(R.id.rl_personB)
    public void onPersonB(){
        btn_reading.performClick();
        person_B.setBackground(ContextCompat.getDrawable(context, R.drawable.convo_correct_bg));
        person_A.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_bg));
        user = userB;
        btn_reading.setVisibility(View.VISIBLE);
    }

    @Click(R.id.rl_personA)
    public void onPersonA(){
        btn_reading.performClick();
        person_B.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_bg));
        person_A.setBackground(ContextCompat.getDrawable(context, R.drawable.convo_correct_bg));
        user = userA;
        btn_reading.setVisibility(View.VISIBLE);
    }

    @Click(R.id.clear)
    public void clear() {
        userAnswer = "";
        setAnswerText(userAnswer);
    }

    @UiThread
    public void setConvoJson(String contentName, int questionID) {
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            mAdapter = new MessageAdapter_3(messageList, context);
            recyclerView.setAdapter(mAdapter);
            question = contentName;
            tv_title.setText(question);
            this.questionID = questionID;
        }
    }

    @UiThread
    public void dataNotFound() {
        ll_convo_mainw.setVisibility(View.GONE);
    }



    @UiThread
    @Override
    public void sendClikChanger(int clickOn) {
        if (!FC_Constants.isTest) {

        } else {
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
            continuousSpeechService.startSpeechInput();
        } else {
            voiceStart = false;
            btn_reading.setImageResource(R.drawable.ic_mic_black);
            btn_reading.setBackgroundResource(R.drawable.button_green);
            continuousSpeechService.stopSpeechInput();
            //  ButtonClickSound.start();
        }
    }

    @Click(R.id.btn_imgsend)
    public void sendMessage() {
        if (userAnswer != null && !userAnswer.isEmpty()) {
            sendClikChanger(0);
            readchat.setText("");
            if (user.equalsIgnoreCase(userB)) {
                addItemInConvo(userAnswer, "", true);
            } else {
                addItemInConvo(userAnswer, "", false);
            }
            userAnswer = "";

            ButtonClickSound.start();
            btn_reading.setImageResource(R.drawable.ic_mic_black);
            if (voiceStart)
                btn_reading.performClick();
        }
    }



    private void addItemInConvo(String text, String audio, boolean user) {
        if (user)
            messageList.add(new Message(text, userB, audio));//user
        else
            messageList.add(new Message(text, userA, audio));//bot
        mAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private void setAnswerText(String answerText) {
        readchat.setText(answerText);

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
      /*  if (voiceStart)
            btn_reading.performClick();*/
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "stop");
        voiceStart = false;
        continuousSpeechService.stopSpeechInput();
        super.onStop();
    }


    @UiThread
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
     public void Stt_onResult(ArrayList<String> sttServerResult) {
        if (sttServerResult.get(0) != null) {
            userAnswer = userAnswer + " " + sttServerResult.get(0);
            setAnswerText(userAnswer);
        }
    }

    @Override
    public void silenceDetected() {
    }

    @Override
    public void onComplete() {

    }
    @UiThread
    public void closeConvo() {
        //todo #
        //finish();

    }

    @Click(R.id.btn_submit)
    public void onSubmitClick() {
        presenter.addLearntWords(messageList);
    }

    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.NEW_CHIT_CHAT_3 + " " + GameConstatnts.END, contentId, true);
    }
}