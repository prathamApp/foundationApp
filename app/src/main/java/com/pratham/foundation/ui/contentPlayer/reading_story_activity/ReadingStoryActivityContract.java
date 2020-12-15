package com.pratham.foundation.ui.contentPlayer.reading_story_activity;

import com.pratham.foundation.modalclasses.ModalParaSubMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ameya on 23-Nov-17.
 **/

public interface ReadingStoryActivityContract {

    interface ReadingStoryView {
        void setListData(List<ModalParaSubMenu> paraDataList);

        void setCategoryTitle(String title);

        void setParaAudio(String paraAudio);

        void initializeContent(int pageNo);

        void allCorrectAnswer();

        void setCorrectViewColor();

        void dismissLoadingDialog();

        void showLoader();
    }

    interface ReadingStoryActivityPresenter {
        void fetchJsonData(String contentPath);

        void getDataList();

        void getPage(int pgNo);

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);

        void addExitScore(float perc, String Label);

        void setResId(String resId);

        void sttResultProcess(ArrayList<String> sttResult, List<String> splitWordsPunct, List<String> wordsResIdList);

        void addProgress();

        void setView(ReadingStoryActivityContract.ReadingStoryView readingView);

        void micStopped(List<String> splitWordsPunct, List<String> wordsResIdList);

    }

}
