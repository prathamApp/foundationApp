package com.pratham.foundation.ui.contentPlayer.reading;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.services.stt.ContinuousSpeechService_New;
import com.pratham.foundation.services.stt.STT_Result_New;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EFragment(R.layout.reading_layout)
public class ReadingFragment extends Fragment implements STT_Result_New.sttView, OnGameClose, ReadingFragment_Contract.ReadingFragmentView {

    @ViewById(R.id.tv_question)
    SansTextView question;
    @ViewById(R.id.iv_question_image)
    ImageView questionImage;
    @ViewById(R.id.iv_question_gif)
    GifView questionGif;
    @ViewById(R.id.et_answer)
    SansTextView etAnswer;
    @ViewById(R.id.ib_mic)
    ImageButton ib_mic;
    @ViewById(R.id.btn_prev)
    ImageButton previous;
    @ViewById(R.id.btn_next)
    ImageButton next;
    @ViewById(R.id.submit)
    SansButton submitBtn;

    @ViewById(R.id.reset_btn)
    SansButton reset_btn;

    @Bean(ReadingFragment_Presenter.class)
    ReadingFragment_Contract.ReadingFragmentPresenter presenter;
    private ScienceQuestion scienceQuestion;
    private List<ScienceQuestionChoice> scienceQuestionChoices;
    private int index = 0;
    private float perc = 0;
    private float percScore = 0;
    private String answer;
    private static boolean voiceStart = false;
    private static boolean[] correctArr;
    public static Intent intent;
    private String readingContentPath, contentPath, contentTitle, StudentID, resId;
    private boolean onSdCard;
    private Context context;
    private List<ScienceQuestion> dataList;
    String resStartTime;
    private ContinuousSpeechService_New continuousSpeechService;
    public Dialog myLoadingDialog;
    boolean dialogFlg = false;
    private String jsonName;

    //private String speechStartTime;
    public ReadingFragment() {
    }

    @AfterViews
    public void initiate() {
        // super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            context = getActivity();
            continuousSpeechService = new ContinuousSpeechService_New(context, ReadingFragment.this, FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI));
            continuousSpeechService.resetSpeechRecognizer();
            contentPath = getArguments().getString("contentPath");
            StudentID = getArguments().getString("StudentID");
            resId = getArguments().getString("resId");
            contentTitle = getArguments().getString("contentName");
            onSdCard = getArguments().getBoolean("onSdCard", false);
            jsonName = getArguments().getString("jsonName");
            if (onSdCard)
                readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";
            etAnswer.setMovementMethod(new ScrollingMovementMethod());
            EventBus.getDefault().register(this);
            resStartTime = FC_Utility.getCurrentDateTime();
            presenter.setView(this,jsonName,resId,resStartTime);
            presenter.addScore(0, "", 0, 0, resStartTime, jsonName + " " + GameConstatnts.START);
            //getData();
            presenter.getData(readingContentPath);
        }
    }

    public void showLoader() {
        if (!dialogFlg) {
            dialogFlg = true;
            myLoadingDialog = new Dialog(context);
            myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myLoadingDialog.setContentView(R.layout.loading_dialog);
            myLoadingDialog.setCanceledOnTouchOutside(false);
            myLoadingDialog.show();
        }
    }

    public void showQuestion(ScienceQuestion scienceQuestion) {
        if (scienceQuestion != null) {
            this.scienceQuestion = scienceQuestion;
            scienceQuestionChoices = scienceQuestion.getLstquestionchoice();
            setFillInTheBlanksQuestion();
        }
    }
    public void setFillInTheBlanksQuestion() {
        if (scienceQuestionChoices != null && !scienceQuestionChoices.isEmpty()) {
            if (continuousSpeechService != null) {
                continuousSpeechService.onEndOfSpeech();
                // speech.stopListening();
                micPressed(0);
                voiceStart = false;
            }
            if (scienceQuestionChoices.get(index).getUserAns().trim() != null && !scienceQuestionChoices.get(index).getUserAns().isEmpty()) {
                myString=scienceQuestionChoices.get(index).getUserAns();
                etAnswer.setText(scienceQuestionChoices.get(index).getUserAns());
            } else {
                myString="";
                etAnswer.setText("");
            }
            question.setText(scienceQuestionChoices.get(index).getSubQues());
            if (!scienceQuestionChoices.get(index).getSubUrl().trim().equalsIgnoreCase("")) {
                questionImage.setVisibility(View.VISIBLE);
                String fileName = scienceQuestionChoices.get(index).getSubUrl();
                final String localPath = readingContentPath + fileName;
                String path = scienceQuestionChoices.get(index).getSubUrl();
                String[] imgPath = path.split("\\.");
                int len;
                if (imgPath.length > 0)
                    len = imgPath.length - 1;
                else len = 0;
                if (imgPath[len].equalsIgnoreCase("gif")) {
                    try {
                        InputStream gif;
                        gif = new FileInputStream(localPath);
                        questionImage.setVisibility(View.GONE);
                        questionGif.setVisibility(View.VISIBLE);
                        questionGif.setGifResource(gif);
                        //  }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Glide.with(getActivity())
                            .load(path)
                            .apply(new RequestOptions()
                                    .placeholder(Drawable.createFromPath(localPath)))
                            .into(questionImage);
                }
            } else
                questionImage.setVisibility(View.GONE);

            etAnswer.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //answer = s.toString();
                   /* scienceQuestionChoices.get(index).setStartTime(FC_Utility.GetCurrentDateTime());
                    scienceQuestionChoices.get(index).setEndTime(FC_Utility.GetCurrentDateTime());
                    scienceQuestionChoices.get(index).setUserAns( s.toString());*/
                }
            });
            if (index == 0) {
                previous.setVisibility(View.INVISIBLE);
            } else {
                previous.setVisibility(View.VISIBLE);
            }
            if (index == (scienceQuestionChoices.size() - 1)) {
                submitBtn.setVisibility(View.VISIBLE);
                next.setVisibility(View.INVISIBLE);
            } else {
                submitBtn.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.ib_mic)
    public void onMicClicked() {
        callSTT();
    }

    public void callSTT() {
        if (!voiceStart) {
            voiceStart = true;
            micPressed(1);
            showLoader();
            continuousSpeechService.startSpeechInput();
        } else {
            voiceStart = false;
            micPressed(0);
            showLoader();
            continuousSpeechService.stopSpeechInput();
        }
    }

    public void micPressed(int micPressed) {
        if (micPressed == 0) {
            ib_mic.setImageResource(R.drawable.ic_mic_black);
            showButtons();

        } else if (micPressed == 1) {
            ib_mic.setImageResource(R.drawable.ic_pause_black);
            hideButtons();
        }
    }

    private void hideButtons() {
        previous.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        reset_btn.setVisibility(View.INVISIBLE);
        submitBtn.setVisibility(View.INVISIBLE);
    }

    private void showButtons() {
        if (index == 0) {
            previous.setVisibility(View.INVISIBLE);
        } else {
            previous.setVisibility(View.VISIBLE);
        }
        if (index == (scienceQuestionChoices.size() - 1)) {
            submitBtn.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
        } else {
            submitBtn.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
        }
        if (index == (scienceQuestionChoices.size() - 1)) {
            submitBtn.setVisibility(View.VISIBLE);
        }
        reset_btn.setVisibility(View.VISIBLE);
    }

    @Override
    public void silenceDetected() {
    }

    @Override
    public void stoppedPressed() {
        dismissLoadingDialog();
    }

    @Override
    public void sttEngineReady() {
        dismissLoadingDialog();
    }


    public void dismissLoadingDialog() {
        if (dialogFlg) {
            dialogFlg = false;
            if (myLoadingDialog != null)
                myLoadingDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        if (voiceStart)
            callSTT();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    String myString = "";

    @Override
    public void Stt_onResult(ArrayList<String> matches) {
        try {
            System.out.println("LogTag" + " onResults");
            String sttResult = "";
            String sttQuestion;
            for (int i = 0; i < matches.size(); i++) {
                System.out.println("LogTag" + " onResults :  " + matches.get(i));
                if (matches.get(i).equalsIgnoreCase(scienceQuestionChoices.get(index).getUserAns()))
                    sttResult = matches.get(i);
                else sttResult = matches.get(0);
            }
            sttQuestion = scienceQuestionChoices.get(index).getUserAns();
            String quesFinal = sttQuestion.replaceAll(STT_REGEX, "");

            String[] splitQues = quesFinal.split(" ");
            String[] splitRes = sttResult.split(" ");

            if (splitQues.length < splitRes.length)
                correctArr = new boolean[splitRes.length];
            else correctArr = new boolean[splitQues.length];

            for (int j = 0; j < splitRes.length; j++) {
                for (int i = 0; i < splitQues.length; i++) {
                    if (splitRes[j].equalsIgnoreCase(splitQues[i])) {
                        // ((TextView) readChatFlow.getChildAt(i)).setTextColor(getResources().getColor(R.color.readingGreen));
                        correctArr[i] = true;
                        //sendClikChanger(1);
                        break;
                    }
                }
            }
            int correctCnt = 0;
            for (int x = 0; x < correctArr.length; x++) {
                if (correctArr[x])
                    correctCnt++;
            }
            percScore = ((float) correctCnt / (float) correctArr.length) * 100;
            Log.d("Punctu", "onResults: " + percScore);
            if (percScore >= 75) {
                for (int i = 0; i < splitQues.length; i++)
                    correctArr[i] = true;
            }
            myString = myString + " " + sttResult;
            etAnswer.setText(myString);
            //scienceQuestionChoices.get(index).setStartTime(resStartTime);
            // scienceQuestionChoices.get(index).setEndTime(FC_Utility.GetCurrentDateTime());
            scienceQuestionChoices.get(index).setUserAns(myString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.submit)
    public void submitClick() {
        voiceStart = false;
        micPressed(0);
        showLoader();
        continuousSpeechService.stopSpeechInput();
        presenter.addLearntWords(scienceQuestionChoices);
    }

    @Click(R.id.reset_btn)
    public void resetClick() {
        myString = "";
        etAnswer.setText(myString);
    }

    @Click(R.id.btn_next)
    public void onNextClick() {
        if (scienceQuestionChoices != null)
            if (index < (scienceQuestionChoices.size() - 1)) {
                //  setAnswer();
                index++;
                setFillInTheBlanksQuestion();
            }
    }

    @Click(R.id.btn_prev)
    public void onPreviousClick() {
        if (scienceQuestionChoices != null)
            if (index > 0) {
                //  setAnswer();
                index--;
                setFillInTheBlanksQuestion();
            }
    }


    public void showResult() {
        if (scienceQuestionChoices != null && presenter.checkAttemptedornot(scienceQuestionChoices)){
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.FALSE, this);
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, this);
        }
        /*
        if (scoredMark == 10) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.app_success_dialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
            dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } else {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.app_failure_dialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
            dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }*/
    }

    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime, jsonName + " " + GameConstatnts.END);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (!scienceQuestion.getInstruction().isEmpty())
            GameConstatnts.showGameInfo(getActivity(), scienceQuestion.getInstruction(), readingContentPath + scienceQuestion.getInstructionUrl());
    }
}