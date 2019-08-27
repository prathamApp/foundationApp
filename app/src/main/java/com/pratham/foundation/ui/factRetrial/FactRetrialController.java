package com.pratham.foundation.ui.factRetrial;

import com.pratham.foundation.ui.identifyKeywords.QuestionModel;

public interface FactRetrialController {
    public interface View {
        public void showParagraph(QuestionModel questionModel);
    }

    public interface Presenter {
        public void getData();
    }

}
