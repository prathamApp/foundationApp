package com.example.foundationapp.factRetrial;

import com.example.foundationapp.identifyKeywords.QuestionModel;

public interface FactRetrialController {
    public interface View {
        public void showParagraph(QuestionModel questionModel);
    }

    public interface Presenter {
        public void getData();
    }

}
