package com.pratham.foundation.ui.app_home.profile_new.display_image_ques_list;

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
public class ImageQuesPresenter implements ImageQuesContract.ImageQuesPresenter , API_Content_Result {

    Context mContext;
    ImageQuesContract.ImageQuesView imageQuesView;

    public ImageQuesPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(ImageQuesContract.ImageQuesView imageQuesView) {
        this.imageQuesView = imageQuesView;
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
                    .getImageQues(StudId, "%"+FC_Constants.IMG_LBL+"%");

        if(scoreList!=null && scoreList.size()>0)
            imageQuesView.addToAdapter(scoreList);
        else
            imageQuesView.showNoData();
    }

    @Override
    public void receivedContent(String header, String response) {
    }

    @Override
    public void receivedError(String header) {
    }
}