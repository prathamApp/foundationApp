package com.pratham.foundation.ui.contentPlayer.old_cos.reading_cards;

import static com.pratham.foundation.ui.contentPlayer.old_cos.reading_cards.ReadingCardsActivity.correctArr;

import android.content.Context;

import com.google.gson.Gson;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ModalReadingCardMenu;
import com.pratham.foundation.modalclasses.ModalReadingCardSubMenu;
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
public class ReadingCardPresenter implements ReadingCardContract.ReadingCardPresenter {

    public ReadingCardContract.ReadingCardView readingView;
    public Context context;
    public ModalReadingCardMenu modalReadingCardMenu;
    public List<ModalReadingCardSubMenu> modalReadingCardSubMenuList;
    public List<KeyWords> learntWordsList;
    public String resId;

    public ReadingCardPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(ReadingCardContract.ReadingCardView readingView) {
        this.readingView = readingView;
        learntWordsList = new ArrayList<>();
    }

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
            modalReadingCardMenu = gson.fromJson(jsonObj.toString(), ModalReadingCardMenu.class);
            getDataList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDataList() {
        try {
            readingView.setContentTitle("" + modalReadingCardMenu.getConvoTitle());
            modalReadingCardSubMenuList = modalReadingCardMenu.getConvoList();
            String cardAudio = modalReadingCardMenu.getAudio();
            readingView.setCardAudio(cardAudio);
            readingView.setListData(modalReadingCardSubMenuList);
            readingView.initializeContent();
            addScore("Start");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    public void addScore(String comicConvo) {
        try {
            String deviceId = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(resId);
            score.setQuestionId(0);
            score.setScoredMarks(0);
            score.setTotalMarks(0);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, ""));
            score.setStartDateTime(FC_Utility.getCurrentDateTime());
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(0);
            score.setSentFlag(0);
            score.setLabel("comicConvo - " + comicConvo);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            BackupDatabase.backup(context);
            e.printStackTrace();
        }
    }

    @Override
    public void setResId(String resId) {
        this.resId = resId;
    }

    @Override
    public void addCompletion() {
        addScore("End");
        addActualCompletion();
    }

    @Background
    public void addActualCompletion() {
        try {
            float perc = 0f;
            int cCounter = 0;
            try {
                for (int i = 0; i < correctArr.length; i++) {
                    if (correctArr[i])
                        cCounter += 1;
                }
                perc = ((float) cCounter / (float) correctArr.length) * 100;
            } catch (Exception e) {
                perc = 0f;
            }

            ContentProgress contentProgress = new ContentProgress();
            contentProgress.setProgressPercentage("" + perc);
            contentProgress.setResourceId("" + resId);
            contentProgress.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            contentProgress.setStudentId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("resourceProgress");
            contentProgress.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}