package com.pratham.foundation.ui.contentPlayer.multipleChoice;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.customView.SansTextViewBold;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.ViewById;
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


public class McqFillInTheBlanksFragment extends Fragment {

    @BindView(R.id.tv_question)
    SansTextView question;
    @BindView(R.id.iv_question_image)
    ImageView questionImage;
    @BindView(R.id.iv_question_gif)
    GifView questionGif;
    @BindView(R.id.rg_mcq)
    RadioGroup radioGroupMcq;
    @BindView(R.id.grid_mcq)
    GridLayout gridMcq;

    @BindView(R.id.previous)
    SansButton previous;
    @BindView(R.id.submitBtn)
    TextView submitBtn;
    @BindView(R.id.next)
    TextView next;
    /*  @BindView(R.id.iv_zoom_img)
      ImageView zoomImg;*/
    List<ScienceQuestionChoice> options;
    private float perc;
    private List<String> correctWordList, wrongWordList;
    /*private static final String POS = "pos";
    private static final String SCIENCE_QUESTION = "scienceQuestion";*/

    private int imgCnt = 0, textCnt = 0, index = 0;
    // private ScienceQuestion scienceQuestion;
    private String readingContentPath, contentPath, contentTitle, StudentID, resId;
    private boolean onSdCard;
    private List<ScienceQuestion> dataList;
    private List<ScienceQuestion> selectedFive;

    private int totalWordCount, learntWordCount;
    private boolean isTest = false;
    private Context context;

    public McqFillInTheBlanksFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            contentPath = getArguments().getString("contentPath");
            StudentID = getArguments().getString("StudentID");
            resId = getArguments().getString("resId");
            contentTitle = getArguments().getString("contentName");
            onSdCard = getArguments().getBoolean("onSdCard", false);
           /* contentPath = "multiple_choice_que";
            resId = "1212";
            StudentID = FC_Constants.currentStudentID;
            contentTitle = "b";
            onSdCard = true;*/
            if (onSdCard)
                readingContentPath = ApplicationClass.contentSDPath + "/.FCA/English/Game/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + "/.FCA/English/Game/" + contentPath + "/";

            getData();
        }
    }

    private void getData() {
        try {
            InputStream is = new FileInputStream(readingContentPath + "multiple_choice_que.json");
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

    private void getDataList() {
        try {
            selectedFive = new ArrayList<ScienceQuestion>();
            perc = getPercentage();
            Collections.shuffle(dataList);
            for (int i = 0; i < dataList.size(); i++) {
                if (perc < 95) {
                    if (!checkWord("" + dataList.get(i).getAnswer()))
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
        return inflater.inflate(R.layout.layout_mcq_fill_in_the_blanks_with_options_row, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        question.setMovementMethod(new ScrollingMovementMethod());
        setMcqsQuestion();
    }

    public void setMcqsQuestion() {
        if (selectedFive != null & selectedFive.size()>0) {
            options = new ArrayList<>();
            question.setText(selectedFive.get(index).getQuestion());
            if (!selectedFive.get(index).getPhotourl().equalsIgnoreCase("")) {
                questionImage.setVisibility(View.VISIBLE);
//            if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {

                String fileName = selectedFive.get(index).getPhotourl();
                final String localPath = readingContentPath + "/" + fileName;

                String path = selectedFive.get(index).getPhotourl();
                String[] imgPath = path.split("\\.");
                int len;
                if (imgPath.length > 0)
                    len = imgPath.length - 1;
                else len = 0;
                if (imgPath[len].equalsIgnoreCase("gif")) {
                    try {
                        InputStream gif;
                   /* if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
                        Glide.with(getActivity()).asGif()
                                .load(path)
                                .apply(new RequestOptions()
                                        .placeholder(Drawable.createFromPath(localPath)))
                                .into(questionImage);
                    } else {*/
                        gif = new FileInputStream(localPath);
                        questionImage.setVisibility(View.GONE);
                        questionGif.setVisibility(View.VISIBLE);
                        questionGif.setGifResource(gif);
                        //  }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

           /*     Glide.with(getActivity()).asGif()
                        .load(path)
                        .apply(new RequestOptions()
                                .placeholder(Drawable.createFromPath(localPath)))
                        .into(questionImage);*/
//                    zoomImg.setVisibility(View.VISIBLE);
                } else {
                    Glide.with(getActivity())
                            .load(path)
                            .apply(new RequestOptions()
                                    .placeholder(Drawable.createFromPath(localPath)))
                            .into(questionImage);
                }

            } else questionImage.setVisibility(View.GONE);

            options.clear();
            options = selectedFive.get(index).getLstquestionchoice();
            imgCnt = 0;
            textCnt = 0;
            if (options != null) {
                radioGroupMcq.removeAllViews();
                gridMcq.removeAllViews();


                for (int r = 0; r < options.size(); r++) {

                    String ans = "$";
                    if (!selectedFive.get(index).getUserAnswer().equalsIgnoreCase(""))
                        ans = selectedFive.get(index).getUserAnswer();


                    /* if (options.get(r).getChoiceurl().equalsIgnoreCase("")) {*/
                    radioGroupMcq.setVisibility(View.VISIBLE);
                    gridMcq.setVisibility(View.GONE);

                    final View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_mcq_radio_item, radioGroupMcq, false);
                    final RadioButton radioButton = (RadioButton) view;
                    //  radioButton.setButtonTintList(Assessment_Utility.colorStateList);
                    radioButton.setId(r);
                    radioButton.setElevation(3);


                    radioButton.setText(options.get(r).getSubQues());
                    radioGroupMcq.addView(radioButton);
                    if (ans.equals(options.get(r).getSubQues())) {
                        radioButton.setChecked(true);
                    } else {
                        radioButton.setChecked(false);
                    }


                }
            }
            radioGroupMcq.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    //((RadioButton) radioGroupMcq.getChildAt(checkedId)).setChecked(true);
                    RadioButton rb = group.findViewById(checkedId);
                    if (rb != null) {
                        rb.setChecked(true);
                        //   rb.setTextColor(Assessment_Utility.selectedColor);
                    }

                    for (int i = 0; i < group.getChildCount(); i++) {
                        if ((group.getChildAt(i)).getId() == checkedId) {
                            selectedFive.get(index).setUserAnswer(options.get(i).getSubQues());
                            break;
                        }
                    }
                }
            });
        }else {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        }
        if (index == 0) {
            previous.setVisibility(View.INVISIBLE);
        } else {
            previous.setVisibility(View.VISIBLE);
        }
        if (index == (selectedFive.size() - 1)) {
            submitBtn.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
        } else {
            submitBtn.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.previous)
    public void onPreviousClick() {
        if (index > 0) {
            index--;
            setMcqsQuestion();
        }
    }

    @OnClick(R.id.next)
    public void onNextClick() {
        if (index < (selectedFive.size()-1)) {
            index++;
            setMcqsQuestion();
        }
    }

    @OnClick(R.id.submitBtn)
    public void onsubmitBtnClick() {
        // selectedFive.toString();
        //   Log.d("tt", "aa");
        if (selectedFive != null)
            addLearntWords(selectedFive);
        GameConstatnts.playGameNext(getActivity());
        /*Bundle bundle = GameConstatnts.findGameData("110");
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new FillInTheBlanksFragment(), R.id.RL_CPA,
                    bundle, FillInTheBlanksFragment.class.getSimpleName());
        }*/

    }

    public void addLearntWords(List<ScienceQuestion> selectedAnsList) {
        int correctCnt = 0;
        correctWordList = new ArrayList<>();
        wrongWordList = new ArrayList<>();
        if (selectedAnsList != null && !selectedAnsList.isEmpty()) {
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (checkAnswer(selectedAnsList.get(i))) {
                    correctCnt++;
                    KeyWords keyWords = new KeyWords();
                    keyWords.setResourceId(resId);
                    keyWords.setSentFlag(0);
                    keyWords.setStudentId(FC_Constants.currentStudentID);
                    String key = selectedAnsList.get(i).getUserAnswer();
                    keyWords.setKeyWord(key);
                    keyWords.setWordType("word");
                    appDatabase.getKeyWordDao().insert(keyWords);
                    correctWordList.add(selectedAnsList.get(i).getUserAnswer());
                    addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid()), GameConstatnts.MULTIPLE_CHOICE_QUE, 10, 10, FC_Utility.getCurrentDateTime(), selectedAnsList.get(i).getUserAnswer());

                } else {
                    if (selectedAnsList.get(i).getUserAnswer() != null && !selectedAnsList.get(i).getUserAnswer().trim().equalsIgnoreCase("")) {
                        wrongWordList.add(selectedAnsList.get(i).getUserAnswer());
                        addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid()), GameConstatnts.MULTIPLE_CHOICE_QUE, 0, 10, FC_Utility.getCurrentDateTime(), selectedAnsList.get(i).getUserAnswer());
                    }
                }
            }
            if (!isTest) {
                showResult(correctWordList, wrongWordList);
            }
        }
        BackupDatabase.backup(getActivity());

    }
    private boolean checkAnswer(ScienceQuestion scienceQuestion) {
        List<ScienceQuestionChoice> optionListlist = scienceQuestion.getLstquestionchoice();
        for (int i = 0; i < optionListlist.size(); i++) {
            if (optionListlist.get(i).getSubQues().equalsIgnoreCase(scienceQuestion.getUserAnswer()) && optionListlist.get(i).getCorrectAnswer().equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }
    private void showResult(List<String> correctWord, List<String> wrongWord) {
        final Dialog dialog = new Dialog(getActivity());
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
}

