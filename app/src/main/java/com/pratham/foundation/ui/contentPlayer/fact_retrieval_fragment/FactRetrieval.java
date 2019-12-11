package com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.dragselectrecyclerview.DragSelectTouchListener;
import com.pratham.foundation.customView.dragselectrecyclerview.DragSelectionProcessor;
import com.pratham.foundation.customView.flexbox.FlexDirection;
import com.pratham.foundation.customView.flexbox.FlexboxLayoutManager;
import com.pratham.foundation.customView.flexbox.JustifyContent;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.ui.contentPlayer.pictionary.PictionaryResult;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EFragment(R.layout.fragment_fact_retrival)
public class FactRetrieval extends Fragment implements FactRetrievalContract.FactRetrievalView, OnGameClose {

    @Bean(FactRetrievalPresenter.class)
    FactRetrievalContract.FactRetrievalPresenter presenter;
    @ViewById(R.id.paragraphRecycler)
    RecyclerView paragraphRecycler;

    @ViewById(R.id.quetion)
    TextView quetion;

    @ViewById(R.id.btn_prev)
    ImageButton previous;

    @ViewById(R.id.btn_submit)
    SansButton submitBtn;

    @ViewById(R.id.btn_next)
    ImageButton next;

   /* @ViewById(R.id.tittle)
    SansTextView tittle;*/

    @ViewById(R.id.clear_selection)
    SansButton clear_selection;

    @ViewById(R.id.show_answer)
    SansButton show_answer;

    @ViewById(R.id.bottom_bar1)
    LinearLayout bottom_control_container;

    private String[] sentences;
    private String answer, para;
    private String contentPath, contentTitle, StudentID, resId, readingContentPath, resStartTime;
    private boolean onSdCard;
    private ArrayList<ScienceQuestionChoice> selectedQuetion;
    private int index = 0;
    private MediaPlayer mediaPlayerWrong;
    private MediaPlayer mediaPlayercorrect;
    private String startTime;
    private ScienceQuestion questionModel;
    //private String REGEXF="(?<=\\.\\s)|(?<=[?!]\\s)|(?<=\\|\\s)";
    private String REGEXF="(?<=\\.\\s)|(?<=[?!]\\s)|(?<=।)|(?<=\\|)";

    private DragSelectionProcessor.Mode mMode = DragSelectionProcessor.Mode.Simple;
    private DragSelectTouchListener mDragSelectTouchListener = null;
    private TestAutoDataAdapter mAdapter;
    private DragSelectionProcessor mDragSelectionProcessor;

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

            mediaPlayerWrong = MediaPlayer.create(getActivity(), R.raw.wrong_sound);
            mediaPlayercorrect = MediaPlayer.create(getActivity(), R.raw.welldone);
//            paragraph.setMovementMethod(new ScrollingMovementMethod());
            quetion.setMovementMethod(new ScrollingMovementMethod());
        }
        EventBus.getDefault().register(this);
        presenter.setView(FactRetrieval.this, contentTitle, resId);
        presenter.getData(readingContentPath);
        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.FACTRETRIEVAL + " " + GameConstatnts.START);
        if (FC_Constants.isTest) {
            show_answer.setVisibility(View.GONE);
        }
    }

    @Override
    public void showParagraph(ScienceQuestion questionModel) {
        this.questionModel = questionModel;
        questionModel.setQuestion(questionModel.getQuestion().replace("\n", " "));
        questionModel.setQuestion(questionModel.getQuestion().replaceAll("\\s+", " "));
        //this.para = questionModel.getQuestion();
        this.selectedQuetion = questionModel.getLstquestionchoice();
        startTime = FC_Utility.getCurrentDateTime();
        /*if (questionModel.getInstruction() != null && !questionModel.getInstruction().isEmpty()) {
            tittle.setText(questionModel.getInstruction());
        }*/
        Collections.shuffle(selectedQuetion);
        LoadRecyclerText();
        getAnswersInPassage();

        showQuestion();
     /*   final int SETANSWER = 0;
        final int CLEAR_ANSWER = 1;*/
       /* paragraph.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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
        });*/
    }

    @Override
    public void showResult(ArrayList<ScienceQuestionChoice> selectedQuestion) {
        Intent intent = new Intent(getActivity(), PictionaryResult.class);
        intent.putExtra("quetionsFact", selectedQuestion);
        intent.putExtra("readingContentPath", readingContentPath);
        intent.putExtra("resourceType", GameConstatnts.FACTRETRIEVAL);
        startActivityForResult(intent, 111);
    }

    private void getAnswersInPassage() {
        for (int queIndex = 0; queIndex < selectedQuetion.size(); queIndex++) {
            String correctAns = selectedQuetion.get(queIndex).getCorrectAnswer().replace("\n", " ");
            String[] correctAnsArr = correctAns.trim().split(REGEXF);
            for (int correctIndex = 0; correctIndex < correctAnsArr.length; correctIndex++) {
                float max, temp = 0;
                int start = -1;
                max = checkAnswer(sentences[0], correctAnsArr[correctIndex]);
                for (int sentenceIndx = 0; sentenceIndx < sentences.length; sentenceIndx++) {
                    temp = checkAnswer(sentences[sentenceIndx], correctAnsArr[correctIndex]);
                    if (temp >= max) {
                        start = sentenceIndx;
                        max = temp;
                    }
                }
                if (start > -1) {
                    String ansInPassage = selectedQuetion.get(queIndex).getAnsInPassage() == null ? sentences[start] : selectedQuetion.get(queIndex).getAnsInPassage() + sentences[start];
                    selectedQuetion.get(queIndex).setAnsInPassage(ansInPassage);
                }
            }
        }
    }

    @UiThread
    public void showQuestion() {
        try {
            mAdapter.deselectAll();
            if (selectedQuetion.get(index).getUserAns() != null && !selectedQuetion.get(index).getUserAns().isEmpty()) {
                mAdapter.selectRange(selectedQuetion.get(index).getStart(), selectedQuetion.get(index).getEnd(), true);
                clear_selection.setVisibility(View.VISIBLE);
            } else {
                clear_selection.setVisibility(View.INVISIBLE);
            }
            quetion.setText(selectedQuetion.get(index).getSubQues());

            //   paragraph.setText(str);


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

    private void LoadRecyclerText() {
        String[] paragraphWords = questionModel.getQuestion().split(" ");
        sentences = questionModel.getQuestion().trim().split(REGEXF);
        // TextAdapter arrayAdapter = new TextAdapter(Arrays.asList(paragraphWords), getActivity());
        mAdapter = new TestAutoDataAdapter(getActivity(), Arrays.asList(paragraphWords));
        paragraphRecycler.setAdapter(mAdapter);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        paragraphRecycler.setLayoutManager(layoutManager);
        mAdapter.setClickListener(new TestAutoDataAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // mAdapter.toggleSelection(position);
            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                // if one item is long pressed, we start the drag selection like following:
                // we just call this function and pass in the position of the first selected item
                // the selection processor does take care to update the positions selection mode correctly
                // and will correctly transform the touch events so that they can be directly applied to your adapter!!!
                if (!mAdapter.isShowAnswerEnabled()) {
                    mAdapter.deselectAll();
                    mDragSelectTouchListener.startDragSelection(position);
                    return true;
                } else {
                    return false;
                }
            }
        });
        mDragSelectionProcessor = new DragSelectionProcessor(new DragSelectionProcessor.ISelectionHandler() {
            @Override
            public HashSet<Integer> getSelection() {
                return mAdapter.getSelection();
            }

            @Override
            public boolean isSelected(int index) {
                return mAdapter.getSelection().contains(index);
            }

            @Override
            public void updateSelection(int start, int end, boolean isSelected, boolean calledFromOnStart) {
                mAdapter.selectRange(start, end, isSelected);
                setAnswer();
                clear_selection.setVisibility(View.VISIBLE);
            }
        }).withMode(mMode);
        mDragSelectTouchListener = new DragSelectTouchListener()
                .withSelectListener(mDragSelectionProcessor);
        updateSelectionListener();
        paragraphRecycler.addOnItemTouchListener(mDragSelectTouchListener);

    }

    @Click(R.id.clear_selection)
    public void clear_selection() {
        mAdapter.deselectAll();
        clearAns();
        clear_selection.setVisibility(View.INVISIBLE);
    }

    @Click(R.id.show_answer)
    public void show_answer() {
        if (!mAdapter.isShowAnswerEnabled()) {
            show_answer.setText("hide Answer");
            bottom_control_container.setVisibility(View.INVISIBLE);
            mAdapter.deselectAll();
            String correctAns = selectedQuetion.get(index).getAnsInPassage().trim().replace("\n", " ");
            String[] correctAnsArr = correctAns.trim().split(REGEXF);
            for (int correctIndex = 0; correctIndex < correctAnsArr.length; correctIndex++) {
                float max, temp = 0;
                int start = -1;
                max = checkAnswer(sentences[0], correctAnsArr[correctIndex]);
                for (int sentenceIndx = 0; sentenceIndx < sentences.length; sentenceIndx++) {
                    temp = checkAnswer(sentences[sentenceIndx], correctAnsArr[correctIndex]);
                    if (temp >= max) {
                        start = sentenceIndx;
                        max = temp;
                    }
                }
                if (start > -1) {
                    List tempList = Arrays.asList(sentences[start].replace("”","").trim().split(" "));
                    int ansStart = Collections.indexOfSubList(mAdapter.datalist, tempList);

                    mAdapter.selectRange(ansStart, ansStart + tempList.size() - 1, true);
                }
            }
            mAdapter.setShowAnswerEnabled(true);
        } else {
            show_answer.setText("show Answer");
            mAdapter.setShowAnswerEnabled(false);
            bottom_control_container.setVisibility(View.VISIBLE);
            showQuestion();
        }

    }

    private float checkAnswer(String originalAns, String userAns) {
        boolean[] correctArr;
        float perc;
        // String originalAns = selectedAnsList.getCorrectAnswer();
        String quesFinal = originalAns.replaceAll(STT_REGEX, "");

        String[] originalAnsArr = quesFinal.split(" ");
        String[] userAnsArr = userAns.replaceAll(STT_REGEX, "").split(" ");

        if (originalAnsArr.length < userAnsArr.length)
            correctArr = new boolean[userAnsArr.length];
        else correctArr = new boolean[originalAnsArr.length];


        for (int j = 0; j < userAnsArr.length; j++) {
            for (int i = 0; i < originalAnsArr.length; i++) {
                if (userAnsArr[j].equalsIgnoreCase(originalAnsArr[i])) {
                    correctArr[i] = true;
                    break;
                }
            }
        }

        int correctCnt = 0;
        for (int x = 0; x < correctArr.length; x++) {
            if (correctArr[x])
                correctCnt++;
        }
        perc = ((float) correctCnt / (float) correctArr.length) * 100;
        return perc;
    }

    private void updateSelectionListener() {
        mDragSelectionProcessor.withMode(mMode);
        // mToolbar.setSubtitle("Mode: " + mMode.name());
    }


    @Click(R.id.btn_prev)
    public void onPreviousClick() {
        if (selectedQuetion != null)
            if (index > 0) {
                //  setAnswer();
                index--;
                showQuestion();
            }
    }

    private void setAnswer() {
        StringBuilder selectedText = new StringBuilder();
        HashSet<Integer> selection = mAdapter.getSelection();
        if (!selection.isEmpty()) {
            List<Integer> list = new ArrayList<Integer>(selection);
            Collections.sort(list);
            for (int i = 0; i < list.size(); i++) {
                selectedText.append(" ").append(mAdapter.datalist.get(list.get(i)));
            }
            selectedQuetion.get(index).setUserAns(selectedText.toString());
            selectedQuetion.get(index).setStart(list.get(0));
            selectedQuetion.get(index).setEnd(list.get(list.size() - 1));
            selectedQuetion.get(index).setStartTime(FC_Utility.getCurrentDateTime());
            selectedQuetion.get(index).setEndTime(FC_Utility.getCurrentDateTime());
        }
    }

    private void clearAns() {
        selectedQuetion.get(index).setUserAns("");
        selectedQuetion.get(index).setStart(-1);
        selectedQuetion.get(index).setEnd(-1);
    }

    @Click(R.id.btn_next)
    public void onNextClick() {
        if (selectedQuetion != null)
            if (index < (selectedQuetion.size() - 1)) {
                //  setAnswer();
                index++;
                showQuestion();
            }
    }

    @Click(R.id.btn_submit)
    public void onsubmitBtnClick() {
        if (selectedQuetion != null) {
            presenter.addLearntWords(selectedQuetion);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.FALSE, FactRetrieval.this);
        }
    }

    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.FACTRETRIEVAL + " " + GameConstatnts.END);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        GameConstatnts.showGameInfo(getActivity(), questionModel.getInstruction(),readingContentPath+questionModel.getInstructionUrl());
        // Toast.makeText(getActivity(), event.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
