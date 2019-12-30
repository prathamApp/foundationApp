package com.pratham.foundation.ui.contentPlayer.reading;

import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;

import java.util.List;

public interface ReadingFragment_Contract {
    interface ReadingFragmentView {
        void showQuestion(ScienceQuestion scienceQuestion);
        void showResult();
    }

    interface ReadingFragmentPresenter {
        void setView(ReadingFragment_Contract.ReadingFragmentView view,String jsonName, String resId,String resStartTime);

        void getData(String readingContentPath);

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);

        void addLearntWords(List<ScienceQuestionChoice> scienceQuestionChoices);
        boolean checkAttemptedornot(List<ScienceQuestionChoice> selectedAnsList);
    }
}
