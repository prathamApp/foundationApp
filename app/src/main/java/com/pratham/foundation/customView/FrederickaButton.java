package com.pratham.foundation.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

@SuppressLint("AppCompatCustomView")
public class FrederickaButton extends android.support.v7.widget.AppCompatButton {

    public FrederickaButton(Context context) {
            super(context);
            setFont();
        }

    public FrederickaButton(Context context, AttributeSet attrs) {
            super(context, attrs);
//        CustomFontHelper.setCustomFont(this, context, attrs);
            setFont();
        }

    public FrederickaButton(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
//        CustomFontHelper.setCustomFont(this, context, attrs);
            setFont();
        }

        private void setFont() {
            Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/FrederickaRegular.ttf");
            setTypeface(font, Typeface.NORMAL);
        }

}
