package com.pratham.foundation.ui.contentPlayer.keywords_mapping;

import com.pratham.foundation.modalclasses.keywordmapping;

import java.util.List;

public interface KeywordMappingContract {
    public interface KeywordMappingView {
        void loadUI(List<keywordmapping> list);
        public void showResult(List correctWord, List wrongWord);
    }

    public interface KeywordMappingPresenter {
        void getData();

        //void setView(String contentTitle, String resId);

        void getDataList();

        void addLearntWords(keywordmapping keywordmapping, List selectedOption);

        void setView(KeywordMappingContract.KeywordMappingView keywordMappingView, String resId);
    }
}
