package com.pratham.foundation.ui.contentPlayer.paragraph_writing;

import com.pratham.foundation.modalclasses.ScienceQuestion;


public interface ParagraphWritingContract {
    public interface ParagraphWritingView {
        public void showParagraph(ScienceQuestion questionModel);
    }

    public interface ParagraphWritingPresenter {
        public void getData();

        public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);

       // public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName);

        public void addLearntWords(ScienceQuestion questionModel, String imageName);

        void setView(ParagraphWritingContract.ParagraphWritingView paragraphWritingView, String imageName, String readingContentPath);
    }
}
