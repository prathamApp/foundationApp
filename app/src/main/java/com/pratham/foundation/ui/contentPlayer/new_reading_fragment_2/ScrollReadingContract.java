package com.pratham.foundation.ui.contentPlayer.new_reading_fragment_2;
import com.pratham.foundation.modalclasses.ModalParaSubMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ameya on 23-Nov-17.
 **/

public interface ScrollReadingContract {

    interface ScrollReadingView {
        void setListData(List<ModalParaSubMenu> paraDataList);

        void setCategoryTitle(String title);

        void setParaAudio(String paraAudio);

        void initializeContent(int pageNo);

        void allCorrectAnswer();

        void setCorrectViewColor();

        void dismissLoadingDialog();

        void showLoader();
    }

    interface ScrollReadingPresenter {
        void fetchJsonData(String contentPath);

        void getDataList();

        void getPage(int pgNo);

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);

        void addExitScore(float perc, String Label);

        void setResId(String resId);

        void sttResultProcess(ArrayList<String> sttResult, List<String> splitWordsPunct, List<String> wordsResIdList);

        void addProgress();

        void setView(ScrollReadingView readingView);

        void micStopped(List<String> splitWordsPunct, List<String> wordsResIdList);
    }
}
