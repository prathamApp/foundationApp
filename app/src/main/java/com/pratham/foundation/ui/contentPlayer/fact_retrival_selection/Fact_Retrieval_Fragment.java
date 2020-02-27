package com.pratham.foundation.ui.contentPlayer.fact_retrival_selection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.pictionary.PictionaryResult;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

@EFragment(R.layout.fact_retrival_selection)
public class Fact_Retrieval_Fragment extends Fragment implements Fact_Retrieval_Contract.Fact_retrival_View, OnGameClose {

    @Bean(Fact_Retrieval_Presenter.class)
    Fact_Retrieval_Contract.Fact_retrival_Presenter presenter;

    @ViewById(R.id.paragraph)
    FlowLayout paraghaph;
    @ViewById(R.id.keywords)
    FlowLayout keywords;
    @ViewById(R.id.keyword_selected)
    RelativeLayout keyword_selected;
    @ViewById(R.id.quetion)
    TextView quetion;


    @ViewById(R.id.btn_prev)
    ImageButton previous;
    @ViewById(R.id.btn_submit)
    SansButton submitBtn;
    @ViewById(R.id.btn_next)
    ImageButton next;

    @ViewById(R.id.clear_selection)
    SansButton clear_selection;

    @ViewById(R.id.show_answer)
    SansButton show_answer;
    @ViewById(R.id.bottom_bar1)
    LinearLayout bottom_control_container;
    RelativeLayout.LayoutParams viewParam;
    //  private HashMap<String, List<Integer>> positionMap;
    private ArrayList<ScienceQuestionChoice> selectedQuetion;
    private String contentPath, contentTitle, StudentID, resId, readingContentPath, resStartTime;
    private boolean onSdCard;
    private ScienceQuestion questionModel;
    private boolean isKeyWordShowing = false;
    private int index = 0;
    TextView textView;
    private String[] paragraphWords;
    private boolean isShowAnswerEnabled = false;

    @AfterViews
    public void initiate() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            contentPath = bundle.getString("contentPath");
            StudentID = bundle.getString("StudentID");
            resId = bundle.getString("resId");
            contentTitle = bundle.getString("contentName");
            onSdCard = bundle.getBoolean("onSdCard", false);
            if (onSdCard)
                readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";
        }
        presenter.setView(Fact_Retrieval_Fragment.this, resId, readingContentPath);
        selectedQuetion = new ArrayList<>();
        //positionMap = new HashMap<>();

        viewParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 5, 10, 5);
        quetion.setMovementMethod(new ScrollingMovementMethod());
        presenter.getData();
        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime,FC_Utility.getCurrentDateTime(), GameConstatnts.FACT_RETRIAL_CLICK + " " + GameConstatnts.START);
        if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
            show_answer.setVisibility(View.GONE);
        }
    }

    @Override
    public void showParagraph(ScienceQuestion questionModel) {
        this.questionModel = questionModel;
        addParagraph();
        showQuetion();
    }

    private void addParagraph() {
        try {
            questionModel.setQuestion(questionModel.getQuestion().replace("\n", " "));
            this.selectedQuetion = questionModel.getLstquestionchoice();
            Collections.shuffle(selectedQuetion);
            paraghaph.removeAllViews();
            paragraphWords = questionModel.getQuestion().split(" ");
            for (int i = 0; i < paragraphWords.length; i++) {
                final SansTextView textView = new SansTextView(getActivity());
                textView.setTextSize(30);
                textView.setText(paragraphWords[i]);
                int finalI = i;
                textView.setOnClickListener(v -> paragraphWordClicked(textView, finalI));
                paraghaph.addView(textView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @UiThread
    public void showQuetion() {

        //HIDE SHOW CLEAR ANSWER BUTTON
        if (selectedQuetion.get(index).getUserAns() != null && !selectedQuetion.get(index).getUserAns().isEmpty()) {
            for (int i = 0; i < paragraphWords.length; i++) {
                if (paragraphWords[i].equalsIgnoreCase(selectedQuetion.get(index).getUserAns())) {
                    SansTextView textView = (SansTextView) paraghaph.getChildAt(i);
                    textView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGreenCorrect));
                    break;
                }
            }
            clear_selection.setVisibility(View.VISIBLE);
        } else {
            clear_selection.setVisibility(View.INVISIBLE);
        }
        try {
            for (int i = 0; i < paraghaph.getChildCount(); i++) {
                paraghaph.getChildAt(i).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
            }
            if (selectedQuetion.get(index).getUserAns() != null && !selectedQuetion.get(index).getUserAns().isEmpty()) {
                TextView tv = (TextView) paraghaph.getChildAt(selectedQuetion.get(index).getPosition());
                if (tv != null) {
                    tv.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGreenCorrect));
                }
            }
            quetion.setText(selectedQuetion.get(index).getSubQues());
            if (index == 0) {
                previous.setVisibility(View.INVISIBLE);
            } else {
                previous.setVisibility(View.VISIBLE);
            }
            if (index == (selectedQuetion.size() - 1)) {
                submitBtn.setVisibility(View.VISIBLE);
                next.setVisibility(View.INVISIBLE);
            } else {
                submitBtn.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showResult(ArrayList<ScienceQuestionChoice> selectedQuestion) {
        Intent intent = new Intent(getActivity(), PictionaryResult.class);
        intent.putExtra("quetionsFact", selectedQuestion);
        intent.putExtra("readingContentPath", readingContentPath);
        intent.putExtra("resourceType", GameConstatnts.FACTRETRIEVAL);
        startActivityForResult(intent, 111);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.FALSE, Fact_Retrieval_Fragment.this);
        }
    }

    private void paragraphWordClicked(TextView paraText, int position) {
        if (textView != null) {
            clear_selection.setVisibility(View.VISIBLE);
            textView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        textView = paraText;
        String ans = paraText.getText().toString();
        selectedQuetion.get(index).setUserAns(ans);
        selectedQuetion.get(index).setStartTime(FC_Utility.getCurrentDateTime());
        selectedQuetion.get(index).setEndTime(FC_Utility.getCurrentDateTime());
        selectedQuetion.get(index).setPosition(position);
        // paraText.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGreenCorrect));
        paraText.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.convo_correct_bg));
       /* if (selectedQuetion.get(index).getCorrectAnswer().equalsIgnoreCase(ans)) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.app_success_dialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
            dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } else {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.app_failure_dialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
            dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }*/
    }

    @Click(R.id.btn_submit)
    public void submitClicked() {
        if (selectedQuetion != null)
            presenter.addLearntWords(selectedQuetion);
        //GameConstatnts.playGameNext(getActivity());
       /* Bundle bundle = GameConstatnts.findGameData("103");
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new KeywordMappingFragment_(), R.id.RL_CPA,
                    bundle, KeywordMappingFragment_.class.getSimpleName());
        }*/
    }

    @Click(R.id.btn_prev)
    public void onPreviousClick() {
        if (index > 0) {
            index--;
            showQuetion();
        }
    }

    @Click(R.id.btn_next)
    public void onNextClick() {
        if (index < (selectedQuetion.size() - 1)) {
            index++;
            showQuetion();
        }
    }

    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.FACT_RETRIAL_CLICK + " " + GameConstatnts.END);
    }

    @Click(R.id.clear_selection)
    public void clear_selection() {
        clearAns();
        clear_selection.setVisibility(View.INVISIBLE);
    }

    private void clearAns() {
        for (int i = 0; i < paragraphWords.length; i++) {
            SansTextView textView = (SansTextView) paraghaph.getChildAt(i);
            textView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        selectedQuetion.get(index).setUserAns("");
        selectedQuetion.get(index).setStartTime("");
        selectedQuetion.get(index).setEndTime("");
    }

    @Click(R.id.show_answer)
    public void show_answer() {
        if (!isShowAnswerEnabled) {
            isShowAnswerEnabled = true;
            show_answer.setText("hide Answer");
            bottom_control_container.setVisibility(View.INVISIBLE);
            //   mAdapter.deselectAll();
            String correctAns = selectedQuetion.get(index).getCorrectAnswer().trim().replace("\n", " ");
            for (int i = 0; i < paragraphWords.length; i++) {
                if (paragraphWords[i].equalsIgnoreCase(correctAns)) {
                    SansTextView textView = (SansTextView) paraghaph.getChildAt(i);
                    //  textView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGreenCorrect));
                    textView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.tag_view));
                    break;
                }
            }

        } else {
            show_answer.setText("show Answer");
            isShowAnswerEnabled = false;
            bottom_control_container.setVisibility(View.VISIBLE);
            String userAnswer = selectedQuetion.get(index).getCorrectAnswer().trim().replace("\n", " ");
            for (int i = 0; i < paragraphWords.length; i++) {
                if (paragraphWords[i].equalsIgnoreCase(userAnswer)) {
                    SansTextView textView = (SansTextView) paraghaph.getChildAt(i);
                    textView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                }
            }
        }
    }
}