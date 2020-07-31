package com.pratham.foundation.customView.fontsview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

@SuppressLint("AppCompatCustomView")
public class SansTextView extends TextView {

    public SansTextView(Context context) {
        super(context);
        setFont();
    }

    public SansTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public SansTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        String lnag = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, "Hindi");
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/GlacialIndifference-Regular.otf");
        if (lnag.equalsIgnoreCase("punjabi"))
            font = Typeface.createFromAsset(getContext().getAssets(), "fonts/raavi_punjabi.ttf");
        else if (lnag.equalsIgnoreCase("Assamese"))
            font = Typeface.createFromAsset(getContext().getAssets(), "fonts/geetl_assamese.ttf");
        else if (lnag.equalsIgnoreCase("Gujarati"))
            font = Typeface.createFromAsset(getContext().getAssets(), "fonts/muktavaani_gujarati.ttf");
        else if (lnag.equalsIgnoreCase("Odiya") || lnag.equalsIgnoreCase("Oriya"))
            font = Typeface.createFromAsset(getContext().getAssets(), "fonts/lohit_oriya.ttf");
        if(FastSave.getInstance().getString(FC_Constants.CURRENT_SUBJECT, "English").equalsIgnoreCase("English"))
            font = Typeface.createFromAsset(getContext().getAssets(), "fonts/GlacialIndifference-Regular.otf");

        setTypeface(font);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
