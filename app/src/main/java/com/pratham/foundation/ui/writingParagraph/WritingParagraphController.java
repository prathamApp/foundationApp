package com.pratham.foundation.ui.writingParagraph;

import com.pratham.foundation.ui.identifyKeywords.QuestionModel;

public interface WritingParagraphController {
    public interface View {
        public void showParagraph(QuestionModel questionModel);
    }

    public interface Presenter {
        public void getData();
    }
}
