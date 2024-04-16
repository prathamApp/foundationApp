package com.pratham.foundation.ui.contentPlayer.word_writting;

import android.graphics.Bitmap;

import com.pratham.foundation.modalclasses.ScienceQuestion;

import java.util.List;


public interface WordWritingContract {
    interface WordWritingView {
        void showParagraph(List<ScienceQuestion> questionModel);
    }


    interface WordWritingPresenter {
        void getData();

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label, String resId, String misc);

        void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName);

        void addLearntWords(List<ScienceQuestion> questionModel, String imageName);

        void setView(WordWritingContract.WordWritingView wordWritingView, String imageName, String readingContentPath,String contentTitle);
    }
}
