package com.pratham.foundation.ui.contentPlayer.paragraph_writing;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.database.AppDatabase;
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

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.IMG_PUSH_LBL;
import static com.pratham.foundation.utility.FC_Constants.activityPhotoPath;
import static com.pratham.foundation.utility.FC_Constants.currentLevel;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

@EBean
public class ParagraphWritingPresenter implements ParagraphWritingContract.ParagraphWritingPresenter {
    private List<ScienceQuestion> questionModel = null;
    private ParagraphWritingContract.ParagraphWritingView view;
    private Context context;
    private List<ScienceQuestion> quetionModelList;
    private float perc;
    private int totalWordCount, learntWordCount;
    private String gameName, resId, contentTitle, readingContentPath;
    private String jsonName;

    public ParagraphWritingPresenter(Context context) {
        this.context = context;
    }

    public void setView(ParagraphWritingContract.ParagraphWritingView view, String resId, String readingContentPath, String jsonName,String contentTitle) {
        this.view = view;
        this.resId = resId;
        this.jsonName = jsonName;
        this.readingContentPath = readingContentPath;
        this.contentTitle = contentTitle;
    }

    @Override
    public void getData() {
       // String text = FC_Utility.loadJSONFromStorage(readingContentPath, "CWiritng.json");
        String text;
        if (jsonName.equalsIgnoreCase(GameConstatnts.PARAGRAPH_WRITING)) {
            text = FC_Utility.loadJSONFromStorage(readingContentPath, "CopyWriting.json");
        } else {
            text = FC_Utility.loadJSONFromStorage(readingContentPath, jsonName + ".json");
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<ScienceQuestion>>() {
        }.getType();

        quetionModelList = gson.fromJson(text, type);
        getDataList();
       /* getDataList();
        try {
            JSONArray jsonArray = new JSONArray(text);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            questionModel.setParagraph((String) jsonObject.get("paragraph"));
            JSONArray array = (JSONArray) jsonObject.get("keywords");
            for (int i = 0; i < array.length(); i++) {
                instrumentNames.add(array.getString(i));
            }
            questionModel.setKeywords(instrumentNames);
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
        //get data
       /* questionModel = new QuestionModel();
        String[] instrumentNames = {"Firstly", "activity", "MainActivityPresenter", "through", "Interface"};
        questionModel.setParagraph("Firstly, This activity implements the MainActivityPresenter.View Interface through which it’s overridden method will be called. Secondly, we have to create the MainActivityPresenter object with view as a constructor. We use this presenter object to listen the user input and update the data as well as view. I think that's better to leverage the xml shape drawable resource power if that fits your needs. With a from scratch project (for android-8), define res/layout/main.xml");
        questionModel.setKeywords(new ArrayList(Arrays.asList(instrumentNames)));*/
        //view.showParagraph(questionModel);
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
            contentProgress.setStudentId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("" + label);
            contentProgress.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDataList() {
        try {
            perc = getPercentage();
            questionModel = new ArrayList<>();
            Collections.shuffle(quetionModelList);
            if (FC_Constants.currentSubjectFolder.equalsIgnoreCase("Science")) {
                for (int i = 0; i < quetionModelList.size(); i++) {
                    questionModel.add(quetionModelList.get(i));
                }
            } else {
                for (int i = 0; i < quetionModelList.size(); i++) {
                    if (perc < 95) {
                        if (!checkWord("" + quetionModelList.get(i).getTitle())) {
                            questionModel.add(quetionModelList.get(i));
                            break;
                        }
                    } else {
                        questionModel.add(quetionModelList.get(i));
                    }
                    if (questionModel != null && questionModel.size()>1) {
                        break;
                    }
                }
            }
            view.showParagraph(questionModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getPercentage() {
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

    private boolean checkWord(String wordStr) {
        try {
            String word = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId, wordStr);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getLearntWordsCount() {
        int count = 0;
        // count = appDatabase.getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
        count = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkUniqueWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);

        return count;
    }

    /* public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
         try {
             File direct = new File(activityPhotoPath);
             File file = new File(direct, fileName);
             FileOutputStream out = new FileOutputStream(file);
             imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
             // isPhotoSaved = true;
             out.flush();
             out.close();
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
 */
    public void addLearntWords(List<ScienceQuestion> questionModel) {
        String newResId;
        if (questionModel != null && !questionModel.isEmpty()) {
            for (int i = 0; i < questionModel.size(); i++) {
                if (checkIsAttempted(questionModel.get(i))) {
                    KeyWords keyWords = new KeyWords();
                    keyWords.setResourceId(resId);
                    keyWords.setSentFlag(0);
                    keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                    keyWords.setKeyWord(questionModel.get(i).getTitle());
                    keyWords.setWordType("word");
                    newResId = GameConstatnts.getString(resId, contentTitle, questionModel.get(i).getQid(), questionModel.get(i).getUserAnswer(), questionModel.get(i).getQuestion(),"");
                    addScore(GameConstatnts.getInt(questionModel.get(i).getQid()), jsonName, 0, 0, questionModel.get(i).getStartTime(), questionModel.get(i).getEndTime(), questionModel.get(i).getUserAnswer(), resId, true);
                    addScore(FC_Utility.getSubjectNo(), jsonName, FC_Utility.getSectionCode(), 0, questionModel.get(i).getStartTime(), questionModel.get(i).getEndTime(), FC_Constants.IMG_LBL, newResId, false);
                    addImageOnly(resId, questionModel.get(i).getUserAnswer());
                    AppDatabase.getDatabaseInstance(context).getKeyWordDao().insert(keyWords);
                }
            }
            setCompletionPercentage();
            GameConstatnts.postScoreEvent(1, 1);
            //Toast.makeText(context, "inserted succussfully", Toast.LENGTH_LONG).show();
            GameConstatnts.playGameNext(context, GameConstatnts.FALSE, (OnGameClose) view);
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) view);
        }
        BackupDatabase.backup(context);
    }

    @Background
    public void addImageOnly(String resId, String imageName) {
        try {
            String deviceId = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceId");
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
            score.setLevel(FastSave.getInstance().getInt(FC_Constants.CURRENT_LEVEL, currentLevel));
            score.setLabel(IMG_PUSH_LBL);
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkIsAttempted(ScienceQuestion scienceQuestion) {
        File filePath = new File(activityPhotoPath + scienceQuestion.getUserAnswer());
        return filePath.exists();
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
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setStartDateTime(resStartTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(resEndTime);
            score.setLevel(FastSave.getInstance().getInt(FC_Constants.CURRENT_LEVEL, currentLevel));
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
