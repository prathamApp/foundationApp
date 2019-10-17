package com.pratham.foundation.services.stt;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import java.util.ArrayList;

import static com.pratham.foundation.BaseActivity.setMute;


/**
 * Created by Ameya on 12-03-2019.
 */

public class ContinuousSpeechService_New implements RecognitionListener, STT_Result_New.sttService {
    public static Intent recognizerIntent;
    public static SpeechRecognizer speech = null;
    public Context context;
    boolean voiceStart = false, silenceDetectionFlg = false, resetFlg = false;
    STT_Result_New.sttView stt_result;
    Handler silenceHandler;
    String LOG_TAG = "ContinuousSpeechService : ", sttResult;

    public ContinuousSpeechService_New(Context context, STT_Result_New.sttView stt_result) {
        this.context = context;
        this.stt_result = stt_result;
        resetSpeechRecognizer();
        resetFlg = false;

    }

    public void setRecogniserIntent() {
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 20000);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
    }

    public void resetSpeechRecognizer() {
        if (speech != null) {
            try {
                speech.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        speech = SpeechRecognizer.createSpeechRecognizer(context);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(context));
        if (SpeechRecognizer.isRecognitionAvailable(context))
            speech.setRecognitionListener(this);
    }

    public void stopSpeechInput() {
        Log.d(LOG_TAG, "stopSpeechInput");
        voiceStart = false;
        setMute(0);
        try {
            if (silenceHandler != null)
                silenceHandler.removeCallbacksAndMessages(null);
        }catch (Exception e){}
        logDBEntry("Stopped");
        speech.stopListening();
    }

    public void startSpeechInput() {
        voiceStart = true;
        setRecogniserIntent();
        speech.startListening(recognizerIntent);
        logDBEntry("Started");
        Log.d(LOG_TAG, "onReadyForSpeech");
    }

    @SuppressLint("StaticFieldLeak")
    private void logDBEntry(String sttString) {
        new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Modal_Log modal_log = new Modal_Log();
                    modal_log.setCurrentDateTime(FC_Utility.getCurrentDateTime());
                    modal_log.setSessionId(FC_Constants.currentSession);
                    modal_log.setLogDetail("Stt Intent Fired - "+sttString);
                    AppDatabase.appDatabase.getLogsDao().insertLog(modal_log);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(LOG_TAG, "onReadyForSpeech");
        startCountDown();
    }

    @Override
    public void onBeginningOfSpeech() {
        setMute(1);
        Log.d(LOG_TAG, "onBeginningOfSpeech");
        startCountDown();
    }

    private void startCountDown() {
        if (!silenceDetectionFlg && !resetFlg) {
            Log.i(LOG_TAG, "silenceHandler Initialized");
            silenceDetectionFlg = true;
            silenceHandler = new Handler();
            silenceHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    silenceDetectionFlg = false;
                    Log.i(LOG_TAG, "silenceHandler postDelayed");
                    stt_result.silenceDetected();
                }
            }, 7000);
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d(LOG_TAG, "onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(LOG_TAG, "onEndOfSpeech");
        speech.stopListening();
    }

    @Override
    public void onError(int error) {
        Log.d(LOG_TAG, "onError");
        if (voiceStart) {
            resetSpeechRecognizer();
            speech.startListening(recognizerIntent);
        } else
            setMute(0);
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        try {
            silenceDetectionFlg = false;
            if (silenceHandler != null) {
                silenceHandler.removeCallbacksAndMessages(null);
                Log.i(LOG_TAG, "silenceHandler removed");
            }
        } catch (Exception e) {
        }

        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        sttResult = matches.get(0);
        Log.d("STT-Res", "\n");
        for (int i = 0; i < matches.size(); i++)
            Log.d("STT-Res", "STT-Res: " + matches.get(0) + "\n");

        stt_result.Stt_onResult(matches);

        if (!voiceStart) {
            resetSpeechRecognizer();
        } else
            speech.startListening(recognizerIntent);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(LOG_TAG, "onEvent");
    }

    @Override
    public void resetHandler(boolean resetActivityFlg) {
        Log.i(LOG_TAG, "silenceHandler reset : "+resetActivityFlg);
        resetFlg = resetActivityFlg;
        try {
            silenceDetectionFlg = false;
            if (silenceHandler != null)
                silenceHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
        }
    }
}