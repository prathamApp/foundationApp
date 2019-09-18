package com.pratham.foundation.custumView.progress_layout;

public interface ProgressLayoutListener {
    void onProgressCompleted();

    void onProgressChanged(int seconds);
}
