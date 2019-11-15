package com.pratham.foundation.ui.contentPlayer.keywords_identification;

import com.pratham.foundation.database.domain.QuetionAns;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;
import com.pratham.foundation.utility.FC_Utility;

import java.util.List;

public interface KeywordsIdentificationContract {

    public interface KeywordsView {
        public void showParagraph(ScienceQuestion questionModel);

        public void showResult(List correctWord, List wrongWord);
    }

    public interface KeywordsPresenter {
        public void getData();

        void setView(KeywordsIdentificationContract.KeywordsView viewKeywords, String resId,String readingContentPath);

        void addLearntWords(List<ScienceQuestionChoice>  selectedQuetion);
        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime,String resEndTime, String Label);
    }

}
