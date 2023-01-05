package com.pratham.foundation.ui.contentPlayer.chit_chat.level_2;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_FOLDER_NAME;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX_2;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

import android.content.Context;
import android.util.Log;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;


@EBean
public class ConversationPresenter_2 implements ConversationContract_2.ConversationPresenter {

    Context context;
    ConversationContract_2.ConversationView conversationView;

    @Override
    public void setView(ConversationContract_2.ConversationView ConversationView) {
        this.conversationView = ConversationView;
    }

    public ConversationPresenter_2(Context context) {
        this.context = context;
    }

    @Background
    @Override
    public void fetchStory(String convoPath) {
        String tittle;
        JSONArray returnStoryNavigate = null, levelJSONArray;
        try {
            InputStream is = new FileInputStream(convoPath + "Data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONObject jsonObj = new JSONObject(jsonStr);
            returnStoryNavigate = jsonObj.getJSONArray("convoList");
            tittle = jsonObj.getString("convoTitle");
            conversationView.setConvoJson(returnStoryNavigate, tittle);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    boolean[] correctArr;

    @Override
    public void setCorrectArray(int arrlength) {
        correctArr = new boolean[arrlength];
    }

    @Background
    @Override
    public void sttResultProcess(ArrayList<String> sttServerResult, String answer) {

        String[] splitQues;
        if (FastSave.getInstance().getString(CURRENT_FOLDER_NAME, "").equalsIgnoreCase("English"))
            splitQues = answer.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        else {
            String answer2 = answer.replaceAll(STT_REGEX_2, "");
            splitQues = answer2 .split(" ");
        }
        String words = " ";

        try {
            for (int k = 0; k < sttServerResult.size(); k++) {
                String sttResult = sttServerResult.get(k);
                String[] splitRes;
                if (FastSave.getInstance().getString(CURRENT_FOLDER_NAME, "").equalsIgnoreCase("English"))
                    splitRes = sttResult.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
                else {
                    sttResult = sttResult.replaceAll(STT_REGEX, "");
                    splitRes = sttResult.split(" ");
                }
                for (int j = 0; j < splitRes.length; j++) {
                    for (int i = 0; i < splitQues.length; i++) {
                        if (splitRes[j].equalsIgnoreCase(splitQues[i]) && !correctArr[i]) {
                            correctArr[i] = true;
                            words = words + splitQues[i] + ",";
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        conversationView.setAnsCorrect(correctArr);
        int correctCnt = setBooleanGetCounter();
        addScore(0, "Words:" + words, correctCnt, correctArr.length, " Convo ");
        float perc = getPercentage();

        Log.d("Punctu", "onResults: " + perc);
        if (perc >= 50)
            conversationView.sendClikChanger(1);
        if (perc >= 75) {
            conversationView.submitAns(splitQues);
        } else {
            conversationView.clearMonkAnimation();
        }
    }

    private int setBooleanGetCounter() {
        int counter = 0;
        try {
            for (int x = 0; x < correctArr.length; x++)
                if (correctArr[x])
                    counter++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return counter;
    }

    @Override
    public float getPercentage() {
        int counter = 0;
        float perc = 0f;
        try {
            for (int x = 0; x < correctArr.length; x++)
                if (correctArr[x])
                    counter++;
            if (counter > 0)
                perc = ((float) counter / (float) correctArr.length) * 100;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return perc;
    }

    @Background
    @Override
    public void addCompletion(float perc) {
        try {
            ContentProgress contentProgress = new ContentProgress();
            contentProgress.setProgressPercentage("" + perc);
            contentProgress.setResourceId("" + contentId);
            contentProgress.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            contentProgress.setStudentId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("resourceProgress");
            contentProgress.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String startTime;

    @Override
    public void setStartTime(String currentDateTime) {
        startTime = currentDateTime;
    }

    private String contentId;

    @Override
    public void setContentId(String currentContentId) {
        contentId = currentContentId;
    }

    @Background
    @Override
    public void addScore(final int wID, final String Word, final int scoredMarks, final int totalMarks, final String Label) {
/*
        _addScore(wID, Word, scoredMarks, totalMarks, Label);
    }

    @Background
    public void _addScore(final int wID, final String Word, final int scoredMarks, final int totalMarks, final String Label) {
*/
        try {
            String deviceId = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(contentId);
            score.setQuestionId(wID);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, ""));
            score.setStartDateTime(startTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(0);
            score.setSentFlag(0);
            score.setLabel(Word + " - " + Label);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);

            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                Assessment assessment = new Assessment();
                assessment.setResourceIDa(contentId);
                assessment.setSessionIDa(FastSave.getInstance().getString(FC_Constants.ASSESSMENT_SESSION, ""));
                assessment.setSessionIDm(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                assessment.setQuestionIda(wID);
                assessment.setScoredMarksa(scoredMarks);
                assessment.setTotalMarksa(totalMarks);
                assessment.setStudentIDa(FastSave.getInstance().getString(FC_Constants.CURRENT_ASSESSMENT_STUDENT_ID, ""));
                assessment.setStartDateTimea(startTime);
                assessment.setDeviceIDa(deviceId.equals(null) ? "0000" : deviceId);
                assessment.setEndDateTime(FC_Utility.getCurrentDateTime());
                assessment.setLevela(FC_Constants.currentLevel);
                assessment.setLabel("test: " + Label);
                assessment.setSentFlag(0);
                AppDatabase.getDatabaseInstance(context).getAssessmentDao().insert(assessment);
            }
            BackupDatabase.backup(context);
        } catch (Exception e) {
            BackupDatabase.backup(context);
            e.printStackTrace();
        }
    }


    public void addExtraScoreEntry(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label, String resId) {
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
            score.setLabel(Label);
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);

            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}