package com.pratham.foundation.ui.contentPlayer.keywords_identification;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.modalclasses.ScienceQuestion;
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
    private ScienceQuestion questionModel;
    private KeywordsIdentificationContract.KeywordsView viewKeywords;
    private Context context;
    private float perc;
    private List<ScienceQuestion> quetionModelList;
    private int totalWordCount, learntWordCount;
    private String gameName, resId, contentTitle, readingContentPath, resStartTime;
    private List<String> correctWordList, wrongWordList;
    private boolean isTest = false;

    public KeywordsIdentificationPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(KeywordsIdentificationContract.KeywordsView viewKeywords, String resId, String readingContentPath) {
        this.viewKeywords = viewKeywords;
        this.resId = resId;
        this.readingContentPath = readingContentPath;
    }

    @Override
    public void getData() {
        String text = FC_Utility.loadJSONFromStorage(readingContentPath, "IKWAndroid.json");
        if (text != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ScienceQuestion>>() {
            }.getType();
            quetionModelList = gson.fromJson(text, type);
            getDataList();
           // setCompletionPercentage();
        } else {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
        }
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
            contentProgress.setSessionId("" + FC_Constants.currentSession);
            contentProgress.setStudentId("" + FC_Constants.currentStudentID);
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
       // count = appDatabase.getKeyWordDao().checkWordCount(FC_Constants.currentStudentID, resId);
        count = appDatabase.getKeyWordDao().checkUniqueWordCount(FC_Constants.currentStudentID, resId);
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

    public void addLearntWords(List<ScienceQuestionChoice> selectedAnsList) {
        correctWordList = new ArrayList<>();
        wrongWordList = new ArrayList<>();
        int scoredMarks = (int) checkAnswer(selectedAnsList);
        if (selectedAnsList != null && !selectedAnsList.isEmpty()) {

            KeyWords keyWords = new KeyWords();
            keyWords.setResourceId(resId);
            keyWords.setSentFlag(0);
            keyWords.setStudentId(FC_Constants.currentStudentID);
            String key = questionModel.getTitle();
            keyWords.setKeyWord(key);
            keyWords.setWordType("word");
            appDatabase.getKeyWordDao().insert(keyWords);

            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (checkAnswerNew(questionModel.getLstquestionchoice(), selectedAnsList.get(i).getSubQues())) {

                    addScore(GameConstatnts.getInt(questionModel.getQid()), GameConstatnts.KEYWORD_IDENTIFICATION, 10, 10,selectedAnsList.get(i).getStartTime(),selectedAnsList.get(i).getEndTime(), selectedAnsList.get(i).getSubQues());

                }else {
                    addScore(GameConstatnts.getInt(questionModel.getQid()), GameConstatnts.KEYWORD_IDENTIFICATION, 0, 10, selectedAnsList.get(i).getStartTime(),selectedAnsList.get(i).getEndTime(), selectedAnsList.get(i).getSubQues());
                }
            }
            setCompletionPercentage();
            if (!FC_Constants.isTest) {
                viewKeywords.showResult(correctWordList, wrongWordList);
            }
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) viewKeywords);
        }
        BackupDatabase.backup(context);

    }

    private boolean checkAnswerNew(List<ScienceQuestionChoice> optionListlist, String word) {
        for (int i = 0; i < optionListlist.size(); i++) {
            if (optionListlist.get(i).getSubQues().replaceAll("\\p{Punct}","").trim().equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime,String resEndTime, String Label) {
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
            score.setEndDateTime(resEndTime);
            score.setLevel(FC_Constants.currentLevel);
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

    public float checkAnswer(List<ScienceQuestionChoice> selectedAnsList) {
        if (questionModel != null) {
            int correctCnt = 0;
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (checkAnswerNew(questionModel.getLstquestionchoice(), selectedAnsList.get(i).getSubQues())) {
                    correctCnt++;
                    correctWordList.add(selectedAnsList.get(i).getSubQues());
                } else {
                    wrongWordList.add(selectedAnsList.get(i).getSubQues());
                }
            }
            return 10 * correctCnt / questionModel.getLstquestionchoice().size();
        }
        return 0;
    }
}

