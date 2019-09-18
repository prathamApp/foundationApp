package com.pratham.foundation.ui.student_profile;

/**
 * Created by yarolegovich on 08.03.2017.
 */

public class Forecast {

    private final String tittle;
    private final int icon;


    public Forecast(String cityName, int cityIcon) {
        this.tittle = cityName;
        this.icon = cityIcon;
    }

    public String getCityName() {
        return tittle;
    }

    public int getCityIcon() {
        return icon;
    }

}
