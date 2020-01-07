package com.pratham.foundation.ui.contentPlayer.multipleChoiceQuetion;

import com.pratham.foundation.modalclasses.ScienceQuestion;

import java.util.ArrayList;

public interface MultipleChoiceContract {
    interface MultipleChoiceView {
        void showResult();

        void setData(ArrayList<ScienceQuestion> selectedFive);
    }

    interface MultipleChoicePresenter {
        void setView(MultipleChoiceContract.MultipleChoiceView multipleChoiceView, String resId);

        void getData(String readingContentPath);
        void addLearntWords(ArrayList<ScienceQuestion> selectedAnsList);
        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label);

    }
}
