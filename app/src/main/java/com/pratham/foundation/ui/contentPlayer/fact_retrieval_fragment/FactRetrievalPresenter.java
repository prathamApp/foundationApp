package com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.QuetionAns;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.QuestionModel;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.database.AppDatabase.appDatabase;

@EBean
public class FactRetrievalPresenter implements FactRetrievalContract.FactRetrievalPresenter {
    private QuestionModel questionModel;
    private FactRetrievalContract.FactRetrievalView view;
    private Context context;
    private String gameName, resId, contentTitle;
    private float perc;
    //private List<QuetionAns> quetionAnsList;
    private List<QuestionModel> quetionModelList;
    private int totalWordCount, learntWordCount;
    //private List<QuetionAns> selectedFive;

    public FactRetrievalPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(FactRetrievalContract.FactRetrievalView factRetrivalView, String contentTitle, String resId) {
        this.view = factRetrivalView;
        this.resId = resId;
        this.contentTitle = contentTitle;
    }

    @Override
    public void getData(String readingContentPath) {
        //get data
        String text = FC_Utility.loadJSONFromStorage(readingContentPath, "factRetrial.json");
        // List instrumentNames = new ArrayList<>();
        if (text != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<QuestionModel>>() {
            }.getType();
            quetionModelList = gson.fromJson(text, type);
            //  quetionAnsList = quetionModelList.get(0).getKeywords();
            getDataList();
        } else {
            Toast.makeText(context, "Data not found", Toast.LENGTH_LONG).show();
        }

    }

    @Background
    @Override
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
           // selectedFive = questionModel.getKeywords();
           // Collections.shuffle(selectedFive);
            view.showParagraph(questionModel);
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

    public void addLearntWords(List<QuetionAns> selectedAnsList) {
        List<KeyWords> learntWords = new ArrayList<>();
        int scoredMarks;
        KeyWords keyWords = new KeyWords();
        keyWords.setResourceId(resId);
        keyWords.setSentFlag(0);
        keyWords.setStudentId(FC_Constants.currentStudentID);
        keyWords.setKeyWord(questionModel.getTitle());
        keyWords.setWordType("word");
        learntWords.add(keyWords);
        for (int i = 0; i < selectedAnsList.size(); i++) {
            if (selectedAnsList.get(i).getUserAns() != null && !selectedAnsList.get(i).getUserAns().isEmpty()) {
                if (checkAnswer(selectedAnsList.get(i)) > 70) {
                    scoredMarks = 10;
                } else {
                    scoredMarks = 0;
                }
                addScore(0,questionModel.getTitle(), scoredMarks, 10, FC_Utility.getCurrentDateTime(), selectedAnsList.get(i).toString());
            }
        }
        appDatabase.getKeyWordDao().insertAllWord(learntWords);
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

    public float checkAnswer(QuetionAns selectedAnsList) {
        boolean[] correctArr;
        float perc;
        String originalAns = selectedAnsList.getAnswer();
        String regex = "[\\-+.\"^?!@#%&*,:]";
        String quesFinal = originalAns.replaceAll(regex, "");

        String[] originalAnsArr = quesFinal.split(" ");
        String[] userAnsArr = selectedAnsList.getUserAns().replaceAll(regex, "").split(" ");

        if (originalAnsArr.length < userAnsArr.length)
            correctArr = new boolean[userAnsArr.length];
        else correctArr = new boolean[originalAnsArr.length];


        for (int j = 0; j < userAnsArr.length; j++) {
            for (int i = 0; i < originalAnsArr.length; i++) {
                if (userAnsArr[j].equalsIgnoreCase(originalAnsArr[i])) {
                    correctArr[i] = true;
                    break;
                }
            }
        }

        int correctCnt = 0;
        for (int x = 0; x < correctArr.length; x++) {
            if (correctArr[x])
                correctCnt++;
        }
        perc = ((float) correctCnt / (float) correctArr.length) * 100;
        return perc;
    }
}
