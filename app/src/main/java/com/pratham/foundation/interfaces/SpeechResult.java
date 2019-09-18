package com.pratham.foundation.interfaces;

import java.util.List;

public interface SpeechResult {
    void onResult(List<String> results);
    void onResult(String results);
}
