package com.pratham.foundation.ui.contentPlayer.chit_chat.level_1;

import android.content.Context;

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

import static com.pratham.foundation.database.AppDatabase.appDatabase;


@EBean
public class ConversationPresenter_1 implements ConversationContract_1.ConversationPresenter_1 {

    Context context;
    ConversationContract_1.ConversationView_1 conversationView_1;

    @Override
    public void setView(ConversationContract_1.ConversationView_1 ConversationView) {
        this.conversationView_1 = ConversationView;
    }

    public ConversationPresenter_1(Context context) {
        this.context = context;
    }

    @Background
    @Override
    public void fetchStory(String convoPath) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        conversationView_1.setConvoJson(returnStoryNavigate);
    }

    boolean[] correctArr;

    @Override
    public void setCorrectArray(int arrlength) {
        correctArr = new boolean[arrlength];
    }

   /* @Background
    @Override
    public void sttResultProcess(ArrayList<String> sttServerResult, String answer) {

        String[] splitQues;
        if (FastSave.getInstance().getString(CURRENT_FOLDER_NAME, "").equalsIgnoreCase("English"))
            splitQues = answer.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        else {
            String answer2 = answer.replaceAll(STT_REGEX_2, "");
            splitQues = answer2.split(" ");
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
        conversationView_1.setAnsCorrect(correctArr);
        int correctCnt = setBooleanGetCounter();
        addScore(0, "Words:" + words, correctCnt, correctArr.length, " Convo ");
        float perc = getPercentage();

        Log.d("Punctu", "onResults: " + perc);
        if (perc >= 50)
            conversationView_1.sendClikChanger(1);
        if (perc >= 75) {
            conversationView_1.submitAns(splitQues);
        } else {
            conversationView_1.clearMonkAnimation();
        }
    }*/

/*    private int setBooleanGetCounter() {
        int counter = 0;
        try {
            for (int x = 0; x < correctArr.length; x++)
                if (correctArr[x])
                    counter++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return counter;
    }*/

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
            appDatabase.getContentProgressDao().insert(contentProgress);
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
            String deviceId = AppDatabase.appDatabase.getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(contentId);
            score.setQuestionId(wID);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setStartDateTime(startTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(0);
            score.setSentFlag(0);
            score.setLabel(Word + " - " + Label);
            AppDatabase.appDatabase.getScoreDao().insert(score);

            if (FC_Constants.isTest) {
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
                appDatabase.getAssessmentDao().insert(assessment);
            }
            BackupDatabase.backup(context);
        } catch (Exception e) {
            BackupDatabase.backup(context);
            e.printStackTrace();
        }
    }

}