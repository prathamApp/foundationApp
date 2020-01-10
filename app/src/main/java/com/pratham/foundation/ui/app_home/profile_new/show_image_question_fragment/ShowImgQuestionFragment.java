package com.pratham.foundation.ui.app_home.profile_new.show_image_question_fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;


@EFragment(R.layout.fragment_profile)
public class ShowImgQuestionFragment extends Fragment implements ShowImgQuestionContract.ShowImgQuestionView{

    @Bean(ShowImgQuestionPresenter.class)
    ShowImgQuestionContract.ShowImgQuestionPresenter presenter;

    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;
    @ViewById(R.id.tv_studentName)
    TextView tv_studentName;
    @ViewById(R.id.tv_usage)
    TextView tv_usage;
    @ViewById(R.id.tv_days)
    TextView tv_days;
    @ViewById(R.id.certi1_perc)
    TextView certi1_perc;
    @ViewById(R.id.certi1_subj)
    TextView certi1_subj;
    @ViewById(R.id.certi2_perc)
    TextView certi2_perc;
    @ViewById(R.id.certi2_subj)
    TextView certi2_subj;
    @ViewById(R.id.certi3_perc)
    TextView certi3_perc;
    @ViewById(R.id.certi3_subj)
    TextView certi3_subj;
    @ViewById(R.id.rl_certi1)
    RelativeLayout rl_certi1;
    @ViewById(R.id.rl_certi2)
    RelativeLayout rl_certi2;
    @ViewById(R.id.rl_certi3)
    RelativeLayout rl_certi3;
    @ViewById(R.id.ib_langChange)
    ImageButton ib_langChange;

    Context context;

    @AfterViews
    public void initialize() {
        FC_Constants.isTest = false;
        FC_Constants.isPractice = false;
        context = getActivity();
        presenter.setView(ShowImgQuestionFragment.this);
    }

}