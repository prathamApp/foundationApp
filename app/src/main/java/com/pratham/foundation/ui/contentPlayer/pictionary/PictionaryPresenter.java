package com.pratham.foundation.ui.contentPlayer.pictionary;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

@EBean
public class PictionaryPresenter implements PictionaryContract.PictionaryPresenter {
    private PictionaryContract.PictionaryView pictionaryView;
    private String resId;
    private List<ScienceQuestion> dataList;
    private ArrayList<ScienceQuestion> selectedFive;
    private int totalWordCount, learntWordCount;
    private float perc;
    private List<ScienceQuestionChoice> correctWordList, wrongWordList;
    private Context context;
    private String readingContentPath;
    public PictionaryPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(PictionaryContract.PictionaryView pictionaryView, String resId) {
        this.pictionaryView = pictionaryView;
        this.resId = resId;
    }

    public void getData(String readingContentPath) {
        try {
            this.readingContentPath=readingContentPath;
            InputStream is = new FileInputStream(readingContentPath + "ShowMeAndroid.json");
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
                /*if (perc < 95) {
                    if (!checkWord("" + dataList.get(i).getAnswer()))
                        selectedFive.add(dataList.get(i));
                } else {
                    selectedFive.add(dataList.get(i));
                }
                if (selectedFive.size() >= 5) {
                    break;
                }*/
                selectedFive.add(dataList.get(i));
            }
            Collections.shuffle(selectedFive);
            for (ScienceQuestion scienceQuestion : selectedFive) {
                ArrayList<ScienceQuestionChoice> list = scienceQuestion.getLstquestionchoice();
                Collections.shuffle(list);
                scienceQuestion.setLstquestionchoice(list);
            }
            pictionaryView.setData(selectedFive);
            //setMcqsQuestion(selectedFive);
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
        // count = appDatabase.getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
        count = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkUniqueWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
        return count;
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
            AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addLearntWords(ArrayList<ScienceQuestion> selectedAnsList) {
        int correctCnt = 0;
        correctWordList = new ArrayList<>();
        wrongWordList = new ArrayList<>();
        if (selectedAnsList != null && checkAttemptedornot(selectedAnsList)) {
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (checkAnswer(selectedAnsList.get(i))) {
                    correctCnt++;
                    KeyWords keyWords = new KeyWords();
                    keyWords.setResourceId(resId);
                    keyWords.setSentFlag(0);
                    keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                    String key = selectedAnsList.get(i).getQuestion();
                    keyWords.setKeyWord(key);
                    keyWords.setWordType("word");
                    AppDatabase.getDatabaseInstance(context).getKeyWordDao().insert(keyWords);
                    List<ScienceQuestionChoice> tempOptionList = selectedAnsList.get(i).getLstquestionchoice();
                    for (int k = 0; k < tempOptionList.size(); k++) {
                        if (tempOptionList.get(k).getQid().equalsIgnoreCase(selectedAnsList.get(i).getUserAnswer())) {
                            correctWordList.add(tempOptionList.get(k));
                        }
                    }

                    addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid().trim()), GameConstatnts.SHOW_ME_ANDROID, 10, 10, selectedAnsList.get(i).getStartTime(), selectedAnsList.get(i).getEndTime(), selectedAnsList.get(i).getUserAnswer());
                } else {
                    if (selectedAnsList.get(i).getUserAnswer() != null && !selectedAnsList.get(i).getUserAnswer().trim().equalsIgnoreCase("")) {
                        List<ScienceQuestionChoice> tempOptionList = selectedAnsList.get(i).getLstquestionchoice();
                        for (int k = 0; k < tempOptionList.size(); k++) {
                            if (tempOptionList.get(k).getQid().equalsIgnoreCase(selectedAnsList.get(i).getUserAnswer())) {
                                wrongWordList.add(tempOptionList.get(k));
                            }
                        }
                        addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid().trim()), GameConstatnts.SHOW_ME_ANDROID, 0, 10, selectedAnsList.get(i).getStartTime(), selectedAnsList.get(i).getEndTime(), selectedAnsList.get(i).getUserAnswer());
                    }
                }
            }
            GameConstatnts.postScoreEvent(selectedAnsList.size(), correctCnt);
            BaseActivity.correctSound.start();
            setCompletionPercentage();
            if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                 pictionaryView.showResult();
            } else {
                GameConstatnts.playGameNext(context, GameConstatnts.FALSE, (OnGameClose) pictionaryView);
            }
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) pictionaryView);
        }
        BackupDatabase.backup(context);
    }

    private boolean checkAttemptedornot(List<ScienceQuestion> selectedAnsList) {
        if (selectedAnsList != null) {
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (selectedAnsList.get(i).getUserAnswer() != null && !selectedAnsList.get(i).getUserAnswer().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean checkAnswer(ScienceQuestion scienceQuestion) {
        List<ScienceQuestionChoice> optionListlist = scienceQuestion.getLstquestionchoice();
        for (int i = 0; i < optionListlist.size(); i++) {
            if (optionListlist.get(i).getQid().equalsIgnoreCase(scienceQuestion.getUserAnswer()) && optionListlist.get(i).getCorrectAnswer().equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label) {
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
            score.setEndDateTime(resEndTime);
            score.setLevel(FC_Constants.currentLevel);
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
                assessment.setEndDateTime(resEndTime);
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

}
