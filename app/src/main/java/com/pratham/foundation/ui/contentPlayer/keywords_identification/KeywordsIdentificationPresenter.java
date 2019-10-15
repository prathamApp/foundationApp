package com.pratham.foundation.ui.contentPlayer.keywords_identification;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.database.AppDatabase.appDatabase;

@EBean
public class KeywordsIdentificationPresenter implements KeywordsIdentificationContract.KeywordsPresenter {
    private QuestionModel questionModel;
    private KeywordsIdentificationContract.KeywordsView viewKeywords;
    private Context context;
    private float perc;
    private List<QuestionModel> quetionModelList;
    private int totalWordCount, learntWordCount;
    private String gameName, resId, contentTitle;
    private List<String> correctWordList, wrongWordList;
    private boolean isTest = false;

    public KeywordsIdentificationPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(KeywordsIdentificationContract.KeywordsView viewKeywords, String resId) {
        this.viewKeywords = viewKeywords;
        this.resId = resId;
    }

    @Override
    public void getData() {

        String text = FC_Utility.loadJSONFromAsset(context, "identifyKeywords.json");
        Gson gson = new Gson();
        Type type = new TypeToken<List<QuestionModel>>() {
        }.getType();
        quetionModelList = gson.fromJson(text, type);
        getDataList();
    }

    public void getDataList() {
        try {
            perc = getPercentage();
            Collections.shuffle(quetionModelList);
            for (int i = 0; i < quetionModelList.size(); i++) {
                if (perc < 95) {
                    if (!checkWord("" + quetionModelList.get(i).getTitle())) {
                        questionModel = quetionModelList.get(i);
                        break;
                    }
                } else {
                    questionModel = quetionModelList.get(i);
                    break;
                }

            }
            viewKeywords.showParagraph(questionModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getPercentage() {
        float perc = 0f;
        try {
            totalWordCount = quetionModelList.size();
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

    public void addLearntWords(List selectedAnsList) {
        correctWordList = new ArrayList<>();
        wrongWordList = new ArrayList<>();
        int scoredMarks = (int) checkAnswer(selectedAnsList);
        if (selectedAnsList != null && !selectedAnsList.isEmpty()) {
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (questionModel.getKeywords().contains(selectedAnsList.get(i))) {
                    KeyWords keyWords = new KeyWords();
                    keyWords.setResourceId(resId);
                    keyWords.setSentFlag(0);
                    keyWords.setStudentId(FC_Constants.currentStudentID);
                    String key=selectedAnsList.get(i).toString();
                    keyWords.setKeyWord(key);
                    keyWords.setWordType("word");
                    appDatabase.getKeyWordDao().insert(keyWords);
                }
            }
            addScore(questionModel.getResourceId(), GameConstatnts.KEYWORD_IDENTIFICATION, scoredMarks, 10, FC_Utility.getCurrentDateTime(), selectedAnsList.toString());
            if (!isTest) {
                viewKeywords.showResult(correctWordList, wrongWordList);
            }
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

    public float checkAnswer(List<String> selectedAnsList) {
        int correctCnt = 0;
        for (int i = 0; i < selectedAnsList.size(); i++) {
            if (questionModel.getKeywords().contains(selectedAnsList.get(i))) {
                correctCnt++;
                correctWordList.add(selectedAnsList.get(i));
            } else {
                wrongWordList.add(selectedAnsList.get(i));
            }
        }
        return 10 * correctCnt / questionModel.getKeywords().size();
    }

}

