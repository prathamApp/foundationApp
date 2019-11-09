package com.pratham.foundation.ui.contentPlayer.pictionary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PictionaryResult extends AppCompatActivity {
    ArrayList<ScienceQuestion> quetions;
    String readingContentPath;

    @BindView(R.id.result)
    RecyclerView result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictionary_result);
        ButterKnife.bind(this);
        quetions = (ArrayList<ScienceQuestion>) getIntent().getSerializableExtra("selectlist");
        readingContentPath = getIntent().getStringExtra("readingContentPath");

        ResultAdapterS resultAdapter = new ResultAdapterS(quetions, this, readingContentPath);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        result.setLayoutManager(linearLayoutManager);
        result.setAdapter(resultAdapter);
    }

    /*@Override
    public void onBackPressed() {
        //super.onBackPressed();
    }*/
}
