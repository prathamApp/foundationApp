package com.pratham.foundation.ui.contentPlayer.paragraph_stt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ParaSttQuestionListModel;
import com.pratham.foundation.services.TTSService;
import com.pratham.foundation.services.stt.ContinuousSpeechService_Lang;
import com.pratham.foundation.services.stt.STT_Result_Lang;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.pictionary.PictionaryResult;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.BaseActivity.setMute;
import static com.pratham.foundation.utility.FC_Constants.HINDI;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.SplashSupportActivity.ButtonClickSound;


@EFragment(R.layout.fragment_stt_questions)
public class STTQuestionsFragment extends Fragment implements
        /*RecognitionListener, */STT_Result_Lang.sttView,
        ParaSttReadingContract.STTQuestionsView, OnGameClose {

    @Bean(STTQuestionsPresenter.class)
    ParaSttReadingContract.STTQuestionsPresenter presenter;

    public static MediaPlayer mp, mPlayer;
    @ViewById(R.id.myflowlayout)
    FlowLayout wordFlowLayout;
    @ViewById(R.id.tv_story_title)
    TextView story_title;
    @ViewById(R.id.stt_ans_tv)
    TextView stt_ans_tv;
    @ViewById(R.id.btn_prev)
    ImageButton btn_prev;
    @ViewById(R.id.reset_btn)
    Button reset_btn;
    @ViewById(R.id.btn_next)
    ImageButton btn_next;
    @ViewById(R.id.ib_mic)
    ImageButton ib_mic;
    @ViewById(R.id.myScrollView)
    ScrollView myScrollView;
    @ViewById(R.id.submit)
    Button submit;
    @ViewById(R.id.story_ll)
    RelativeLayout story_ll;

    ContinuousSpeechService_Lang continuousSpeechService;

    public static String storyId, StudentID = "", readingContentPath;
    TTSService ttsService;
    Context context;

    List<ParaSttQuestionListModel> paraSttQuestionList;
    String[] sttAnswers;
    String[] sttAnswersTime;
    String contentType, storyPath, storyName, pageTitle, sttLang, quesStartTime;
    static int currentPage;
    boolean lastPgFlag = false;
    boolean playFlg = false, pauseFlg = false;
    int wordCounter = 0, totalPages = 0, pageNo = 1;
    List<Integer> readSounds = new ArrayList<>();
    private String LOG_TAG = "VoiceRecognitionActivity", /*myCurrentSentence,*/
            startTime;
    boolean voiceStart = false, flgPerMarked = false, onSdCard;

    @AfterViews
    public void initialize() {
        showLoader();
        context = getActivity();
        silence_outer_layout.setVisibility(View.GONE);
        Bundle bundle = getArguments();
        contentType = bundle.getString("contentType");
        storyPath = bundle.getString("contentPath");
        storyId = bundle.getString("resId");
        storyName = bundle.getString("contentName");
        sttLang = bundle.getString("sttLang");
        onSdCard = bundle.getBoolean("onSdCard", false);
        ttsService = ApplicationClass.ttsService;
        contentType = "Stt QA";

        presenter.setView(STTQuestionsFragment.this);
        continuousSpeechService = new ContinuousSpeechService_Lang(context, STTQuestionsFragment.this, HINDI);

        if (contentType.equalsIgnoreCase(FC_Constants.RHYME_RESOURCE))
            ib_mic.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);

        readSounds.add(R.raw.tap_the_mic);
        readSounds.add(R.raw.your_turn_to_read);
        readSounds.add(R.raw.would_you_like_to_read);
        readSounds.add(R.raw.tap_the_mic_to_read_out);
        Collections.shuffle(readSounds);
        startTime = FC_Utility.getCurrentDateTime();
        quesStartTime = FC_Utility.getCurrentDateTime();
        presenter.setResId(storyId);

        currentPage = 0;

        presenter.addScore(0, "", 0, 0, startTime, contentType + " start");
        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + storyPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + storyPath + "/";

        continuousSpeechService.resetSpeechRecognizer();

        try {
            presenter.fetchJsonData(readingContentPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListData(List<ParaSttQuestionListModel> paraSttQuestionList) {
        this.paraSttQuestionList = paraSttQuestionList;
        totalPages = paraSttQuestionList.size();
        sttAnswers = new String[totalPages];
        sttAnswersTime = new String[totalPages];
        for(int i=0;i<totalPages;i++) {
            sttAnswers[i] = "";
            paraSttQuestionList.get(i).setStudentText("");
            sttAnswersTime[i] = "NA";
        }
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
        if (dialogFlg) {
            dialogFlg = false;
            if (myLoadingDialog != null)
                myLoadingDialog.dismiss();
        }
    }

    @UiThread
    @Override
    public void setCategoryTitle(String title) {
        story_title.setText(Html.fromHtml("" + storyName));
//        toolbar.setTitle(storyName);
    }

    @UiThread
    @Override
    public void initializeContent(int pageNo) {
        currentPage = pageNo;
        if (currentPage == 0) {
            lastPgFlag = false;
            btn_prev.setVisibility(View.GONE);
        }
        if (currentPage < totalPages - 1 && currentPage > 0) {
            lastPgFlag = false;
            btn_prev.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.VISIBLE);
        }
        if (currentPage == totalPages - 1 ) {
            lastPgFlag = true;
//            submit.setVisibility(View.VISIBLE);
            btn_prev.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.GONE);
        }
        pageTitle = paraSttQuestionList.get(currentPage).getQuesText();
        startTime = FC_Utility.getCurrentDateTime();
        story_title.setText(pageTitle);
        showHint();
        dismissLoadingDialog();
    }

    @Click(R.id.btn_Stop)
    void stopBtn() {
        ib_mic.performClick();
    }

    @Click(R.id.reset_btn)
    void resetBtn() {
        sttAnswers[currentPage] = "";
        paraSttQuestionList.get(currentPage).setStudentText("");
        stt_ans_tv.setText(""+sttAnswers[currentPage]);
    }

    @Click(R.id.hint_btn)
    void showHint() {
        if(voiceStart)
            ib_mic.performClick();
        showDialog();
    }

    private void showDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(context);
        final View dialogView = View.inflate(getActivity(),R.layout.fc_show_hint_dialog,null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);
        TextView dialog_hint_tv = dialog.findViewById(R.id.dialog_hint_tv);
        ImageButton dia_btn_cross = dialog.findViewById(R.id.dia_btn_cross);

        dialog_hint_tv.setText(""+paraSttQuestionList.get(currentPage).getAnswerText());

        dialog.show();
        dia_btn_cross.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Click(R.id.ib_mic)
    void sttMethod() {
        if(sttAnswersTime[currentPage] == "NA") {
            quesStartTime = ""+FC_Utility.getCurrentDateTime();
            sttAnswersTime[currentPage] = quesStartTime;
        }
    if (!voiceStart) {
            try {
                voiceStart = true;
                showLoader();
                ib_mic.setImageResource(R.drawable.ic_stop_black);
                ib_mic.setBackgroundResource(R.drawable.button_red);
                continuousSpeechService.startSpeechInput();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                voiceStart = false;
                ib_mic.setImageResource(R.drawable.ic_mic_black);
                ib_mic.setBackgroundResource(R.drawable.button_green);
                continuousSpeechService.stopSpeechInput();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Click(R.id.btn_prev)
    void gotoPrevPage() {
        stt_ans_tv.setText("");
        if (currentPage > 0) {
            if (voiceStart) {
                ib_mic.performClick();
                setMute(0);
            }
            wordCounter = 0;
            ButtonClickSound.start();
            Log.d("click", "totalPages: PreviousBtn: " + totalPages + "  currentPage: " + currentPage);
            try {
                currentPage--;
                pageNo--;
                playFlg = false;
                pauseFlg = true;
                flgPerMarked = false;
                stt_ans_tv.setText(""+sttAnswers[currentPage]);
                presenter.getPage(currentPage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Click(R.id.submit)
    public void submitTest() {
        if(voiceStart)
            ib_mic.performClick();

        for(int i=0; i<totalPages; i++)
            presenter.addScore(0,""+sttAnswers[i],currentPage,totalPages,""+sttAnswersTime[i],"Stt_QA Submit");

        super.onStop();
    }

    @Click(R.id.btn_next)
    void gotoNextPage() {
        stt_ans_tv.setText("");
        if (currentPage < totalPages - 1) {
            wordCounter = 0;
//            presenter.addScore(0,""+sttAnswers[currentPage],currentPage,totalPages,""+quesStartTime,"Stt_QA");
            if (voiceStart) {
                ib_mic.performClick();
                setMute(0);
            }
            ButtonClickSound.start();
            Log.d("click", "totalPages: NextBtn: " + totalPages + "  currentPage: " + currentPage);
            currentPage++;
            pageNo++;
            flgPerMarked = true;
            playFlg = false;
            pauseFlg = true;
            stt_ans_tv.setText(""+sttAnswers[currentPage]);
            presenter.getPage(currentPage);
            Log.d("click", "NextBtn - totalPages: " + totalPages + "  currentPage: " + currentPage);
        } else {
//            GameConstatnts.playGameNext(getActivity(), true, this);
        }
    }

    public void exitDBEntry() {
        try {
            for(int i=0; i<totalPages; i++) {
                presenter.addScore(0, "" + sttAnswers[i], currentPage, totalPages, "" + sttAnswersTime[i], "Stt_QA Submit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        presenter.addScore(0, "", 0, 0, startTime, contentType + " End");
        presenter.addProgress(sttAnswers,sttAnswersTime);
    }

    int correctCnt = 0, total = 0;
    @Override
    public void onPause() {
        super.onPause();
        if (voiceStart) {
            ib_mic.performClick();
            voiceStart = false;
            setMute(0);
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

    @Background
    @Override
    public void Stt_onResult(ArrayList<String> sttResult) {
        setAnswerToView(sttResult);
    }

    @UiThread
    public void setAnswerToView(ArrayList<String> sttResult) {
        String tempText = sttAnswers[currentPage] + " "+ sttResult.get(0);
        sttAnswers[currentPage] = tempText;
        paraSttQuestionList.get(currentPage).setStudentText(tempText);
        stt_ans_tv.setText(tempText);
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
    public void gameClose() {
        Log.d("gameClose", "gameClose: gameClose: ");
        exitDBEntry();
        showCompareDialog();
    }

    private void showCompareDialog() {
        Intent intent = new Intent(getActivity(), PictionaryResult.class);
        intent.putExtra("paraSttQuestionList", (Serializable) paraSttQuestionList);
        intent.putExtra("resourceType", GameConstatnts.PARAQA);
        startActivityForResult(intent, 111);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(FC_Constants.INFO_CLICKED)){
//                showAnswer();
            }
            else if (message.getMessage().equalsIgnoreCase(FC_Constants.BACK_PRESSED)){
                if(voiceStart)
                    ib_mic.performClick();
                for(int i=0; i<totalPages; i++)
                    presenter.addScore(0,""+sttAnswers[i],currentPage,totalPages,""+sttAnswersTime[i],"Stt_QA Submit");
//                presenter.addScore(0,""+sttAnswers[currentPage],currentPage,totalPages,""+quesStartTime,"Stt_QA");
            }
        }
    }
}