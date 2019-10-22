package com.pratham.foundation.ui.contentPlayer.paragraph_writing;

import android.graphics.Bitmap;

import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;


public interface ParagraphWritingContract {
    public interface ParagraphWritingView {
        public void showParagraph(ScienceQuestion questionModel);
    }


    public interface ParagraphWritingPresenter {
        public void getData();

        public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName);

        public void addLearntWords(ScienceQuestion questionModel, String imageName);

        void setView(ParagraphWritingContract.ParagraphWritingView paragraphWritingView, String imageName, String readingContentPath);
    }
}
