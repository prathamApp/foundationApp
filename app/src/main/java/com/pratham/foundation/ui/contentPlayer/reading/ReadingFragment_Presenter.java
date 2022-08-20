package com.pratham.foundation.ui.contentPlayer.reading;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

import android.content.Context;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.List;

@EBean
public class ReadingFragment_Presenter implements ReadingFragment_Contract.ReadingFragmentPresenter {
    private List<ScienceQuestion> dataList;
    private final Context context;
    private String resId;
    private float perc;
    private String jsonName;
    private ScienceQuestion scienceQuestion;
    private int totalWordCount, learntWordCount;
    private String resStartTime;
    private ReadingFragment_Contract.ReadingFragmentView view;

    public ReadingFragment_Presenter(Context context) {
        this.context = context;

    }

    @Override
    public void setView(ReadingFragment_Contract.ReadingFragmentView view,String jsonName, String resId,String resStartTime) {
        this.jsonName = jsonName;
        this.resId = resId;
        this.resStartTime = resStartTime;
        this.view =  view;
    }

    public void getData(String readingContentPath) {
        String text = FC_Utility.loadJSONFromStorage(readingContentPath, jsonName + ".json");
        // List instrumentNames = new ArrayList<>();
        if (text != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ScienceQuestion>>() {
            }.getType();
            dataList = gson.fromJson(text, type);
            getDataList();
        } else {
            Toast.makeText(context, "Data not found", Toast.LENGTH_LONG).show();
        }
    }

    public void getDataList() {
        try {
            perc = getPercentage();
            Collections.shuffle(dataList);
            if (dataList.get(0).getTitle() == null || dataList.get(0).getTitle().isEmpty()) {
                scienceQuestion = dataList.get(0);
            } else {
                for (int i = 0; i < dataList.size(); i++) {
                    if (perc < 95) {
                        if (!checkWord("" + dataList.get(i).getTitle())) {
                            scienceQuestion = dataList.get(i);
                            break;
                        }
                    } else {
                        scienceQuestion = dataList.get(i);
                        break;
                    }
                }
            }
            view.showQuestion(scienceQuestion);
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

    private boolean checkWord(String wordStr) {
        try {
            String word = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId, wordStr);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private int getLearntWordsCount() {
        int count = 0;
        //  count = appDatabase.getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
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

    public void addLearntWords(List<ScienceQuestionChoice> scienceQuestionChoices) {
        int correctCnt = 0;
        if (scienceQuestionChoices != null && checkAttemptedornot(scienceQuestionChoices)) {
            KeyWords keyWords = new KeyWords();
            keyWords.setResourceId(resId);
            keyWords.setSentFlag(0);
            keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            keyWords.setKeyWord(scienceQuestion.getTitle());
            keyWords.setWordType("word");
            AppDatabase.getDatabaseInstance(context).getKeyWordDao().insert(keyWords);
            for (int i = 0; i < scienceQuestionChoices.size(); i++) {
                if (scienceQuestionChoices.get(i).getUserAns() != null && !scienceQuestionChoices.get(i).getUserAns().isEmpty()) {
                    correctCnt++;
                    addScore(GameConstatnts.getInt(scienceQuestion.getQid()), jsonName, 10, 10, resStartTime, scienceQuestionChoices.get(i).toString());
                }
            }
            GameConstatnts.postScoreEvent(scienceQuestionChoices.size(), correctCnt);
            setCompletionPercentage();
            if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                view.showResult();
            }
            BackupDatabase.backup(context);
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) view);
        }
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
            score.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, ""));
            score.setStartDateTime(resStartTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
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

    public boolean checkAttemptedornot(List<ScienceQuestionChoice> selectedAnsList) {
        for (int i = 0; i < selectedAnsList.size(); i++) {
            if (selectedAnsList.get(i).getUserAns() != null && !selectedAnsList.get(i).getUserAns().isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
