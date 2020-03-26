package com.pratham.foundation.ui.contentPlayer.new_reading_fragment_2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ModalParaSubMenu;
import com.pratham.foundation.services.TTSService;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.services.stt.ContinuousSpeechService_New;
import com.pratham.foundation.services.stt.STT_Result_New;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.BaseActivity.setMute;
import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_FOLDER_NAME;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;


//@EFragment(R.layout.reading_layout_xml_file)
@EFragment(R.layout.fragment_story_reading_2)
public class ScrollReadingFragment extends Fragment implements
        /*RecognitionListener, */STT_Result_New.sttView,
        ScrollReadingContract.ScrollReadingView, OnGameClose {

    @Bean(ScrollReadingPresenter.class)
    ScrollReadingContract.ScrollReadingPresenter presenter;

    public static MediaPlayer mp, mPlayer;
    @ViewById(R.id.myflowlayout)
    FlowLayout wordFlowLayout;
    //    @ViewById(R.id.toolbar)
//    Toolbar toolbar;
//    @ViewById(R.id.parapax_image)
//    ImageView parapax_image;
    @ViewById(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.iv_image)
    ImageView iv_image;
//    @ViewById(R.id.tv_story_title)
//    TextView story_title;
    @ViewById(R.id.btn_prev)
    ImageButton btn_previouspage;
    @ViewById(R.id.btn_next)
    ImageButton btn_nextpage;
    @ViewById(R.id.btn_play)
    ImageButton btn_Play;
    @ViewById(R.id.btn_read_mic)
    ImageButton btn_Mic;
    @ViewById(R.id.myScrollView)
    ScrollView myScrollView;
    @ViewById(R.id.btn_submit)
    Button btn_submit;
    @ViewById(R.id.story_ll)
    RelativeLayout story_ll;
    @ViewById(R.id.btn_Stop)
    ImageButton btn_Stop;
    @ViewById(R.id.btn_camera)
    ImageButton btn_camera;
    //    @ViewById(R.id.ib_back)
//    ImageButton ib_back;
//    @ViewById(R.id.ib_page_img)
//    ImageButton ib_page_img;
    @ViewById(R.id.bottom_bar2)
    LinearLayout bottom_bar2;
    //    @ViewById(R.id.ll_btn_next)
//    LinearLayout ll_btn_next;
//    @ViewById(R.id.ll_btn_prev)
//    LinearLayout ll_btn_prev;
    @ViewById(R.id.floating_img)
    FloatingActionButton floating_img;
/*    @ViewById(R.id.rl1)
    RelativeLayout rl1;
    @ViewById(R.id.rl2)
    RelativeLayout rl2;
    @ViewById(R.id.rl3)
    RelativeLayout rl3;
    @ViewById(R.id.rl4)
    RelativeLayout rl4;*/

    ContinuousSpeechService_New continuousSpeechService;

    public static String storyId, StudentID = "", readingContentPath;
    TTSService ttsService;
    Context context;

    List<ModalParaSubMenu> modalPagesList;

    String contentType, storyPath, storyName, storyAudio, certiCode, storyBg, pageTitle, sttLang;
    static int currentPage, lineBreakCounter = 0;

    public Handler handler, audioHandler, soundStopHandler, colorChangeHandler,
            startReadingHandler, quesReadHandler, endhandler;

    List<String> splitWords = new ArrayList<String>();
    List<String> splitWordsPunct = new ArrayList<String>();
    List<String> wordsDurationList = new ArrayList<String>();
    List<String> wordsResIdList = new ArrayList<String>();

    boolean fragFlg = false, lastPgFlag = false;
    boolean playFlg = false, mediaPauseFlag = false, pauseFlg = false, playHideFlg = false;
    int wordCounter = 0, totalPages = 0, correctAnswerCount, pageNo = 1, quesNo = 0, quesPgNo = 0;
    float stopPlayBack = 0f, startPlayBack = 0f;
    List<Integer> readSounds = new ArrayList<>();
    private String LOG_TAG = "VoiceRecognitionActivity", /*myCurrentSentence,*/
            startTime;
    boolean voiceStart = false, flgPerMarked = false, onSdCard;
    static boolean[] correctArr;
    static boolean[] testCorrectArr;
//    AnimationDrawable animationDrawable;

/*
        bundle.putString("nodeID", nodeID);
        bundle.putString("contentType","s");
        bundle.putString("storyPath","s");
        bundle.putString("storyId","s");
        bundle.putString("storyTitle","s");
        bundle.putString("certiCode","s");
        bundle.putBoolean("onSdCard", false);
        FC_Utility.showFragment(ContentPlayerActivity.this, new ContentReadingFragment_(), R.id.RL_CPA,
    bundle, ContentReadingFragment_.class.getSimpleName());
*/


    @AfterViews
    public void initialize() {
        showLoader();
        context = getActivity();

//        if (getActivity() instanceof ContentPlayerActivity) {
//            ((ContentPlayerActivity) getActivity()).hideFloating_info();
//        }

        silence_outer_layout.setVisibility(View.GONE);
        Bundle bundle = getArguments();
        contentType = bundle.getString("contentType");
        storyPath = bundle.getString("contentPath");
        storyId = bundle.getString("resId");
        storyName = bundle.getString("contentName");
        certiCode = bundle.getString("certiCode");
        sttLang = bundle.getString("sttLang");
        onSdCard = bundle.getBoolean("onSdCard", false);
        ttsService = ApplicationClass.ttsService;
        contentType = "story";

//        image_container.setVisibility(View.GONE);
//        floating_img.setImageResource(R.drawable.ic_image_white);

        bottom_bar2.setVisibility(View.GONE);
        btn_camera.setVisibility(View.GONE);

//        animationDrawable = (AnimationDrawable) story_ll.getBackground();
//        animationDrawable.setEnterFadeDuration(4500);
//        animationDrawable.setExitFadeDuration(4500);
//        animationDrawable.start();
        presenter.setView(ScrollReadingFragment.this);
        modalPagesList = new ArrayList<>();

        continuousSpeechService = new ContinuousSpeechService_New(context, ScrollReadingFragment.this, sttLang);
        if (contentType.equalsIgnoreCase(FC_Constants.RHYME_RESOURCE))
            btn_Mic.setVisibility(View.GONE);

        startFrag();
    }

    private void startFrag() {
        final Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Sarala_Bold.ttf");
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        readSounds.add(R.raw.tap_the_mic);
        readSounds.add(R.raw.your_turn_to_read);
        readSounds.add(R.raw.would_you_like_to_read);
        readSounds.add(R.raw.tap_the_mic_to_read_out);
        Collections.shuffle(readSounds);
        startTime = FC_Utility.getCurrentDateTime();
        presenter.setResId(storyId);

        currentPage = 0;

        presenter.addScore(0, "", 0, 0, startTime, contentType + " start");
        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + storyPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + storyPath + "/";

        continuousSpeechService.resetSpeechRecognizer();

        try {
            toolbar.setTitle(Html.fromHtml("" + storyName));
//            story_title.setText(Html.fromHtml("" + storyName));
//            toolbar.setTitle(storyName);
            presenter.fetchJsonData(readingContentPath);
            //pageArray = presenter.fetchJsonData(storyName);
//            getWordsOfStoryOfPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListData(List<ModalParaSubMenu> paraDataList) {
        modalPagesList = paraDataList;
        totalPages = modalPagesList.size();
    }

    public CustomLodingDialog myLoadingDialog;
    boolean dialogFlg = false;

    @UiThread
    @Override
    public void showLoader() {
        if (!dialogFlg) {
            dialogFlg = true;
            myLoadingDialog = new CustomLodingDialog(context);
            myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(myLoadingDialog.getWindow()).setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            myLoadingDialog.setContentView(R.layout.loading_dialog);
            myLoadingDialog.setCanceledOnTouchOutside(false);
            myLoadingDialog.show();
        }
    }

    @UiThread
    @Override
    public void dismissLoadingDialog() {
        try {
            if (myLoadingDialog != null && myLoadingDialog.isShowing()) {
                dialogFlg = false;
                myLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void setCategoryTitle(String title) {
//        story_title.setText(Html.fromHtml("" + storyName));
        toolbar.setTitle(storyName);
    }

    @Override
    public void setParaAudio(String paraAudio) {
        storyAudio = paraAudio;
    }

    @UiThread
    @Override
    public void initializeContent(int pageNo) {
        currentPage = pageNo;
        if (currentPage == 0) {
            lastPgFlag = false;
            btn_previouspage.setVisibility(View.GONE);
        }
        if (currentPage < totalPages - 1 && currentPage > 0) {
            lastPgFlag = false;
            btn_previouspage.setVisibility(View.VISIBLE);
            btn_nextpage.setVisibility(View.VISIBLE);
        }

        wordFlowLayout.removeAllViews();
        storyAudio = modalPagesList.get(currentPage).getReadPageAudio();
        storyBg = modalPagesList.get(currentPage).getPageImage();
        pageTitle = modalPagesList.get(currentPage).getPageTitle();
//        if (pageTitle != null && !pageTitle.equalsIgnoreCase(""))
//            story_title.setText(Html.fromHtml("" + pageTitle));
        if (pageTitle != null && !pageTitle.equalsIgnoreCase(""))
            toolbar.setTitle(storyName);

        playHideFlg = storyAudio.equalsIgnoreCase("NA");
        btn_Stop.performClick();
//        floating_img.performClick();
//        floating_info.performClick();
        showPageImage();
        correctArr = new boolean[modalPagesList.get(currentPage).getReadList().size()];
        splitWords = new ArrayList<>();
        splitWordsPunct = new ArrayList<>();
        wordsDurationList = new ArrayList<>();
        wordsResIdList = new ArrayList<>();
        lineBreakCounter = 0;

        for (int i = 0; i < modalPagesList.get(currentPage).getReadList().size(); i++) {
            splitWords.add(modalPagesList.get(currentPage).getReadList().get(i).getWord());
            if (modalPagesList.get(currentPage).getReadList().get(i).getWord().equalsIgnoreCase("#"))
                lineBreakCounter += 1;
            correctArr[i] = false;
            if (FastSave.getInstance().getString(CURRENT_FOLDER_NAME, "").equalsIgnoreCase("English"))
                splitWordsPunct.add(modalPagesList.get(currentPage).getReadList().get(i).getWord().replaceAll("[^a-zA-Z ]", "").toLowerCase());
            else
                splitWordsPunct.add(splitWords.get(i).replaceAll(STT_REGEX, ""));
            wordsDurationList.add(modalPagesList.get(currentPage).getReadList().get(i).getWordDuration());
            wordsResIdList.add(modalPagesList.get(currentPage).getReadList().get(i).getWordId());
        }
        startPlayBack = Float.parseFloat(modalPagesList.get(currentPage).getReadList().get(0).getWordFrom());
        setWordsToLayout();
        if (playHideFlg)
            btn_Play.setVisibility(View.GONE);
        startTime = FC_Utility.getCurrentDateTime();
    }

    private void revealShow(View dialogView, boolean b, final CustomLodingDialog dialog) {

        final View view = dialogView.findViewById(R.id.dialog_main);
        int w = view.getWidth();
        int h = view.getHeight();
        int endRadius = (int) Math.hypot(w, h);
        int cx = (int) (view.getX() + (view.getWidth() / 2));
        int cy = (int) (view.getY()) + view.getHeight() + 56;

        if (b) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);
            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(700);
            revealAnimator.start();

        } else {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);
                }
            });
            anim.setDuration(700);
            anim.start();
        }

    }

    private void showPageImage() {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(readingContentPath + storyBg);
            BitmapFactory.decodeStream(new FileInputStream(readingContentPath + storyBg));
            iv_image.setImageBitmap(bitmap);
            setColorForToolBar(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
/*        final Dialog dialog = new Dialog(getActivity());
        final View dialogView = View.inflate(getActivity(),R.layout.fc_show_image_dialog,null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);
        ImageView iv_dia_preview = dialog.findViewById(R.id.iv_dia_img);
        ImageButton dia_btn_cross = dialog.findViewById(R.id.dia_btn_cross);
        GifView gif_view = dialog.findViewById(R.id.gif_dia_view);

        dialog.setOnShowListener(dialogInterface -> revealShow(dialogView, true, null));

        try {
            File f = new File(readingContentPath + storyBg);
            if (f.exists()) {
                if (storyBg.contains(".gif")) {
                    iv_dia_preview.setVisibility(View.GONE);
                    gif_view.setVisibility(View.VISIBLE);
                    gif_view.setGifResource(new FileInputStream(readingContentPath + storyBg));
                    gif_view.play();
                } else {
                    gif_view.setVisibility(View.GONE);
                    iv_dia_preview.setVisibility(View.VISIBLE);
                    Bitmap bmImg = BitmapFactory.decodeFile(readingContentPath + storyBg);
                    BitmapFactory.decodeStream(new FileInputStream(readingContentPath + storyBg));
                    iv_dia_preview.setImageBitmap(bmImg);
                }
            } else {
                gif_view.setVisibility(View.GONE);
                iv_dia_preview.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.show();
        dia_btn_cross.setOnClickListener(v -> {
            dialog.dismiss();
            new Handler().postDelayed(() -> {
                if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test))
                    btn_Play.performClick();
                else {
                    btn_Mic.performClick();
                    btn_Play.setVisibility(View.GONE);
                    bottom_bar2.setVisibility(View.VISIBLE);
                    btn_submit.setVisibility(View.VISIBLE);
                }
            }, 200);
        });*/
    }

/*
        <RelativeLayout
    android:layout_width="wrap_content"
    android:layout_centerHorizontal="true"
    android:layout_above="@id/bottom_bar"
    android:elevation="@dimen/_60sdp"
    android:layout_height="50dp">
        <RelativeLayout
    android:id="@+id/rl1"
    android:layout_width="50dp"
    android:layout_height="50dp"/>
        <RelativeLayout
    android:id="@+id/rl2"
    android:layout_toEndOf="@+id/rl1"
    android:layout_marginStart="@dimen/_10sdp"
    android:layout_width="50dp"
    android:layout_height="50dp"/>
        <RelativeLayout
    android:id="@+id/rl3"
    android:layout_toEndOf="@+id/rl2"
    android:layout_marginStart="@dimen/_10sdp"
    android:layout_width="50dp"
    android:layout_height="50dp"/>
        <RelativeLayout
    android:id="@+id/rl4"
    android:layout_toEndOf="@+id/rl3"
    android:layout_marginStart="@dimen/_10sdp"
    android:layout_width="50dp"
    android:layout_height="50dp"/>

    </RelativeLayout>
*/
    @UiThread
    public void setColorForToolBar(Bitmap bitmap) {
        try {
            Palette p = Palette.from(bitmap).generate();
            Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
            // Load default colors
            int backgroundColor = ContextCompat.getColor(context,
                    R.color.level_3_color);
            int textColor = ContextCompat.getColor(context,
                    R.color.white);
            // Check that the Vibrant swatch is available
            if(vibrantSwatch != null){
                backgroundColor = vibrantSwatch.getRgb();
                textColor = vibrantSwatch.getTitleTextColor();
            }

            // Set the toolbar background and text colors
/*            rl1.setBackgroundColor(p.getDarkMutedColor(backgroundColor));
            rl2.setBackgroundColor(backgroundColor);
            rl3.setBackgroundColor(textColor);
            rl4.setBackgroundColor(p.getLightVibrantColor(textColor));*/
            collapsingToolbarLayout.setBackgroundColor(textColor);
            collapsingToolbarLayout.setStatusBarScrimColor(p.getDarkMutedColor(backgroundColor));
            collapsingToolbarLayout.setContentScrimColor(backgroundColor);
            collapsingToolbarLayout.setCollapsedTitleTextColor(p.getDarkMutedColor(backgroundColor));
            collapsingToolbarLayout.setExpandedTitleColor(backgroundColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void allCorrectAnswer() {
        dismissLoadingDialog();
        for (int i = 0; i < splitWordsPunct.size(); i++) {
            ((SansTextView) wordFlowLayout.getChildAt(i)).setTextColor(getResources().getColor(R.color.colorBtnGreenDark));
            correctArr[i] = true;
        }
        new Handler().postDelayed(() -> {
            //setMute(0);
            if (lastPgFlag) {
                if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test))
                    showStars(true);
                else
                    showAcknowledgeDialog(true);
            } else {
                btn_nextpage.performClick();
            }
        }, 1200);
    }

    private void setWordsToLayout() {
        for (int i = 0; i < splitWords.size(); i++) {
            if (splitWords.get(i).equalsIgnoreCase("#")) {
                final SansTextView myTextView = new SansTextView(context);
                myTextView.setWidth(2000);
                wordFlowLayout.addView(myTextView);
            } else {
                final SansTextView myTextView = new SansTextView(context);
                myTextView.setText(Html.fromHtml(splitWords.get(i)));
                myTextView.setId(i);
                myTextView.setTextSize(30);
                myTextView.setTextColor(getResources().getColor(R.color.colorText));
                final int finalI = i;
                myTextView.setOnClickListener(v -> {
                    if (!FastSave.getInstance().getString(FC_Constants.CURRENT_FOLDER_NAME, "").equalsIgnoreCase("maths"))
                        if ((!playFlg || pauseFlg) && !voiceStart) {
                            setMute(0);
                            myTextView.setTextColor(getResources().getColor(R.color.colorRedDark));
                            Animation animation = AnimationUtils.loadAnimation(context, R.anim.reading_zoom_in);
                            myTextView.startAnimation(animation);
                            if (colorChangeHandler == null)
                                colorChangeHandler = new Handler();
                            colorChangeHandler.postDelayed(() -> {
                                myTextView.setTextColor(getResources().getColor(R.color.colorText));
                                Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.reading_zoom_out);
                                myTextView.startAnimation(animation1);
                            }, 350);
                            if (!storyAudio.equalsIgnoreCase("NA"))
                                playClickedWord(finalI);
//                        ttsService.play("" + linesStringList[finalI]);
                        }
                });
                wordFlowLayout.addView(myTextView);
            }
        }
        dismissLoadingDialog();
    }

    Boolean clickFlag = false;

    private void playClickedWord(int id) {
        try {
            if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test) && !clickFlag) {
                clickFlag = true;
                Log.d("ReadingPara", "wordCounter : " + wordCounter);
                float end = Float.parseFloat(modalPagesList.get(currentPage).getReadList().get(id).getWordDuration());
                end = end * 1000;
                float seekTime = Float.parseFloat(modalPagesList.get(currentPage).getReadList().get(id).getWordFrom());
                MediaPlayer clickMP = new MediaPlayer();
                clickMP.setDataSource(readingContentPath + storyAudio);
                clickMP.prepare();
                clickMP.seekTo((int) (seekTime * 1000));
                clickMP.start();
                endhandler = new Handler();
                endhandler.postDelayed(() -> {
                    clickMP.stop();
                    final SansTextView myView = (SansTextView) wordFlowLayout.getChildAt(id);
                    myView.setTextColor(getResources().getColor(R.color.colorText));
                    clickFlag = false;
                }, (long) end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAudioReading(int wordCounter) {
        try {
            mp = new MediaPlayer();
            float seekTime = Float.parseFloat(modalPagesList.get(currentPage).getReadList().get(wordCounter).getWordFrom());
            mp.setDataSource(readingContentPath + storyAudio);
            mp.prepare();
            mp.seekTo((int) (seekTime * 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        startStoryReading(wordCounter);
    }

    public void startStoryReading(final int index) {
        float wordDuration = 1;
        handler = new Handler();
        colorChangeHandler = new Handler();
        mp.start();
        SansTextView myNextView = null;

        if (index < wordsDurationList.size()) {
            wordDuration = Float.parseFloat(wordsDurationList.get(index));
            final SansTextView myView = (SansTextView) wordFlowLayout.getChildAt(index);
            if (index < wordFlowLayout.getChildCount())
                myNextView = (SansTextView) wordFlowLayout.getChildAt(index + 1);

            if (myNextView != null)
                isScrollBelowVisible(myNextView);
            myView.setTextColor(getResources().getColor(R.color.colorRedDark));
            myView.setBackgroundColor(getResources().getColor(R.color.yellow_text_bg));
//            Animation animation = AnimationUtils.loadAnimation(context, R.anim.reading_zoom_in);
//            myView.startAnimation(animation);
//            wordPopUp(this, myView);
            colorChangeHandler.postDelayed(() -> {
                myView.setTextColor(getResources().getColor(R.color.colorText));
                myView.setBackgroundColor(getResources().getColor(R.color.full_transparent));
//                    wordPopDown(ReadingStoryActivity.this, myView);
//                Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.reading_zoom_out);
//                myView.startAnimation(animation1);
            }, 350);
            if (index == wordsDurationList.size() - 1) {
                try {
                    handler.postDelayed(() -> {
                        try {
                            playFlg = false;
                            pauseFlg = true;
                            if (!contentType.equalsIgnoreCase(FC_Constants.RHYME_RESOURCE)) {
                                btn_Mic.setVisibility(View.VISIBLE);
                                btn_Stop.setVisibility(View.GONE);
                                wordCounter = 0;
                                quesReadHandler = new Handler();
                                quesReadHandler.postDelayed(() -> {
                                    Collections.shuffle(readSounds);
                                    mPlayer = MediaPlayer.create(context, readSounds.get(0));
                                    mPlayer.start();
                                }, 5000);
                            }
                            btn_Stop.performClick();
//                            layout_mic_ripple.startRippleAnimation();
//                            layout_ripplepulse_right.startRippleAnimation();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, (long) (wordDuration * 1000));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPauseFlag = true;
            }
        } else
            wordDuration = 1;

        handler.postDelayed(() -> {
            if (playFlg && !pauseFlg) {
                if (index < wordFlowLayout.getChildCount()) {
                    wordCounter += 1;
                    if (!pauseFlg) {
                        startStoryReading(wordCounter);
                    }
                } else {
                    for (int i = 0; i < wordsDurationList.size(); i++) {
                        SansTextView myView = (SansTextView) wordFlowLayout.getChildAt(i);
                        myView.setBackgroundColor(Color.TRANSPARENT);
                        myView.setTextColor(getResources().getColor(R.color.colorText));
                    }
                    wordCounter = 0;
                }
            }
        }, (long) (wordDuration * 1000));
    }

    @UiThread
    @Override
    public void setCorrectViewColor() {
        try {
            for (int x = 0; x < correctArr.length; x++) {
                if (correctArr[x]) {
                    ((SansTextView) wordFlowLayout.getChildAt(x)).setTextColor(getResources().getColor(R.color.colorBtnGreenDark));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (voiceStart)
//            sttMethod();
    }

    //If you want to detect that the view is FULLY visible:
    private void isScrollBelowVisible(View view) {
//        Rect scrollBounds = new Rect();
//        myScrollView.getDrawingRect(scrollBounds);
//        float top = view.getY();
//        float bottom = top + view.getHeight();
//        if (!(scrollBounds.top < top) || !(scrollBounds.bottom > bottom))
//            view.getParent().requestChildFocus(view, view);
    }

    @Click(R.id.btn_Stop)
    void stopBtn() {
        if (voiceStart) {
            voiceStart = false;
            btn_Stop.setVisibility(View.GONE);
            if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test) && !playHideFlg)
                btn_Play.setVisibility(View.VISIBLE);
            btn_Mic.setVisibility(View.VISIBLE);
            continuousSpeechService.stopSpeechInput();
        } else if (playFlg || pauseFlg) {
            wordCounter = 0;
            btn_Stop.setVisibility(View.GONE);
            btn_Play.setVisibility(View.VISIBLE);
            if (!contentType.equalsIgnoreCase(FC_Constants.RHYME_RESOURCE))
                btn_Mic.setVisibility(View.VISIBLE);
            btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
            startPlayBack = Float.parseFloat(modalPagesList.get(currentPage).getReadList().get(0).getWordFrom());

            try {
                playFlg = false;
                pauseFlg = true;
                try {
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.reset();
                        mp.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (startReadingHandler != null)
                    startReadingHandler.removeCallbacksAndMessages(null);
                if (soundStopHandler != null)
                    soundStopHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Click(R.id.btn_read_mic)
    void sttMethod() {
//        if (!voiceStart) {
        voiceStart = true;
        flgPerMarked = false;
        showLoader();
        btn_Mic.setVisibility(View.GONE);
        btn_Play.setVisibility(View.GONE);
        btn_Stop.setVisibility(View.VISIBLE);
        try {
            if (quesReadHandler != null) {
                quesReadHandler.removeCallbacksAndMessages(null);
                try {
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
        continuousSpeechService.startSpeechInput();
//        } else {
//            voiceStart = false;
//            if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test) && !playHideFlg)
//                btn_Play.setVisibility(View.VISIBLE);
//            btn_Mic.setImageResource(R.drawable.ic_mic_black);
////            layout_mic_ripple.startRippleAnimation();
////            layout_ripplepulse_right.startRippleAnimation();
//            continuousSpeechService.stopSpeechInput();
//        }
    }

    @Click(R.id.btn_play)
    void playReading() {
        if (!storyAudio.equalsIgnoreCase("NA")) {
            if (!playFlg) {
                btn_Mic.setVisibility(View.GONE);
                btn_Stop.setVisibility(View.VISIBLE);
                btn_Play.setImageResource(R.drawable.ic_pause_black);
                playFlg = true;
                pauseFlg = false;
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            btn_Play.setImageResource(R.drawable.ic_stop_black_24dp);
//            btn_Play.setText("Stop");
                if (audioHandler != null)
                    audioHandler.removeCallbacksAndMessages(null);
                if (handler != null)
                    handler.removeCallbacksAndMessages(null);
                if (colorChangeHandler != null)
                    colorChangeHandler.removeCallbacksAndMessages(null);
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
                if (startReadingHandler != null)
                    startReadingHandler.removeCallbacksAndMessages(null);
                if (soundStopHandler != null)
                    soundStopHandler.removeCallbacksAndMessages(null);
//            layout_ripplepulse_right.stopRippleAnimation();
                setMute(0);
                startAudioReading(wordCounter);
            } else {
                playFlg = false;
                pauseFlg = true;
                btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
                if (wordCounter > 1)
                    wordCounter--;
                try {
                    if (mp.isPlaying()) {
                        mp.pause();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

/*            btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
            if (!contentType.equalsIgnoreCase(FC_Constants.RHYME_RESOURCE))
                btn_Mic.setVisibility(View.VISIBLE);
            wordCounter = 0;
            try {
                playFlg = false;
                pauseFlg = true;
                try {
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.reset();
                        mp.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (startReadingHandler != null)
                    startReadingHandler.removeCallbacksAndMessages(null);
                if (soundStopHandler != null)
                    soundStopHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            }
        }
    }

    public int setBooleanGetCounter() {
        int counter = 0;
        for (int x = 0; x < correctArr.length; x++) {
            if (correctArr[x]) {
                ((SansTextView) wordFlowLayout.getChildAt(x)).setTextColor(getResources().getColor(R.color.colorBtnGreenDark));
                counter++;
            }
        }
        return counter;
    }

    public float getPercentage() {
        int counter = 0;
        float perc = 0f;
        int totLen = correctArr.length - lineBreakCounter;
        try {
            for (int x = 0; x < correctArr.length; x++) {
                if (correctArr[x]) {
                    ((SansTextView) wordFlowLayout.getChildAt(x)).setTextColor(getResources().getColor(R.color.colorBtnGreenDark));
                    counter++;
                }
            }
            if (counter > 0)
                perc = ((float) counter / (float) totLen) * 100;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return perc;
    }

    @Click(R.id.btn_prev)
    void gotoPrevPage() {
        if (currentPage > 0) {
            wordCounter = 0;
            ButtonClickSound.start();
            try {
                if (audioHandler != null)
                    audioHandler.removeCallbacksAndMessages(null);
                if (handler != null)
                    handler.removeCallbacksAndMessages(null);
                if (colorChangeHandler != null)
                    colorChangeHandler.removeCallbacksAndMessages(null);
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
                if (soundStopHandler != null)
                    soundStopHandler.removeCallbacksAndMessages(null);
                if (startReadingHandler != null)
                    startReadingHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (voiceStart) {
                voiceStart = false;
                if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test) && !playHideFlg)
                    btn_Play.setVisibility(View.VISIBLE);
                btn_Mic.setImageResource(R.drawable.ic_mic_black);
                continuousSpeechService.stopSpeechInput();
                setMute(0);
            }
            Log.d("click", "totalPages: PreviousBtn: " + totalPages + "  currentPage: " + currentPage);
            try {
                if (mp != null && mp.isPlaying()) {
                    mp.stop();
                    mp.reset();
                    mp.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!flgPerMarked) {
                flgPerMarked = true;
                correctAnswerCount = setBooleanGetCounter();
                float perc = getPercentage();
                presenter.addScore(0, "perc - " + perc, correctAnswerCount, correctArr.length, startTime, "" + contentType);
            }

            try {
                currentPage--;
                pageNo--;
                playFlg = false;
                pauseFlg = true;
                flgPerMarked = false;
                presenter.getPage(currentPage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Click(R.id.btn_submit)
    public void submitTest() {
        showStars(false);
    }

    @Click(R.id.btn_next)
    void gotoNextPage() {
        if (currentPage < totalPages - 1) {
//            layout_ripplepulse_right.startRippleAnimation();
            wordCounter = 0;
            setMute(0);
            try {
                if (audioHandler != null)
                    audioHandler.removeCallbacksAndMessages(null);
                if (handler != null)
                    handler.removeCallbacksAndMessages(null);
                if (colorChangeHandler != null)
                    colorChangeHandler.removeCallbacksAndMessages(null);
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
                if (soundStopHandler != null)
                    soundStopHandler.removeCallbacksAndMessages(null);
                if (startReadingHandler != null)
                    startReadingHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (voiceStart) {
                voiceStart = false;
                if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test) && !playHideFlg)
                    btn_Play.setVisibility(View.VISIBLE);
                btn_Mic.setImageResource(R.drawable.ic_mic_black);
                continuousSpeechService.stopSpeechInput();
                setMute(0);
            }
            ButtonClickSound.start();
            Log.d("click", "totalPages: NextBtn: " + totalPages + "  currentPage: " + currentPage);
            try {
                if (mp != null && mp.isPlaying()) {
                    mp.stop();
                    mp.reset();
                    mp.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!flgPerMarked) {
                flgPerMarked = true;
                correctAnswerCount = setBooleanGetCounter();
                float perc = getPercentage();
                presenter.addScore(0, "perc - " + perc, correctAnswerCount, correctArr.length, startTime, "" + contentType);
            }

            currentPage++;
            pageNo++;
            flgPerMarked = true;
            playFlg = false;
            pauseFlg = true;
            presenter.getPage(currentPage);
            Log.d("click", "NextBtn - totalPages: " + totalPages + "  currentPage: " + currentPage);
        } else {
            GameConstatnts.playGameNext(getActivity(), true, this);
        }
    }

    @SuppressLint("SetTextI18n")
    public void showAcknowledgeDialog(boolean diaComplete) {
        final CustomLodingDialog dialog = new CustomLodingDialog(context);
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
            dia_title.setText("Exit\n'" + storyName + "'");
        else {
            dia_title.setText("Good Job\nRead another one???");
        }
        dia_btn_red.setOnClickListener(v -> dialog.dismiss());
        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
        dia_btn_green.setOnClickListener(v -> {
            dialog.dismiss();
            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                float correctCnt = getPercentage();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("cCode", certiCode);
                returnIntent.putExtra("sMarks", correctCnt);
                returnIntent.putExtra("tMarks", correctArr.length);
//                setResult(Activity.RESULT_OK, returnIntent);
            }
            exitDBEntry();
            //TODO Back Press Here
//            ReadingStoryActivity.super.onBackPressed();
        });
    }

    public void exitDBEntry() {
        try {
            if (mp != null && mp.isPlaying()) {
                mp.stop();
                mp.reset();
                mp.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (colorChangeHandler != null)
                colorChangeHandler.removeCallbacksAndMessages(null);
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
            if (soundStopHandler != null)
                soundStopHandler.removeCallbacksAndMessages(null);
            if (handler != null)
                handler.removeCallbacksAndMessages(null);
            if (mp != null && mp.isPlaying()) mp.stop();
            if (startReadingHandler != null)
                startReadingHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!flgPerMarked) {
                flgPerMarked = true;
                float correctCnt = getPercentage();
                correctAnswerCount = setBooleanGetCounter();
                presenter.addScore(0, "perc - " + correctCnt, correctAnswerCount, correctArr.length, startTime, "" + contentType);
            }
            presenter.addScore(0, "", 0, 0, startTime, contentType + " End");
        } catch (Exception e) {
            e.printStackTrace();
        }
        presenter.addProgress();
    }

/*
    @Override
    public void onBackPressed() {

        if (voiceStart) {
            btn_Mic.performClick();
            voiceStart = false;
            if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test))
                ll_btn_play.setVisibility(View.VISIBLE);
            setMute(0);
        }
        try {
            if (playFlg)
                btn_Play.performClick();
            if (audioHandler != null)
                audioHandler.removeCallbacksAndMessages(null);
            if (handler != null)
                handler.removeCallbacksAndMessages(null);
            if (colorChangeHandler != null)
                colorChangeHandler.removeCallbacksAndMessages(null);
            if (quesReadHandler != null) {
                quesReadHandler.removeCallbacksAndMessages(null);
            }
            if (soundStopHandler != null)
                soundStopHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!fragFlg) {
            showAcknowledgeDialog(false);
        } else {
            fragFlg = false;
//            getSupportFragmentManager().popBackStack();
        }
    }
*/

    int correctCnt = 0, total = 0;

    @SuppressLint("SetTextI18n")
    private void showStars(boolean diaComplete) {
        final CustomLodingDialog dialog = new CustomLodingDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_test_star_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        RatingBar dia_ratingBar = dialog.findViewById(R.id.dia_ratingBar);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);
        correctCnt = 0;
        total = 0;

        try {
            if (testCorrectArr != null) {
                total = testCorrectArr.length;
                for (int i = 0; i < testCorrectArr.length; i++)
                    if (testCorrectArr[i])
                        correctCnt += 1;
            }
        } catch (Exception e) {
            correctCnt = 0;
            total = 0;
        }

        float perc = 0f;
        if (total > 0)
            perc = ((float) correctCnt / (float) total) * 100;

        float rating = getStarRating(perc);
        dia_ratingBar.setRating(rating);

        dia_btn_red.setVisibility(View.GONE);
        dia_btn_green.setText("SUBMIT");
        dia_btn_yellow.setText("" + dialog_btn_cancel);
        if (diaComplete)
            dia_btn_yellow.setVisibility(View.GONE);

        dialog.show();

        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
        dia_btn_green.setOnClickListener(v -> {
            dialog.dismiss();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("cCode", certiCode);
            returnIntent.putExtra("sMarks", correctCnt);
            returnIntent.putExtra("tMarks", total);
//            setResult(Activity.RESULT_OK, returnIntent);
//            finish();
        });
    }

    public float getStarRating(float perc) {
        float ratings = 0;
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
    public void onPause() {
        super.onPause();
        if (voiceStart) {
            btn_Mic.performClick();
            voiceStart = false;
            if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test) && !playHideFlg)
                btn_Play.setVisibility(View.VISIBLE);
            setMute(0);
        }
        try {
            if (playFlg)
                btn_Play.performClick();
            if (audioHandler != null)
                audioHandler.removeCallbacksAndMessages(null);
            if (handler != null)
                handler.removeCallbacksAndMessages(null);
            if (colorChangeHandler != null)
                colorChangeHandler.removeCallbacksAndMessages(null);
            if (quesReadHandler != null) {
                quesReadHandler.removeCallbacksAndMessages(null);
/*                try {
                    if (mPlayer.isPlaying() && mPlayer != null) {
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayer.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
            if (soundStopHandler != null)
                soundStopHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Background
    @Override
    public void Stt_onResult(ArrayList<String> sttResult) {

        flgPerMarked = false;
        presenter.sttResultProcess(sttResult, splitWordsPunct, wordsResIdList);

/*        if (!voiceStart) {
            resetSpeechRecognizer();
            btn_Play.setVisibility(View.VISIBLE);
            btn_Mic.setImageResource(R.drawable.ic_mic_black);
            setMute(0);
        } else
            speech.startListening(recognizerIntent);*/
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
        if (voiceStart) {
            continuousSpeechService.resetHandler(true);
            silence_outer_layout.setVisibility(View.VISIBLE);
            silenceViewHandler = new Handler();
            silence_iv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_continuous_shake));
            AnimateTextView(context, silence_main_layout);
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

    @Override
    public void gameClose() {
        if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
            float correctCnt = getPercentage();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("cCode", certiCode);
            returnIntent.putExtra("sMarks", correctCnt);
            returnIntent.putExtra("tMarks", correctArr.length);
//                setResult(Activity.RESULT_OK, returnIntent);
        }
        exitDBEntry();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(FC_Constants.INFO_CLICKED))
                showInstructions();
        }
    }

//    @Click(R.id.floating_img)
//    public void showPageImageDialog() {
//        btn_Stop.performClick();
//        readingImgPath = readingContentPath + storyBg;
//        File f = new File(readingImgPath);
//        if (f.exists()) {
//            Intent intent = new Intent(getActivity(), Activity_DisplayImage_.class);
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
//                    floating_info, "transition_dialog");
//            startActivityForResult(intent, 11, options.toBundle());
//        }
//    }

    //Insert Instuctions
    private void showInstructions() {
//        btn_Stop.performClick();
//        readingImgPath = readingContentPath + storyBg;
//        Intent intent = new Intent(getActivity(), Activity_DisplayImage_.class);
//        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
//                floating_info, "transition_dialog");
//        startActivityForResult(intent, 11, options.toBundle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (resultCode == Activity.RESULT_OK) {
                new Handler().postDelayed(() -> {
                    if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test))
                        btn_Play.performClick();
                    else {
                        btn_Mic.performClick();
                        btn_Play.setVisibility(View.GONE);
                        bottom_bar2.setVisibility(View.VISIBLE);
                        btn_submit.setVisibility(View.VISIBLE);
                    }
                }, 200);
            }
        }
    }
}