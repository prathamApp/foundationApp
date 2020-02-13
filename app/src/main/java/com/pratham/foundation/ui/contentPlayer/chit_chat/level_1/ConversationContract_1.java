package com.pratham.foundation.ui.contentPlayer.chit_chat.level_1;

import com.pratham.foundation.modalclasses.Message;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ketan on 23-Nov-17.
 */

public interface ConversationContract_1 {

    interface ConversationView_1{
        void setConvoJson(JSONArray returnStoryNavigate,String convoTitle);
        void setAnsCorrect(boolean[] ansCorrect);

        void sendClikChanger(int i);

        void submitAns(String[] splitQues);

        void clearMonkAnimation();
    }

    interface ConversationPresenter_1 {
        void setView(ConversationView_1 ConversationView,String resId,String gameName,String resStartTime);

        void fetchStory(String convoPath);

        void setCorrectArray(int length);
        void addLearntWords(List<Message> messageList);
      //  void sttResultProcess(ArrayList<String> sttServerResult, String answer);

        float getPercentage();

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label, String resId, boolean addInAssessment);
        void setCompletionPercentage(int totalWordCount, int learntWordCount);
        //void setStartTime(String currentDateTime);

      //  void setContentId(String contentId);

       // void addCompletion(float perc);
    }

}