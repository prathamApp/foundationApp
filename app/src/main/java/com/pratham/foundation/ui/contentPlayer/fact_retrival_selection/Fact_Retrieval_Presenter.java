package com.pratham.foundation.ui.contentPlayer.fact_retrival_selection;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

@EBean
public class Fact_Retrieval_Presenter implements Fact_Retrieval_Contract.Fact_retrival_Presenter {

    private ScienceQuestion questionModel;
    private Fact_Retrieval_Contract.Fact_retrival_View viewKeywords;
    private final Context context;
    private float perc;
    private List<ScienceQuestion> quetionModelList;
    private int totalWordCount, learntWordCount;
    private String gameName, resId, readingContentPath, contentTitle;
    private List<String> correctWordList, wrongWordList;

    public Fact_Retrieval_Presenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(Fact_Retrieval_Contract.Fact_retrival_View viewKeywords, String resId, String readingContentPath) {
        this.viewKeywords = viewKeywords;
        this.resId = resId;
        this.readingContentPath = readingContentPath;
    }


    @Override
    public void getData() {

        //String text = FC_Utility.loadJSONFromAsset(context, "fact_retrial_selection.json");


        try {
            InputStream is = new FileInputStream(readingContentPath + "fact_retrieval_click.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONArray jsonObj = new JSONArray(jsonStr);
            Gson gson = new Gson();
            Type type = new TypeToken<List<ScienceQuestion>>() {
            }.getType();
            quetionModelList = gson.fromJson(jsonObj.toString(), type);
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
            Collections.shuffle(quetionModelList);
            for (int i = 0; i < quetionModelList.size(); i++) {
                if (perc < 95) {
                    if (!checkWord("" + quetionModelList.get(i).getTopicid())) {
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
        //count = appDatabase.getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
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
            AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addLearntWords(ArrayList<ScienceQuestionChoice> selectedAnsList) {
        if (selectedAnsList != null && checkAttemptedornot(selectedAnsList)) {
            // correctWordList = new ArrayList<>();
            //  wrongWordList = new ArrayList<>();
              int correctCnt = 0;
            /* int scoredMarks = (int) checkAnswer(selectedAnsList);*/
            int scoredMarks;
            String key = questionModel.getTitle();
            KeyWords keyWords = new KeyWords();
            keyWords.setResourceId(resId);
            keyWords.setSentFlag(0);
            keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            keyWords.setKeyWord(key);
            keyWords.setWordType("word");
            keyWords.setTopic("");
            AppDatabase.getDatabaseInstance(context).getKeyWordDao().insert(keyWords);
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (selectedAnsList.get(i).getUserAns() != null && !selectedAnsList.get(i).getUserAns().isEmpty()) {
                    if (selectedAnsList.get(i).getCorrectAnswer().equalsIgnoreCase(selectedAnsList.get(i).getUserAns())) {
                        scoredMarks = 10;
                        selectedAnsList.get(i).setTrue(true);
                        correctCnt++;
                    } else {
                        scoredMarks = 0;
                        selectedAnsList.get(i).setTrue(false);
                    }
                    addScore(Integer.parseInt(questionModel.getQid()), GameConstatnts.FACT_RETRIAL_CLICK, scoredMarks, 10,selectedAnsList.get(i).getStartTime(),selectedAnsList.get(i).getEndTime(), selectedAnsList.get(i).toString());
                }
            }
            setCompletionPercentage();
            GameConstatnts.postScoreEvent(selectedAnsList.size(),correctCnt);
            if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                viewKeywords.showResult(selectedAnsList);
            } else {
                GameConstatnts.playGameNext(context, GameConstatnts.FALSE, (OnGameClose) viewKeywords);
            }
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) viewKeywords);
        }
        BackupDatabase.backup(context);

    }

    private boolean checkAttemptedornot(List<ScienceQuestionChoice> selectedAnsList) {
        for (int i = 0; i < selectedAnsList.size(); i++) {
            if (selectedAnsList.get(i).getUserAns() != null && !selectedAnsList.get(i).getUserAns().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime,String resEndTime, String Label) {
        try {
            String deviceId = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(resId);
            score.setQuestionId(wID);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, ""));
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

   /* public float checkAnswer(List<String> selectedAnsList) {
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
    }*/

}

