package com.pratham.foundation.ui.contentPlayer.pictionary;

import com.pratham.foundation.modalclasses.ScienceQuestion;

import java.util.ArrayList;

public interface PictionaryContract {
    interface PictionaryView {
        void setData(ArrayList<ScienceQuestion> selectedFive);

        void showResult();
    }

    interface PictionaryPresenter {
        void setView(PictionaryContract.PictionaryView pictionaryView, String resId);

        void getData(String readingContentPath);

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label);

        void addLearntWords(ArrayList<ScienceQuestion> selectedAnsList);
    }
}
