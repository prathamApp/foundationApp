package com.pratham.foundation.ui.contentPlayer.keywords_mapping;

import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.modalclasses.ScienceQuestion;

import java.util.List;

public interface KeywordMappingContract {
    public interface KeywordMappingView {
        void loadUI(List<ScienceQuestion> list);
        public void showResult(ScienceQuestion scienceQuestions);
    }

    public interface KeywordMappingPresenter {
        void getData();

        //void setView(String contentTitle, String resId);

        void getDataList();
        boolean checkAnswerNew(List<ScienceQuestionChoice> optionListlist, String word);
        void addLearntWords(ScienceQuestion keywordmapping, List<ScienceQuestionChoice> selectedOption);

        void setView(KeywordMappingContract.KeywordMappingView keywordMappingView, String resId,String readingContentPath);
        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime,String resEndTime, String Label);
    }
}
