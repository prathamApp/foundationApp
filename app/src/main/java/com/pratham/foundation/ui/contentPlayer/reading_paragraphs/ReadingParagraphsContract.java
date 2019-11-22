package com.pratham.foundation.ui.contentPlayer.reading_paragraphs;

import com.pratham.foundation.modalclasses.ModalParaWord;

import java.util.ArrayList;
import java.util.List;


public interface ReadingParagraphsContract {

    interface ReadingParagraphsView {
        void setListData(List<ModalParaWord> paraDataList);

        void setCategoryTitle(String title);

        void setParaAudio(String paraAudio);

        void initializeContent();

        void allCorrectAnswer();

        void setCorrectViewColor();

        void dismissLoadingDialog();
        
        void showLoader();
    }

    interface ReadingParagraphsPresenter {
        void fetchJsonData(String contentPath, String resType);

        void getDataList();

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);

        void setResId(String resId);

        void sttResultProcess(ArrayList<String> sttResult, List<String> splitWordsPunct, List<String> wordsResIdList);

        void exitDBEntry();

        int getCorrectCounter();

        int getTotalCount();

        void addParaVocabProgress();

        void setView(ReadingParagraphsContract.ReadingParagraphsView readingView);

        void micStopped(List<String> splitWordsPunct, List<String> wordsResIdList);
    }

}
