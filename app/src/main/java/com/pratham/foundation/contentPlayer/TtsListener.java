package com.pratham.foundation.contentPlayer;

import android.annotation.TargetApi;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.pratham.foundation.contentPlayer.TextToSpeechCustom;

import java.util.Locale;

import static com.pratham.foundation.contentPlayer.TextToSpeechCustom.textToSpeech;


public class TtsListener implements TextToSpeech.OnInitListener {
    float spRate;
    public TtsListener(float spRate) {
        this.spRate = spRate;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(new Locale("hi", "IN"));
            textToSpeech.setSpeechRate(spRate);
        } else {
            textToSpeech = null;
            Toast.makeText(TextToSpeechCustom.mContext, "Failed to initialize TTS engine.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
