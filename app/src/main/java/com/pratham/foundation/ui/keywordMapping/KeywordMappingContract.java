package com.pratham.foundation.ui.keywordMapping;

import com.pratham.foundation.modalclasses.keywordmapping;

import java.util.List;

public interface KeywordMappingContract {
    public interface View {
       void loadUI(List<keywordmapping> list);
    }

    public interface Presenter {
        void getData();
        void setView(String contentTitle, String resId);
        void getDataList();
    }
}
