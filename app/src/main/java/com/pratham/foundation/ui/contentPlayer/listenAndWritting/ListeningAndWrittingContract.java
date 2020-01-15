package com.pratham.foundation.ui.contentPlayer.listenAndWritting;

import com.pratham.foundation.modalclasses.ScienceQuestion;

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

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label,String resId, boolean addInAssessment);

     //   void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName);
        //   void setView(ListeningAndWrittingContract.ListeningAndWrittingView listeningAndWrittingView);
    }
}
