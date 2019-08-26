package com.example.foundationapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.foundationapp.factRetrial.FactRetrial_;
import com.example.foundationapp.identifyKeywords.IdentifyKeywordsActivity;
import com.example.foundationapp.writingParagraph.WritingParagraph;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

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
