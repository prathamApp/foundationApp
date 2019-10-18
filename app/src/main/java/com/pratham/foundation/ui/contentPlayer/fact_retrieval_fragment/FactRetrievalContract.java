package com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment;

import com.pratham.foundation.database.domain.QuetionAns;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.QuestionModel;

import java.util.List;

public interface FactRetrievalContract {
    public interface FactRetrievalView {
        public void showParagraph(QuestionModel questionModel);
    }

    public interface FactRetrievalPresenter {
        void getData(String readingContentPath);
        void setView(FactRetrievalContract.FactRetrievalView factRetrievalView, String contentTitle, String resId);
        void getDataList();
        void addLearntWords(List<QuetionAns> selectedQuetion);
        float checkAnswer(QuetionAns selectedAnsList);
    }

}
