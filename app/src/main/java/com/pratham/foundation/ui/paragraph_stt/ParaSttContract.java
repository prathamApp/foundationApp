package com.pratham.foundation.ui.paragraph_stt;
import com.pratham.foundation.modalclasses.ModalParaSubMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ameya on 23-Nov-17.
 **/

public interface ParaSttContract {

    interface ParaSttView {
        void setListData(List<ModalParaSubMenu> paraDataList);

        void setCategoryTitle(String title);

        void setParaAudio(String paraAudio);

        void initializeContent(int pageNo);

        void allCorrectAnswer();

        void setCorrectViewColor();

        void dismissLoadingDialog();

        void showLoader();
    }

    interface ParaSttPresenter {
        void fetchJsonData(String contentPath);

        void getDataList();

        void getPage(int pgNo);

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);

        void addExitScore(float perc, String Label);

        void setResId(String resId);

        void sttResultProcess(ArrayList<String> sttResult, List<String> splitWordsPunct, List<String> wordsResIdList);

        void addProgress();

        void setView(ParaSttContract.ParaSttView ParaSttView);

        void micStopped(List<String> splitWordsPunct, List<String> wordsResIdList);
    }

}
