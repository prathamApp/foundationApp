package com.pratham.foundation.services.stt;


import static com.pratham.foundation.ApplicationClass.BUILD_DATE;
import static com.pratham.foundation.BaseActivity.setMute;

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
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import java.util.ArrayList;


/**
 * Created by Ameya on 12-03-2019.
 */

public class ContinuousSpeechService_Lang implements RecognitionListener, STT_Result_Lang.sttService {
    public static Intent recognizerIntent_Lang;
    public static SpeechRecognizer speech_Lang = null;
    public Context context;
    boolean voiceStart = false, silenceDetectionFlg = false, resetFlg = false;
    STT_Result_Lang.sttView stt_result;
    Handler silenceHandler;
    String LOG_TAG = "ContinuousSpeechService : ", sttResult, language, myLocal = "en-IN";

    public ContinuousSpeechService_Lang(Context context, STT_Result_Lang.sttView stt_result, String language) {
        this.context = context;
        this.stt_result = stt_result;
        resetFlg = false;
        this.language = language;
//        if (FastSave.getInstance().getString(FC_Constants.CURRENT_FOLDER_NAME, "").equalsIgnoreCase("English"))
        if (language.equalsIgnoreCase("English"))
            myLocal = "en-IN";
        else
            myLocal = "hi-IN";
        resetSpeechRecognizer();
    }

    public void setRecogniserIntent() {
        recognizerIntent_Lang = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent_Lang.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, myLocal);
        recognizerIntent_Lang.putExtra(RecognizerIntent.EXTRA_LANGUAGE, myLocal);
        recognizerIntent_Lang.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000);
        recognizerIntent_Lang.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 20000);
        recognizerIntent_Lang.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent_Lang.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent_Lang.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
    }

    public void stopSpeechService() {
        speech_Lang.destroy();
    }

    public void resetSpeechRecognizer() {
        try {
            if (speech_Lang != null) {
                speech_Lang.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            speech_Lang = SpeechRecognizer.createSpeechRecognizer(context);
            Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(context));
            if (SpeechRecognizer.isRecognitionAvailable(context))
                speech_Lang.setRecognitionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopSpeechInput() {
        Log.d(LOG_TAG, "stopSpeechInput");
        voiceStart = false;
        setMute(0);
        try {
            if (silenceHandler != null)
                silenceHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
        }
        logDBEntry("Stopped");
        speech_Lang.stopListening();
        stt_result.stoppedPressed();
    }

    public void startSpeechInput() {
        try {
            voiceStart = true;
            setRecogniserIntent();
            speech_Lang.startListening(recognizerIntent_Lang);
            logDBEntry("Started");
            Log.d(LOG_TAG, "onReadyForSpeech");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void logDBEntry(String sttString) {
        new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Modal_Log modal_log = new Modal_Log();
                    modal_log.setCurrentDateTime(FC_Utility.getCurrentDateTime());
                    modal_log.setExceptionMessage("");
                    modal_log.setExceptionStackTrace("APK BUILD DATE : " + BUILD_DATE);
                    modal_log.setMethodName("");
                    modal_log.setErrorType("");
                    modal_log.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "no_group"));
                    modal_log.setDeviceId("" + FC_Utility.getDeviceID());
                    modal_log.setLogDetail("Stt Intent Fired - " + sttString);
                    modal_log.setSessionId(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                    modal_log.setSentFlag(0);
                    AppDatabase.getDatabaseInstance(context).getLogsDao().insertLog(modal_log);
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
        stt_result.sttEngineReady();
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
        speech_Lang.stopListening();
    }

    @Override
    public void onError(int error) {
        Log.d(LOG_TAG, "onError");
        if (voiceStart) {
            resetSpeechRecognizer();
            speech_Lang.startListening(recognizerIntent_Lang);
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
            Log.d("STT-Res", "STT-Res: " + matches.get(i) + "\n");

        stt_result.Stt_onResult(matches);

        if (!voiceStart) {
            resetSpeechRecognizer();
        } else
            speech_Lang.startListening(recognizerIntent_Lang);
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
        Log.i(LOG_TAG, "silenceHandler reset : " + resetActivityFlg);
        resetFlg = resetActivityFlg;
        try {
            silenceDetectionFlg = false;
            if (silenceHandler != null)
                silenceHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
        }
    }
}