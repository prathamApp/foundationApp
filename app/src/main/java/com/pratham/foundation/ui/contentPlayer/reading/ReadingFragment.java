package com.pratham.foundation.ui.contentPlayer.reading;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.services.stt.ContinuousSpeechService_New;
import com.pratham.foundation.services.stt.STT_Result_New;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

public class ReadingFragment extends Fragment implements STT_Result_New.sttView, OnGameClose {

    @BindView(R.id.tv_question)
    SansTextView question;
    @BindView(R.id.iv_question_image)
    ImageView questionImage;
    @BindView(R.id.iv_question_gif)
    GifView questionGif;
    @BindView(R.id.et_answer)
    SansTextView etAnswer;
    @BindView(R.id.ib_mic)
    ImageButton ib_mic;

    ScienceQuestion scienceQuestion;
    private int totalWordCount, learntWordCount;
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
    private boolean isTest = false;
    String resStartTime;
    private ContinuousSpeechService_New continuousSpeechService;
    public Dialog myLoadingDialog;
    boolean dialogFlg = false;
    private String jsonName;

    public ReadingFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            context = getActivity();
            continuousSpeechService = new ContinuousSpeechService_New(context, ReadingFragment.this, FC_Constants.currentSelectedLanguage);
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

            EventBus.getDefault().register(this);
            resStartTime = FC_Utility.getCurrentDateTime();
            addScore(0, "", 0, 0, resStartTime, jsonName + " " + GameConstatnts.START);
            getData();
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

    private void getData() {
        String text = FC_Utility.loadJSONFromStorage(readingContentPath, jsonName+".json");
        // List instrumentNames = new ArrayList<>();
        if (text != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ScienceQuestion>>() {
            }.getType();
            dataList = gson.fromJson(text, type);
            getDataList();
        } else {
            Toast.makeText(context, "Data not found", Toast.LENGTH_LONG).show();
        }
    }

    public void setCompletionPercentage() {
        try {
            totalWordCount = dataList.size();
            learntWordCount = getLearntWordsCount();
            String Label = "resourceProgress";
            if (learntWordCount > 0) {
                perc = ((float) learntWordCount / (float) totalWordCount) * 100;
                addContentProgress(perc, Label);
            } else {
                addContentProgress(0, Label);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            appDatabase.getContentProgressDao().insert(contentProgress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDataList() {
        try {
            perc = getPercentage();
            Collections.shuffle(dataList);
            if (dataList.get(0).getTitle() == null || dataList.get(0).getTitle().isEmpty()) {
                scienceQuestion = dataList.get(0);
            }else {
                for (int i = 0; i < dataList.size(); i++) {
                    if (perc < 95) {
                        if (!checkWord("" + dataList.get(i).getTitle())) {
                            scienceQuestion = dataList.get(i);
                            break;
                        }
                    } else {
                        scienceQuestion = dataList.get(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getPercentage() {
        float perc = 0f;
        try {
            totalWordCount = dataList.size();
            learntWordCount = getLearntWordsCount();
            if (learntWordCount > 0) {
                perc = ((float) learntWordCount / (float) totalWordCount) * 100;
                return perc;
            } else
                return 0f;
        } catch (Exception e) {
            return 0f;
        }
    }

    private int getLearntWordsCount() {
        int count = 0;
        //  count = appDatabase.getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
        count = appDatabase.getKeyWordDao().checkUniqueWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
        return count;
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = appDatabase.getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId, wordStr);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reading_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        etAnswer.setMovementMethod(new ScrollingMovementMethod());
        setFillInTheBlanksQuestion();
    }

    public void setFillInTheBlanksQuestion() {
        if (scienceQuestion != null) {
            question.setText(scienceQuestion.getQuestion());
            if (!scienceQuestion.getPhotourl().trim().equalsIgnoreCase("")) {
                questionImage.setVisibility(View.VISIBLE);
                String fileName = scienceQuestion.getPhotourl();
                final String localPath = readingContentPath + fileName;
                String path = scienceQuestion.getPhotourl();
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
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void afterTextChanged(Editable s) {
                    answer = s.toString();
                }
            });

        } else {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.ib_mic)
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
        } else if (micPressed == 1) {
            ib_mic.setImageResource(R.drawable.ic_pause_black);
        }
    }

    @Override
    public void silenceDetected() { }

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
        if(voiceStart)
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
                if (matches.get(i).equalsIgnoreCase(scienceQuestion.getAnswer()))
                    sttResult = matches.get(i);
                else sttResult = matches.get(0);
            }
            sttQuestion = scienceQuestion.getAnswer();
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
            myString = myString + " " + sttResult ;
            etAnswer.setText(myString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.submit)
    public void submitClick() {
        addLearntWords();
    }

    @OnClick(R.id.reset_btn)
    public void resetClick() {
        myString = "";
        etAnswer.setText(myString);
    }

    public void addLearntWords() {
        if (answer != null && !answer.isEmpty()) {
            int scoredMarks;
            if (percScore > 70) {
                scoredMarks = 10;
            } else {
                scoredMarks = 0;
            }
            KeyWords keyWords = new KeyWords();
            keyWords.setResourceId(resId);
            keyWords.setSentFlag(0);
            keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            keyWords.setKeyWord(scienceQuestion.getTitle());
            keyWords.setWordType("word");
            addScore(GameConstatnts.getInt(scienceQuestion.getQid()), jsonName, 0, 10, FC_Utility.getCurrentDateTime(), answer);
            appDatabase.getKeyWordDao().insert(keyWords);
            setCompletionPercentage();
            if (!isTest) {
                showResult(scoredMarks);
            }
            BackupDatabase.backup(context);
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, this);
        }
    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label) {
        try {
            String deviceId = appDatabase.getStatusDao().getValue("DeviceId");
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
            appDatabase.getScoreDao().insert(score);

            if (FC_Constants.isTest) {
                Assessment assessment = new Assessment();
                assessment.setResourceIDa(resId);
                assessment.setSessionIDa(FastSave.getInstance().getString(FC_Constants.ASSESSMENT_SESSION, ""));
                assessment.setSessionIDm(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                assessment.setQuestionIda(wID);
                assessment.setScoredMarksa(scoredMarks);
                assessment.setTotalMarksa(totalMarks);
                assessment.setStudentIDa(FastSave.getInstance().getString(FC_Constants.CURRENT_ASSESSMENT_STUDENT_ID, ""));
                assessment.setStartDateTimea(resStartTime);
                assessment.setDeviceIDa(deviceId.equals(null) ? "0000" : deviceId);
                assessment.setEndDateTime(FC_Utility.getCurrentDateTime());
                assessment.setLevela(FC_Constants.currentLevel);
                assessment.setLabel("test: " + Label);
                assessment.setSentFlag(0);
                appDatabase.getAssessmentDao().insert(assessment);
            }
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showResult(int scoredMark) {
        if (answer != null && !answer.isEmpty()) {
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
        addScore(0, "", 0, 0, resStartTime, jsonName+ " " + GameConstatnts.END);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (!scienceQuestion.getInstruction().isEmpty())
            GameConstatnts.showGameInfo(getActivity(), scienceQuestion.getInstruction(),readingContentPath+scienceQuestion.getInstructionUrl());
    }
}