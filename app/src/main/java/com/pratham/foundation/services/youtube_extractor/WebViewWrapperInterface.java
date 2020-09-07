package com.pratham.foundation.services.youtube_extractor;
import android.webkit.WebView;

public interface WebViewWrapperInterface {
    void loadJavaScript(String javascript);

    // Destroys the web view in order to free the memory.
    // The web view can not be accessed after is has been destroyed.
    void destroy();

    // Returns the WebView object
    WebView getWebView();
}