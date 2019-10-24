package com.pratham.foundation.ui.contentPlayer.vocabulary_qa;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.dao.KeyWordDao;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ModalVocabulary;
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

import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.ui.contentPlayer.vocabulary_qa.ReadingVocabularyActivity.currentPageNo;
import static com.pratham.foundation.ui.contentPlayer.vocabulary_qa.ReadingVocabularyActivity.testCorrectArr;


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
        count = appDatabase.getKeyWordDao().checkWordCount(FC_Constants.currentStudentID, "" + resId);
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
                if (perc < 99.5 || FC_Constants.isPractice || FC_Constants.isTest) {
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
            String word = appDatabase.getKeyWordDao().checkWord(FC_Constants.currentStudentID, "" + resId, checkWord);
            if (word != null)
                return true;
            else
                return false;
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
        contentProgress.setSessionId("" + FC_Constants.currentSession);
        contentProgress.setStudentId("" + FC_Constants.currentStudentID);
        contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
        contentProgress.setLabel("" + label);
        contentProgress.setSentFlag(0);
        appDatabase.getContentProgressDao().insert(contentProgress);
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
            String regex = "[\\-+.\"^?!@#%&*,:]";
            String quesCheckFinal = ansCheck.replaceAll(regex, "");
            String quesFinal = fullAns.replaceAll(regex, "");

            String[] splitQues = quesFinal.split(" ");
            String words = " ";
            String keyWordCorrect = "";

            for (int k = 0; k < sttResults.size(); k++) {
                addSttResultDB(sttResults.get(k));
                String sttResult = sttResults.get(k);
                if (sttResult.toLowerCase().contains(quesCheckFinal.toLowerCase())) {
                    allCorrect = true;
                    keyWordCorrect = " : keyWord" + quesCheckFinal.toLowerCase();
                    addLearntWords(quesCheckFinal.toLowerCase());
                }
            }

            if (!allCorrect)
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
            else if (allCorrect) {
                for (int i = 0; i < ReadingVocabularyActivity.correctArr.length; i++)
                    ReadingVocabularyActivity.correctArr[i] = true;
                testCorrectArr[currentPageNo] = true;
            }

            int correctCnt = getCorrectCounter();
            addScore(0, "Words:" + words, correctCnt, ReadingVocabularyActivity.correctArr.length,
                    "" + FC_Utility.getCurrentDateTime(), "Vocab" + keyWordCorrect);
            perc = getPercentage(correctCnt, ReadingVocabularyActivity.correctArr.length);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if ((perc > 50) && !FC_Constants.isTest) {
            readingView.sendClikChanger(1);
            readingView.setCorrectViewColor();
        }
        if (allCorrect) {
            readingView.sendClikChanger(0);
            readingView.setCorrectViewColor();
            readingView.allCorrectAnswer();
        }
    }

    private void addSttResultDB(String stt_Result) {
        try {
            Score score = new Score();
            score.setSessionID(FC_Constants.currentSession);
            score.setResourceID(resId);
            score.setQuestionId(0);
            score.setScoredMarks(0);
            score.setTotalMarks(0);
            score.setStudentID(FC_Constants.currentStudentID);
            score.setStartDateTime(resStartTime);
            score.setDeviceID(stt_Result);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(0);
            score.setLabel("STT_ALL_RESULT");
            score.setSentFlag(0);
            appDatabase.getScoreDao().insert(score);
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
            learntWords.setStudentId(FC_Constants.currentStudentID);
            learntWords.setKeyWord(word.toLowerCase());
            learntWords.setTopic("" + vocabCategory);
            learntWords.setWordType("word");
            appDatabase.getKeyWordDao().insert(learntWords);
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
            String deviceId = appDatabase.getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FC_Constants.currentSession);
            score.setResourceID(resId);
            score.setQuestionId(wID);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FC_Constants.currentStudentID);
            score.setStartDateTime(resStartTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(0);
            score.setLabel(Word + " - " + Label);
            score.setSentFlag(0);
            appDatabase.getScoreDao().insert(score);

            if (FC_Constants.isTest) {
                Assessment assessment = new Assessment();
                assessment.setResourceIDa(resId);
                assessment.setSessionIDa(FC_Constants.assessmentSession);
                assessment.setSessionIDm(FC_Constants.currentSession);
                assessment.setQuestionIda(wID);
                assessment.setScoredMarksa(scoredMarks);
                assessment.setTotalMarksa(totalMarks);
                assessment.setStudentIDa(FC_Constants.currentAssessmentStudentID);
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