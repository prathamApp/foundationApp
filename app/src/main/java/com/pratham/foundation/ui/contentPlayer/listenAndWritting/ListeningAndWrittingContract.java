package com.pratham.foundation.ui.contentPlayer.listenAndWritting;

import android.graphics.Bitmap;

import com.pratham.foundation.modalclasses.keywordmapping;

import java.util.List;

public interface ListeningAndWrittingContract {

    public interface ListeningAndWrittingView {
        void loadUI(ListenAndWrittingModal list);
    }

    public interface ListeningAndWrittingPresenter {

        void fetchJsonData(String contentPath);

        void setView(ListeningAndWrittingContract.ListeningAndWrittingView listeningAndWrittingView, String contentTitle, String resId);

        void getDataList();

        void addLearntWords(ListenAndWrittingModal listenAndWrittingModal, String imageName);

        void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName);
        //   void setView(ListeningAndWrittingContract.ListeningAndWrittingView listeningAndWrittingView);
    }
}
