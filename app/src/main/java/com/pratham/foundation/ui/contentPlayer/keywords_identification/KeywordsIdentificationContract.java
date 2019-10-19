package com.pratham.foundation.ui.contentPlayer.keywords_identification;

import com.pratham.foundation.database.domain.QuetionAns;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;

import java.util.List;

public interface KeywordsIdentificationContract {

    public interface KeywordsView {
        public void showParagraph(ScienceQuestion questionModel);

        public void showResult(List correctWord, List wrongWord);
    }

    public interface KeywordsPresenter {
        public void getData();

        void setView(KeywordsIdentificationContract.KeywordsView viewKeywords, String resId,String readingContentPath);

        void addLearntWords(List selectedQuetion);
    }

}
