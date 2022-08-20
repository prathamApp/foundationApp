package com.pratham.foundation.ui.contentPlayer.new_reading_fragment;

import static com.pratham.foundation.ui.contentPlayer.new_reading_fragment.ContentReadingFragment.correctArr;
import static com.pratham.foundation.ui.contentPlayer.new_reading_fragment.ContentReadingFragment.lineBreakCounter;
import static com.pratham.foundation.ui.contentPlayer.new_reading_fragment.ContentReadingFragment.testCorrectArr;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_FOLDER_NAME;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX_2;

import android.content.Context;

import com.google.gson.Gson;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ModalParaMainMenu;
import com.pratham.foundation.modalclasses.ModalParaSubMenu;
import com.pratham.foundation.services.shared_preferences.FastSave;
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
public class ContentReadingPresenter implements ContentReadingContract.ContentReadingPresenter {

    ContentReadingContract.ContentReadingView readingView;

    Context context;
    ModalParaMainMenu modalParaMainMenu;
    List<ModalParaSubMenu> modalParaSubMenuList;
    List<KeyWords> learntWordsList;
    KeyWords learntWords;
    public static float[] pagePercentage;
    int pgNo;
    String resId, resStartTime;
    public ArrayList<String> remainingResult;


    public ContentReadingPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(ContentReadingContract.ContentReadingView readingView) {
        this.readingView = readingView;
        learntWordsList = new ArrayList<>();
        remainingResult = new ArrayList<>();
    }

    @Background
    @Override
    public void setResId(String resourceId) {
        resId = resourceId;
        resStartTime = FC_Utility.getCurrentDateTime();
    }

    @Override
    public void getDataList() {
        try {
            String title = modalParaMainMenu.getContentTitle();
            modalParaSubMenuList = modalParaMainMenu.getPageList();
            readingView.setCategoryTitle(title);
            pagePercentage = new float[modalParaSubMenuList.size()];
            testCorrectArr = new boolean[modalParaSubMenuList.size()];
            readingView.setListData(modalParaSubMenuList);
            getPage(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getPage(int pgNo) {
        readingView.showLoader();
        this.pgNo = pgNo;
        String paraAudio = modalParaSubMenuList.get(pgNo).getReadPageAudio();
        readingView.setParaAudio(paraAudio);
        readingView.initializeContent(pgNo);
    }

    @Background
    @Override
    public void fetchJsonData(String contentPath) {
        try {
            InputStream is = new FileInputStream(contentPath + "/StoryReadingData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONObject jsonObj = new JSONObject(jsonStr);
            Gson gson = new Gson();
            modalParaMainMenu = gson.fromJson(jsonObj.toString(), ModalParaMainMenu.class);
            getDataList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSttResultDB(ArrayList<String> stt_Result) {
        String deviceId = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceId");
        StringBuilder strWord = new StringBuilder("STT_ALL_RESULT - ");
        for(int i =0 ; i<stt_Result.size(); i++) {
            strWord.append(stt_Result.get(i)).append(" - ");
            stt_Result.size();
            if(i > 0 && i < 3)
                remainingResult.add(stt_Result.get(i));
        }
        try {
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(resId);
            score.setQuestionId(0);
            score.setScoredMarks(0);
            score.setTotalMarks(0);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, ""));
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

    @Background
    @Override
    public void sttResultProcess(ArrayList<String> sttResult, List<String> splitWordsPunct, List<String> wordsResIdList) {

        String sttRes = sttResult.get(0);
        String[] splitRes;
        String word = " ";
        addSttResultDB(sttResult);

        if (FastSave.getInstance().getString(CURRENT_FOLDER_NAME, "").equalsIgnoreCase("English"))
            splitRes = sttRes.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        else {
            String answer2 = sttRes.replaceAll(STT_REGEX_2, "");
            splitRes = answer2.split(" ");
        }

        for (int j = 0; j < splitRes.length; j++) {
            for (int i = 0; i < splitWordsPunct.size(); i++) {
                if ((splitRes[j].equalsIgnoreCase(splitWordsPunct.get(i))) && !correctArr[i]) {
                    correctArr[i] = true;
                    word = word + splitWordsPunct.get(i) + "(" + wordsResIdList.get(i) + "),";
                    break;
                }
            }
        }

        int correctWordCount = getCorrectCounter();
        String wordTime = FC_Utility.getCurrentDateTime();
//        addLearntWords(splitWordsPunct, wordsResIdList);
        addScore(0, "Words:" + word, correctWordCount, correctArr.length, wordTime, " ");
            readingView.setCorrectViewColor();

    }

    @Background
    @Override
    public void micStopped(List<String> splitWordsPunct, List<String> wordsResIdList) {
        float perc;
        String word = " ";
        for(int k =0; k<remainingResult.size(); k++) {
            String[] splitRes = remainingResult.get(k).split("");
            for (int j = 0; j < splitRes.length; j++) {
                splitRes[j] = splitRes[j].replaceAll(STT_REGEX, "");
                for (int i = 0; i < splitWordsPunct.size(); i++) {
                    if ((splitRes[j].equalsIgnoreCase(splitWordsPunct.get(i))) && !correctArr[i]) {
                        correctArr[i] = true;
                        word = word + splitWordsPunct.get(i) + "(" + wordsResIdList.get(i) + "),";
                        break;
                    }
                }
            }
        }
        remainingResult.clear();
        List<String> learntWords_List = new ArrayList<>();
        List<String> wordResId_List = new ArrayList<>();
        for (int i = 0; i < splitWordsPunct.size(); i++) {
            if (correctArr[i]) {
                learntWords_List.add(splitWordsPunct.get(i));
                wordResId_List.add(wordsResIdList.get(i));
            }
        }

        addLearntWords(learntWords_List, wordResId_List);
        int correctWordCount = getCorrectCounter();
        perc = getPercentage(correctWordCount);

        addScore(0, "Words:" + word, correctWordCount, correctArr.length, FC_Utility.getCurrentDateTime(), " ");

        if (pagePercentage[pgNo] < perc) {
            pagePercentage[pgNo] = perc;
        }
/*
        if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test))
            if (perc >= 75)
                testCorrectArr[pgNo] = true;
*/

        if (perc >= 75)
            readingView.allCorrectAnswer();
        else
            readingView.dismissLoadingDialog();
    }

    public boolean checkLearnt(String wordCheck) {
        try {
            String word = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), "" + resId, wordCheck.toLowerCase());
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addLearntWords(List<String> splitWordsPunct, List<String> wordsResIdList) {
        try {
            for (int i = 0; i < correctArr.length; i++) {
                if (!checkLearnt(splitWordsPunct.get(i).toLowerCase())) {
                    KeyWords keyWords = new KeyWords();
                    if (correctArr[i]) {
                        keyWords.setKeyWordId(Integer.parseInt(wordsResIdList.get(i)));
                        keyWords.setSentFlag(0);
                        keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                        keyWords.setResourceId(resId);
                        keyWords.setKeyWord(splitWordsPunct.get(i).toLowerCase());
                        keyWords.setWordType("word");
                        keyWords.setTopic("Topic");
                        AppDatabase.getDatabaseInstance(context).getKeyWordDao().insert(keyWords);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getPercentage(int count) {
        float perc = 0f;
        try {
            int totLen = correctArr.length - lineBreakCounter;
            if (count > 0)
                perc = ((float) count / (float) totLen) * 100;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return perc;
    }

    public int getCorrectCounter() {
        int counter = 0;

        for (int x = 0; x < correctArr.length; x++)
            if (correctArr[x])
                counter++;
        return counter;
    }

/*    @Background
    @Override
    public void exitDBEntry() {
        int ansCnt = getCorrectCounter();
        float perc = getPercentage(ansCnt);
        String Label = "resourceProgress";
        addExitScore(perc, Label);
    }*/

    @Background
    @Override
    public void addProgress() {
        float perc = getCompletionPercentage();
        String Label = "resourceProgress";
        addExitScore(perc, Label);
    }

    private float getCompletionPercentage() {
        float tot = 0f, perc = 0f;
        try {
            for (int i = 0; i < pagePercentage.length; i++)
                tot += pagePercentage[i];
            if (tot > 0)
                perc = (tot / pagePercentage.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return perc;
    }

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

/*
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
*/
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void addExitScore(float perc, String Label) {
        try {
            ContentProgress contentProgress = new ContentProgress();
            contentProgress.setProgressPercentage("" + perc);
            contentProgress.setResourceId("" + resId);
            contentProgress.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            contentProgress.setStudentId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("" + Label);
            contentProgress.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}