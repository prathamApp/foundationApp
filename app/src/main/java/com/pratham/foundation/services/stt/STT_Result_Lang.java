package com.pratham.foundation.services.stt;

import java.util.ArrayList;


public interface STT_Result_Lang {

    interface sttService {
        void resetHandler(boolean resetActivityFlg);
    }

    interface sttView {
        void Stt_onResult(ArrayList<String> sttResult);
        void silenceDetected();

        void stoppedPressed();

        void sttEngineReady();
    }
}
