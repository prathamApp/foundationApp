package com.pratham.foundation.ui.contentPlayer.listenAndWritting;

import android.graphics.Bitmap;

import com.pratham.foundation.modalclasses.keywordmapping;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;

import java.util.List;

public interface ListeningAndWrittingContract {

    public interface ListeningAndWrittingView {
        void loadUI(List<ScienceQuestion> list);
    }

    public interface ListeningAndWrittingPresenter {

        void fetchJsonData(String contentPath);

        void setView(ListeningAndWrittingContract.ListeningAndWrittingView listeningAndWrittingView, String contentTitle, String resId);

        void getDataList();

        void addLearntWords(List<ScienceQuestion> listenAndWrittingModal, String imageName);

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);

        void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName);
        //   void setView(ListeningAndWrittingContract.ListeningAndWrittingView listeningAndWrittingView);
    }
}
