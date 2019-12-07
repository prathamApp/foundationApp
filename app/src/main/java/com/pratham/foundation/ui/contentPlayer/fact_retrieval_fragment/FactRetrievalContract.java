package com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment;

import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.modalclasses.ScienceQuestion;

import java.util.ArrayList;

public interface FactRetrievalContract {
    public interface FactRetrievalView {
        public void showParagraph(ScienceQuestion questionModel);
        public void showResult(ArrayList<ScienceQuestionChoice> selectedQuestion);
    }

    public interface FactRetrievalPresenter {
        void getData(String readingContentPath);

        void setView(FactRetrievalContract.FactRetrievalView factRetrievalView, String contentTitle, String resId);

        void getDataList();

        void addLearntWords(ArrayList<ScienceQuestionChoice> selectedQuetion);

        float checkAnswer(ScienceQuestionChoice selectedAnsList);
        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime,String resEndTime, String Label);
    }

}
