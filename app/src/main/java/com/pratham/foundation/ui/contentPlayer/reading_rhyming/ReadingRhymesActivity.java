package com.pratham.foundation.ui.contentPlayer.reading_rhyming;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.interfaces.MediaCallbacks;
import com.pratham.foundation.modalclasses.ModalRhymingWords;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;
import com.pratham.foundation.utility.MediaPlayerUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EActivity(R.layout.activity_rhymes_reading)
public class ReadingRhymesActivity extends BaseActivity
        implements MediaCallbacks, RhymingWordClicked, ReadingRhymesContract.ReadingRhymesView {

    @Bean(ReadingRhymesPresenter.class)
    ReadingRhymesContract.ReadingRhymesPresenter presenter;

    @ViewById(R.id.story_ll)
    RelativeLayout story_ll;
    @ViewById(R.id.rl_card1)
    RelativeLayout cardLayout1;
    @ViewById(R.id.rl_card2)
    RelativeLayout cardLayout2;
    @ViewById(R.id.rl_card3)
    RelativeLayout cardLayout3;
    @ViewById(R.id.rl_card4)
    RelativeLayout cardLayout4;
    @ViewById(R.id.rl_card5)
    RelativeLayout cardLayout5;
    @ViewById(R.id.rhyming_card_word1)
    TextView cardText1;
    @ViewById(R.id.rhyming_card_word2)
    TextView cardText2;
    @ViewById(R.id.rhyming_card_word3)
    TextView cardText3;
    @ViewById(R.id.rhyming_card_word4)
    TextView cardText4;
    @ViewById(R.id.rhyming_card_word5)
    TextView cardText5;
    @ViewById(R.id.btn_play)
    ImageButton btn_play;
    @ViewById(R.id.btn_next)
    ImageButton btn_next;
    @ViewById(R.id.floating_back)
    FloatingActionButton floating_back;
    @ViewById(R.id.floating_info)
    FloatingActionButton floating_info;
//    @ViewById(R.id.layout_play_ripple)
//    RipplePulseLayout layout_play_ripple;


    List<Integer> readSounds = new ArrayList<>();
    public static MediaPlayer mp, correctSound;
    Context mContext;
    static int currentPageNo, currentQueNo;
    int rhymeLevel, correctDone = 0;
    Handler handler;
    CustomLodingDialog nextDialog;
    static boolean readingFlg;
    static boolean newData;
    static boolean[] correctArr;
    boolean testFlg, playingFlg, audioPlaying, noNextButton, dbEntry, onSdCard;
    List<ModalRhymingWords> rhymingWordsList;
    static String readingContentPath;
    String contentPath, contentTitle, StudentID, resId;

    @AfterViews
    public void initialize() {

        mContext = ReadingRhymesActivity.this;
        testFlg = false;
        correctSound = MediaPlayer.create(this, R.raw.correct_ans);
        mediaPlayerUtil = new MediaPlayerUtil(ReadingRhymesActivity.this);
        mediaPlayerUtil.initCallback(ReadingRhymesActivity.this);

        floating_back.setImageResource(R.drawable.ic_left_arrow_white);
        floating_info.setImageResource(R.drawable.ic_info_outline_white);

        presenter.setView(ReadingRhymesActivity.this);

        currentPageNo = 0;
        currentQueNo = 0;
        rhymeLevel = 1;
        rhymingWordsList = new ArrayList<>();
        noNextButton = false;

        Intent intent = getIntent();
        contentTitle = intent.getStringExtra("contentTitle");
        contentPath = intent.getStringExtra("contentPath");
        StudentID = intent.getStringExtra("StudentID");
        resId = intent.getStringExtra("resId");
        onSdCard = getIntent().getBooleanExtra("onSdCard", false);

        presenter.setResIdAndStartTime(resId);
        readSounds.add(R.raw.tap_the_mic);
        readSounds.add(R.raw.your_turn_to_read);
        readSounds.add(R.raw.would_you_like_to_read);
        readSounds.add(R.raw.tap_the_mic_to_read_out);
        Collections.shuffle(readSounds);

        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";
        showViewDialog();
    }

    private void showViewDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);

        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_title.setText("Click the round to listen");
        dia_btn_green.setText("Okay");
        dia_btn_red.setVisibility(View.GONE);
        dia_btn_yellow.setVisibility(View.GONE);

        dialog.show();

        dia_btn_green.setOnClickListener(v -> {
            presenter.fetchJsonData(readingContentPath);
            dialog.dismiss();
        });
        dia_btn_red.setOnClickListener(v -> dialog.dismiss());
        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
    }

    @UiThread
    @Override
    public void noNextButton() {
        noNextButton = true;
    }

    @UiThread
    @Override
    public void setListData(List<ModalRhymingWords> wordsDataList) {
        rhymingWordsList.addAll(wordsDataList);
        correctArr = new boolean[rhymingWordsList.size()];
        newData = true;
        new Handler().postDelayed(this::showALLViews, 800);
    }

    public void animateRoundView(final RelativeLayout roundView, int quesNo) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rhyming_zoom_in_and_out);
        changeViewColor(quesNo);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
        roundView.setAnimation(animation);
    }

    private void showALLViews() {
        for (int quesNo = 0; quesNo < rhymingWordsList.size(); quesNo++) {
            rhymingWordsList.get(quesNo).setReadOut(true);
            int finalQuesNo = quesNo;
            changeViewColorYellow(quesNo);
            switch (quesNo) {
                case 0:
                    cardLayout1.setVisibility(View.VISIBLE);
                    cardText1.setVisibility(View.GONE);
                    cardText1.setText(rhymingWordsList.get(quesNo).getWord());
                    cardLayout1.setOnClickListener(v -> {
                        changeViewColor(finalQuesNo);
                        cardText1.setVisibility(View.VISIBLE);
                        startAudioReading(rhymingWordsList.get(finalQuesNo),finalQuesNo);
                    });
                    break;
                case 1:
                    cardLayout2.setVisibility(View.VISIBLE);
                    cardText2.setVisibility(View.GONE);
                    cardText2.setText(rhymingWordsList.get(quesNo).getWord());
                    cardLayout2.setOnClickListener(v -> {
                        changeViewColor(finalQuesNo);
                        cardText2.setVisibility(View.VISIBLE);
                        startAudioReading(rhymingWordsList.get(finalQuesNo),finalQuesNo);
                    });
                    break;
                case 2:
                    cardLayout3.setVisibility(View.VISIBLE);
                    cardText3.setVisibility(View.GONE);
                    cardText3.setText(rhymingWordsList.get(quesNo).getWord());
                    cardLayout3.setOnClickListener(v -> {
                        changeViewColor(finalQuesNo);
                        cardText3.setVisibility(View.VISIBLE);
                        startAudioReading(rhymingWordsList.get(finalQuesNo),finalQuesNo);
                    });
                    break;
                case 3:
                    cardLayout4.setVisibility(View.VISIBLE);
                    cardText4.setVisibility(View.GONE);
                    cardText4.setText(rhymingWordsList.get(quesNo).getWord());
                    cardLayout4.setOnClickListener(v -> {
                        changeViewColor(finalQuesNo);
                        cardText4.setVisibility(View.VISIBLE);
                        startAudioReading(rhymingWordsList.get(finalQuesNo),finalQuesNo);
                    });
                    break;
                case 4:
                    cardLayout5.setVisibility(View.VISIBLE);
                    cardText5.setVisibility(View.GONE);
                    cardText5.setText(rhymingWordsList.get(quesNo).getWord());
                    cardLayout5.setOnClickListener(v -> {
                        changeViewColor(finalQuesNo);
                        cardText5.setVisibility(View.VISIBLE);
                        startAudioReading(rhymingWordsList.get(finalQuesNo),finalQuesNo);
                    });
                    break;
            }
        }
    }

    private void startAudioReading(ModalRhymingWords rhymingWords,int pos) {
        try {
            presenter.addScore(0, "Word:" + rhymingWords.getWord(),
                    getCorrectCounter(), correctArr.length, FC_Utility.getCurrentDateTime(), "RhymingWords");
            presenter.addLearntWords(0, rhymingWords);
            mp = new MediaPlayer();
            mp.setDataSource(readingContentPath + "Sounds/" + rhymingWords.getWordAudio());
            mp.prepare();
            mp.start();
            mp.setOnCompletionListener(mp -> {
                    changeViewColorYellow(pos);
         });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean nextClicked = false;

    @Click(R.id.btn_next)
    public void nextBtnPressed() {
        if (!nextClicked) {
            nextClicked = true;
            currentQueNo = 0;
            showWordNextDialog(this);
        }
    }

    @SuppressLint("SetTextI18n")
    public void showExitDialog() {

        final CustomLodingDialog dialog = new CustomLodingDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);

        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_title.setText("Exit game?");
        dia_btn_green.setText("Yes");
        dia_btn_red.setText("No");
        dia_btn_yellow.setText("" + dialog_btn_cancel);

        dialog.show();

        dia_btn_green.setOnClickListener(v -> {
//            setCompletionPercentage();
            presenter.setCompletionPercentage();
            dialog.dismiss();
            finish();
            ReadingRhymesActivity.super.onBackPressed();
        });
        dia_btn_red.setOnClickListener(v -> dialog.dismiss());
        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
    }


    @SuppressLint("SetTextI18n")
    public void showWordNextDialog(Context context) {

        nextDialog = new CustomLodingDialog(context);
        nextDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(nextDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nextDialog.setContentView(R.layout.fc_custom_dialog);
        nextDialog.setCancelable(false);
        nextDialog.setCanceledOnTouchOutside(false);
        nextDialog.show();

        Button dia_btn_yellow = nextDialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = nextDialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = nextDialog.findViewById(R.id.dia_btn_red);

        dia_btn_yellow.setText(dialog_btn_cancel);
        dia_btn_green.setText("Revise");
        dia_btn_red.setText("Next");

        dia_btn_red.setOnClickListener(v -> {
            currentPageNo = 0;
            currentQueNo = 0;
            correctDone = 0;
            rhymingWordsList.clear();
            testFlg = false;
            nextClicked = false;
            nextDialog.dismiss();
            hideViews();
            if (!noNextButton)
                presenter.getNextDataList();
            else
                presenter.getDataList();

        });

        dia_btn_yellow.setOnClickListener(v -> {
            nextClicked = false;
            nextDialog.dismiss();
        });

        dia_btn_green.setOnClickListener(v -> {
            currentPageNo = 0;
            correctDone = 0;
            currentQueNo = 0;
            testFlg = false;
            nextClicked = false;
            nextDialog.dismiss();
//            startPlayback();
        });

    }

    private void hideViews() {
        cardLayout1.setBackgroundResource(R.drawable.gragiance_ripple_round);
        cardLayout1.setVisibility(View.GONE);
        cardText1.setText("");
        cardLayout2.setBackgroundResource(R.drawable.gragiance_ripple_round);
        cardLayout2.setVisibility(View.GONE);
        cardText1.setText("");
        cardLayout3.setBackgroundResource(R.drawable.gragiance_ripple_round);
        cardLayout3.setVisibility(View.GONE);
        cardText1.setText("");
        cardLayout4.setBackgroundResource(R.drawable.gragiance_ripple_round);
        cardLayout4.setVisibility(View.GONE);
        cardText1.setText("");
        cardLayout5.setBackgroundResource(R.drawable.gragiance_ripple_round);
        cardLayout5.setVisibility(View.GONE);
        cardText1.setText("");
    }

    public int getCorrectCounter() {
        int counter = 0;
        for (boolean b : correctArr)
            if (b) {
                counter++;
            }
        return counter;
    }

    public void setCorrectViewColor(int correctCounter) {
        boolean allCorrect = true;
        if (rhymingWordsList.get(correctCounter).isCorrectRead() && !correctArr[correctCounter]) {
            correctArr[correctCounter] = true;
            presenter.addScore(0, "Word:" + rhymingWordsList.get(correctCounter).getWord(),
                    getCorrectCounter(), correctArr.length, FC_Utility.getCurrentDateTime(), "RhymingWords");
            changeViewColor(correctCounter);
//          mAdapter.notifyItemChanged(correctCounter, rhymingWordsList.get(correctCounter));
            for (int i = 0; i < rhymingWordsList.size(); i++) {
                if (!correctArr[i])
                    allCorrect = false;
            }
            if (allCorrect && correctDone < 1) {
                correctDone++;
                handler.postDelayed(this::nextBtnPressed, 1000);
            }
        } else {
            for (int i = 0; i < rhymingWordsList.size(); i++) {
                if (!correctArr[i])
                    allCorrect = false;
            }
            if (allCorrect && correctDone < 1) {
                correctDone++;
                handler.postDelayed(this::nextBtnPressed, 1000);
            }
        }
    }

    private void changeViewColor(int correctCounter) {
        switch (correctCounter) {
            case 0:
                cardLayout1.setBackgroundResource(R.drawable.gragiance_correct_ripple_round);
                break;
            case 1:
                cardLayout2.setBackgroundResource(R.drawable.gragiance_correct_ripple_round);
                break;
            case 2:
                cardLayout3.setBackgroundResource(R.drawable.gragiance_correct_ripple_round);
                break;
            case 3:
                cardLayout4.setBackgroundResource(R.drawable.gragiance_correct_ripple_round);
                break;
            case 4:
                cardLayout5.setBackgroundResource(R.drawable.gragiance_correct_ripple_round);
                break;
        }
    }

    private void changeViewColorYellow(int correctCounter) {
        switch (correctCounter) {
            case 0:
                cardLayout1.setBackgroundResource(R.drawable.gragiance_ripple_round);
                break;
            case 1:
                cardLayout2.setBackgroundResource(R.drawable.gragiance_ripple_round);
                break;
            case 2:
                cardLayout3.setBackgroundResource(R.drawable.gragiance_ripple_round);
                break;
            case 3:
                cardLayout4.setBackgroundResource(R.drawable.gragiance_ripple_round);
                break;
            case 4:
                cardLayout5.setBackgroundResource(R.drawable.gragiance_ripple_round);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (playingFlg)
                mp.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click (R.id.floating_back)
    public void pressedBackBtn(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        try {
            if (playingFlg)
                mp.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        showExitDialog();
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = AppDatabase.appDatabase.getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), "" + resId, wordStr.toLowerCase());
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onItemClicked(int position, String audioPath) {
        try {
            if (!readingFlg) {
                setMute(0);
                mediaPlayerUtil.playMedia(readingContentPath + "Sound/" + audioPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
