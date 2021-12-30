package com.pratham.foundation.interfaces;

public interface API_Content_Result {

    void receivedContent(String header, String response);

    void receivedContent_PI_SubLevel(String header, String response, int pos, int size);

    void receivedError(String header);

}
