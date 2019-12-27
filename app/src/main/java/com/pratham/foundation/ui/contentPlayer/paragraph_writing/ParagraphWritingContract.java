package com.pratham.foundation.ui.contentPlayer.paragraph_writing;

import com.pratham.foundation.modalclasses.ScienceQuestion;

import java.util.List;


public interface ParagraphWritingContract {
    public interface ParagraphWritingView {
        public void showParagraph(List<ScienceQuestion> questionModel);
    }

    public interface ParagraphWritingPresenter {
        public void getData();

        public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);

       // public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName);

        public void addLearntWords(List<ScienceQuestion> questionModel);

        public void setView(ParagraphWritingView paragraphWritingView, String imageName, String readingContentPath,String jsonName);

        public boolean checkIsAttempted(ScienceQuestion scienceQuestion);
    }
}
