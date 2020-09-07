package com.pratham.foundation.customView.media_controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;

import com.pratham.foundation.R;


public class PausePlayButton extends AppCompatImageButton {

    private Drawable playDrawable;
    private Drawable pauseDrawable;

    public PausePlayButton(Context context) {
        this(context, null);
    }

    public PausePlayButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.imageButtonStyle);
    }

    public PausePlayButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            setImageResource(R.drawable.vid_play);
            return;
        }
        pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.vid_pause);//63316D
        playDrawable = ContextCompat.getDrawable(getContext(), R.drawable.vid_play);//E48E66
        toggleImage(false);
    }

    public Drawable getPlayDrawable() {
        return playDrawable;
    }

    public void setPlayDrawable(Drawable playDrawable) {
        this.playDrawable = playDrawable;
    }

    public Drawable getPauseDrawable() {
        return pauseDrawable;
    }

    public void setPauseDrawable(Drawable pauseDrawable) {
        this.pauseDrawable = pauseDrawable;
    }

    public void toggleImage(boolean isPlaying) {
        if (isPlaying) {
            setImageDrawable(pauseDrawable);
        } else {
            setImageDrawable(playDrawable);
        }
    }
}
