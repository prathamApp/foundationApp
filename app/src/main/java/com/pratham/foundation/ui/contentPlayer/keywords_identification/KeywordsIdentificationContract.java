package com.pratham.foundation.ui.contentPlayer.keywords_identification;

import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;

import java.util.List;

public interface KeywordsIdentificationContract {

    interface KeywordsView {
        void showParagraph(ScienceQuestion questionModel);

      //  void showAnswer();

        void showResult(List correctWord, List wrongWord);
    }

    interface KeywordsPresenter {
        void getData();

        void setView(KeywordsIdentificationContract.KeywordsView viewKeywords, String resId,String readingContentPath);

        void addLearntWords(List<ScienceQuestionChoice>  selectedQuetion);
        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime,String resEndTime, String Label);
    }

}
