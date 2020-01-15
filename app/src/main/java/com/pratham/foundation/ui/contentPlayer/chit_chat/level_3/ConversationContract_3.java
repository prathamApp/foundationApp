package com.pratham.foundation.ui.contentPlayer.chit_chat.level_3;

import org.json.JSONArray;

/**
 * Created by Ketan on 23-Nov-17.
 */

public interface ConversationContract_3 {

    interface ConversationView_3{
        void setConvoJson(JSONArray returnStoryNavigate);
        void setAnsCorrect(boolean[] ansCorrect);

        void sendClikChanger(int i);

        void submitAns(String[] splitQues);

        void clearMonkAnimation();
    }

    interface ConversationPresenter_3 {
        void setView(ConversationView_3 ConversationView);

        void fetchStory(String convoPath);

        void setCorrectArray(int length);

      //  void sttResultProcess(ArrayList<String> sttServerResult, String answer);

        float getPercentage();

        void addScore(int i, String s, int i1, int i2, String convo_end);

        void setStartTime(String currentDateTime);

        void setContentId(String contentId);

        void addCompletion(float perc);
    }

}