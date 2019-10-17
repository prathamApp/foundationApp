package com.pratham.foundation.ui.contentPlayer.reading;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.services.stt.ContinuousSpeechService;
import com.pratham.foundation.services.stt.STT_Result;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_fragment.FactRetrival_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.foundation.database.AppDatabase.appDatabase;


public class ReadingFragment extends Fragment implements STT_Result {

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


    ContinuousSpeechService speechService;
    ScienceQuestion scienceQuestion;
    private int totalWordCount, learntWordCount;
    private float perc = 0;
    private float percScore = 0;
    private String answer;
    public static SpeechRecognizer speech = null;
    private static boolean voiceStart = false, correctArr[];
    public static Intent intent;

    private String readingContentPath, contentPath, contentTitle, StudentID, resId;
    private boolean onSdCard;
    private Context context;
    private List<ScienceQuestion> dataList;
    private boolean isTest = false;

    public ReadingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            context = getActivity();
            speechService = new ContinuousSpeechService(context, ReadingFragment.this);
            speechService.resetSpeechRecognizer();
            contentPath = getArguments().getString("contentPath");
            StudentID = getArguments().getString("StudentID");
            resId = getArguments().getString("resId");
            contentTitle = getArguments().getString("contentName");
            onSdCard = getArguments().getBoolean("onSdCard", false);
            if (onSdCard)
                readingContentPath = ApplicationClass.contentSDPath + "/.LLA/English/Game/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + "/.LLA/English/Game/" + contentPath + "/";


            getData();
        }
    }

    private void getData() {
        //String text = FC_Utility.loadJSONFromAsset(context, "reading.json");
        try {
            InputStream is = new FileInputStream(readingContentPath + "reading.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONArray jsonObj = new JSONArray(jsonStr);

            // List instrumentNames = new ArrayList<>();
            Gson gson = new Gson();
            Type type = new TypeToken<List<ScienceQuestion>>() {
            }.getType();
            dataList = gson.fromJson(jsonObj.toString(), type);
            getDataList();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDataList() {
        try {
            perc = getPercentage();
            Collections.shuffle(dataList);
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
            //view.loadUI(listenAndWrittingModal);
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
        count = appDatabase.getKeyWordDao().checkWordCount(FC_Constants.currentStudentID, resId);
        return count;
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = appDatabase.getKeyWordDao().checkWord(FC_Constants.currentStudentID, resId, wordStr);
            if (word != null)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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


        question.setText(scienceQuestion.getQname());
        if (!scienceQuestion.getPhotourl().equalsIgnoreCase("")) {
            questionImage.setVisibility(View.VISIBLE);
//            if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {


            String fileName = scienceQuestion.getPhotourl();
//                String localPath = Environment.getExternalStorageDirectory() + Assessment_Constants.STORE_DOWNLOADED_MEDIA_PATH + "/" + fileName;
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

        } else questionImage.setVisibility(View.GONE);


        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                answer = s.toString();
            }
        });

    }


    @OnClick(R.id.ib_mic)
    public void onMicClicked() {
        callSTT();
    }

    public void callSTT() {
        if (!voiceStart) {
            voiceStart = true;
            micPressed(1);
            speechService.startSpeechInput();
        } else {
            voiceStart = false;
            micPressed(0);
            speechService.stopSpeechInput();
        }
    }


    public void micPressed(int micPressed) {
        if (micPressed == 0) {
            ib_mic.setImageResource(R.drawable.ic_mic_24dp);
        } else if (micPressed == 1) {
            ib_mic.setImageResource(R.drawable.ic_stop_black_24dp);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (speech != null) {
            speech.stopListening();
            micPressed(0);
            voiceStart = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (speechService != null)
            speechService.stopSpeechInput();
        micPressed(0);
        voiceStart = false;
        if (speech != null) {
            speech.stopListening();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        speechService.resetSpeechRecognizer();
       /* if (speech != null) {
            micPressed(0);
            voiceStart = false;
        }*/
    }


    @Override
    public void Stt_onResult(ArrayList<String> matches) {
        micPressed(0);
//        ib_mic.stopRecording();

        System.out.println("LogTag" + " onResults");
//        ArrayList<String> matches = results;

        String sttResult = "";
//        String sttResult = matches.get(0);
        String sttQuestion;
        for (int i = 0; i < matches.size(); i++) {
            System.out.println("LogTag" + " onResults :  " + matches.get(i));

            if (matches.get(i).equalsIgnoreCase(scienceQuestion.getAnswer()))
                sttResult = matches.get(i);
            else sttResult = matches.get(0);
        }
        sttQuestion = scienceQuestion.getAnswer();
        String regex = "[\\-+.\"^?!@#%&*,:]";
        String quesFinal = sttQuestion.replaceAll(regex, "");


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

            for (int i = 0; i < splitQues.length; i++) {
                //((TextView) readChatFlow.getChildAt(i)).setTextColor(getResources().getColor(R.color.readingGreen));
                correctArr[i] = true;
            }

//            scrollView.setBackgroundResource(R.drawable.convo_correct_bg);

        }


        etAnswer.setText(sttResult);
        voiceStart = false;
        micPressed(0);
    }

    @OnClick(R.id.submit)
    public void submitClick() {
        addLearntWords();
        Bundle bundle = GameConstatnts.findGameData("101");
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new FactRetrival_(), R.id.RL_CPA,
                    bundle, FactRetrival_.class.getSimpleName());
        }
    }

    public void addLearntWords() {
        int scoredMarks;
        if (percScore > 70) {
            scoredMarks = 10;
        } else {
            scoredMarks = 0;
        }
        KeyWords keyWords = new KeyWords();
        keyWords.setResourceId(resId);
        keyWords.setSentFlag(0);
        keyWords.setStudentId(FC_Constants.currentStudentID);
        keyWords.setKeyWord(scienceQuestion.getTitle());
        keyWords.setWordType("word");
        addScore(scienceQuestion.getQid(), GameConstatnts.READING, scoredMarks, 10, FC_Utility.getCurrentDateTime(), answer);
        appDatabase.getKeyWordDao().insert(keyWords);
        if (!isTest) {
            showResult(scoredMarks);
        }

        BackupDatabase.backup(context);

    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label) {
        try {
            String deviceId = appDatabase.getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FC_Constants.currentSession);
            score.setResourceID(resId);
            score.setQuestionId(wID);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FC_Constants.currentStudentID);
            score.setStartDateTime(resStartTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(4);
            score.setLabel(Word + " - " + Label);
            score.setSentFlag(0);
            appDatabase.getScoreDao().insert(score);

            if (FC_Constants.isTest) {
                Assessment assessment = new Assessment();
                assessment.setResourceIDa(resId);
                assessment.setSessionIDa(FC_Constants.assessmentSession);
                assessment.setSessionIDm(FC_Constants.currentSession);
                assessment.setQuestionIda(wID);
                assessment.setScoredMarksa(scoredMarks);
                assessment.setTotalMarksa(totalMarks);
                assessment.setStudentIDa(FC_Constants.currentAssessmentStudentID);
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
        }
    }
}