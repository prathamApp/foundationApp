package com.pratham.foundation.ui.contentPlayer.pictionary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment.ResultAdapterFactRetrieval;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PictionaryResult extends AppCompatActivity {
    ArrayList<ScienceQuestion> quetions;
    ArrayList<ScienceQuestionChoice> quetionsFact;
    String readingContentPath;
    String resourceType;

    @BindView(R.id.result)
    RecyclerView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictionary_result);
        ButterKnife.bind(this);
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
            case GameConstatnts.READINGGAME:
                ResultAdapterS resultAdapter = new ResultAdapterS(quetions, this, readingContentPath);
                result.setAdapter(resultAdapter);
                break;

        }


    }

    /*@Override
    public void onBackPressed() {
        //super.onBackPressed();
    }*/
}
