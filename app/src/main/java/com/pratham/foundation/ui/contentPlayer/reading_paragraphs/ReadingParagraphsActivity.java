package com.pratham.foundation.ui.contentPlayer.reading_paragraphs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nex3z.flowlayout.FlowLayout;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.RipplePulseLayout;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.fontsview.SansTextView;
import com.pratham.foundation.modalclasses.ModalParaWord;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.services.stt.ContinuousSpeechService_New;
import com.pratham.foundation.services.stt.STT_Result_New;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

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

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_FOLDER_NAME;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;


@EActivity(R.layout.activity_paragraphs_reading)
public class ReadingParagraphsActivity extends BaseActivity
        implements STT_Result_New.sttView,
        ReadingParagraphsContract.ReadingParagraphsView {

    @Bean(ReadingParagraphsPresenter.class)
    ReadingParagraphsContract.ReadingParagraphsPresenter presenter;

    @ViewById(R.id.myflowlayout)
    FlowLayout myFlowLayout;
    @ViewById(R.id.tv_content_title)
    TextView tvContentTitle;
    @ViewById(R.id.stt_result_tv)
    TextView stt_result_tv;
    @ViewById(R.id.clean_stt)
    ImageView clean_stt;
    @ViewById(R.id.btn_play)
    ImageButton btn_Play;
    @ViewById(R.id.btn_mic)
    ImageButton btn_Mic;
    @ViewById(R.id.myScrollView)
    ScrollView myScrollView;
    @ViewById(R.id.layout_mic_ripple)
    RipplePulseLayout layout_mic_ripple;
    @ViewById(R.id.ll_btn_play)
    LinearLayout ll_btn_play;
    @ViewById(R.id.ll_btn_submit)
    LinearLayout ll_btn_submit;
    @ViewById(R.id.btn_submit)
    Button btn_submit;
    @ViewById(R.id.ll_btn_stop)
    LinearLayout ll_btn_stop;
    @ViewById(R.id.iv_monk)
    ImageView iv_monk;
    @ViewById(R.id.floating_back)
    FloatingActionButton floating_back;
    @ViewById(R.id.floating_info)
    FloatingActionButton floating_info;

    @ViewById(R.id.ll_edit_text)
    LinearLayout ll_edit_text;
    @ViewById(R.id.et_edit_ans)
    EditText et_edit_ans;
    @ViewById(R.id.bt_edit_ok)
    Button bt_edit_ok;

    ContinuousSpeechService_New continuousSpeechService;

    List<Integer> readSounds = new ArrayList<>();
    public static MediaPlayer mp, mPlayer;
    Context mContext;
    List<String> splitWords = new ArrayList<String>();
    List<String> splitWordsPunct = new ArrayList<String>();
    List<String> wordsDurationList = new ArrayList<String>();
    List<String> wordsResIdList = new ArrayList<String>();
    static int currentPageNo, lineBreakCounter = 0;
    int wordCounter = 0, flow_Width, flow_Height;
    Handler handler, colorChangeHandler, quesReadHandler, endhandler;
    CustomLodingDialog nextDialog;
    static boolean[] correctArr;
    boolean readingFlg, playingFlg, fragFlg = false, onSdCard,diaOpen=false;
    List<ModalParaWord> modalParaWordList;
    String readingContentPath, contentPath, contentTitle, StudentID, resId, paraAudio,
            useText, englishWord, startPlayBack, resStartTime, certiCode, resType;
    String [] attAnsList;

    @SuppressLint("SetTextI18n")
    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        iv_monk.setVisibility(View.GONE);
        mContext = ReadingParagraphsActivity.this;
        endhandler = new Handler();
        continuousSpeechService = new ContinuousSpeechService_New(mContext, ReadingParagraphsActivity.this, FC_Constants.ENGLISH);

        floating_back.setImageResource(R.drawable.ic_left_arrow_white);
        floating_info.setImageResource(R.drawable.ic_info_outline_white);

        presenter.setView(ReadingParagraphsActivity.this);
        currentPageNo = 0;

        silence_outer_layout.setVisibility(View.GONE);

        Intent intent = getIntent();
        contentTitle = getIntent().getStringExtra("contentTitle");
        contentPath = intent.getStringExtra("contentPath");
        StudentID = intent.getStringExtra("StudentID");
        resId = intent.getStringExtra("resId");
        certiCode = intent.getStringExtra("certiCode");
        resType = intent.getStringExtra("resType");
        onSdCard = getIntent().getBooleanExtra("onSdCard", false);
        tvContentTitle.setText("" + contentTitle);
        tvContentTitle.setSelected(true);

        readSounds.add(R.raw.tap_the_mic);
        readSounds.add(R.raw.your_turn_to_read);
        readSounds.add(R.raw.would_you_like_to_read);
        readSounds.add(R.raw.tap_the_mic_to_read_out);
        Collections.shuffle(readSounds);

        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.setResId(resId);

        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";

        continuousSpeechService.resetSpeechRecognizer();
        attAnsList = new String[1];
        attAnsList[0]="";

        try {
            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                ll_btn_play.setVisibility(View.GONE);
                ll_btn_submit.setVisibility(View.VISIBLE);
            }
            presenter.fetchJsonData(readingContentPath, resType);
//            getWordsOfStoryOfPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void setCategoryTitle(String title) {
        tvContentTitle.setText(title);
    }

    @UiThread
    @Override
    public void setListData(List<ModalParaWord> paraDataList) {
        modalParaWordList = paraDataList;
    }

    @UiThread
    @Override
    public void setParaAudio(String paraAudio) {
        this.paraAudio = paraAudio;
    }

    @UiThread
    @Override
    public void initializeContent() {

        wordsDurationList.clear();
        myFlowLayout.removeAllViews();
        lineBreakCounter = 0;

        try {
            correctArr = new boolean[modalParaWordList.size()];

            for (int j = 0; j < modalParaWordList.size(); j++) {
                splitWords.add(modalParaWordList.get(j).getWord());
                if (modalParaWordList.get(j).getWord().equalsIgnoreCase("#"))
                    lineBreakCounter += 1;
                if (FastSave.getInstance().getString(CURRENT_FOLDER_NAME, "").equalsIgnoreCase("English"))
                    splitWordsPunct.add(splitWords.get(j)
                            .replaceAll("[^a-zA-Z ]", "")
                            .toLowerCase());
                else
                    splitWordsPunct.add(splitWords.get(j).replaceAll(STT_REGEX, ""));
                wordsDurationList.add(modalParaWordList.get(j).getWordDuration());
                wordsResIdList.add(modalParaWordList.get(j).getWordId());
            }
            startPlayBack = modalParaWordList.get(0).getWordFrom();

            setWordsToLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWordsToLayout() {
        try {
            for (int i = 0; i < modalParaWordList.size(); i++) {
                if (modalParaWordList.get(i).getWord().equalsIgnoreCase("#")) {
                    final SansTextView myTextView = new SansTextView(this);
                    myTextView.setWidth(2000);
                    myFlowLayout.addView(myTextView);
                } else {
                    final SansTextView myTextView = new SansTextView(this);
                    myTextView.setText("" + modalParaWordList.get(i).getWord());
                    myTextView.setTextSize(30);
                    myTextView.setTextColor(getResources().getColor(R.color.colorText));
                    myTextView.setId(i);
                    myTextView.setOnClickListener(v -> {
                        if (!FastSave.getInstance().getString(FC_Constants.CURRENT_FOLDER_NAME, "").equalsIgnoreCase("maths"))
                            if (!readingFlg && !playingFlg && !FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                                myTextView.setTextColor(getResources().getColor(R.color.colorRed));
                                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.reading_zoom_in_and_out);
                                myTextView.startAnimation(animation);
                                playClickedWord(myTextView.getId());
//                            ttsService.play("" + linesStringList[finalI]);
                            }
                    });
                    myFlowLayout.addView(myTextView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    MediaPlayer clickMP;
    Boolean clickFlag = false;

    private void playClickedWord(int id) {
        try {
            if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test) && !clickFlag) {
                clickFlag = true;
                Log.d("ReadingPara", "wordCounter : " + wordCounter);
                float end = Float.parseFloat(modalParaWordList.get(id).getWordDuration());
                end = end * 1000;
                float seekTime = Float.parseFloat(modalParaWordList.get(id).getWordFrom());
                float seekTimes = seekTime * 1000;
                Log.d("ReadingPara", "WORD START " + seekTimes + "      END TIME : " + end);
                clickMP = new MediaPlayer();
                clickMP.setDataSource(readingContentPath + paraAudio);
                clickMP.prepare();
                clickMP.seekTo((int) (seekTime * 1000));
                clickMP.start();
                endhandler = new Handler();
                endhandler.postDelayed(() -> {
                    clickMP.stop();
                    final SansTextView myView = (SansTextView) myFlowLayout.getChildAt(id);
                    myView.setTextColor(getResources().getColor(R.color.colorText));
                    clickFlag = false;
                }, (long) end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeAudioReading(int wordCounter) {
        try {
            Log.d("ReadingPara", "wordCounter : " + wordCounter);
            float seekTime = Float.parseFloat(modalParaWordList.get(wordCounter).getWordFrom());
            mp = new MediaPlayer();
            mp.setDataSource(readingContentPath + paraAudio);
            mp.prepare();
            mp.seekTo((int) (seekTime * 1000));
            mp.start();
            startStoryReading(wordCounter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startStoryReading(final int index) {
        float wordDuration;
        handler = new Handler();
        colorChangeHandler = new Handler();
        SansTextView myNextView = null;

        if (index < wordsDurationList.size()) {
            wordDuration = Float.parseFloat(wordsDurationList.get(index));
            final SansTextView myView = (SansTextView) myFlowLayout.getChildAt(index);

            myNextView = (SansTextView) myFlowLayout.getChildAt(index + 1);
            if (myNextView != null)
                isScrollBelowVisible(myNextView);

            myView.setTextColor(getResources().getColor(R.color.colorRedDark));
            myView.setBackgroundColor(getResources().getColor(R.color.yellow_text_bg));
            colorChangeHandler.postDelayed(() -> {
                myView.setBackgroundColor(getResources().getColor(R.color.full_transparent));
            }, 350);
            if (index == wordsDurationList.size() - 1) {
                try {
                    handler.postDelayed(() -> {
                        try {
                            playingFlg = false;
                            layout_mic_ripple.setVisibility(View.VISIBLE);
                            wordCounter = 0;
                            quesReadHandler = new Handler();
                            quesReadHandler.postDelayed(() -> {
                                Collections.shuffle(readSounds);
                                mPlayer = MediaPlayer.create(ReadingParagraphsActivity.this, readSounds.get(0));
                                mPlayer.start();
                            }, 5000);
                            if (mp != null && mp.isPlaying())
                                mp.stop();
                            ll_btn_stop.setVisibility(View.GONE);
                            btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
                            btn_Play.setBackgroundResource(R.drawable.button_green);
                            resetParaTheme();
                            layout_mic_ripple.startRippleAnimation();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, (long) (wordDuration * 1000));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                endhandler.postDelayed(() -> {
                    if (playingFlg) {
                        wordCounter += 1;
                        startStoryReading(wordCounter);
                    }
                }, (long) (wordDuration * 1000));
            }
        }
    }

    private void resetParaTheme() {
        for (int i = 0; i < wordsDurationList.size(); i++) {
            SansTextView myView = (SansTextView) myFlowLayout.getChildAt(i);
            myView.setBackgroundColor(Color.TRANSPARENT);
            myView.setTextColor(getResources().getColor(R.color.colorText));
        }
    }

    private void isScrollBelowVisible(View view) {
        Rect scrollBounds = new Rect();
        myScrollView.getDrawingRect(scrollBounds);

        float top = view.getY();
        float bottom = top + view.getHeight();

        if (!(scrollBounds.top < top) || !(scrollBounds.bottom > bottom))
            view.getParent().requestChildFocus(view, view);
        //myScrollView.smoothScrollTo(view.getTop(), view.getBottom());
    }

    @Click(R.id.btn_mic)
    public void startReading() {
        if (!readingFlg) {
            showLoader();
            if (quesReadHandler != null) {
                quesReadHandler.removeCallbacksAndMessages(null);
                try {
                    if (mPlayer.isPlaying() && mPlayer != null) {
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayer.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            readingFlg = true;
            ll_btn_play.setVisibility(View.GONE);
            continuousSpeechService.startSpeechInput();
            btn_Mic.setImageResource(R.drawable.ic_stop_black);
            btn_Mic.setBackgroundResource(R.drawable.button_red);
        } else {
            readingFlg = false;
            if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test))
                ll_btn_play.setVisibility(View.VISIBLE);
            continuousSpeechService.stopSpeechInput();
            btn_Mic.setImageResource(R.drawable.ic_mic_black);
            btn_Mic.setBackgroundResource(R.drawable.button_green);
        }
    }

    @Click(R.id.btn_play)
    public void playSound() {
        if (!playingFlg) {
            if (quesReadHandler != null) {
                quesReadHandler.removeCallbacksAndMessages(null);
                try {
                    if (mPlayer.isPlaying() && mPlayer != null) {
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayer.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            playingFlg = true;
            layout_mic_ripple.setVisibility(View.GONE);
            btn_Play.setImageResource(R.drawable.ic_pause_black);
            btn_Play.setBackgroundResource(R.drawable.button_yellow);
            ll_btn_stop.setVisibility(View.VISIBLE);
            initializeAudioReading(wordCounter);
        } else {
            playingFlg = false;
            //resetParaTheme();
            if (wordCounter > 1)
                wordCounter--;
            btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
            btn_Play.setBackgroundResource(R.drawable.button_green);
            ll_btn_stop.setVisibility(View.GONE);
            layout_mic_ripple.setVisibility(View.VISIBLE);
            mp.stop();
        }
    }

    @Click(R.id.btn_stop_audio)
    public void stopAudioSound() {
        mp.stop();
        if (quesReadHandler != null)
            quesReadHandler.removeCallbacksAndMessages(null);
        playingFlg = false;
        resetParaTheme();
        wordCounter = 0;
        btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
        btn_Play.setBackgroundResource(R.drawable.button_green);
        layout_mic_ripple.setVisibility(View.VISIBLE);
        ll_btn_stop.setVisibility(View.GONE);
        wordCounter = 0;
    }

    @Click(R.id.floating_back)
    public void pressedBackBtn() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        try {
            if (handler != null)
                handler.removeCallbacksAndMessages(null);
            if (colorChangeHandler != null)
                colorChangeHandler.removeCallbacksAndMessages(null);
            if (quesReadHandler != null) {
                quesReadHandler.removeCallbacksAndMessages(null);
                try {
                    if (mPlayer != null)
                        if (mPlayer.isPlaying()) {
                            mPlayer.stop();
                            mPlayer.reset();
                            mPlayer.release();
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (playingFlg) {
            playingFlg = false;
            resetParaTheme();
            wordCounter = 0;
            btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
            btn_Play.setBackgroundResource(R.drawable.button_green);
            ll_btn_stop.setVisibility(View.GONE);
            layout_mic_ripple.setVisibility(View.VISIBLE);
            mp.stop();
        }

        if (readingFlg) {
            readingFlg = false;
            ll_btn_play.setVisibility(View.VISIBLE);
            btn_Mic.setImageResource(R.drawable.ic_mic_black);
            btn_Mic.setBackgroundResource(R.drawable.button_green);
            continuousSpeechService.stopSpeechInput();
        }
        try {
            if (mp != null)
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.reset();
                    mp.release();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!fragFlg) {
            showAcknowledgeDialog(false);
        } else {
            fragFlg = false;
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (readingFlg) {
            readingFlg = false;
            ll_btn_play.setVisibility(View.VISIBLE);
            btn_Mic.setImageResource(R.drawable.ic_mic_black);
            btn_Mic.setBackgroundResource(R.drawable.button_green);
            continuousSpeechService.stopSpeechInput();
            wordCounter = 0;
        }
        try {
            if (playingFlg)
                playSound();
            if (handler != null)
                handler.removeCallbacksAndMessages(null);
            if (colorChangeHandler != null)
                colorChangeHandler.removeCallbacksAndMessages(null);
            if (quesReadHandler != null) {
                quesReadHandler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    public void showAcknowledgeDialog(boolean diaComplete) {
        if(!diaOpen) {
            diaOpen = true;
            final CustomLodingDialog dialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.fc_custom_dialog);
            dialog.setCanceledOnTouchOutside(false);

            TextView dia_title = dialog.findViewById(R.id.dia_title);
            Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
            Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
            Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

            dia_btn_green.setText("Yes");
            dia_btn_red.setText("No");
            dia_btn_yellow.setText("" + dialog_btn_cancel);
            dialog.show();

            if (!diaComplete)
                dia_title.setText("Exit\n'" + contentTitle + "'");
            else if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test)) {
                dia_title.setText("Good Job\nRead another one???");
            } else {
                dia_title.setText("Good Job\nRead another one???");
            }
            dia_btn_yellow.setOnClickListener(v -> {dialog.dismiss();
                diaOpen = false;
            });
            dia_btn_red.setOnClickListener(v -> {dialog.dismiss();
                diaOpen = false;
            });

            dia_btn_green.setOnClickListener(v -> {
                diaOpen = false;
                if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test)) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("cCode", certiCode);
                    returnIntent.putExtra("sMarks", presenter.getCorrectCounter());
                    returnIntent.putExtra("tMarks", presenter.getTotalCount());
                    setResult(Activity.RESULT_OK, returnIntent);
                }
                if (resType.equalsIgnoreCase(FC_Constants.PARA_VOCAB_ANDROID))
                    presenter.addParaVocabProgress();
                else
                    presenter.exitDBEntry();
                dialog.dismiss();
                finish();
            });
        }
    }

    @UiThread
    @Override
    public void setCorrectViewColor() {
        try {
            for (int x = 0; x < correctArr.length; x++) {
                if (correctArr[x]) {
                    ((SansTextView) myFlowLayout.getChildAt(x)).setTextColor(getResources().getColor(R.color.readingGreen));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        iv_monk.clearAnimation();
        iv_monk.setVisibility(View.GONE);
    }

    @UiThread
    @Override
    public void allCorrectAnswer() {
        for (int x = 0; x < myFlowLayout.getChildCount(); x++) {
            ((SansTextView) myFlowLayout.getChildAt(x)).setTextColor(getResources().getColor(R.color.readingGreen));
        }
        if (readingFlg)
            btn_Mic.performClick();
        iv_monk.clearAnimation();
        iv_monk.setVisibility(View.GONE);
        if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test))
            showAcknowledgeDialog(true);
        else
            showStars(true);
    }

    @Click(R.id.btn_submit)
    public void submitTest() {
        showStars(false);
    }


    @SuppressLint("SetTextI18n")
    private void showStars(boolean diaComplete) {

        final CustomLodingDialog dialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_test_star_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        RatingBar dia_ratingBar = dialog.findViewById(R.id.dia_ratingBar);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        int crtCnt = presenter.getCorrectCounter();
        int totCnt = presenter.getTotalCount();

        float perc = 0f;
        perc = ((float) crtCnt / (float) totCnt) * 100;

        float rating = getStarRating(perc);

        dia_ratingBar.setRating(rating);

        dia_btn_red.setVisibility(View.GONE);
        dia_btn_green.setText("SUBMIT");
        dia_btn_yellow.setText("" + dialog_btn_cancel);
        if (diaComplete)
            dia_btn_yellow.setVisibility(View.GONE);

        dialog.show();

        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
        dia_btn_red.setOnClickListener(v -> dialog.dismiss());

        dia_btn_green.setOnClickListener(v -> {
            dialog.dismiss();
            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("cCode", certiCode);
                returnIntent.putExtra("sMarks", presenter.getCorrectCounter());
                returnIntent.putExtra("tMarks", presenter.getTotalCount());
                setResult(Activity.RESULT_OK, returnIntent);
            }
            if (resType.equalsIgnoreCase(FC_Constants.PARA_VOCAB_ANDROID))
                presenter.addParaVocabProgress();
            else
                presenter.exitDBEntry();
            finish();
        });
    }

    public float getStarRating(float perc) {
        float ratings = 5;
        if (perc < 21)
            ratings = (float) 1;
        else if (perc >= 21 && perc < 41)
            ratings = (float) 2;
        else if (perc >= 41 && perc < 61)
            ratings = (float) 3;
        else if (perc >= 61 && perc < 81)
            ratings = (float) 4;
        else if (perc >= 81)
            ratings = (float) 5;

        return ratings;
    }

    @Override
    public void stoppedPressed() {
        showLoader();
        presenter.micStopped(splitWordsPunct, wordsResIdList);
    }

    @Override
    public void sttEngineReady() {
        dismissLoadingDialog();
    }

    @Override
    public void Stt_onResult(ArrayList<String> sttResult) {
        iv_monk.setVisibility(View.VISIBLE);
        iv_monk.startAnimation(AnimationUtils.loadAnimation(this, R.anim.float_anim));
        presenter.sttResultProcess(sttResult, splitWordsPunct, wordsResIdList);
        if(sttResult.size()>0) {
            String txt = String.valueOf(stt_result_tv.getText());
            String atxt = txt + sttResult.get(0)+ " ";
            attAnsList[currentPageNo]=atxt;
            stt_result_tv.setText("");
            stt_result_tv.setTextSize(28);
            stt_result_tv.setText(attAnsList[currentPageNo]);
            stt_result_tv.setMovementMethod(new ScrollingMovementMethod());
        }
    }

    @Click(R.id.clean_stt)
    void sttClearClicked() {
        if(readingFlg)
            btn_Mic.performClick();
        et_edit_ans.setText(attAnsList[currentPageNo]);
        ll_edit_text.setVisibility(View.VISIBLE);
//        stt_result_tv.setText("");
//        attAnsList[currentPage]="";
    }

    @Click(R.id.bt_edit_ok)
    public void editOKClicked(){
        attAnsList[currentPageNo] = ""+et_edit_ans.getText();
        stt_result_tv.setText(attAnsList[currentPageNo]);
        ArrayList<String> sttResult = new ArrayList<>();
        sttResult.add(attAnsList[currentPageNo]);
        presenter.sttResultProcess(sttResult, splitWordsPunct, wordsResIdList);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(ll_edit_text.getWindowToken(), 0);
        ll_edit_text.setVisibility(View.GONE);
//        hideSystemUI();
    }

    public CustomLodingDialog myLoadingDialog;
    boolean dialogFlg = false;

    @UiThread
    @Override
    public void showLoader() {
        if (!dialogFlg) {
            dialogFlg = true;
            myLoadingDialog = new CustomLodingDialog(this);
            myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(myLoadingDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myLoadingDialog.setContentView(R.layout.loading_dialog);
            myLoadingDialog.setCanceledOnTouchOutside(false);
            myLoadingDialog.show();
        }
    }

    @UiThread
    @Override
    public void dismissLoadingDialog() {
        try {
            dialogFlg = false;
            new Handler().postDelayed(() -> {
                if (myLoadingDialog != null && myLoadingDialog.isShowing())
                    myLoadingDialog.dismiss();
            }, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @ViewById(R.id.silence_outer)
    RelativeLayout silence_outer_layout;
    @ViewById(R.id.silence_main_layout)
    RelativeLayout silence_main_layout;
    @ViewById(R.id.silence_iv)
    ImageView silence_iv;
    Handler silenceViewHandler;

    @Override
    public void silenceDetected() {
        if (readingFlg) {
            continuousSpeechService.resetHandler(true);
            silence_outer_layout.setVisibility(View.VISIBLE);
            silenceViewHandler = new Handler();
            silence_iv.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_continuous_shake));
            AnimateTextView(this, silence_main_layout);
        }
    }

    public void AnimateTextView(Context c, final RelativeLayout silence_layout) {
        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.zoom_in_new);
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out_full);
        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                silence_layout.setAnimation(anim_out);
            }
        });
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                resetSilence();
            }
        });
        //(holder.mTextView).setAnimation(anim_in);
        silence_layout.setAnimation(anim_in);
    }

    private void resetSilence() {
        silenceViewHandler.postDelayed(() -> {
            silence_iv.clearAnimation();
            silence_outer_layout.setVisibility(View.GONE);
            continuousSpeechService.resetHandler(false);
        }, 10);
    }
}
