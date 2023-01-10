package com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EBean
public class FactRetrievalPresenter implements FactRetrievalContract.FactRetrievalPresenter {
    private ScienceQuestion questionModel;
    private FactRetrievalContract.FactRetrievalView view;
    private final Context context;
    private String resId;
    private float perc;
    //private List<QuetionAns> quetionAnsList;
    private List<ScienceQuestion> quetionModelList;
    private int totalWordCount, learntWordCount;
    //private List<QuetionAns> selectedFive;

    public FactRetrievalPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(FactRetrievalContract.FactRetrievalView factRetrivalView, String contentTitle, String resId) {
        this.view = factRetrivalView;
        this.resId = resId;
    }

    @Override
    public void getData(String readingContentPath) {
        //get data
        String text = FC_Utility.loadJSONFromStorage(readingContentPath, "factRetrival.json");
        // List instrumentNames = new ArrayList<>();
        if (text != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ScienceQuestion>>() {
            }.getType();
            quetionModelList = gson.fromJson(text, type);
            //  quetionAnsList = quetionModelList.get(0).getKeywords();
            getDataList();
            //setCompletionPercentage();
        } else {
            Toast.makeText(context, "Data not found", Toast.LENGTH_LONG).show();
        }

    }


    public void setCompletionPercentage() {
        try {
            totalWordCount = quetionModelList.size();
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
            contentProgress.setStudentId("" + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ?"NA"
                    :FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "")));
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("" + label);
            contentProgress.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void getDataList() {
        try {
            perc = getPercentage();
            Collections.shuffle(quetionModelList);
            for (int i = 0; i < quetionModelList.size(); i++) {
                if (perc < 95) {
                    if (!checkWord("" + quetionModelList.get(i).getTitle())) {
                        questionModel = quetionModelList.get(i);
                        break;
                    }
                } else {
                    questionModel = quetionModelList.get(i);
                    break;
                }
            }

            view.showParagraph(questionModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private float getPercentage() {
        float perc = 0f;
        try {
            totalWordCount = quetionModelList.size();
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
        //count = appDatabase.getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"), resId);
        count = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkUniqueWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"), resId);
        return count;
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"), resId, wordStr);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addLearntWords(ArrayList<ScienceQuestionChoice> selectedAnsList) {
        if (selectedAnsList != null && checkAttemptedornot(selectedAnsList)) {
            int correctCnt = 0;
            List<KeyWords> learntWords = new ArrayList<>();
            int scoredMarks;
            KeyWords keyWords = new KeyWords();
            keyWords.setResourceId(resId);
            keyWords.setSentFlag(0);
            keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"));
            keyWords.setKeyWord(questionModel.getTitle());
            keyWords.setWordType("word");
            learntWords.add(keyWords);
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (selectedAnsList.get(i).getUserAns() != null && !selectedAnsList.get(i).getUserAns().isEmpty()) {
                    //compare with passage answer so accuracy must be above 80 because answer is present in para
                    if (checkAnswer(selectedAnsList.get(i)) > 80) {
                        scoredMarks = 10;
                        correctCnt++;
                        selectedAnsList.get(i).setTrue(true);
                    } else {
                        //compare with json answer so accuracy must be above 70 because  answer may be different than para sentence
                        if (checkAnswerWithOriginal(selectedAnsList.get(i)) > 70) {
                            scoredMarks = 10;
                            correctCnt++;
                            selectedAnsList.get(i).setTrue(true);
                        } else {
                            scoredMarks = 0;
                            selectedAnsList.get(i).setTrue(false);
                        }
                    }
                    // addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid()), GameConstatnts.FACTRETRIEVAL, scoredMarks, 10, FC_Utility.getCurrentDateTime(), selectedAnsList.get(i).toString());
                    addScore(GameConstatnts.getInt(questionModel.getQid()), GameConstatnts.FACTRETRIEVAL, scoredMarks, 10, selectedAnsList.get(i).getStartTime(), selectedAnsList.get(i).getEndTime(), selectedAnsList.get(i).toString());
                }
            }
            AppDatabase.getDatabaseInstance(context).getKeyWordDao().insertAllWord(learntWords);
            setCompletionPercentage();
            GameConstatnts.postScoreEvent(selectedAnsList.size(), correctCnt);
            BaseActivity.correctSound.start();
            if (!FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test)) {
                view.showResult(selectedAnsList);
            } else {
                GameConstatnts.playGameNext(context, GameConstatnts.FALSE, (OnGameClose) view);
            }
            //GameConstatnts.playGameNext(context, GameConstatnts.FALSE, (OnGameClose) view);
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) view);
        }
        BackupDatabase.backup(context);
    }

    private boolean checkAttemptedornot(List<ScienceQuestionChoice> selectedAnsList) {
        for (int i = 0; i < selectedAnsList.size(); i++) {
            if (selectedAnsList.get(i).getUserAns() != null && !selectedAnsList.get(i).getUserAns().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label) {
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
            //score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setEndDateTime(resEndTime);
            score.setLevel(FC_Constants.currentLevel);
            score.setLabel(Word + " - " + Label);
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);

            if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test)) {
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

    public float checkAnswer(ScienceQuestionChoice selectedAnsList) {
        boolean[] correctArr;
        float perc;
        String originalAns = selectedAnsList.getAnsInPassage().trim();
        String quesFinal = originalAns.replaceAll(STT_REGEX, "");

        String[] originalAnsArr = quesFinal.split(" ");
        String[] userAnsArr = selectedAnsList.getUserAns().replaceAll(STT_REGEX, "").trim().split(" ");

        if (originalAnsArr.length < userAnsArr.length)
            correctArr = new boolean[userAnsArr.length];
        else correctArr = new boolean[originalAnsArr.length];


        for (int j = 0; j < userAnsArr.length; j++) {
            for (int i = 0; i < originalAnsArr.length; i++) {
                if (userAnsArr[j].equalsIgnoreCase(originalAnsArr[i])) {
                    correctArr[j] = true;
                    break;
                }
            }
        }

        int correctCnt = 0;
        for (int x = 0; x < correctArr.length; x++) {
            if (correctArr[x])
                correctCnt++;
        }
        perc = ((float) correctCnt / (float) correctArr.length) * 100;
        return perc;
    }

    //Check answer with json answer (which answer get from server)
    public float checkAnswerWithOriginal(ScienceQuestionChoice selectedAnsList) {
        boolean[] correctArr;
        float perc;
        String originalAns = selectedAnsList.getCorrectAnswer().trim();
        String quesFinal = originalAns.replaceAll(STT_REGEX, "");

        String[] originalAnsArr = quesFinal.split(" ");
        String[] userAnsArr = selectedAnsList.getUserAns().replaceAll(STT_REGEX, "").trim().split(" ");

        if (originalAnsArr.length < userAnsArr.length)
            correctArr = new boolean[userAnsArr.length];
        else correctArr = new boolean[originalAnsArr.length];


        for (int j = 0; j < userAnsArr.length; j++) {
            for (int i = 0; i < originalAnsArr.length; i++) {
                if (userAnsArr[j].equalsIgnoreCase(originalAnsArr[i])) {
                    correctArr[j] = true;
                    break;
                }
            }
        }

        int correctCnt = 0;
        for (int x = 0; x < correctArr.length; x++) {
            if (correctArr[x])
                correctCnt++;
        }
        perc = ((float) correctCnt / (float) correctArr.length) * 100;
        return perc;
    }
}
