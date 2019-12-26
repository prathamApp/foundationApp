package com.pratham.foundation.ui.contentPlayer.old_cos.reading_cards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.modalclasses.ModalReadingCardSubMenu;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.SplashSupportActivity.ButtonClickSound;


@EActivity(R.layout.activity_card_reading)
public class ReadingCardsActivity extends BaseActivity implements
        ReadingCardContract.ReadingCardView {

    @Bean(ReadingCardPresenter.class)
    ReadingCardPresenter presenter;

    @ViewById(R.id.cat_title)
    TextView tvContentTitle;
    @ViewById(R.id.story_ll)
    RelativeLayout story_ll;
    @ViewById(R.id.text_intro)
    TextView text_intro;
    @ViewById(R.id.btn_play)
    ImageButton btn_Play;
    @ViewById(R.id.iv_image)
    ImageView iv_image;
    @ViewById(R.id.btn_next)
    ImageView btn_next;
    @ViewById(R.id.btn_prev)
    ImageView btn_prev;
    @ViewById(R.id.intro_scroll)
    ScrollView intro_scroll;

    List<ModalReadingCardSubMenu> modalReadingCardSubMenuList;
    public MediaPlayer mp;
    Context mContext;
    static int currentPageNo, totalPages = 0;
    Handler endhandler;
    static boolean[] correctArr;
    boolean isAudioPlaying = false, onSdCard, gotoNext = false;
    String readingContentPath, contentPath, contentTitle, StudentID, resId, cardAudio, resStartTime;
//    AnimationDrawable animationDrawable;
    public CustomLodingDialog myLoadingDialog;


    @SuppressLint("SetTextI18n")
    @AfterViews
    public void initialize() {

//        animationDrawable = (AnimationDrawable) story_ll.getBackground();
//        animationDrawable.setEnterFadeDuration(4500);
//        animationDrawable.setExitFadeDuration(4500);
//        animationDrawable.start();

        mContext = ReadingCardsActivity.this;
        presenter.setView(ReadingCardsActivity.this);
        showLoader();
        currentPageNo = 0;

        Intent intent = getIntent();
        contentPath = intent.getStringExtra("contentPath");
        StudentID = intent.getStringExtra("StudentID");
        resId = intent.getStringExtra("resId");
        onSdCard = getIntent().getBooleanExtra("onSdCard", false);
        tvContentTitle.setText("" + contentTitle);

        resStartTime = FC_Utility.getCurrentDateTime();

        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";

        tvContentTitle.setText("" + contentTitle);
        presenter.setResId(resId);
        try {
            endhandler = new Handler();
            modalReadingCardSubMenuList = new ArrayList<>();
            presenter.fetchJsonData(readingContentPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    @Override
    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
        tvContentTitle.setText("" + contentTitle);
    }

    @Override
    public void setCardAudio(String cardAudio) {
        this.cardAudio = cardAudio;
    }

    @Override
    public void setListData(List<ModalReadingCardSubMenu> modalReadingCardSubMenuList) {
        this.modalReadingCardSubMenuList.addAll(modalReadingCardSubMenuList);
        totalPages = modalReadingCardSubMenuList.size();
        correctArr = new boolean[totalPages];
    }

    @Override
    public void initializeContent() {
        currentPageNo = 0;
        addDataToPage(currentPageNo);
    }

    @UiThread
    @Override
    public void showLoader() {
        myLoadingDialog = new CustomLodingDialog(this);
        myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myLoadingDialog.setContentView(R.layout.loading_dialog);
        myLoadingDialog.setCanceledOnTouchOutside(false);
//        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }

    @UiThread
    @Override
    public void dismissLoadingDialog() {
        if (myLoadingDialog != null) {
            myLoadingDialog.dismiss();
        }
    }

    @UiThread
    public void addDataToPage(int pageNo) {
        currentPageNo = pageNo;
        correctArr[pageNo] = true;
        if (modalReadingCardSubMenuList.get(currentPageNo).getType().equalsIgnoreCase("cardImage")) {
            gotoNext = false;
            intro_scroll.setVisibility(View.GONE);
            btn_Play.setVisibility(View.VISIBLE);
            iv_image.setVisibility(View.VISIBLE);
            try {
                cardAudio = modalReadingCardSubMenuList.get(currentPageNo).getCardAudio();
                String img = modalReadingCardSubMenuList.get(currentPageNo).getCardImage();
                File f = new File(readingContentPath + img);
                if (f.exists()) {
                    Bitmap bmImg = BitmapFactory.decodeFile(readingContentPath + img);
                    BitmapFactory.decodeStream(new FileInputStream(readingContentPath + img));
                    iv_image.setImageBitmap(bmImg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            gotoNext = false;
            playStopClicked();
        } else {
            iv_image.setVisibility(View.GONE);
            gotoNext = true;
            btn_next.setVisibility(View.GONE);
            btn_prev.setVisibility(View.GONE);
            intro_scroll.setVisibility(View.VISIBLE);
            text_intro.setText(modalReadingCardSubMenuList.get(currentPageNo).getText());
        }
        dismissLoadingDialog();
    }

    @Click(R.id.btn_next)
    void gotoNextPage() {
        if (currentPageNo < totalPages - 1) {
            btn_prev.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.VISIBLE);
            try {
                if (endhandler != null)
                    endhandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ButtonClickSound.start();
            if (isAudioPlaying) {
                isAudioPlaying = false;
                stopAudio();
                btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
            }
            currentPageNo++;
            btn_prev.setVisibility(View.VISIBLE);
            if (currentPageNo == totalPages - 1) {
                btn_next.setVisibility(View.GONE);
            }
            addDataToPage(currentPageNo);
            Log.d("click", "NextBtn - totalPages: " + totalPages + "  currentPageNo: " + currentPageNo);
        }
    }

    @Click(R.id.btn_prev)
    void gotoPrevPage() {
        if (currentPageNo > 0) {
            try {
                if (endhandler != null)
                    endhandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ButtonClickSound.start();
            if (isAudioPlaying) {
                isAudioPlaying = false;
                stopAudio();
                btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
            }
            currentPageNo--;
            btn_next.setVisibility(View.VISIBLE);
            if (currentPageNo == 0) {
                btn_prev.setVisibility(View.GONE);
            }
            addDataToPage(currentPageNo);
            Log.d("click", "NextBtn - totalPages: " + totalPages + "  currentPageNo: " + currentPageNo);
        }
    }

    @Click(R.id.btn_play)
    void playStopClicked() {
        if (gotoNext) {
            gotoNext = false;
            gotoNextPage();
        }
        if (!isAudioPlaying) {
            isAudioPlaying = true;
            playAudio();
            btn_Play.setImageResource(R.drawable.ic_stop_black);
        } else {
            isAudioPlaying = false;
            stopAudio();
            btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
        }
    }

    private void stopAudio() {
        try {
            if (mp != null) {
                mp.stop();
                mp.reset();
                mp.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.btn_back)
    public void pressedBack(){
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        try {
            if (endhandler != null)
                endhandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ButtonClickSound.start();
        if (isAudioPlaying) {
            isAudioPlaying = false;
            stopAudio();
            btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
        }
        showExitDialog();
    }

    @SuppressLint("SetTextI18n")
    public void showExitDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText ("YES");
        dia_btn_red.setText   ("NO");
        dia_btn_yellow.setText(""+dialog_btn_cancel);

        dia_title.setText("Exit the game?");
        dialog.show();

        dia_btn_green.setOnClickListener(v -> {
            presenter.addCompletion();
            finish();
            dialog.dismiss();
        });
        
        dia_btn_red.setOnClickListener(v -> dialog.dismiss());

        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (endhandler != null)
                endhandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
        }
        if (isAudioPlaying) {
            isAudioPlaying = false;
            stopAudio();
            btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
        }
    }


    Handler endHandler = new Handler();

    private void playAudio() {
        try {
            mp = new MediaPlayer();
            mp.setDataSource(readingContentPath + cardAudio);
            mp.prepare();
            mp.setOnPreparedListener(mp -> {
                mp.start();
                mp.setOnCompletionListener(mp1 -> {
                    btn_Play.setImageResource(R.drawable.ic_play_arrow_black);
                    isAudioPlaying = false;
                    try {
                        if (mp1.isPlaying())
                            mp1.stop();
                        if (currentPageNo == totalPages - 1) {
                            endHandler.postDelayed(this::showExitDialog, (long) (1200));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
            mp.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
