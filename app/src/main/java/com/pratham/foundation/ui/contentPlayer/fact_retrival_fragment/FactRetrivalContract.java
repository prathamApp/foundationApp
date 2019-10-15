package com.pratham.foundation.ui.contentPlayer.fact_retrival_fragment;

import com.pratham.foundation.database.domain.QuetionAns;
import com.pratham.foundation.ui.identifyKeywords.QuestionModel;

import java.util.List;

public interface FactRetrivalContract {
    public interface FactRetrivalView {
        public void showParagraph(QuestionModel questionModel);
    }

    public interface FactRetrivalPresenter {
        void getData(String readingContentPath);
        void setView(FactRetrivalContract.FactRetrivalView factRetrivalView, String contentTitle, String resId);
        void getDataList();
        void addLearntWords(List<QuetionAns> selectedQuetion);
        float checkAnswer(QuetionAns selectedAnsList);
    }

}
