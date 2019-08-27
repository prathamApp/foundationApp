package com.pratham.foundation;

import android.content.Intent;
import android.os.Bundle;

import com.pratham.foundation.ui.factRetrial.FactRetrial_;
import com.pratham.foundation.ui.identifyKeywords.IdentifyKeywordsActivity;
import com.pratham.foundation.utility.BaseActivity;
import com.pratham.foundation.ui.writingParagraph.WritingParagraph;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.writingParagraph)
    public void writingParagraphTemp() {
        Intent intent = new Intent(this, WritingParagraph.class);
        startActivity(intent);
    }

    @OnClick(R.id.fact_retrial)
    public void fact_retrial() {
        Intent intent = new Intent(this, FactRetrial_.class);
        startActivity(intent);
    }

    @OnClick(R.id.identify_keyword)
    public void identify_keyword() {
        Intent intent = new Intent(this, IdentifyKeywordsActivity.class);
        startActivity(intent);
    }
}
