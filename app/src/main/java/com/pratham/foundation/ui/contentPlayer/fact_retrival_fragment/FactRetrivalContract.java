package com.pratham.foundation.ui.contentPlayer.fact_retrival_fragment;

import com.pratham.foundation.database.domain.QuetionAns;

import java.util.List;

public interface FactRetrivalContract {
    public interface FactRetrivalView {
        public void showParagraph(String para, List<QuetionAns> list);
    }

    public interface FactRetrivalPresenter {
        void getData();
        void setView(FactRetrivalContract.FactRetrivalView factRetrivalView, String contentTitle, String resId);
        void getDataList();
    }

}
