package com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.List;

@EFragment(R.layout.fragment_fact_retrival)
public class FactRetrieval extends Fragment implements FactRetrievalContract.FactRetrievalView, OnGameClose {

    @Bean(FactRetrievalPresenter.class)
    FactRetrievalContract.FactRetrievalPresenter presenter;
    @ViewById(R.id.paragraph)
    TextView paragraph;
    @ViewById(R.id.quetion)
    TextView quetion;

    @ViewById(R.id.previous)
    SansButton previous;
    @ViewById(R.id.submitBtn)
    SansButton submitBtn;
    @ViewById(R.id.next)
    SansButton next;

    @ViewById(R.id.tittle)
    SansTextView tittle;

    private String answer, para;
    private String contentPath, contentTitle, StudentID, resId, readingContentPath, resStartTime;
    boolean onSdCard;
    private List<ScienceQuestionChoice> selectedQuetion;
    private int index = 0;
    private MediaPlayer mediaPlayerWrong;
    private MediaPlayer mediaPlayercorrect;
    private String startTime;

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
                readingContentPath = ApplicationClass.contentSDPath + "/.FCA/English/Game/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + "/.FCA/English/Game/" + contentPath + "/";

            mediaPlayerWrong = MediaPlayer.create(getActivity(), R.raw.wrong_sound);
            mediaPlayercorrect = MediaPlayer.create(getActivity(), R.raw.welldone);
//            paragraph.setMovementMethod(new ScrollingMovementMethod());
            quetion.setMovementMethod(new ScrollingMovementMethod());
        }

        presenter.setView(FactRetrieval.this, contentTitle, resId);
        presenter.getData(readingContentPath);
        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.FACTRETRIEVAL + " " + GameConstatnts.START);

    }

    @Override
    public void showParagraph(ScienceQuestion questionModel) {
        this.para = questionModel.getQuestion();
        this.selectedQuetion = questionModel.getLstquestionchoice();
        if (questionModel.getInstruction() != null && !questionModel.getInstruction().isEmpty()) {
            tittle.setText(questionModel.getInstruction());
        }
        Collections.shuffle(selectedQuetion);
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
                submitBtn.setVisibility(View.GONE);
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
                submitBtn.setVisibility(View.GONE);
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
                        selectedQuetion.get(index).setStartTime(startTime);
                        selectedQuetion.get(index).setEndTime(FC_Utility.getCurrentDateTime());

                        if (!answer.isEmpty()) {
                            selectedQuetion.get(index).setStart(start);
                            selectedQuetion.get(index).setEnd(end);
                            selectedQuetion.get(index).setUserAns(answer);
                        }
                        // Log.d("tag :::", answer);
                        if (!FC_Constants.isTest) {
                            float pertc = presenter.checkAnswer(selectedQuetion.get(index));
                            if (pertc > 75) {
                                final Dialog dialog = new Dialog(getActivity());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.app_success_dialog);
                                dialog.setCancelable(false);
                                dialog.setCanceledOnTouchOutside(false);
                                SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
                                dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
                                dialog.show();
                                mediaPlayercorrect.start();

                            } else {
                                final Dialog dialog = new Dialog(getActivity());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.app_failure_dialog);
                                dialog.setCancelable(false);
                                dialog.setCanceledOnTouchOutside(false);
                                SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
                                SansTextView correctAns = dialog.findViewById(R.id.correctAns);
                                correctAns.setText(selectedQuetion.get(index).getCorrectAnswer());
                                dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
                                dialog.show();
                                mediaPlayerWrong.start();
                            }
                        }
                        paragraph.setText(str);
                        if (index == (selectedQuetion.size() - 1)) {
                            submitBtn.setVisibility(View.VISIBLE);
                        }
                        break;
                    case CLEAR_ANSWER:
                        paragraph.setText(para);
                        answer = "";
                        selectedQuetion.get(index).setUserAns(answer);
                        selectedQuetion.get(index).setStart(0);
                        selectedQuetion.get(index).setEnd(0);
                        if (index == (selectedQuetion.size() - 1)) {
                            submitBtn.setVisibility(View.VISIBLE);
                        }
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
            quetion.setText(selectedQuetion.get(index).getSubQues());
            startTime = FC_Utility.getCurrentDateTime();

            paragraph.setText(str);
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

    @Click(R.id.previous)
    public void onPreviousClick() {
        if (selectedQuetion != null)
            if (index > 0) {
                index--;
                showQuetion();
            }
    }

    @Click(R.id.next)
    public void onNextClick() {
        if (selectedQuetion != null)
            if (index < (selectedQuetion.size() - 1)) {
                index++;
                showQuetion();
            }
    }

    @Click(R.id.submitBtn)
    public void onsubmitBtnClick() {
        if (selectedQuetion != null) {
            presenter.addLearntWords(selectedQuetion);
        }
        //  GameConstatnts.playGameNext(getActivity());
        /* Bundle bundle = GameConstatnts.findGameData(resId);
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new KeywordsIdentificationFragment_(), R.id.RL_CPA,
                    bundle, KeywordsIdentificationFragment_.class.getSimpleName());
        }*/

    }

    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.FACTRETRIEVAL + " " + GameConstatnts.END);
    }
}
