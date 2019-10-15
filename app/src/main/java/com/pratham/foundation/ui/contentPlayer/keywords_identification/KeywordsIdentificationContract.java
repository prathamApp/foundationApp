package com.pratham.foundation.ui.contentPlayer.keywords_identification;

import com.pratham.foundation.database.domain.QuetionAns;

import java.util.List;

public interface KeywordsIdentificationContract {

    public interface KeywordsView {
        public void showParagraph(QuestionModel questionModel);

        public void showResult(List correctWord, List wrongWord);
    }

    public interface KeywordsPresenter {
        public void getData();

        void setView(KeywordsIdentificationContract.KeywordsView viewKeywords, String resId);

        void addLearntWords(List selectedQuetion);
    }

}
