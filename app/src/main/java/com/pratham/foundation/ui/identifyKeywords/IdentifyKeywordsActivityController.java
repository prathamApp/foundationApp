package com.pratham.foundation.ui.identifyKeywords;

public interface IdentifyKeywordsActivityController {
    public interface View {
        public void showParagraph(QuestionModel questionModel);
    }

    public interface Presenter {
        public void getData();
    }

}
