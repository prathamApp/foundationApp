package com.pratham.foundation.ui.contentPlayer.keywords_mapping;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.database.AppDatabase.appDatabase;

@EBean
public class KeywordMappingPresenterImp implements KeywordMappingContract.KeywordMappingPresenter {
    private Context context;
    // private List<QuetionAns> quetionAnsList;
    private List<ScienceQuestion> quetionModelList;
    private int totalWordCount, learntWordCount;
    private List<ScienceQuestion> selectedFive;
    private float perc;
    private KeywordMappingContract.KeywordMappingView view;
    private String gameName, resId, contentTitle, readingContentPath;
    private List<String> correctWordList, wrongWordList;
    private boolean isTest = false;


    public KeywordMappingPresenterImp(Context context) {
        this.context = context;
    }

    @Override
    public void setView(KeywordMappingContract.KeywordMappingView view, String resId, String readingContentPath) {
        this.resId = resId;
        this.view = view;
        this.readingContentPath = readingContentPath;
    }

    @Override
    public void getData() {
        //get data
        String text = FC_Utility.loadJSONFromStorage(readingContentPath, "chKeywords.json");
        // List instrumentNames = new ArrayList<>();
        Gson gson = new Gson();
        Type type = new TypeToken<List<ScienceQuestion>>() {
        }.getType();
        quetionModelList = gson.fromJson(text, type);
        getDataList();
        //setCompletionPercentage();
    }
    public void setCompletionPercentage() {
        try {
            totalWordCount = quetionModelList.size();
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
  /*  @Override
    public void setView(String contentTitle, String resId) {
        this.resId = resId;
        this.contentTitle = contentTitle;
    }*/

    @Background
    @Override
    public void getDataList() {
        try {
            selectedFive = new ArrayList<ScienceQuestion>();
            perc = getPercentage();
            Collections.shuffle(quetionModelList);
            for (int i = 0; i < quetionModelList.size(); i++) {
                if (perc < 95) {
                    if (!checkWord("" + quetionModelList.get(i).getQuestion()))
                        selectedFive.add(quetionModelList.get(i));
                } else {
                    selectedFive.add(quetionModelList.get(i));
                }
                if (selectedFive.size() >= 1) {
                    break;
                }
            }
            Collections.shuffle(selectedFive);
            view.loadUI(selectedFive);
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
        count = appDatabase.getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
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

    public void addLearntWords(ScienceQuestion keywordmapping, List<ScienceQuestionChoice> selectedAnsList) {
        correctWordList = new ArrayList<>();
        wrongWordList = new ArrayList<>();
        int correctCnt = 0;
       // int scoredMarks = (int) checkAnswer(keywordmapping.getLstquestionchoice(), selectedAnsList);
        if (selectedAnsList != null && !selectedAnsList.isEmpty()) {
            KeyWords keyWords = new KeyWords();
            keyWords.setResourceId(resId);
            keyWords.setSentFlag(0);
            keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            String key = keywordmapping.getQuestion();
            keyWords.setKeyWord(key);
            keyWords.setWordType("word");
            appDatabase.getKeyWordDao().insert(keyWords);
            setCompletionPercentage();
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if ( checkAnswerNew( keywordmapping.getLstquestionchoice(),selectedAnsList.get(i).getSubQues())){
                    correctCnt++;
                    selectedAnsList.get(i).setTrue(true);
                    addScore(GameConstatnts.getInt(keywordmapping.getQid()), GameConstatnts.KEYWORD_MAPPING, 10, 10, selectedAnsList.get(i).getStartTime(),selectedAnsList.get(i).getEndTime(), selectedAnsList.get(i).getSubQues());
                }else {
                    selectedAnsList.get(i).setTrue(false);
                    addScore(GameConstatnts.getInt(keywordmapping.getQid()), GameConstatnts.KEYWORD_MAPPING, 0, 10,selectedAnsList.get(i).getStartTime(),selectedAnsList.get(i).getEndTime(),selectedAnsList.get(i).getSubQues());
                }
            }
            GameConstatnts.postScoreEvent(selectedAnsList.size(),correctCnt);
            if (!FC_Constants.isTest) {
                view.showResult(keywordmapping);
            }else {
                GameConstatnts.playGameNext(context, GameConstatnts.FALSE,  (OnGameClose) view);
            }
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) view);
        }
        BackupDatabase.backup(context);
    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime,String resEndTime, String Label) {
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
            score.setEndDateTime(resEndTime);
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
                assessment.setEndDateTime(resEndTime);
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

    public boolean checkAnswerNew(List<ScienceQuestionChoice> optionListlist, String word) {
        for (int i = 0; i < optionListlist.size(); i++) {
            if (optionListlist.get(i).getSubQues().equalsIgnoreCase(word)&&(optionListlist.get(i).getCorrectAnswer().equalsIgnoreCase("true"))) {
                return true;
            }
        }
        return false;
    }



    public float checkAnswer(List<ScienceQuestionChoice> ansSet,List<ScienceQuestionChoice> selectedAnsList) {
        int correctCnt = 0;
        for (int i = 0; i < selectedAnsList.size(); i++) {
            if (checkAnswerNew(ansSet, selectedAnsList.get(i).getSubQues())) {
                correctCnt++;
                correctWordList.add(selectedAnsList.get(i).getSubQues());
            } else {
                wrongWordList.add(selectedAnsList.get(i).getSubQues());
            }
        }
        return 10 * correctCnt / getCorrectCnt(ansSet);
    }

    private int getCorrectCnt(List<ScienceQuestionChoice> lstquestionchoice) {
        int correctCnt = 0;
        for (int i = 0; i < lstquestionchoice.size(); i++) {
            if (lstquestionchoice.get(i).getCorrectAnswer().equalsIgnoreCase("true")) {
                correctCnt++;
            }
        }
        return correctCnt;
    }
}
