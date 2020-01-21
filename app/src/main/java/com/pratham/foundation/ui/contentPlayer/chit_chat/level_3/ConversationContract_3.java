package com.pratham.foundation.ui.contentPlayer.chit_chat.level_3;

import com.pratham.foundation.modalclasses.Message;

import java.util.List;

/**
 * Created by Ketan on 23-Nov-17.
 */

public interface ConversationContract_3 {

    interface ConversationView_3{
        void setConvoJson(String returnStoryNavigate, int quetionID);
       // void setAnsCorrect(boolean[] ansCorrect);

        void sendClikChanger(int i);

        void dataNotFound();
      //  void submitAns(String[] splitQues);

       // void clearMonkAnimation();
    }

    interface ConversationPresenter_3 {
        void setView(ConversationView_3 ConversationView,String resId,String gameName,String resStartTime);

        void fetchStory(String convoPath);
        void addLearntWords(List<Message> messageList);
       // void setCorrectArray(int length);

      //  void sttResultProcess(ArrayList<String> sttServerResult, String answer);

      //  float getPercentage();

        public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label, String resId, boolean addInAssessment);

       // void addCompletion(float perc);
    }

}