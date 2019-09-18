package com.pratham.foundation.ui.factRetrial;

import com.pratham.foundation.database.domain.QuetionAns;

import java.util.List;

public interface FactRetrialController {
    public interface View {
        public void showParagraph(String para, List<QuetionAns> list);
    }

    public interface Presenter {
        void getData();
        void setView(String contentTitle, String resId);
        void getDataList();
    }

}
