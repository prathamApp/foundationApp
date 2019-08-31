package com.pratham.foundation.ui.writingParagraph;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.pratham.foundation.R;
import com.pratham.foundation.ui.identifyKeywords.QuestionModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
@EActivity(R.layout.activity_writing_paragraph)
public class WritingParagraph extends AppCompatActivity implements WritingParagraphController.View {
    private WritingParagraphController.Presenter presenter;
    private Context context;
    private int index = 0;

    @ViewById(R.id.paragraph)
    RecyclerView paragraph;
    @ViewById(R.id.scrollView)
    ScrollView scrollView;
    @ViewById(R.id.previous)
    Button previous;
    @ViewById(R.id.capture)
    Button capture;
    @ViewById(R.id.next)
    Button next;

    private String[] paragraphWords;
    private RelativeLayout.LayoutParams viewParam;
    private LinearLayoutManager layoutManager;
    private RecyclerView.SmoothScroller smoothScroller;


    @AfterViews
    protected void initiate() {
       // super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_writing_paragraph);
       // ButterKnife.bind(this);
        context = WritingParagraph.this;
        presenter = new WritingParagraphPresenter(this);
        viewParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        // viewParam.setMargins(10, 20, 10, 5);
        presenter.getData();
       /* smoothScroller = new LinearSmoothScroller(context) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };*/
    }

    @Override
    public void showParagraph(QuestionModel questionModel) {
        paragraphWords = questionModel.getParagraph().trim().split("(?<=\\.\\s)|(?<=[?!]\\s)");
        SentenceAdapter arrayAdapter = new SentenceAdapter(Arrays.asList(paragraphWords), context);
        paragraph.setAdapter(arrayAdapter);
        // paragraph.setOrientation(DSVOrientation.VERTICAL);
        //  layoutManager = new LinearLayoutManager(this);
        // paragraph.setLayoutManager(layoutManager);


        // RecyclerView rv = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        paragraph.setLayoutManager(layoutManager);
        //layoutManager.smoothScrollToPosition(paragraph,0,4);
        layoutManager.scrollToPositionWithOffset(4, 20);


       /*  for (int i = 0; i < paragraphWords.length; i++) {
            final TextView textView = new TextView(context);
            textView.setTextSize(30);
            textView.setPadding(5, 5, 5, 5);
            textView.setText(paragraphWords[i] + " ");
           // textView.setLayoutParams(viewParam);
            paragraph.addView(textView);
        }*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = paragraph.getChildAt(index);
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_border_yellow));
            }
        }, 300);
    }

    @OnClick(R.id.previous)
    public void showPrevios() {
        if (index > 0) {
            View view = paragraph.getChildAt(index);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setElevation(0);
            }
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            index--;
            //   paragraph.requestChildFocus(paragraph.getChildAt(index), paragraph.getChildAt(index + 1));
            highlightText();
        }
    }

    @OnClick(R.id.next)
    public void showNext() {

        if (index < (paragraph.getAdapter().getItemCount() - 1)) {
            View view = paragraph.getChildAt(index);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setElevation(0);
            }
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            index++;
            highlightText();
        }
    }

    private void highlightText() {
        // paragraph.smoothScrollToPosition(index);
        View view = paragraph.getChildAt(index);
        paragraph.getLayoutManager().scrollToPosition(index + 1);
        view.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_border_yellow));
    }
}
