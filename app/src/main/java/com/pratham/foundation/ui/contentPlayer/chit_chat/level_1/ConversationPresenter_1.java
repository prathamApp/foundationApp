package com.pratham.foundation.ui.contentPlayer.chit_chat.level_1;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

import android.content.Context;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.Message;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@EBean
public class ConversationPresenter_1 implements ConversationContract_1.ConversationPresenter_1 {

    private final Context context;
    private ConversationContract_1.ConversationView_1 conversationView_1;
    private String resId;
    private String convoTitle;
    int questionID;
    private String gameName,resStartTime;
    private JSONArray returnStoryNavigate = null;

    @Override
    public void setView(ConversationContract_1.ConversationView_1 ConversationView,String resId,String gameName,String resStartTime) {
        this.conversationView_1 = ConversationView;
        this.resId = resId;
        this.gameName = gameName;
        this.resStartTime = resStartTime;
    }

    public ConversationPresenter_1(Context context) {
        this.context = context;
    }

    @Override
    public void fetchStory(String convoPath) {
       // JSONArray levelJSONArray;
        try {
            InputStream is = new FileInputStream(convoPath + "Data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONObject jsonObj = new JSONObject(jsonStr);
            convoTitle=jsonObj.getString("convoTitle");
            questionID=jsonObj.getInt("nodeId");
            returnStoryNavigate = jsonObj.getJSONArray("convoList");
        } catch (Exception e) {
            e.printStackTrace();
        }
        conversationView_1.setConvoJson(returnStoryNavigate,convoTitle);
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

  /*  @Background
    @Override
    public void addCompletion(float perc) {
        try {
            ContentProgress contentProgress = new ContentProgress();
            contentProgress.setProgressPercentage("" + perc);
            contentProgress.setResourceId("" +resId);
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
    }*/

    // private String startTime;

   /* @Override
    public void setStartTime(String currentDateTime) {
        startTime = currentDateTime;
    }*/

    public void setCompletionPercentage(int totalWordCount, int learntWordCount) {
        float perc;
        try {
            // totalWordCount = quetionModelList.size();
            // learntWordCount = getLearntWordsCount();
            String Label = "resourceProgress";
            if (learntWordCount > 0) {
                perc = ((float) (learntWordCount/2) / (float) totalWordCount) * 100;
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

    public void addLearntWords(List<Message> messageList) {
        if (messageList != null && messageList.size() > 0) {
            List<KeyWords> learntWords = new ArrayList<>();
            KeyWords keyWords = new KeyWords();
            keyWords.setResourceId(resId);
            keyWords.setSentFlag(0);
            keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            keyWords.setKeyWord(convoTitle);
            keyWords.setWordType("word");
            learntWords.add(keyWords);
            AppDatabase.getDatabaseInstance(context).getKeyWordDao().insertAllWord(learntWords);
            //setCompletionPercentage();
            setCompletionPercentage(returnStoryNavigate.length(), messageList.size());
            //addCompletion(100);
            GameConstatnts.postScoreEvent(1, 1);
            BaseActivity.correctSound.start();
            int perc = (int) (((float) (messageList.size()/2) / (float) returnStoryNavigate.length()) * 10);
            addScore(0, "", perc, 10, resStartTime, FC_Utility.getCurrentDateTime(),convoTitle , resId, true);
//            GameConstatnts.playGameNext(context, GameConstatnts.FALSE, (OnGameClose) conversationView_1);
        } else {
       //     GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) conversationView_1);
        }
        BackupDatabase.backup(context);
    }

    @Background
    @Override
    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label, String resId, boolean addInAssessment) {
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

            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test) && addInAssessment) {
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