package com.pratham.foundation.ui.contentPlayer.fact_retrival_selection;

import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;

import java.util.List;

public interface Fact_Retrieval_Contract {

    public interface Fact_retrival_View {
        public void showParagraph(ScienceQuestion questionModel);

        /*public void showResult(List correctWord, List wrongWord);*/
    }

    public interface Fact_retrival_Presenter {
        public void getData();
        public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);
        void setView(Fact_Retrieval_Contract.Fact_retrival_View viewKeywords, String resId, String readingContentPath);

        void addLearntWords(List<ScienceQuestionChoice> selectedQuetion);
    }

}
