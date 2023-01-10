package com.pratham.foundation.ui.contentPlayer.chit_chat.level_3;

import android.content.Context;

import com.google.gson.Gson;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.Message;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@EBean
public class ConversationPresenter_3 implements ConversationContract_3.ConversationPresenter_3 {

    private final Context context;
    private ConversationContract_3.ConversationView_3 conversationView_3;
    private String resId;
    private String convoTitle;
    // private int questionID;
    private String gameName, resStartTime;
    @Override
    public void setView(ConversationContract_3.ConversationView_3 ConversationView, String resId, String gameName, String resStartTime) {
        this.conversationView_3 = ConversationView;
        this.resId=resId;
        this.gameName = gameName;
        this.resStartTime = resStartTime;
    }

    public ConversationPresenter_3(Context context) {
        this.context = context;
    }

    public void addCompletion(float perc) {
        try {
            ContentProgress contentProgress = new ContentProgress();
            contentProgress.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            contentProgress.setStudentId("" + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ?"NA"
                    :FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "")));
            contentProgress.setResourceId("" + resId);
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setProgressPercentage("" + perc);
            contentProgress.setLabel("resourceProgress");
            contentProgress.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void fetchStory(String convoPath) {
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
            //questionID = jsonObj.getInt("nodeId");
            if (convoTitle != null && !convoTitle.isEmpty())
                conversationView_3.setConvoJson(convoTitle, 0);
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

    public void addLearntWords(List<Message> messageList) {
        if (messageList != null && messageList.size() > 0) {
            List<KeyWords> learntWords = new ArrayList<>();
            KeyWords keyWords = new KeyWords();
            keyWords.setResourceId(resId);
            keyWords.setSentFlag(0);
            keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"));
            keyWords.setKeyWord(convoTitle);
            keyWords.setWordType("word");
            learntWords.add(keyWords);
            AppDatabase.getDatabaseInstance(context).getKeyWordDao().insertAllWord(learntWords);
            //setCompletionPercentage();

            Gson gson = new Gson();
            String json = gson.toJson(messageList);
            if (json != null && !json.isEmpty()) {
                String newResId = GameConstatnts.getString(resId, gameName, "" + 0, json, convoTitle, "");
                addScore(0, GameConstatnts.NEW_CHIT_CHAT_3, 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.NEW_CHIT_CHAT_3 + "_" + json, resId, true);
                addScore(FC_Utility.getSubjectNo(), GameConstatnts.NEW_CHIT_CHAT_3, FC_Utility.getSectionCode(), 0, resStartTime, FC_Utility.getCurrentDateTime(), FC_Constants.CHIT_CHAT_LBL, newResId, false);
            }
            addCompletion(100);
            GameConstatnts.postScoreEvent(1, 1);
            BaseActivity.correctSound.start();
            GameConstatnts.playGameNext(context, GameConstatnts.FALSE, (OnGameClose) conversationView_3);
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) conversationView_3);
        }
        BackupDatabase.backup(context);
    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label, String resId, boolean addInAssessment) {
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
            score.setEndDateTime(resEndTime);
            score.setLevel(FC_Constants.currentLevel);
            score.setLabel(Label);
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);

/*            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test) && addInAssessment) {
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
            }*/
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}