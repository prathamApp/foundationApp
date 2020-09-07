package com.pratham.foundation.customView.fontsview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

@SuppressLint("AppCompatCustomView")
public class SansButton extends androidx.appcompat.widget.AppCompatButton {

    public SansButton(Context context) {
        super(context);
        setFont();
    }

    public SansButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public SansButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/GlacialIndifference-Bold.otf");
        if (FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, "Hindi").equalsIgnoreCase("punjabi"))
            font = Typeface.createFromAsset(getContext().getAssets(), "fonts/raavi_punjabi.ttf");
        else if (FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, "Hindi").equalsIgnoreCase("Assamese"))
            font = Typeface.createFromAsset(getContext().getAssets(), "fonts/geetl_assamese.ttf");
        else if (FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, "Hindi").equalsIgnoreCase("Gujarati"))
            font = Typeface.createFromAsset(getContext().getAssets(), "fonts/muktavaani_gujarati.ttf");
        else if (FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, "Hindi")
                .equalsIgnoreCase("Odiya") ||
                FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, "Hindi")
                        .equalsIgnoreCase("Oriya"))
            font = Typeface.createFromAsset(getContext().getAssets(), "fonts/lohit_oriya.ttf");
        if(FastSave.getInstance().getString(FC_Constants.CURRENT_SUBJECT, "English").equalsIgnoreCase("English"))
            font = Typeface.createFromAsset(getContext().getAssets(), "fonts/GlacialIndifference-Bold.otf");

        setTypeface(font, Typeface.BOLD);
    }

}
