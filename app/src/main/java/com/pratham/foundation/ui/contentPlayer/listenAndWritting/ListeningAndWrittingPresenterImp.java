package com.pratham.foundation.ui.contentPlayer.listenAndWritting;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.modalclasses.ScienceQuestion;
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
public class ListeningAndWrittingPresenterImp implements ListeningAndWrittingContract.ListeningAndWrittingPresenter {
    private Context context;
    // private List<QuetionAns> quetionAnsList;
    private int totalWordCount, learntWordCount;
    private float perc;
    private ListeningAndWrittingContract.ListeningAndWrittingView view;
    private String gameName, resId, contentTitle;
    private List<String> correctWordList, wrongWordList;
    private List<ScienceQuestion> dataList;
    private List<ScienceQuestion> listenAndWrittingModal;
    private int count = 1;

    public ListeningAndWrittingPresenterImp(Context context) {
        this.context = context;
    }


    @Background
    @Override
    public void fetchJsonData(String contentPath) {
        try {
            InputStream is = new FileInputStream(contentPath + "Dict.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONArray jsonObj = new JSONArray(jsonStr);
            Gson gson = new Gson();
            Type type = new TypeToken<List<ScienceQuestion>>() {
            }.getType();

            dataList = gson.fromJson(jsonObj.toString(), type);
            getDataList();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            contentProgress.setSessionId("" + FC_Constants.currentSession);
            contentProgress.setStudentId("" + FC_Constants.currentStudentID);
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("" + label);
            contentProgress.setSentFlag(0);
            appDatabase.getContentProgressDao().insert(contentProgress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setView(ListeningAndWrittingContract.ListeningAndWrittingView listeningAndWrittingView, String contentTitle, String resId) {
        view = listeningAndWrittingView;
        this.resId = resId;
        this.contentTitle = contentTitle;
    }

    @Background
    @Override
    public void getDataList() {
        try {
            count = GameConstatnts.getCount();
            listenAndWrittingModal = new ArrayList<>();
            perc = getPercentage();
            Collections.shuffle(dataList);
            for (int i = 0; i < dataList.size(); i++) {
                if (perc < 95) {
                    if (!checkWord("" + dataList.get(i).getTitle()))
                        listenAndWrittingModal.add(dataList.get(i));
                } else {
                    listenAndWrittingModal.add(dataList.get(i));
                }
                if (listenAndWrittingModal.size() == count) {
                    break;
                }
            }
            view.loadUI(listenAndWrittingModal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addLearntWords(List<ScienceQuestion> listenAndWrittingModal, String imageName) {
        if (imageName != null && !imageName.isEmpty()) {
            if (listenAndWrittingModal != null && !listenAndWrittingModal.isEmpty()) {
                for (int i = 0; i < listenAndWrittingModal.size(); i++) {
                    KeyWords keyWords = new KeyWords();
                    keyWords.setResourceId(resId);
                    keyWords.setSentFlag(0);
                    keyWords.setStudentId(FC_Constants.currentStudentID);
                    String key = listenAndWrittingModal.get(i).getTitle();
                    keyWords.setKeyWord(key);
                    keyWords.setWordType("word");
                    appDatabase.getKeyWordDao().insert(keyWords);
                    addScore(GameConstatnts.getInt(listenAndWrittingModal.get(i).getQid()), GameConstatnts.LISTNING_AND_WRITTING, 0, 0, FC_Utility.getCurrentDateTime(), imageName);
                }
                GameConstatnts.playGameNext(context, GameConstatnts.FALSE, (OnGameClose) view);
            }
            setCompletionPercentage();
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) view);
        }
        BackupDatabase.backup(context);
    }

   /* public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        try {
            File direct = new File(activityPhotoPath);
            File file = new File(direct, fileName);
            FileOutputStream out = new FileOutputStream(file);
            String path = file.getAbsolutePath();
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // isPhotoSaved = true;
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
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
        //count = appDatabase.getKeyWordDao().checkWordCount(FC_Constants.currentStudentID, resId);
        count = appDatabase.getKeyWordDao().checkUniqueWordCount(FC_Constants.currentStudentID, resId);
        return count;
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = appDatabase.getKeyWordDao().checkWord(FC_Constants.currentStudentID, resId, wordStr);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


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
            score.setLevel(FC_Constants.currentLevel);
            score.setLabel(Word + "___" + Label);
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
                assessment.setLabel("test: ___" + Label);
                assessment.setSentFlag(0);
                appDatabase.getAssessmentDao().insert(assessment);
            }
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float checkAnswer(List ansSet, List<String> selectedAnsList) {
        int correctCnt = 0;
        for (int i = 0; i < selectedAnsList.size(); i++) {
            if (ansSet.contains(selectedAnsList.get(i))) {
                correctCnt++;
                correctWordList.add(selectedAnsList.get(i));
            } else {
                wrongWordList.add(selectedAnsList.get(i));
            }
        }
        return 10 * correctCnt / ansSet.size();
    }
}
