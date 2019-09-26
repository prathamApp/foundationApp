package com.pratham.foundation.ui.contentPlayer.keywords_identification;

public interface KeywordsIdentificationContract {

    public interface KeywordsView {
        public void showParagraph(QuestionModel questionModel);
    }

    public interface KeywordsPresenter {
        public void getData();
        void setView(KeywordsIdentificationContract.KeywordsView viewKeywords);
    }

}
