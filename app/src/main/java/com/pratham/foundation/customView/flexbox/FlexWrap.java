package com.pratham.foundation.customView.flexbox;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({FlexWrap.NOWRAP, FlexWrap.WRAP, FlexWrap.WRAP_REVERSE})
@Retention(RetentionPolicy.SOURCE)
public @interface FlexWrap {

    /**
     * The flex container is single-line.
     */
    int NOWRAP = 0;

    /**
     * The flex container is multi-line.
     */
    int WRAP = 1;

    /**
     * The flex container is multi-line. The direction of the
     * cross axis is opposed to the direction as the {@link #WRAP}
     */
    int WRAP_REVERSE = 2;
}