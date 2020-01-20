package com.pratham.foundation.ui.contentPlayer.chit_chat.level_3;

import android.content.Context;

import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;

import static com.pratham.foundation.database.AppDatabase.appDatabase;


@EBean
public class ConversationPresenter_3 implements ConversationContract_3.ConversationPresenter_3 {

    Context context;
    ConversationContract_3.ConversationView_3 conversationView_3;
    String resId;

    @Override
    public void setView(ConversationContract_3.ConversationView_3 ConversationView,String resId) {
        this.conversationView_3 = ConversationView;
        this.resId=resId;
    }

    public ConversationPresenter_3(Context context) {
        this.context = context;
    }


    @Background
    @Override
    public void addCompletion(float perc) {
        try {
            ContentProgress contentProgress = new ContentProgress();
            contentProgress.setProgressPercentage("" + perc);
            contentProgress.setResourceId("" + resId);
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

    @Background
    @Override
    public void fetchStory(String convoPath) {
        String convoTitle = null;
        int questionID;
        // JSONArray returnStoryNavigate = null, levelJSONArray;
        try {
            InputStream is = new FileInputStream(convoPath + "Data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONObject jsonObj = new JSONObject(jsonStr);
            convoTitle = jsonObj.getString("convoTitle");
            questionID = jsonObj.getInt("nodeId");
            if (convoTitle != null && !convoTitle.isEmpty())
                conversationView_3.setConvoJson(convoTitle, questionID);
            else {
                conversationView_3.dataNotFound();
            }
            //returnStoryNavigate = jsonObj.getJSONArray("convoList");
        } catch (Exception e) {
            e.printStackTrace();
            conversationView_3.dataNotFound();
        }
        //Toast.makeText(context, "Tittle not found", Toast.LENGTH_SHORT).show();
    }
    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label, String resId, boolean addInAssessment) {
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
            score.setLabel(Label);
            score.setSentFlag(0);
            appDatabase.getScoreDao().insert(score);

            if (FC_Constants.isTest && addInAssessment) {
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

}