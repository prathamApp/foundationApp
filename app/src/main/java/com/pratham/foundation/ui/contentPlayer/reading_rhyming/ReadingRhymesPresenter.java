package com.pratham.foundation.ui.contentPlayer.reading_rhyming;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ModalRhymingWords;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EBean
public class ReadingRhymesPresenter implements ReadingRhymesContract.ReadingRhymesPresenter {

    Context context;
    ReadingRhymesContract.ReadingRhymesView readingView;
    List<ModalRhymingWords> modalMainRhymingList, modalSubCategoryList, wordList;
    ModalRhymingWords modalRhymingWords;
    List<KeyWords> learntWordsList;
    KeyWords learntWords;
    String resStartTime, resId;
    int randomCategory, GLC, totalWordCount, learntWordCount;


    public ReadingRhymesPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(ReadingRhymesContract.ReadingRhymesView readingView) {
        this.readingView = readingView;
        learntWordsList = new ArrayList<>();
    }

    @Background
    public void createWholeList() {
        try {
            modalMainRhymingList = modalRhymingWords.getWordList();
            totalWordCount = getTotalCount(modalMainRhymingList);
            getLearntWordsCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getDataList();
    }

    @Background
    @Override
    public void setCompletionPercentage() {
        try {
            String Label = "resourceProgress";
            float perc = 0f;
            getLearntWordsCount();
            perc = getCompletionPercentage();
            addContentProgress(perc, Label);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getTotalCount() {
        return getTotalCount(modalMainRhymingList);
    }


    public float getCompletionPercentage() {
        float perc = 0f;
        try {
            totalWordCount = getTotalCount(modalMainRhymingList);

            if (learntWordCount > 0) {
                perc = ((float) learntWordCount / (float) totalWordCount) * 100;
            } else
                perc = 0f;

            return perc;
        } catch (Exception e) {
            e.printStackTrace();
            return 0f;
        }
    }

    @Background
    public void addContentProgress(float perc, String label) {
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
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    @Background
    public void addExitScore(float perc, int scoredMarks, int totalMarks, String resStartTime, String Label) {
        try {
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(resId);
            score.setQuestionId(0);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, ""));
            score.setStartDateTime(resStartTime);
            score.setDeviceID("" + perc);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(0);
            score.setLabel("" + Label);
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void getDataList() {
        try {
            getLearntWordsCount();
            randomCategory = FC_Utility.generateRandomNum(modalMainRhymingList.size());
            modalSubCategoryList = new ArrayList<>();

            float perc = getCompletionPercentage();
            totalWordCount = getTotalCount(modalMainRhymingList);

            GLC = 0;
            for (int i = 0; i < modalMainRhymingList.get(randomCategory).getWordList().size(); i++) {
                if (perc < 99.5) {
                    if (!checkWord("" + modalMainRhymingList.get(randomCategory).getWordList().get(i).getWord()))
                        modalSubCategoryList.add(modalMainRhymingList.get(randomCategory).getWordList().get(i));
                } else
                    modalSubCategoryList.add(modalMainRhymingList.get(randomCategory).getWordList().get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (modalSubCategoryList.size() == 0)
            createWholeList();
        Collections.shuffle(modalSubCategoryList);
        getNextDataList();
    }

    //@Background
    private int getTotalCount(List<ModalRhymingWords> modalMainRhymingList) {
        int count = 0;
        for (int i = 0; i < modalMainRhymingList.size(); i++) {
            for (int j = 0; j < modalMainRhymingList.get(i).getWordList().size(); j++)
                count++;
        }
        Log.d("TotalCount", "getTotalCount: " + count);
        return count;
    }

    @Override
    public void setLearntWordsCount(int learntWordCount) {
        this.learntWordCount = learntWordCount;
    }

    @Background
    public void getLearntWordsCount() {
        learntWordCount = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), "" + resId);
    }

    @Background
    @Override
    public void getNextDataList() {
        wordList = new ArrayList<>();
        for (int i = 0; i < 5 && GLC < modalSubCategoryList.size(); i++, GLC++) {
            wordList.add(modalSubCategoryList.get(GLC));
        }
        for (int i = 0; i < wordList.size(); i++) {
            wordList.get(i).setReadOut(false);
            wordList.get(i).setCorrectRead(false);
        }
        if (GLC == modalSubCategoryList.size())
            readingView.noNextButton();
        readingView.setListData(wordList);
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), "" + resId, wordStr.toLowerCase());
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setResIdAndStartTime(String resId) {
        this.resId = resId;
        resStartTime = FC_Utility.getCurrentDateTime();
    }

/*
    @Override
    public void sttResultProcess(ArrayList<String> sttResults, List<ModalRhymingWords> rhymingWordsList) {
        new AsyncTask<Object, Void, Object>() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    System.out.println("LogTag" + " onResults");

                    for (int i = 0; i < sttResults.size(); i++) {
                        String resString = sttResults.get(i);
                        for (int j = 0; j < rhymingWordsList.size(); j++)
                            if (resString.equalsIgnoreCase(rhymingWordsList.get(j).getWord())) {
                                rhymingWordsList.get(j).setCorrectRead(true);
                                break;
                            }
                    }

                    for (int i = 0; i < sttResults.size(); i++) {
                        String resString = sttResults.get(i);
                        String splitRes[] = resString.split(" ");
                        for (int k = 0; k < splitRes.length; k++)
                            for (int j = 0; j < rhymingWordsList.size(); j++)
                                if (rhymingWordsList.get(j).getWord().equalsIgnoreCase("" + splitRes[k]))
                                    rhymingWordsList.get(j).setCorrectRead(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                for (int i = 0; i < correctArr.length; i++)
                    readingView.setCorrectViewColor(i);
            }
        }.execute();
    }
*/

    @Background
    @Override
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
            score.setLevel(0);
            score.setLabel(Word + " - " + Label);
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void addLearntWords(int pos, ModalRhymingWords rhymingWords) {
        try {
            if (!checkWord(rhymingWords.getWord().toLowerCase())) {
                KeyWords keyWords = new KeyWords();
                keyWords.setResourceId(resId);
                keyWords.setSentFlag(0);
                keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                keyWords.setKeyWord(rhymingWords.getWord().toLowerCase());
                keyWords.setTopic("Rhyming Words");
                keyWords.setWordType("word");
                AppDatabase.getDatabaseInstance(context).getKeyWordDao().insert(keyWords);
                BackupDatabase.backup(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            JSONObject jsonObj = new JSONObject(jsonStr);
            Gson gson = new Gson();
            modalRhymingWords = gson.fromJson(jsonObj.toString(), ModalRhymingWords.class);
            createWholeList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}