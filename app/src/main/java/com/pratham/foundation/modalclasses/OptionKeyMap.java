package com.pratham.foundation.modalclasses;

public class OptionKeyMap {
    String option;
    boolean isclicked;

    public OptionKeyMap(String option, boolean isclicked) {
        this.option = option;
        this.isclicked = isclicked;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isIsclicked() {
        return isclicked;
    }

    public void setIsclicked(boolean isclicked) {
        this.isclicked = isclicked;
    }
}
