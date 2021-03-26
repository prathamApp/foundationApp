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
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import java.util.ArrayList;

import static com.pratham.foundation.BaseActivity.setMute;
import static com.pratham.foundation.utility.FC_Constants.ASSAMESE;
import static com.pratham.foundation.utility.FC_Constants.ASSAMESE_LOCAL;
import static com.pratham.foundation.utility.FC_Constants.BENGALI;
import static com.pratham.foundation.utility.FC_Constants.BENGALI_LOCAL;
import static com.pratham.foundation.utility.FC_Constants.ENGLISH;
import static com.pratham.foundation.utility.FC_Constants.ENGLISH_LOCAL;
import static com.pratham.foundation.utility.FC_Constants.GUJARATI;
import static com.pratham.foundation.utility.FC_Constants.GUJARATI_LOCAL;
import static com.pratham.foundation.utility.FC_Constants.HINDI;
import static com.pratham.foundation.utility.FC_Constants.HINDI_LOCAL;
import static com.pratham.foundation.utility.FC_Constants.KANNADA;
import static com.pratham.foundation.utility.FC_Constants.KANNADA_LOCAL;
import static com.pratham.foundation.utility.FC_Constants.MALAYALAM;
import static com.pratham.foundation.utility.FC_Constants.MALAYALAM_LOCAL;
import static com.pratham.foundation.utility.FC_Constants.MARATHI;
import static com.pratham.foundation.utility.FC_Constants.MARATHI_LOCAL;
import static com.pratham.foundation.utility.FC_Constants.ODIYA;
import static com.pratham.foundation.utility.FC_Constants.ODIYA_LOCAL;
import static com.pratham.foundation.utility.FC_Constants.PUNJABI;
import static com.pratham.foundation.utility.FC_Constants.PUNJABI_LOCAL;
import static com.pratham.foundation.utility.FC_Constants.TAMIL;
import static com.pratham.foundation.utility.FC_Constants.TAMIL_LOCAL;
import static com.pratham.foundation.utility.FC_Constants.TELUGU;
import static com.pratham.foundation.utility.FC_Constants.TELUGU_LOCAL;


/**
 * Created by Ameya on 12-03-2019.
 */

public class ContinuousSpeechService_New implements RecognitionListener, STT_Result_New.sttService {
    public static Intent recognizerIntent;
    public static SpeechRecognizer speech = null;
    public Context context;
    private boolean stopClicked = false, voiceStart = false, silenceDetectionFlg = false, resetFlg = false;
    STT_Result_New.sttView stt_result;
    Handler silenceHandler;
    String LOG_TAG = "ContinuousSpeechService : ", sttResult, language, myLocal = "en-IN";

    public ContinuousSpeechService_New(Context context, STT_Result_New.sttView stt_result, String language) {
        this.context = context;
        this.stt_result = stt_result;
        resetFlg = false;
        this.language = language;
        myLocal = getLangLocal();
        resetSpeechRecognizer();
    }

    public String getLangLocal(){
        String setLocal = HINDI_LOCAL;
        if (FastSave.getInstance().getString(FC_Constants.CURRENT_FOLDER_NAME, "").equalsIgnoreCase(ENGLISH))
            setLocal = ENGLISH_LOCAL;
        else {
            String appLang = FastSave.getInstance().getString(FC_Constants.CURRENT_SUBJECT, "");
            switch (appLang) {
                case HINDI:
                    setLocal = HINDI_LOCAL;
                    break;
                case MARATHI:
                    setLocal = MARATHI_LOCAL;
                    break;
                case ENGLISH:
                    setLocal = ENGLISH_LOCAL;
                    break;
                case KANNADA:
                    setLocal = KANNADA_LOCAL;
                    break;
                case TELUGU:
                    setLocal = TELUGU_LOCAL;
                    break;
                case BENGALI:
                    setLocal = BENGALI_LOCAL;
                    break;
                case GUJARATI:
                    setLocal = GUJARATI_LOCAL;
                    break;
                case PUNJABI:
                    setLocal = PUNJABI_LOCAL;
                    break;
                case TAMIL:
                    setLocal = TAMIL_LOCAL;
                    break;
                case ODIYA:
                    setLocal = ODIYA_LOCAL;
                    break;
                case MALAYALAM:
                    setLocal = MALAYALAM_LOCAL;
                    break;
                case ASSAMESE:
                    setLocal = ASSAMESE_LOCAL;
                    break;
                default:
                    setLocal = HINDI_LOCAL;
                    break;

            }
        }
        Log.d(LOG_TAG, "getLangLocal :  "+ setLocal);
        return setLocal;
    }

    public void setRecogniserIntent() {
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, myLocal);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, myLocal);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 20000);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    public void stopSpeechService() {
        setMute(0);
        speech.destroy();
        stt_result.stoppedPressed();
    }

/*
    public void setMyLocal(String language) {
        if (language.equalsIgnoreCase("English"))
            myLocal = "en-IN";
        else
            myLocal = "hi-IN";
        resetSpeechRecognizer();
    }
*/


    public void resetSpeechRecognizer() {
        try {
            if (speech != null) {
                speech.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //initialize speechrecognizer and set the listener
            speech = SpeechRecognizer.createSpeechRecognizer(context);
            Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(context));
            if (SpeechRecognizer.isRecognitionAvailable(context))
                speech.setRecognitionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopSpeechInput() {
        Log.d(LOG_TAG, "stopSpeechInput");
        setMute(0);
        voiceStart = false;
        speech.stopListening();
        setMute(0);
        try {
            if (silenceHandler != null) {
                silenceHandler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logDBEntry("STT Stopped");
        setMute(0);
        stt_result.stoppedPressed();
        setMute(0);
    }

    public void startSpeechInput() {
        try {
            voiceStart = true;
            setRecogniserIntent();
            speech.startListening(recognizerIntent);
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
                    modal_log.setSessionId(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                    modal_log.setLogDetail("Stt Intent Fired - " + sttString);
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