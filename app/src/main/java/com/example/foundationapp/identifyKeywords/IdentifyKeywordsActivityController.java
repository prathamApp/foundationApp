package com.example.foundationapp.identifyKeywords;

public interface IdentifyKeywordsActivityController {
    public interface View {
        public void showParagraph(QuestionModel questionModel);
    }

    public interface Presenter {
        public void getData();
    }

}
