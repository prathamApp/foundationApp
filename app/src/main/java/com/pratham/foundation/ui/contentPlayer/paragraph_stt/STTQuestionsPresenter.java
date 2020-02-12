package com.pratham.foundation.ui.contentPlayer.paragraph_stt;

import android.content.Context;

import com.google.gson.Gson;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ParaSttQuestionListModel;
import com.pratham.foundation.modalclasses.ParaSttQuestionModel;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.ui.contentPlayer.paragraph_stt.ParaSttReadingFragment.correctArr;
import static com.pratham.foundation.ui.contentPlayer.paragraph_stt.ParaSttReadingFragment.lineBreakCounter;


@EBean
public class STTQuestionsPresenter implements ParaSttReadingContract.STTQuestionsPresenter {

    private ParaSttReadingContract.STTQuestionsView readingView;

    private Context context;
    ParaSttQuestionModel paraSttQuestionModel;
    List<ParaSttQuestionListModel> paraSttQuestionList;
    public static float[] pagePercentage;
    private int pgNo;
    private String resId, resStartTime;
    private ArrayList<String> remainingResult;


    public STTQuestionsPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(ParaSttReadingContract.STTQuestionsView readingView) {
        this.readingView = readingView;
    }

    @Background
    @Override
    public void setResId(String resourceId) {
        resId = resourceId;
        resStartTime = FC_Utility.getCurrentDateTime();
    }

    @Override
    public void getDataList() {
        try {
            paraSttQuestionList = paraSttQuestionModel.getQuesList();
            readingView.setListData(paraSttQuestionList);
            getPage(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getPage(int pgNo) {
        readingView.showLoader();
        this.pgNo = pgNo;
        readingView.initializeContent(pgNo);
    }

    @Background
    @Override
    public void fetchJsonData(String contentPath, String jsonName) {
        try {
            InputStream is = new FileInputStream(contentPath + "/"+jsonName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONObject jsonObj = new JSONObject(jsonStr);
            Gson gson = new Gson();
            paraSttQuestionModel = gson.fromJson(jsonObj.toString(), ParaSttQuestionModel.class);
            getDataList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSttResultDB(ArrayList<String> stt_Result) {
        String deviceId = appDatabase.getStatusDao().getValue("DeviceId");
        StringBuilder strWord = new StringBuilder("STT_ALL_RESULT - ");
        for (int i = 0; i < stt_Result.size(); i++) {
            strWord.append(stt_Result.get(i)).append(" - ");
            stt_Result.size();
            if (i > 0 && i < 3)
                remainingResult.add(stt_Result.get(i));
        }
        try {
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(resId);
            score.setQuestionId(0);
            score.setScoredMarks(0);
            score.setTotalMarks(0);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setStartDateTime(resStartTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(0);
            score.setLabel("" + strWord);
            score.setSentFlag(0);
            appDatabase.getScoreDao().insert(score);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getPercentage(int count) {
        float perc = 0f;
        try {
            int totLen = correctArr.length - lineBreakCounter;
            if (count > 0)
                perc = ((float) count / (float) totLen) * 100;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return perc;
    }

    @Background
    @Override
    public void addProgress(String[] sttAnswers, String[] sttAnswersTime) {
        float perc = getCompletionPercentage(sttAnswers);
        String Label = "resourceProgress";
        addExitScore(perc, Label);
    }

    private float getCompletionPercentage(String[] sttAnswers) {
        float tot = 0f, perc = 0f;
        try {
            for (int i = 0; i < sttAnswers.length; i++) {
                if (sttAnswers[i].length() > 1)
                    tot++;
            }
            if (tot > 0)
                perc = (tot / pagePercentage.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return perc;
    }

    @Background
    @Override
    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label) {
        try {
            String deviceId = appDatabase.getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(resId);
            score.setQuestionId(wID);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setStartDateTime(resStartTime.equals("NA") ? ""+FC_Utility.getCurrentDateTime() : resStartTime);
            score.setDeviceID(deviceId == null ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(0);
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

    @Background
    @Override
    public void addExitScore(float perc, String Label) {
        try {
            ContentProgress contentProgress = new ContentProgress();
            contentProgress.setProgressPercentage("" + perc);
            contentProgress.setResourceId("" + resId);
            contentProgress.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            contentProgress.setStudentId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("" + Label);
            contentProgress.setSentFlag(0);
            appDatabase.getContentProgressDao().insert(contentProgress);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}