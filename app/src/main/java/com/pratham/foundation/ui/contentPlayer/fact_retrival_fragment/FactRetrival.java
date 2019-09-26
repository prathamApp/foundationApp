package com.pratham.foundation.ui.contentPlayer.fact_retrival_fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.QuetionAns;
import com.pratham.foundation.ui.contentPlayer.ContentPlayerActivity;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.KeywordsIdentificationFragment;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.KeywordsIdentificationFragment_;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EFragment(R.layout.fragment_fact_retrival)
public class FactRetrival extends Fragment implements FactRetrivalContract.FactRetrivalView {

    @Bean(FactRetrivalPresenter.class)
    FactRetrivalContract.FactRetrivalPresenter presenter;
    @ViewById(R.id.paragraph)
    TextView paragraph;
    @ViewById(R.id.quetion)
    TextView quetion;

    private String answer, para;
    private String contentPath, contentTitle, StudentID, resId;
    boolean onSdCard;
    private List<QuetionAns> selectedQuetion;
    private int index = 0;
    /*
    @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         //setContentView(R.layout.activity_fact_retrial);
         //  ButterKnife.bind(this);
     } */

    @AfterViews
    public void initiate() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            contentPath =bundle.getString("contentPath");
            StudentID = bundle.getString("StudentID");
            resId = bundle.getString("resId");
            contentTitle= bundle.getString("contentName");
            onSdCard = bundle.getBoolean("onSdCard",false);
        }
//        Intent intent = getIntent();
//        contentPath = intent.getStringExtra("contentPath");
//        StudentID = intent.getStringExtra("StudentID");
//        resId = intent.getStringExtra("resId");
//        contentTitle = intent.getStringExtra("contentName");
//        onSdCard = getIntent().getBooleanExtra("onSdCard", false);

        presenter.setView(FactRetrival.this,contentTitle,resId);
        presenter.getData();
    }

    @Override
    public void showParagraph(String para, List<QuetionAns> selectedQuetionTemp) {
        this.para = para;
        this.selectedQuetion = selectedQuetionTemp;
        showQuetion();
        final int SETANSWER = 0;
        final int CLEAR_ANSWER = 1;
        paragraph.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                menu.removeGroup(1);
                menu.add(0, SETANSWER, 0, "set answer");
                menu.add(0, CLEAR_ANSWER, 1, "clear answer");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Remove the "select all" option
                menu.removeItem(android.R.id.selectAll);
                // Remove the "cut" option
                menu.removeItem(android.R.id.cut);
                // Remove the "copy all" option
                menu.removeItem(android.R.id.copy);
                menu.removeItem(android.R.id.textAssist);
                //    return true;
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case SETANSWER:
                        int start = paragraph.getSelectionStart();
                        int end = paragraph.getSelectionEnd();
                        SpannableString str = new SpannableString(para);
                        str.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.colorBtnOrangeLight)), start, end, 0);
                        answer = para.substring(start, end).trim();
                        if (!answer.isEmpty()) {
                            selectedQuetion.get(index).setStart(start);
                            selectedQuetion.get(index).setEnd(end);
                            selectedQuetion.get(index).setUserAns(answer);
                        }
                        Log.d("tag :::", answer);
                        paragraph.setText(str);
                        break;
                    case CLEAR_ANSWER:
                        paragraph.setText(para);
                        answer = "";
                        selectedQuetion.get(index).setUserAns(answer);
                        selectedQuetion.get(index).setStart(0);
                        selectedQuetion.get(index).setEnd(0);
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    @UiThread
    public void showQuetion() {
        try {
            SpannableString str = new SpannableString(para);
            if (selectedQuetion.get(index).getUserAns() != null && !selectedQuetion.get(index).getUserAns().isEmpty()) {
                str.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.colorBtnOrangeLight)), selectedQuetion.get(index).getStart(), selectedQuetion.get(index).getEnd(), 0);
            }
            quetion.setText(selectedQuetion.get(index).getQuetion());
            paragraph.setText(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Click(R.id.previous)
    public void onPreviousClick() {
        if (index > 0) {
            index--;
            showQuetion();
        }
    }

    @Click(R.id.next)
    public void onNextClick() {
        if (index < 4) {
            index++;
            showQuetion();
        }
    }

    @Click(R.id.submitBtn)
    public void onsubmitBtnClick() {
        FC_Utility.showFragment(getActivity(), new KeywordsIdentificationFragment_(), R.id.RL_CPA,
                null, KeywordsIdentificationFragment_.class.getSimpleName());

/*        Intent intent = new Intent(this, KeywordMapping_.class);
        startActivity(intent);*/
    }
}
