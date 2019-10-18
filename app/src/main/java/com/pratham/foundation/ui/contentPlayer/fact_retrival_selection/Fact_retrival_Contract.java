package com.pratham.foundation.ui.contentPlayer.fact_retrival_selection;

import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.QuestionModel;

import java.util.List;

public interface Fact_retrival_Contract {

    public interface Fact_retrival_View {
        public void showParagraph(ScienceQuestion questionModel);

        /*public void showResult(List correctWord, List wrongWord);*/
    }

    public interface Fact_retrival_Presenter {
        public void getData();

        void setView(Fact_retrival_Contract.Fact_retrival_View viewKeywords, String resId,String readingContentPath);

        void addLearntWords(List<ScienceQuestionChoice> selectedQuetion);
    }

}
