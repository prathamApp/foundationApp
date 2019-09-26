package com.pratham.foundation.ui.contentPlayer.keywords_mapping;

import com.pratham.foundation.modalclasses.keywordmapping;

import java.util.List;

public interface KeywordMappingContract {
    public interface KeywordMappingView {
       void loadUI(List<keywordmapping> list);
    }

    public interface KeywordMappingPresenter {
        void getData();
        void setView(String contentTitle, String resId);
        void getDataList();

        void setView(KeywordMappingContract.KeywordMappingView keywordMappingView);
    }
}
