package com.pratham.foundation.ui.contentPlayer.opposites;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.interfaces.MediaCallbacks;
import com.pratham.foundation.modalclasses.ModalReadingVocabulary;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.MediaPlayerUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import static com.pratham.foundation.ApplicationClass.BackBtnSound;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;



@EActivity(R.layout.activity_opposites)
public class OppositesActivity extends BaseActivity
        implements OppositesContract.OppositesView, MediaCallbacks {

    @ViewById(R.id.cat_title)
    TextView tvContentTitle;
    @ViewById(R.id.texttop)
    TextView texttop;
    @ViewById(R.id.textbottom)
    TextView textbottom;
    @ViewById(R.id.imagetop)
    ImageView imagettop;
    @ViewById(R.id.imagebottom)
    ImageView imagetbottom;
    @ViewById(R.id.btn_next)
    ImageView btn_next;
    @ViewById(R.id.btn_prev)
    ImageView btn_prev;
    @ViewById(R.id.bottomContainer)
    CardView bottomContainer;
    @ViewById(R.id.topContainer)
    CardView topContainer;

    CustomLodingDialog nextDialog;
    Context mContext;
    boolean isAudioPlaying = false, onSdCard;
    String readingContentPath, contentPath, contentTitle, StudentID, resId;
    MediaPlayerUtil mediaPlayerUtil;
    Handler mhandler;
    
    @Bean(OppositesPresenter.class)
    OppositesContract.OppositesPresenter presenter;

    boolean flag, isNext, isOptionDialogShown;
    Animation animation;


    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        mContext = OppositesActivity.this;
        mhandler = new Handler();
        Intent intent = getIntent();
        contentPath = intent.getStringExtra("contentPath");
        StudentID = intent.getStringExtra("StudentID");
        resId = intent.getStringExtra("resId");
        contentTitle = intent.getStringExtra("contentName");
        onSdCard = getIntent().getBooleanExtra("onSdCard", false);

        presenter.setView(OppositesActivity.this, contentTitle, resId);

        mediaPlayerUtil = new MediaPlayerUtil(this);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";

        tvContentTitle.setText("" + contentTitle);

        try {
            presenter.fetchJsonData(readingContentPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            bottomContainer.setClickable(false);
            topContainer.setClickable(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayerUtil.initCallback(this);
    }

    @UiThread
    @Override
    public void loadUi(ModalReadingVocabulary modalReadingVocabulary) {
        try {
            texttop.setText(modalReadingVocabulary.getSentence().getText());
            Glide.with(this).load(readingContentPath + "Images/" + modalReadingVocabulary.getSentence().getImage()).into(imagettop);
            topContainer.setTag(modalReadingVocabulary.getSentence().getSound());

            textbottom.setText(modalReadingVocabulary.getOpposite().getText());
            Glide.with(this).load(readingContentPath + "Images/" + modalReadingVocabulary.getOpposite().getImage()).into(imagetbottom);
            bottomContainer.setTag((modalReadingVocabulary.getOpposite().getSound()));

            mhandler.postDelayed(() -> {
                flag = true;
                topContainer.startAnimation(animation);
                bottomContainer.clearAnimation();
                mediaPlayerUtil.playMedia(readingContentPath + "/Sounds/" + topContainer.getTag().toString());
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void hidePrevious() {
        btn_prev.setVisibility(View.INVISIBLE);
    }

    @UiThread
    @Override
    public void showPrevious() {
        btn_prev.setVisibility(View.VISIBLE);
    }

    @Click(R.id.btn_prev)
    public void onPrevClicked() {
        if (mhandler != null) {
            mhandler.removeCallbacksAndMessages(null);
        }
        topContainer.clearAnimation();
        bottomContainer.clearAnimation();
        mediaPlayerUtil.stopMedia();
        presenter.showPrevious();
    }

    @Click(R.id.btn_next)
    public void onNextClicked() {
        if (mhandler != null) {
            mhandler.removeCallbacksAndMessages(null);
        }
        topContainer.clearAnimation();
        bottomContainer.clearAnimation();
        mediaPlayerUtil.stopMedia();
        presenter.showNext();
    }

    @UiThread
    @Override
    public void showWordNextDialog() {
        if(!isOptionDialogShown) {
            isOptionDialogShown = true;
            if (mhandler != null) {
                mhandler.removeCallbacksAndMessages(null);
            }
            mediaPlayerUtil.stopMedia();
            nextDialog = new CustomLodingDialog(this);
            nextDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            nextDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            nextDialog.setContentView(R.layout.fc_custom_dialog);
            nextDialog.setCancelable(false);
            nextDialog.setCanceledOnTouchOutside(false);
            nextDialog.show();

            TextView dia_title = nextDialog.findViewById(R.id.dia_title);
            Button dia_btn_yellow = nextDialog.findViewById(R.id.dia_btn_yellow);
            Button dia_btn_green = nextDialog.findViewById(R.id.dia_btn_green);
            Button dia_btn_red = nextDialog.findViewById(R.id.dia_btn_red);

            dia_btn_green.setText("Next");
            dia_btn_red.setText("" + FC_Constants.dialog_btn_cancel);
            dia_btn_yellow.setText("Revise");
            nextDialog.show();

            dia_btn_green.setOnClickListener(v -> {
                isOptionDialogShown = false;
                nextDialog.dismiss();
                presenter.getDataList();
            });

            dia_btn_yellow.setOnClickListener(v -> {
                isOptionDialogShown = false;
                nextDialog.dismiss();
                presenter.revise();
            });

            dia_btn_red.setOnClickListener(v -> {
                isOptionDialogShown = false;
                nextDialog.dismiss();
            });
        }
    }

    @Override
    public void onBackPressed() {
        try {
            mediaPlayerUtil.stopMedia();
            BackBtnSound.start();
            if (isAudioPlaying) {
                isAudioPlaying = false;
            }
            showExitDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void showExitDialog() {
        try {
            if (mhandler != null)
                mhandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final CustomLodingDialog dialog = new CustomLodingDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_title.setText("Exit the game?");
        dia_btn_green.setText("Yes");
        dia_btn_red.setText("No");
        dia_btn_yellow.setText("" + FC_Constants.dialog_btn_cancel);
        dialog.show();

        dia_btn_green.setOnClickListener(v -> {
            presenter.setCompletionPercentage();
            dialog.dismiss();
            finish();
        });

        dia_btn_red.setOnClickListener(v -> dialog.dismiss());
        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isAudioPlaying) {
            isAudioPlaying = false;
        }
    }


    @Override
    public void onComplete() {
        if (flag) {
            mhandler.removeCallbacksAndMessages(null);
            mhandler.postDelayed(() -> {
                flag = false;
                isNext = true;
                bottomContainer.setClickable(false);
                topContainer.setClickable(false);
                bottomContainer.startAnimation(animation);
                topContainer.clearAnimation();
                mediaPlayerUtil.playMedia(readingContentPath + "/Sounds/" + bottomContainer.getTag().toString());
            }, 1000);
        } else {
            bottomContainer.setClickable(true);
            topContainer.setClickable(true);
            if (isNext && !isOptionDialogShown) {
                mhandler.removeCallbacksAndMessages(null);
                mhandler.postDelayed(() -> {
                    isNext = false;
                    bottomContainer.setClickable(false);
                    topContainer.setClickable(false);
                    presenter.showNext();
                }, 4000);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            mediaPlayerUtil.stopMedia();
            mediaPlayerUtil.initCallback(null);
            mhandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.topContainer)
    public void topContainerClick() {
        mediaPlayerUtil.playMedia(readingContentPath + "/Sounds/" + topContainer.getTag().toString());
    }

    @Click(R.id.bottomContainer)
    public void bottomContainerClick() {
        mediaPlayerUtil.playMedia(readingContentPath + "/Sounds/" + bottomContainer.getTag().toString());
    }
}
