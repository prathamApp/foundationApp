package com.pratham.foundation.ui.contentPlayer.fillInTheBlanks;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.fontsview.SansButton;
import com.pratham.foundation.customView.fontsview.SansTextView;
import com.pratham.foundation.customView.fontsview.SansTextViewBold;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.services.stt.ContinuousSpeechService_New;
import com.pratham.foundation.services.stt.STT_Result_New;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.json.JSONArray;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FillInTheBlanksFragment extends Fragment implements STT_Result_New.sttView {

    @BindView(R.id.tv_question)
    SansTextView question;
    @BindView(R.id.iv_question_image)
    ImageView questionImage;
    @BindView(R.id.iv_question_gif)
    GifView questionGif;
    @BindView(R.id.et_answer)
    EditText etAnswer;
    @BindView(R.id.ib_mic)
    ImageButton ib_mic;

    ContinuousSpeechService_New speechService;
    // ScienceQuestion scienceQuestion;
    private int totalWordCount, learntWordCount, index = 0;
    private float perc = 0;
    private float percScore = 0;
    private String answer;
    public static SpeechRecognizer speech = null;
    private static boolean voiceStart = false;
    private static boolean[] correctArr;
    public static Intent intent;

    private String readingContentPath, contentPath, contentTitle, StudentID, resId;
    private boolean onSdCard;
    private Context context;
    private List<ScienceQuestion> dataList;
    private List<ScienceQuestion> selectedFive;
    private boolean isTest = false;
    private List<String> correctWordList, wrongWordList;

    public FillInTheBlanksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            context = getActivity();
            speechService = new ContinuousSpeechService_New(context, FillInTheBlanksFragment.this,"en");
            speechService.resetSpeechRecognizer();
            contentPath = getArguments().getString("contentPath");
            StudentID = getArguments().getString("StudentID");
            resId = getArguments().getString("resId");
            contentTitle = getArguments().getString("contentName");
            onSdCard = getArguments().getBoolean("onSdCard", false);

            contentPath = "fill_in_the_blanks";
            resId = "1212";
            StudentID = FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "");
            contentTitle = "b";
            onSdCard = true;
            isTest= FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test);

            if (onSdCard)
                readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";
            getData();
        }
    }

    private void getData() {
        //String text = FC_Utility.loadJSONFromAsset(context, "reading.json");
        try {
            InputStream is = new FileInputStream(readingContentPath + "fill_in_the_blanks.json");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDataList() {
        try {
            selectedFive = new ArrayList<ScienceQuestion>();
            perc = getPercentage();
            Collections.shuffle(dataList);
            for (int i = 0; i < dataList.size(); i++) {
                if (perc < 95) {
                    if (!checkWord("" + dataList.get(i).getQuestion()))
                        selectedFive.add(dataList.get(i));
                } else {
                    selectedFive.add(dataList.get(i));
                }
                if (selectedFive.size() >= 5) {
                    break;
                }
            }
            Collections.shuffle(selectedFive);
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
        count = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
        return count;
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId, wordStr);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fill_in_the_blanks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        question.setMovementMethod(new ScrollingMovementMethod());
        etAnswer.setMovementMethod(new ScrollingMovementMethod());
        setFillInTheBlanksQuestion();
    }


    public void setFillInTheBlanksQuestion() {


        question.setText(selectedFive.get(index).getQuestion());
        etAnswer.setText(selectedFive.get(index).getUserAnswer());
        if (!selectedFive.get(index).getPhotourl().equalsIgnoreCase("")) {
            questionImage.setVisibility(View.VISIBLE);
//            if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {


            String fileName = selectedFive.get(index).getPhotourl();
//                String localPath = ApplicationClass.getStoragePath() + Assessment_Constants.STORE_DOWNLOADED_MEDIA_PATH + "/" + fileName;
            final String localPath = readingContentPath + fileName;


            String path = selectedFive.get(index).getPhotourl();
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
                try {
                    Bitmap bmImg = BitmapFactory.decodeFile(localPath);
                    BitmapFactory.decodeStream(new FileInputStream(localPath));
                    questionImage.setImageBitmap(bmImg);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
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
                selectedFive.get(index).setUserAnswer(s.toString());
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
            ib_mic.setImageResource(R.drawable.ic_mic_black);
        } else if (micPressed == 1) {
            ib_mic.setImageResource(R.drawable.ic_stop_black);
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

            if (matches.get(i).equalsIgnoreCase(selectedFive.get(index).getAnswer()))
                sttResult = matches.get(i);
            else sttResult = matches.get(0);
        }
        sttQuestion = selectedFive.get(index).getAnswer();
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

    @Override
    public void silenceDetected() {

    }

    @Override
    public void stoppedPressed() {

    }

    @Override
    public void sttEngineReady() {

    }


    public void addLearntWords(List<ScienceQuestion> selectedAnsList) {
        int correctCnt = 0;
        correctWordList = new ArrayList<>();
        wrongWordList = new ArrayList<>();
        if (selectedAnsList != null && !selectedAnsList.isEmpty()) {
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (selectedAnsList.get(i).getAnswer().equalsIgnoreCase(selectedAnsList.get(i).getUserAnswer())) {
                    correctCnt++;
                    KeyWords keyWords = new KeyWords();
                    keyWords.setResourceId(resId);
                    keyWords.setSentFlag(0);
                    keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                    String key = selectedAnsList.get(i).getUserAnswer();
                    keyWords.setKeyWord(key);
                    keyWords.setWordType("word");
                    AppDatabase.getDatabaseInstance(context).getKeyWordDao().insert(keyWords);
                    correctWordList.add(selectedAnsList.get(i).getUserAnswer());
                    addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid()), GameConstatnts.FILL_IN_THE_BLANKS, 10, 10, FC_Utility.getCurrentDateTime(), selectedAnsList.get(i).getUserAnswer());

                } else {
                    if (selectedAnsList.get(i).getUserAnswer() != null && !selectedAnsList.get(i).getUserAnswer().trim().equalsIgnoreCase("")) {
                        wrongWordList.add(selectedAnsList.get(i).getUserAnswer());
                        addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid()), GameConstatnts.FILL_IN_THE_BLANKS, 0, 10, FC_Utility.getCurrentDateTime(), selectedAnsList.get(i).getUserAnswer());
                    }
                }
            }
            if (!isTest) {
                showResult(correctWordList, wrongWordList);
            }
        }
        BackupDatabase.backup(getActivity());

    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label) {
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
            score.setLevel(4);
            score.setLabel(Word + " - " + Label);
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);

            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
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
                AppDatabase.getDatabaseInstance(context).getAssessmentDao().insert(assessment);
            }
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showResult(List<String> correctWord, List<String> wrongWord) {
        final CustomLodingDialog dialog = new CustomLodingDialog(getActivity(), R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_result);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        SansTextViewBold correct_keywords = dialog.findViewById(R.id.correct_keywords);
        SansTextViewBold wrong_keywords = dialog.findViewById(R.id.wrong_keywords);
        SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        correct_keywords.setText(correctWord.toString().substring(1, correctWord.toString().length() - 1));
        wrong_keywords.setText(wrongWord.toString().substring(1, wrongWord.toString().length() - 1));
        dia_btn_yellow.setText("OK");
        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @OnClick(R.id.previous)
    public void onPreviousClick() {
        if (index > 0) {
            index--;
            setFillInTheBlanksQuestion();
        }
    }

    @OnClick(R.id.next)
    public void onNextClick() {
        if (index < 4) {
            index++;
            setFillInTheBlanksQuestion();
        }
    }

    @OnClick(R.id.submitBtn)
    public void onsubmitBtnClick() {
        if (selectedFive != null)
            addLearntWords(selectedFive);
      //  GameConstatnts.playGameNext(getActivity());
       /* Bundle bundle = GameConstatnts.findGameData("109");
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new TrueFalseFragment(), R.id.RL_CPA,
                    bundle, TrueFalseFragment.class.getSimpleName());

        }*/
    }
}
