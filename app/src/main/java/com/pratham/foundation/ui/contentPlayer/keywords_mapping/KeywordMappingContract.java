package com.pratham.foundation.ui.contentPlayer.keywords_mapping;

import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.modalclasses.keywordmapping;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;

import java.util.List;

public interface KeywordMappingContract {
    public interface KeywordMappingView {
        void loadUI(List<ScienceQuestion> list);
        public void showResult(List correctWord, List wrongWord);
    }

    public interface KeywordMappingPresenter {
        void getData();

        //void setView(String contentTitle, String resId);

        void getDataList();

        void addLearntWords(ScienceQuestion keywordmapping, List<ScienceQuestionChoice> selectedOption);

        void setView(KeywordMappingContract.KeywordMappingView keywordMappingView, String resId,String readingContentPath);
        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime,String resEndTime, String Label);
    }
}
