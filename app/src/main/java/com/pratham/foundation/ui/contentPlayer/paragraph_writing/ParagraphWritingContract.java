package com.pratham.foundation.ui.contentPlayer.paragraph_writing;

import android.graphics.Bitmap;

import com.pratham.foundation.ui.identifyKeywords.QuestionModel;

public interface ParagraphWritingContract {
    public interface ParagraphWritingView {
        public void showParagraph(QuestionModel questionModel);
    }

    public interface ParagraphWritingPresenter {
        public void getData();
        public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName);
        public void addLearntWords(QuestionModel questionModel,String imageName);
        void setView(ParagraphWritingContract.ParagraphWritingView paragraphWritingView,String imageName);
    }
}
