package com.pratham.foundation.ui.contentPlayer.pictionary;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.fontsview.SansButton;
import com.pratham.foundation.modalclasses.ParaSttQuestionListModel;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment.ResultAdapterFactRetrieval;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PictionaryResult extends BaseActivity {

    ArrayList<ScienceQuestion> quetions;
    ArrayList<ScienceQuestionChoice> quetionsFact;
    String readingContentPath;
    String resourceType;

    @BindView(R.id.result)
    RecyclerView result;

    @BindView(R.id.dia_btn_green)
    SansButton dia_btn_green;

    List<ParaSttQuestionListModel> paraSttQuestionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictionary_result);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ButterKnife.bind(this);
        paraSttQuestionList = (ArrayList<ParaSttQuestionListModel>) getIntent()
                .getSerializableExtra("paraSttQuestionList");
        quetions = (ArrayList<ScienceQuestion>) getIntent().getSerializableExtra("selectlist");
        quetionsFact = (ArrayList<ScienceQuestionChoice>) getIntent().getSerializableExtra("quetionsFact");
        readingContentPath = getIntent().getStringExtra("readingContentPath");
        resourceType = getIntent().getStringExtra("resourceType");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        result.setLayoutManager(linearLayoutManager);

        switch (resourceType) {
            case GameConstatnts.FACTRETRIEVAL:
                ResultAdapterFactRetrieval resultAdapterFactRetrieval = new ResultAdapterFactRetrieval(quetionsFact, this, readingContentPath);
                result.setAdapter(resultAdapterFactRetrieval);
                break;
            case GameConstatnts.SHOW_ME_ANDROID:
                ResultAdapterS resultAdapter = new ResultAdapterS(quetions, this, readingContentPath);
                result.setAdapter(resultAdapter);
                break;
            case GameConstatnts.PARAQA:
                ParaSttResultAdapter paraSttResultAdapter = new ParaSttResultAdapter(paraSttQuestionList, this);
                result.setAdapter(paraSttResultAdapter);
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }

    @OnClick(R.id.dia_btn_green)
    public void onNext() {
        setResult(111);
        finish();
    }
}
