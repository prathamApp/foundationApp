package com.pratham.foundation.ui.contentPlayer.opposites;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ModalReadingVocabulary;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.database.AppDatabase.appDatabase;


@EBean
public class OppositesPresenter implements OppositesContract.OppositesPresenter {

    OppositesContract.OppositesView oppositesView;

    Context context;
    List<ModalReadingVocabulary> modalReadingVocabulary;
    List<ModalReadingVocabulary> selectedFive;
    int totalWordCount, learntWordCount, index;
    float perc;
    String gameName, resId, resStartTimeGlobal;


    public OppositesPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(OppositesContract.OppositesView oppositesView, String gameName, String resId) {
        this.oppositesView = oppositesView;
        selectedFive = new ArrayList<>();
        this.gameName = gameName;
        this.resId = resId;
        resStartTimeGlobal = FC_Utility.getCurrentDateTime();
    }

    @Background
    @Override
    public void fetchJsonData(String contentPath) {
        try {
            InputStream is = new FileInputStream(contentPath + "Data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONArray jsonObj = new JSONArray(jsonStr);
            Gson gson = new Gson();
            Type type = new TypeToken<List<ModalReadingVocabulary>>() {
            }.getType();

            modalReadingVocabulary = gson.fromJson(jsonObj.toString(), type);
            getDataList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void getDataList() {
        try {
            perc = getPercentage();
            selectedFive.clear();
            Collections.shuffle(modalReadingVocabulary);
            for (int i = 0; i < modalReadingVocabulary.size(); i++) {
                if (perc < 95) {
                    if (!checkWord("" + modalReadingVocabulary.get(i).getConvoTitle()))
                        selectedFive.add(modalReadingVocabulary.get(i));
                } else {
                    selectedFive.add(modalReadingVocabulary.get(i));
                }
                if (selectedFive.size() >= 5) {
                    break;
                }
            }
            index = 0;
            Collections.shuffle(selectedFive);
            UpdateUi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void showPrevious() {
        try {
            if (index > 0) {
                index--;
            }
            UpdateUi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void showNext() {
        try {
            if (index < 4) {
                index++;
                UpdateUi();
            } else {
                oppositesView.showWordNextDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void revise() {
        index = 0;
        UpdateUi();
    }

    @Background
    public void UpdateUi() {
        try {
            ModalReadingVocabulary modalReadingVocabulary;
            modalReadingVocabulary = selectedFive.get(index);
            addLearntWords(modalReadingVocabulary);
            String resStartTime = FC_Utility.getCurrentDateTime();
            addScore(Integer.parseInt(modalReadingVocabulary.getResourceId()), modalReadingVocabulary.getConvoTitle(), 10, 10, resStartTime, gameName);
            if (index == 0) {
                oppositesView.hidePrevious();
            } else {
                oppositesView.showPrevious();
            }
            oppositesView.loadUi(modalReadingVocabulary);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void setCompletionPercentage() {
        try {
            totalWordCount = modalReadingVocabulary.size();
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

    public float getPercentage() {
        float perc = 0f;
        try {
            totalWordCount = modalReadingVocabulary.size();
            learntWordCount = getLearntWordsCount();
            String Label = "resourceProgress";
            if (learntWordCount > 0) {
                perc = ((float) learntWordCount / (float) totalWordCount) * 100;
                return perc;
            } else
                return 0f;
        } catch (Exception e) {
            return 0f;
        }
    }


   /* public void addExitScore(float perc, int scoredMarks, int totalMarks, String resStartTime, String Label) {
        try {
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(resId);
            score.setQuestionId(0);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setStartDateTime(resStartTime);
            score.setDeviceID("" + perc);
            score.setEndDateTime(COSApplication.getCurrentDateTime());
            score.setLevel(0);
            score.setLabel("" + Label);
            score.setSentFlag(0);
            appDatabase.getScoreDao().insert(score);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    private int getLearntWordsCount() {
        int count = 0;
        count = appDatabase.getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
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

    private void addLearntWords(ModalReadingVocabulary modalReadingVocabulary) {
        KeyWords learntWords = new KeyWords();
        learntWords.setResourceId(resId);
        learntWords.setSentFlag(0);
        learntWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
        learntWords.setKeyWord(modalReadingVocabulary.getConvoTitle());
        learntWords.setWordType("word");
        learntWords.setTopic(gameName);
        appDatabase.getKeyWordDao().insert(learntWords);
        BackupDatabase.backup(context);
    }

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
            score.setStartDateTime(resStartTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(4);
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

}