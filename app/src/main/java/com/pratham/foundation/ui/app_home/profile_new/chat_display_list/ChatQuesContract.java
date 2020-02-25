package com.pratham.foundation.ui.app_home.profile_new.chat_display_list;
import com.pratham.foundation.database.domain.Score;

import java.util.List;

public interface ChatQuesContract {

    interface ChatQuesItemClicked {
        void gotoQuestions(Score score);
    }

    interface ChatQuesView {
        void addToAdapter(List<Score> scoreList);

        void showNoData();
    }

    interface ChatQuesPresenter {
        void setView(ChatQuesContract.ChatQuesView chatQuesView);

        void showQuestion();
    }
}
