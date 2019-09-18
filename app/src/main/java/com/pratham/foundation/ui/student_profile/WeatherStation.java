package com.pratham.foundation.ui.student_profile;

import android.content.Context;


import com.pratham.foundation.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yarolegovich on 08.03.2017.
 */

public class WeatherStation {
    private static Context mContext;

    public static WeatherStation get(Context context) {
        mContext = context;
        return new WeatherStation();
    }

    private WeatherStation() {
    }

    public List<Forecast> getForecasts() {
        return Arrays.asList(
                new Forecast(mContext.getString(R.string.Summary), R.drawable.summary),
                new Forecast(mContext.getString(R.string.Certificates), R.drawable.certificates),
                new Forecast(mContext.getString(R.string.Progress), R.drawable.progress_report));
               /* new Forecast("Rome", R.drawable.rome, "18", Weather.PARTLY_CLOUDY),
                new Forecast("London", R.drawable.london, "6", Weather.PERIODIC_CLOUDS),
                new Forecast("Washington", R.drawable.washington, "20", Weather.CLEAR));*/
    }
}
