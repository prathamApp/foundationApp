package com.example.foundationapp.writingParagraph;

import com.example.foundationapp.identifyKeywords.QuestionModel;

public interface WritingParagraphController {
    public interface View {
        public void showParagraph(QuestionModel questionModel);
    }

    public interface Presenter {
        public void getData();
    }
}
