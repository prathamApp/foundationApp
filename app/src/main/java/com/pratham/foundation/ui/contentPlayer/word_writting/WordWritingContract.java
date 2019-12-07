package com.pratham.foundation.ui.contentPlayer.word_writting;

import android.graphics.Bitmap;

import com.pratham.foundation.modalclasses.ScienceQuestion;

import java.util.List;


public interface WordWritingContract {
    public interface WordWritingView {
        public void showParagraph(List<ScienceQuestion> questionModel);
    }


    public interface WordWritingPresenter {
        public void getData();

        public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);

        public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName);

        public void addLearntWords(List<ScienceQuestion> questionModel, String imageName);

        void setView(WordWritingContract.WordWritingView wordWritingView, String imageName, String readingContentPath);
    }
}
