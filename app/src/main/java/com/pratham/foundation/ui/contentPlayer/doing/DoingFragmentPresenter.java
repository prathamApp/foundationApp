package com.pratham.foundation.ui.contentPlayer.doing;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.IMG_PUSH_LBL;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

@EBean
public class DoingFragmentPresenter implements DoingFragmentContract.DoingFragmentPresenter {
    private String jsonName,contentTitle;
    private DoingFragmentContract.DoingFragmentView view;
    private String resId;
    private int totalWordCount, learntWordCount;
    private List<ScienceQuestion> dataList;
    private float perc;
    private ScienceQuestion scienceQuestion;
    private Context context;
    private String readingContentPath;


    public DoingFragmentPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(DoingFragmentContract.DoingFragmentView doingFragmentView, String jsonName, String resId,String contentTitle) {
        this.view = doingFragmentView;
        this.jsonName = jsonName;
        this.resId = resId;
        this.contentTitle = contentTitle;
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
            appDatabase.getContentProgressDao().insert(contentProgress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getData(String readingContentPath) {
        try {
            this.readingContentPath = readingContentPath;
            InputStream is = new FileInputStream(readingContentPath + jsonName + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONArray jsonObj = new JSONArray(jsonStr);

            // List instrumentNames = new ArrayList<>();
            Gson gson = new Gson();
            Type type = new TypeToken<List<ScienceQuestion>>() {
            }.getType();
            dataList = gson.fromJson(jsonObj.toString(), type);
            getDataList();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDataList() {
        try {
            perc = getPercentage();
            Collections.shuffle(dataList);
            for (int i = 0; i < dataList.size(); i++) {
                if (perc < 95) {
                    if (!checkWord("" + dataList.get(i).getTitle()) || dataList.get(i).getTitle().isEmpty()) {
                        scienceQuestion = dataList.get(i);
                        break;
                    }
                } else {
                    scienceQuestion = dataList.get(i);
                    break;
                }
            }
            //view.loadUI(listenAndWrittingModal);
            view.setVideoQuestion(scienceQuestion);
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

    private int getLearntWordsCount() {
        int count = 0;
        // count = appDatabase.getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
        count = appDatabase.getKeyWordDao().checkUniqueWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
        return count;
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = appDatabase.getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId, wordStr);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void addLearntWords(ScienceQuestion questionModel, String imageName) {
        String newResId;
        String queImageName = "";
        if (imageName != null && !imageName.isEmpty()) {
            KeyWords keyWords = new KeyWords();
            keyWords.setResourceId(resId);
            keyWords.setSentFlag(0);
            keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            keyWords.setKeyWord(questionModel.getTitle());
            keyWords.setWordType("word");
            if (questionModel.getPhotourl() != null && !questionModel.getPhotourl().isEmpty()) {
                queImageName = readingContentPath + questionModel.getPhotourl();
            }
            newResId = GameConstatnts.getString(resId, contentTitle, questionModel.getQid(), imageName, questionModel.getQuestion(), queImageName);
            addScore(GameConstatnts.getInt(questionModel.getQid()), jsonName, 0, 0, questionModel.getStartTime(), questionModel.getEndTime(), imageName, resId, true);
            addScore(FC_Utility.getSubjectNo(), jsonName, FC_Utility.getSectionCode(), 0, questionModel.getStartTime(), questionModel.getEndTime(), FC_Constants.IMG_LBL, newResId, false);
            addImageOnly(resId, imageName);
            appDatabase.getKeyWordDao().insert(keyWords);
            setCompletionPercentage();
            //Toast.makeText(context, "inserted successfully", Toast.LENGTH_LONG).show();
            GameConstatnts.postScoreEvent(1,1);
            GameConstatnts.playGameNext(context, GameConstatnts.FALSE, (OnGameClose) view);
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) view);
        }
        BackupDatabase.backup(context);
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
                appDatabase.getAssessmentDao().insert(assessment);
            }
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    public void addImageOnly(String resId, String imageName) {
        try {
            String deviceId = appDatabase.getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(resId);
            score.setQuestionId(0);
            score.setScoredMarks(0);
            score.setTotalMarks(0);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setStartDateTime(imageName);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(FC_Constants.currentLevel);
            score.setLabel(IMG_PUSH_LBL);
            score.setSentFlag(0);
            appDatabase.getScoreDao().insert(score);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
