package com.pratham.foundation.ui.app_home.profile_new.show_image_question;

import android.content.Context;

import org.androidannotations.annotations.EBean;


@EBean
public class ShowImgQuestionPresenter implements ShowImgQuestionContract.ShowImgQuestionPresenter {

    private Context mContext;
    private ShowImgQuestionContract.ShowImgQuestionView ShowImgQuestionView;

    public ShowImgQuestionPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(ShowImgQuestionContract.ShowImgQuestionView ShowImgQuestionView) {
        this.ShowImgQuestionView = ShowImgQuestionView;
    }

}