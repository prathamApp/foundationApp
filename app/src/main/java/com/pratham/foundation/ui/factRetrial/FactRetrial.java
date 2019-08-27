package com.pratham.foundation.ui.factRetrial;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.ui.identifyKeywords.QuestionModel;
import com.pratham.foundation.utility.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_fact_retrial)
public class FactRetrial extends BaseActivity implements FactRetrialController.View {
    @Bean(FactRetrialPresenter.class)
    FactRetrialController.Presenter presenter;

    @ViewById(R.id.paragraph)
    TextView paragraph;

    @ViewById(R.id.quetion)
    TextView quetion;

    private QuestionModel questionModel;
    private String answer;

    /*@Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         //setContentView(R.layout.activity_fact_retrial);
         //  ButterKnife.bind(this);
     } */
    @AfterViews
    public void initiate() {
        presenter.getData();
    }

    @Override
    public void showParagraph(final QuestionModel questionModel) {
        this.questionModel = questionModel;
        paragraph.setText(questionModel.getParagraph());
        quetion.setText(questionModel.getKeywords().get(0));
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
                        SpannableString str = new SpannableString(questionModel.getParagraph());
                        str.setSpan(new BackgroundColorSpan(Color.YELLOW), start, end, 0);
                        answer = questionModel.getParagraph().substring(start, end);
                        Log.d("tag :::", answer);
                        paragraph.setText(str);
                        break;
                    case CLEAR_ANSWER:
                        paragraph.setText(questionModel.getParagraph());
                        answer = "";
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }
}
