package com.pratham.foundation.customView.display_image_dialog;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.transition.ArcMotion;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.customView.MorphingView.MorphDialogToFab;
import com.pratham.foundation.customView.MorphingView.MorphFabToDialog;
import com.pratham.foundation.customView.MorphingView.MorphTransition;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileInputStream;

import static com.pratham.foundation.ui.contentPlayer.new_reading_fragment.ContentReadingFragment.readingImgPath;

@EActivity(R.layout.fc_show_image_dialog)
public class Activity_DisplayImage extends BaseActivity {

    @ViewById(R.id.iv_dia_img)
    ImageView iv_dia_preview;
    @ViewById(R.id.dia_btn_cross)
    ImageButton dia_btn_cross;
    @ViewById(R.id.gif_dia_view)
    GifView gif_view;

    @AfterViews
    public void initialize() {
        setupEnterTransitions();
        showImage();
    }

    private void showImage() {
        try {
            File f = new File(readingImgPath);
            if (f.exists()) {
                if (readingImgPath.contains(".gif")) {
                    iv_dia_preview.setVisibility(View.GONE);
                    gif_view.setVisibility(View.VISIBLE);
                    gif_view.setGifResource(new FileInputStream(readingImgPath));
                    gif_view.play();
                } else {
                    gif_view.setVisibility(View.GONE);
                    iv_dia_preview.setVisibility(View.VISIBLE);
                    Bitmap bmImg = BitmapFactory.decodeFile(readingImgPath);
                    BitmapFactory.decodeStream(new FileInputStream(readingImgPath));
                    iv_dia_preview.setImageBitmap(bmImg);
                }
            } else {
                gif_view.setVisibility(View.GONE);
                iv_dia_preview.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void  setupEnterTransitions() {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(90f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.cycle);

        MorphFabToDialog sharedEnter = new MorphFabToDialog();
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphDialogToFab sharedReturn = new MorphDialogToFab();
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    private void setupExitTransitions() {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);
        MorphTransition sharedEnter = new MorphTransition(ContextCompat.getColor(this, R.color.mustord_yellow),
                ContextCompat.getColor(this, R.color.dialog_background_color), 100, getResources().getDimensionPixelSize(R.dimen._5sdp), true);
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphTransition sharedReturn = new MorphTransition(ContextCompat.getColor(this, R.color.dialog_background_color),
                ContextCompat.getColor(this, R.color.mustord_yellow), getResources().getDimensionPixelSize(R.dimen._5sdp), 100, false);
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @Click(R.id.dia_btn_cross)
    public void setLang_okay() {
        setResult(Activity.RESULT_OK);
        finishAfterTransition();
    }
}
