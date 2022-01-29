package com.pratham.foundation.ui.contentPlayer.web_view;

import static com.pratham.foundation.ui.contentPlayer.web_view.WebViewActivity.gameCategory;
import static com.pratham.foundation.ui.contentPlayer.web_view.WebViewActivity.webResId;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.WebViewInterface;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import java.util.ArrayList;


public class JSInterface implements RecognitionListener {

    static Context mContext;
    String sdCardPath, sttJSResult;
    AudioPlayer recordAudio;
    static Boolean audioFlag = false;
    public static MediaPlayer mp;
    TextToSpeechCustom tts;
    public String extractedText, quesWord;
    WebViewActivity activity_instance;
    WebViewInterface webViewInterface;
    WebView webView;
    boolean resArray = false;
    private Intent recognizerIntent;
    private SpeechRecognizer speech = null;


    JSInterface(Context mContext, WebView w, TextToSpeechCustom tts, WebViewActivity activity_instance,
                WebViewInterface webViewInterface) {
        JSInterface.mContext = mContext;
        this.activity_instance = activity_instance;
        sdCardPath = ApplicationClass.contentSDPath;
        mp = new MediaPlayer();
        this.tts = tts;
        this.webViewInterface = webViewInterface;
        this.webView = w;
        resetSpeechRecognizer();
    }

    @JavascriptInterface
    public String getMediaPath(String gameFolder) {
        return ApplicationClass.contentSDPath;
    }

    @JavascriptInterface
    public void startRecording(String recName) {
        try {
            recordAudio = new AudioPlayer(recName);
            recordAudio.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void stopRecording() {
        audioFlag = false;
        try {
            recordAudio.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void StudentCertification(int scoredMarks, int totalMarks, String certiCode) {
        boolean oldFlg = false;
/*        WebViewActivity.sMarks = scoredMarks;
        WebViewActivity.tMarks = totalMarks;
        WebViewActivity.cCode = certiCode;*/
    }

    @JavascriptInterface
    public void audioPlayerForStory(String filename, String storyName) {
        try {
            mp.stop();
            mp.reset();
            if (TextToSpeechCustom.textToSpeech.isSpeaking()) {
                tts.stopSpeakerDuringJS();
            }
            String path = "";
            audioFlag = true;

            try {
                path = ApplicationClass.getStoragePath().toString() + "/.KKSInternal/Recordings/" + filename;
                mp.setDataSource(path);

                if (mp.isPlaying())
                    mp.stop();

                mp.prepare();
                mp.start();

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mp.stop();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void stopAudioPlayer() {
        try {
            if (mp != null) {
                mp.stop();
                mp.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void GotoNextGame() {
        webViewInterface.onNextGame(webView);
    }

    @SuppressLint("StaticFieldLeak")
    @JavascriptInterface
    public void addScore(String resId, final int questionId, final int scorefromGame,
                         final int totalMarks, final int level, final String startTime) {
        boolean _wasSuccessful = false;

        new AsyncTask<Object, Void, Object>() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {

                    String[] splited;
                    String[] splitedDate;
                    String[] splitedTime;
                    String customDate;
                    String customTime;

                    String deviceId = AppDatabase.getDatabaseInstance(mContext).getStatusDao().getValue("DeviceId");


                    Score score = new Score();
                    score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                    score.setResourceID(webResId);
                    score.setQuestionId(questionId);
                    score.setScoredMarks(scorefromGame);
                    score.setTotalMarks(totalMarks);
                    score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));

                    splited = startTime.split("\\s+");
                    splitedDate = splited[0].split("\\-+");
                    splitedTime = splited[1].split("\\:+");
                    customDate = formatCustomDate(splitedDate, "-");
                    customTime = formatCustomDate(splitedTime, ":");
                    score.setStartDateTime(customDate + " " + customTime);

                    score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
                    score.setEndDateTime(FC_Utility.getCurrentDateTime());
                    score.setLevel(level);
                    score.setLabel("");
                    score.setSentFlag(0);
                    AppDatabase.getDatabaseInstance(mContext).getScoreDao().insert(score);

                    if (WebViewActivity.mode.equalsIgnoreCase("test")) {

                        Assessment assessment = new Assessment();
                        assessment.setResourceIDa(/*gameWebViewList.get(WebViewActivity.gameCounter).getResourceId()*/webResId);
                        assessment.setSessionIDa(FastSave.getInstance().getString(FC_Constants.ASSESSMENT_SESSION, ""));
                        assessment.setSessionIDm(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                        assessment.setQuestionIda(questionId);
                        assessment.setScoredMarksa(scorefromGame);
                        assessment.setTotalMarksa(totalMarks);
                        assessment.setStudentIDa(FastSave.getInstance().getString(FC_Constants.CURRENT_ASSESSMENT_STUDENT_ID, ""));
                        assessment.setStartDateTimea(customDate + " " + customTime);
                        assessment.setDeviceIDa(deviceId.equals(null) ? "0000" : deviceId);
                        assessment.setEndDateTime(FC_Utility.getCurrentDateTime());
                        assessment.setLevela(level);
                        assessment.setLabel("");
                        assessment.setSentFlag(0);
                        AppDatabase.getDatabaseInstance(mContext).getAssessmentDao().insert(assessment);
                    }

                    BackupDatabase.backup(mContext);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    @JavascriptInterface
    public void addLearntData(String word, int totalNos, String type) {
        WebViewActivity.dataTotalLength = totalNos;
        new AsyncTask<Object, Void, Object>() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    KeyWords learntWords = new KeyWords();
                    learntWords.setResourceId(webResId);
                    learntWords.setSentFlag(0);
                    learntWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                   // learntWords.setSessionId(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                    learntWords.setKeyWord(word.toLowerCase());
                    //learntWords.setSynId("" + WebViewActivity.gameName);
                    learntWords.setWordType("" + type);
                    WebViewActivity.learntWordsList.add(learntWords);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    @JavascriptInterface
    public String getCategory() {
        return gameCategory;
    }

    @SuppressLint("StaticFieldLeak")
    @JavascriptInterface
    public boolean isPractice() {
//        return FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Practice);
        return false;
    }

    @JavascriptInterface
    public String getLevel() {
        return WebViewActivity.gameLevel;
    }

    @SuppressLint("StaticFieldLeak")
    @JavascriptInterface
    public void addScore(String resId, final int questionId, final int scorefromGame, final int totalMarks, final int level, final String startTime, final String Label) {
        boolean _wasSuccessful = false;
        new AsyncTask<Object, Void, Object>() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    String[] splited;
                    String[] splitedDate;
                    String[] splitedTime;
                    String customDate;
                    String customTime;

                    String deviceId = AppDatabase.getDatabaseInstance(mContext).getStatusDao().getValue("DeviceId");

                    Score score = new Score();
                    score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                    score.setResourceID(webResId);
                    score.setQuestionId(questionId);
                    score.setScoredMarks(scorefromGame);
                    score.setTotalMarks(totalMarks);
                    score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));

                    splited = startTime.split("\\s+");
                    splitedDate = splited[0].split("\\-+");
                    splitedTime = splited[1].split("\\:+");
                    customDate = formatCustomDate(splitedDate, "-");
                    customTime = formatCustomDate(splitedTime, ":");
                    score.setStartDateTime(customDate + " " + customTime);

                    score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
                    score.setEndDateTime(FC_Utility.getCurrentDateTime());
                    score.setLevel(level);
                    score.setLabel(Label);
                    score.setSentFlag(0);

                    AppDatabase.getDatabaseInstance(mContext).getScoreDao().insert(score);

                    if (WebViewActivity.mode.equalsIgnoreCase("test")) {

                        Assessment assessment = new Assessment();
                        assessment.setResourceIDa(/*gameWebViewList.get(WebViewActivity.gameCounter).getResourceId()*/webResId);
                        assessment.setSessionIDa(FastSave.getInstance().getString(FC_Constants.ASSESSMENT_SESSION, ""));
                        assessment.setSessionIDm(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                        assessment.setQuestionIda(questionId);
                        assessment.setScoredMarksa(scorefromGame);
                        assessment.setTotalMarksa(totalMarks);
                        assessment.setStudentIDa(FastSave.getInstance().getString(FC_Constants.CURRENT_ASSESSMENT_STUDENT_ID, ""));
                        assessment.setStartDateTimea(customDate + " " + customTime);
                        assessment.setDeviceIDa(deviceId.equals(null) ? "0000" : deviceId);
                        assessment.setEndDateTime(FC_Utility.getCurrentDateTime());
                        assessment.setLevela(level);
                        assessment.setLabel("test: " + Label);
                        assessment.setSentFlag(0);
                        AppDatabase.getDatabaseInstance(mContext).getAssessmentDao().insert(assessment);
                    }
                    BackupDatabase.backup(mContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }


    public String formatCustomDate(String[] splitedDate, String delimiter) {
        for (int k = 0; k < splitedDate.length; k++) {
            if (Integer.parseInt(splitedDate[k]) < 10) {
                splitedDate[k] = "0" + splitedDate[k];
            }
        }
        return TextUtils.join(delimiter, splitedDate);
    }


    @JavascriptInterface
    public void playTts(String theWordWasAndYouSaid, String ttsLanguage) {
        mp.stop();
        mp.reset();
        if (TextToSpeechCustom.textToSpeech.isSpeaking()) {
            tts.stopSpeakerDuringJS();
        }
        if (ttsLanguage == null) {
            tts.ttsFunction(theWordWasAndYouSaid, "eng", 0.6f);
        }
        if (ttsLanguage.equals("eng") || ttsLanguage.equals("hin")) {
            tts.ttsFunction(theWordWasAndYouSaid, ttsLanguage, 0.6f);
        }
    }

    @JavascriptInterface
    public void stopTts() {
        tts.stopSpeakerDuringJS();
    }


    /**
     * To get duration of media file
     */
    public long getMediaDuration(String mediaPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mediaPath);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong(time);
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        return timeInmillisec;
    }


    @JavascriptInterface
    public String getSttResult() {
        return sttJSResult;
    }

    @JavascriptInterface
    public void startStt(final String selectedLanguage) {
        activity_instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startSttIntent("en-IN");
                speech.startListening(recognizerIntent);
            }
        });
    }

    @JavascriptInterface
    public void startSttArray(final String selectedLanguage) {
        activity_instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resArray = true;
                startSttIntent("en-IN");
                speech.startListening(recognizerIntent);
            }
        });
    }

    @JavascriptInterface
    public void startStt(final String selectedLanguage, String quesWword) {
        quesWord = quesWword;
        activity_instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selectedLanguage.equals("eng"))
                    startSttIntent("en-IN");
                else
                    startSttIntent("hi-IN");

                speech.startListening(recognizerIntent);

            }
        });
    }

    @JavascriptInterface
    public void stopStt() {
        activity_instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    speech.stopListening();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void resetSpeechRecognizer() {
        if (speech != null)
            speech.destroy();
        speech = SpeechRecognizer.createSpeechRecognizer(mContext);
        Log.i("Speech", "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(mContext));
        if (SpeechRecognizer.isRecognitionAvailable(mContext))
            speech.setRecognitionListener(this);
        else
            activity_instance.finish();
    }

    public void startSttIntent(String selectedLanguage) {
        try {
            sttJSResult = "$$$";
            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                    "en");
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 20000);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        sttJSResult = "###";
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        extractedText = matches.get(0);

        boolean[] correctArr;
        if (resArray) {
            String tempRes = "";
            for (int i = 0; i < matches.size(); i++) {
                tempRes = tempRes + matches.get(0) + ",";
            }
            sttJSResult = tempRes;
            resArray = false;
        } else {

            String quesFinal;
            quesFinal = quesWord.replaceAll("\\p{Punct}","");

            String[] questionSplit = quesFinal.split(" ");
            String[] resSplit = extractedText.split(" ");
            correctArr = new boolean[questionSplit.length];

            for (int j = 0; j < resSplit.length; j++) {
                for (int i = 0; i < questionSplit.length; i++) {
                    if (resSplit[j].equalsIgnoreCase(questionSplit[i]) && !correctArr[i]) {
                        correctArr[i] = true;
                        break;
                    }
                }
            }

            int counter = 0;
            float perc = 0f;
            for (int x = 0; x < correctArr.length; x++) {
                if (correctArr[x]) {
                    counter++;
                }
            }
            if (counter > 0)
                perc = ((float) counter / (float) correctArr.length) * 100;

            if (perc >= 75)
                sttJSResult = "true";
            else
                sttJSResult = "false";
        }

        Log.d("Myresult:::", "" + extractedText);
//        Toast.makeText(mContext, ""+matches.get(0), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

}