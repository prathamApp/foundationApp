package com.pratham.foundation.ui.contentPlayer.paragraph_writing;

import com.pratham.foundation.modalclasses.ScienceQuestion;

import java.util.List;


public interface ParagraphWritingContract {
    interface ParagraphWritingView {
        void showParagraph(List<ScienceQuestion> questionModel);
    }

    interface ParagraphWritingPresenter {
        void getData();

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label, String resId, String Misc);

       // public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName);

        void addLearntWords(List<ScienceQuestion> questionModel);

        void setView(ParagraphWritingView paragraphWritingView, String imageName, String readingContentPath, String jsonName, String contentTitle);

        boolean checkIsAttempted(ScienceQuestion scienceQuestion);
    }
}
