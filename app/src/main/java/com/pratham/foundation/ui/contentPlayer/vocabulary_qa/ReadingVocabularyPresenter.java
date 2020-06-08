package com.pratham.foundation.ui.contentPlayer.vocabulary_qa;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ModalVocabulary;
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

import static com.pratham.foundation.ui.contentPlayer.vocabulary_qa.ReadingVocabularyActivity.currentPageNo;
import static com.pratham.foundation.ui.contentPlayer.vocabulary_qa.ReadingVocabularyActivity.testCorrectArr;
import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.sec_Practice;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;


@EBean
public class ReadingVocabularyPresenter implements ReadingVocabularyContract.ReadingVocabularyPresenter {

    public Context context;
    public ReadingVocabularyContract.ReadingVocabularyView readingView;
    public List<ModalVocabulary> modalMainVocabList, modalMainVocabCatList, modalSubVocabList, questionList;
    public ModalVocabulary modalVocabulary;
    public int randomCategory, GLC, learntWordCount, totalWordCount, randomTestCategory;
    public List<KeyWords> learntWordsList;
    public KeyWords learntWords;
    public String resStartTime, resId, vocabCategory;


    public ReadingVocabularyPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(ReadingVocabularyContract.ReadingVocabularyView readingView) {
        this.readingView = readingView;
    }

    @Override
    public void setResIdAndStartTime(String resId) {
        this.resId = resId;
        resStartTime = FC_Utility.getCurrentDateTime();
        learntWordsList = new ArrayList<>();
    }

    @Background
    @Override
    public void createWholeList(String vocabNewCategory) {
        vocabCategory = vocabNewCategory;
        modalMainVocabList = modalVocabulary.getDataList();
        learntWordCount = getLearntWordsCount();
        if (vocabCategory.equalsIgnoreCase("$$$")) {
            randomTestCategory = FC_Utility.generateRandomNum(modalMainVocabList.size());
            modalMainVocabCatList = modalMainVocabList.get(randomTestCategory).getDataList();
        } else {
            for (int i = 0; i < modalMainVocabList.size(); i++)
                if (modalMainVocabList.get(i).getValue().equalsIgnoreCase("" + vocabCategory)) {
                    modalMainVocabCatList = modalMainVocabList.get(i).getDataList();
                    break;
                }
        }
        totalWordCount = getTotalCount(modalMainVocabCatList);
        getDataList();
    }

    private int getTotalCount(List<ModalVocabulary> modalMainVocabList) {
        int count = 0;
        try {
            if (modalMainVocabList.size() > 0)
                for (int i = 0; i < modalMainVocabList.size(); i++) {
                    for (int j = 0; j < modalMainVocabList.get(i).getDataList().size(); j++)
                        count++;
                }
            Log.d("TotalCount", "getTotalCount: " + count);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getLearntWordsCount() {
        int count;
        count = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), "" + resId);
        return count;
    }

    @Background
    @Override
    public void getDataList() {
        try {
            randomCategory = FC_Utility.generateRandomNum(modalMainVocabCatList.size());
            GLC = 0;
            modalSubVocabList = new ArrayList<>();
            float perc = getPercentage(learntWordCount, totalWordCount);
            for (int i = 0; i < modalMainVocabCatList.get(randomCategory).getDataList().size(); i++) {
                if (perc < 99.5 ||
                        FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Practice)||
                        FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                    if (!checkWord("" + modalMainVocabCatList.get(randomCategory).getDataList().get(i).getQuestion1Text()))
                        modalSubVocabList.add(modalMainVocabCatList.get(randomCategory).getDataList().get(i));
                } else
                    modalSubVocabList.add(modalMainVocabCatList.get(randomCategory).getDataList().get(i));
            }
            if (modalSubVocabList.size() == 0)
                createWholeList(vocabCategory);

            Collections.shuffle(modalSubVocabList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getNextDataList();
    }

    private boolean checkWord(String checkWord) {
        try {
            String word = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), "" + resId, checkWord);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void getNextDataList() {
        try {
            questionList = new ArrayList<>();
            for (int i = 0; i < 5 && GLC < modalSubVocabList.size(); i++, GLC++) {
                questionList.add(modalSubVocabList.get(GLC));
            }
            readingView.setListData(questionList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void setCompletionPercentage() {
        float perc;
        totalWordCount = getTotalCount(modalMainVocabCatList);
        learntWordCount = getLearntWordsCount();
        String Label = "resourceProgress";
        if (learntWordCount > 0) {
            perc = ((float) learntWordCount / (float) totalWordCount) * 100;
            addContentProgress(perc, Label);
//                    addExitScore(perc, learntWordCount, totalWordCount, resStartTime, Label);
        } else
            addContentProgress(0, Label);
//                    addExitScore(0, learntWordCount, totalWordCount, resStartTime, Label);
    }

    private void addContentProgress(float perc, String label) {
        ContentProgress contentProgress = new ContentProgress();
        contentProgress.setProgressPercentage("" + perc);
        contentProgress.setResourceId("" + resId);
        contentProgress.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
        contentProgress.setStudentId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
        contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
        contentProgress.setLabel("" + label);
        contentProgress.setSentFlag(0);
        AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);
    }

    @Override
    @Background
    public void fetchJsonData(String contentPath, String vocabCategory) {
        try {
            InputStream is = new FileInputStream(contentPath + "/Data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONObject jsonObj = new JSONObject(jsonStr);
            Gson gson = new Gson();
            modalVocabulary = gson.fromJson(jsonObj.toString(), ModalVocabulary.class);
            createWholeList(vocabCategory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void sttResultProcess(ArrayList<String> sttResults, ModalVocabulary modalVocabulary, String ansCheck, String fullAns) {
        boolean allCorrect = false;
        float perc = 0;

        try {
            String quesCheckFinal = ansCheck.replaceAll(STT_REGEX, "");
            String quesFinal = fullAns.replaceAll(STT_REGEX, "");

            String[] splitQues = quesFinal.split(" ");
            String words = " ";
            String keyWordCorrect = "";

            addSttResultDB(sttResults);

            for (int k = 0; k < sttResults.size(); k++) {
                String sttResult = sttResults.get(k);
                if (sttResult.toLowerCase().contains(quesCheckFinal.toLowerCase())) {
                    allCorrect = true;
                    keyWordCorrect = " : keyWord " + quesCheckFinal.toLowerCase();
                    addLearntWords(quesCheckFinal.toLowerCase());
                }
            }

            if (!allCorrect) {
                for (int k = 0; k < sttResults.size(); k++) {
                    String sttResult = sttResults.get(k);
                    String[] splitRes = sttResult.split(" ");
                    for (int j = 0; j < splitRes.length; j++)
                        for (int i = 0; i < splitQues.length; i++)
                            if (splitRes[j].equalsIgnoreCase(splitQues[i]) && !ReadingVocabularyActivity.correctArr[i]) {
                                ReadingVocabularyActivity.correctArr[i] = true;
                                words = words + splitQues[i] + ",";
                                break;
                            }
                }
                int correctCnt = getCorrectCounter();
                perc = getPercentage(correctCnt, ReadingVocabularyActivity.correctArr.length);
                if ((perc > 50) && !FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                    readingView.sendClikChanger(1);
                    readingView.setCorrectViewColor();
                }
            }else if (allCorrect) {
                for (int i = 0; i < ReadingVocabularyActivity.correctArr.length; i++)
                    ReadingVocabularyActivity.correctArr[i] = true;
                readingView.sendClikChanger(0);
                readingView.setCorrectViewColor();
                readingView.allCorrectAnswer();
                testCorrectArr[currentPageNo] = true;
            }

            int correctCnt = getCorrectCounter();
            addScore(0, "Words:" + words, correctCnt, ReadingVocabularyActivity.correctArr.length,
                    "" + FC_Utility.getCurrentDateTime(), "Vocab" + keyWordCorrect);
//            perc = getPercentage(correctCnt, ReadingVocabularyActivity.correctArr.length);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSttResultDB(ArrayList<String> stt_Result) {
        String deviceId = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceId");
        String strWord = "STT_ALL_RESULT - ";
        for(int i =0 ; i<stt_Result.size(); i++)
            strWord = strWord +stt_Result.get(i)+ " - ";

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
            score.setLabel(""+strWord);
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addLearntWords(String word) {
        boolean wordPresent = checkWord(word);
        if (!wordPresent) {
            KeyWords learntWords = new KeyWords();
            learntWords.setResourceId(resId);
            learntWords.setSentFlag(0);
            learntWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            learntWords.setKeyWord(word.toLowerCase());
            learntWords.setTopic("" + vocabCategory);
            learntWords.setWordType("word");
            AppDatabase.getDatabaseInstance(context).getKeyWordDao().insert(learntWords);
        }
        BackupDatabase.backup(context);
    }


    private float getPercentage(int counter, int totLen) {
        float perc = 0f;
        if (counter > 0)
            perc = ((float) counter / totLen) * 100;
        return perc;
    }

    private int getCorrectCounter() {
        int counter = 0;
        for (int x = 0; x < ReadingVocabularyActivity.correctArr.length; x++)
            if (ReadingVocabularyActivity.correctArr[x]) {
                counter++;
            }
        return counter;
    }

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
            score.setStartDateTime(resStartTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(0);
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
}