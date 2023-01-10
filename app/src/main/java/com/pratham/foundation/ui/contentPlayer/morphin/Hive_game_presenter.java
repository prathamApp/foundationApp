package com.pratham.foundation.ui.contentPlayer.morphin;

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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@EBean
public class Hive_game_presenter implements Hive_game_contract.Hive_game_presenter {

    private Hive_game_contract.Hive_game_view view;
    private List<ScienceQuestion> questionModel = null;
    private final Context context;
    private String resId;
    private List<ScienceQuestion> quetionModelList;
    private int totalWordCount, learntWordCount;
    private float perc;

    public Hive_game_presenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(Hive_game_contract.Hive_game_view hive_game_view, String contentTitle, String resId) {
        this.view = hive_game_view;
        this.resId = resId;
    }

    @Override
    public void getData(String readingContentPath) {
        //get data
        String text = FC_Utility.loadJSONFromStorage(readingContentPath, "hive_game.json");
        // List instrumentNames = new ArrayList<>();
        if (text != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ScienceQuestion>>() {
            }.getType();
            quetionModelList = gson.fromJson(text, type);
            //  quetionAnsList = quetionModelList.get(0).getKeywords();
            getDataList();
            //setCompletionPercentage();
        } else {
            view.onDataNotFound();
        }
    }

    public void getDataList() {
        try {
            questionModel = new ArrayList<>();
            perc=getPercentage();
            for (int i = 0; i < quetionModelList.size(); i++) {
                if (perc < 95) {
                    if (!checkWord("" + quetionModelList.get(i).getTitle())) {
                        questionModel.add(quetionModelList.get(i));
                        break;
                    }
                } else {
                    questionModel.add(quetionModelList.get(i));
                }
                if (questionModel != null && questionModel.size() > 1) {
                    break;
                }
            }
            view.loadQuestion(questionModel);
        } catch (Exception e) {
            e.printStackTrace();
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
            contentProgress.setStudentId("" + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ?"NA"
                    :FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "")));
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("" + label);
            contentProgress.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private float getPercentage() {
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
        //count = appDatabase.getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"), resId);
        count = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkUniqueWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"), resId);
        return count;
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"), resId, wordStr);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void addLearntWords(ScienceQuestion quetionModel, List<ScienceQuestionChoice> selectedAnsList) {
        //  correctWordList = new ArrayList<>();
        //  wrongWordList = new ArrayList<>();
        // int scoredMarks = (int) checkAnswer(keywordmapping.getLstquestionchoice(), selectedAnsList);
        if (selectedAnsList != null && checkIsAttempted(selectedAnsList)) {
            KeyWords keyWords = new KeyWords();
            keyWords.setResourceId(resId);
            keyWords.setSentFlag(0);
            keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"));
            String key = quetionModel.getTitle();
            keyWords.setKeyWord(key);
            keyWords.setWordType("word");
            keyWords.setTopic("");
            AppDatabase.getDatabaseInstance(context).getKeyWordDao().insert(keyWords);
            setCompletionPercentage();
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (selectedAnsList.get(i).getUserAns() != null && !selectedAnsList.get(i).getUserAns().isEmpty()) {
                    if (checkAnswerNew(selectedAnsList.get(i))) {
                        selectedAnsList.get(i).setTrue(true);
                        addScore(GameConstatnts.getInt(quetionModel.getQid()), GameConstatnts.HIVELAYOUT_GAME, 10, 10, selectedAnsList.get(i).getStartTime(), selectedAnsList.get(i).getEndTime(), selectedAnsList.get(i).toString());
                    } else {
                        selectedAnsList.get(i).setTrue(false);
                        addScore(GameConstatnts.getInt(quetionModel.getQid()), GameConstatnts.HIVELAYOUT_GAME, 0, 10, selectedAnsList.get(i).getStartTime(), selectedAnsList.get(i).getEndTime(), selectedAnsList.get(i).toString());
                    }
                }
            }
            //  totalCount = +totalCount + selectedAnsList.size();
            if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                view.showResult();
            }
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) view);
        }
        BackupDatabase.backup(context);
    }

    private boolean checkAnswerNew(ScienceQuestionChoice scienceQuestionChoice) {
        return scienceQuestionChoice.getCorrectAnswer().equalsIgnoreCase("True") && scienceQuestionChoice.getUserAns() != null && scienceQuestionChoice.getEnglish().equalsIgnoreCase(scienceQuestionChoice.getUserAns());
    }


    public boolean checkIsAttempted(List<ScienceQuestionChoice> selectedAnsList) {
        for (ScienceQuestionChoice scienceQuestionChoice : selectedAnsList) {
            if (scienceQuestionChoice.getUserAns() != null && !scienceQuestionChoice.getUserAns().isEmpty()) {
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
            score.setStudentID(((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ? "NA"
                    : FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA")));
            score.setGroupId(((FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "").equals(null)) ? "NA"
                    : FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "NA")));
            score.setStartDateTime(resStartTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            //score.setEndDateTime(FC_Utility.getCurrentDateTime());
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
