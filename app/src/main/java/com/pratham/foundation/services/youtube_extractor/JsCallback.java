package com.pratham.foundation.services.youtube_extractor;

public interface JsCallback {
    void onResult(String value);

    void onError(String errorMessage);
}