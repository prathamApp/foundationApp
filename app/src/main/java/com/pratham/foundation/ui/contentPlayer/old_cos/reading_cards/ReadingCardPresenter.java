package com.pratham.foundation.ui.contentPlayer.old_cos.reading_cards;

import android.content.Context;

import com.google.gson.Gson;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.modalclasses.ModalReadingCardMenu;
import com.pratham.foundation.modalclasses.ModalReadingCardSubMenu;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.ui.contentPlayer.old_cos.reading_cards.ReadingCardsActivity.correctArr;


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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setResId(String resId) {
        this.resId = resId;
    }

    @Override
    public void addCompletion() {
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
            contentProgress.setSessionId("" + FC_Constants.currentSession);
            contentProgress.setStudentId("" + FC_Constants.currentStudentID);
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("resourceProgress");
            contentProgress.setSentFlag(0);
            appDatabase.getContentProgressDao().insert(contentProgress);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}