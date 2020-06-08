package com.pratham.foundation.ui.contentPlayer.multipleChoiceQuetion;

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

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

@EBean
public class MultipleChoicePresenter implements MultipleChoiceContract.MultipleChoicePresenter {
    private List<ScienceQuestion> dataList;
    private ArrayList<ScienceQuestion> selectedFive;
    private int totalWordCount, learntWordCount;
    private String resId;
    private float perc;
    private MultipleChoiceContract.MultipleChoiceView view;
    private Context context;
    private List<ScienceQuestionChoice> correctWordList, wrongWordList;

    public MultipleChoicePresenter(Context context) {
        this.context = context;
    }

    public void setView(MultipleChoiceContract.MultipleChoiceView multipleChoiceView, String resId){
        this.resId=resId;
        view=multipleChoiceView;
    }

    public void getData(String readingContentPath) {
        try {
            InputStream is = new FileInputStream(readingContentPath + "multiple_choice.json");
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
            for (ScienceQuestion scienceQuestion : selectedFive) {
                ArrayList<ScienceQuestionChoice> list = scienceQuestion.getLstquestionchoice();
                Collections.shuffle(list);
                scienceQuestion.setLstquestionchoice(list);
            }

            //setMcqsQuestion();
            view.setData(selectedFive);
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
    private int getLearntWordsCount() {
        int count = 0;
        // count = appDatabase.getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
        count = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkUniqueWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
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
    public void addLearntWords(ArrayList<ScienceQuestion> selectedAnsList) {
        int correctCnt = 0;
        correctWordList = new ArrayList<>();
        wrongWordList = new ArrayList<>();
        if (selectedAnsList != null && checkAttemptedornot(selectedAnsList)) {
            for (int i = 0; i < selectedAnsList.size(); i++) {
                String ans = getAnswer(selectedAnsList.get(i));
                if (ans != null) {
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

                        addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid().trim()), GameConstatnts.MULTIPLE_CHOICE, 10, 10, selectedAnsList.get(i).getStartTime(), selectedAnsList.get(i).getEndTime(), ans);
                    } else {
                        if (selectedAnsList.get(i).getUserAnswer() != null && !selectedAnsList.get(i).getUserAnswer().trim().equalsIgnoreCase("")) {
                            List<ScienceQuestionChoice> tempOptionList = selectedAnsList.get(i).getLstquestionchoice();
                            for (int k = 0; k < tempOptionList.size(); k++) {
                                if (tempOptionList.get(k).getQid().equalsIgnoreCase(selectedAnsList.get(i).getUserAnswer())) {
                                    wrongWordList.add(tempOptionList.get(k));
                                }
                            }
                            addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid().trim()), GameConstatnts.MULTIPLE_CHOICE, 0, 10, selectedAnsList.get(i).getStartTime(), selectedAnsList.get(i).getEndTime(), ans);
                        }
                    }
                }
            }
            setCompletionPercentage();
            GameConstatnts.postScoreEvent(selectedAnsList.size(),correctCnt);
            BaseActivity.correctSound.start();
            if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                 view.showResult();
            } else {
                GameConstatnts.playGameNext(context, GameConstatnts.FALSE, (OnGameClose) view);
            }
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) view);
        }
        BackupDatabase.backup(context);
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


    private boolean checkAnswer(ScienceQuestion scienceQuestion) {
        List<ScienceQuestionChoice> optionListlist = scienceQuestion.getLstquestionchoice();
        for (int i = 0; i < optionListlist.size(); i++) {
            if (optionListlist.get(i).getQid().equalsIgnoreCase(scienceQuestion.getUserAnswer()) && optionListlist.get(i).getCorrectAnswer().equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }

    private String getAnswer(ScienceQuestion scienceQuestion) {
        List<ScienceQuestionChoice> optionListlist = scienceQuestion.getLstquestionchoice();
        for (int i = 0; i < optionListlist.size(); i++) {
            if (optionListlist.get(i).getQid().equalsIgnoreCase(scienceQuestion.getUserAnswer())) {
                if (optionListlist.get(i).getSubQues() != null && !optionListlist.get(i).getSubQues().isEmpty()) {
                    return optionListlist.get(i).getSubQues();
                } else {
                    return optionListlist.get(i).getSubUrl().replace("Images/", "");
                }
            }
        }
        return null;
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


}
