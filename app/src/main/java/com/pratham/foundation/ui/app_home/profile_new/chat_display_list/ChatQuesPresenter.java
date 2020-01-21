package com.pratham.foundation.ui.app_home.profile_new.chat_display_list;

import android.content.Context;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.List;

@EBean
public class ChatQuesPresenter implements ChatQuesContract.ChatQuesPresenter , API_Content_Result {

    Context mContext;
    ChatQuesContract.ChatQuesView chatQuesView;

    public ChatQuesPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(ChatQuesContract.ChatQuesView chatQuesView) {
        this.chatQuesView = chatQuesView;
    }

    @Background
    @Override
    public void showQuestion() {
        List<Score> scoreList;
        String StudId = FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "");
//        if(GROUP_LOGIN)
//            scoreList = AppDatabase.getDatabaseInstance(mContext).getScoreDao()
//                    .getImageQuesGroups(FC_Constants.currentGroup, FC_Constants.CERTIFICATE_LBL);
//        else
            scoreList = AppDatabase.getDatabaseInstance(mContext).getScoreDao()
                    .getImageQues(StudId, "%"+FC_Constants.CHIT_CHAT_LBL+"%");

        chatQuesView.addToAdapter(scoreList);
    }

    @Override
    public void receivedContent(String header, String response) {
    }

    @Override
    public void receivedError(String header) {
    }
}