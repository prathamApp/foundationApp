package com.pratham.foundation.utility;

import android.content.Context;
import android.media.MediaPlayer;


import com.pratham.foundation.interfaces.MediaCallbacks;

import java.io.IOException;

/**
 * Created by HP on 10-04-2018.
 */

public class MediaPlayerUtil implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    Context context;
    private MediaPlayer mediaPlayer;
    private MediaCallbacks mCallbacks;


    public MediaPlayerUtil(Context context) {
        this.context = context;
        initMediaPlayer();
    }

    /**
     * Initializes speech interface callback.
     */
    public void initCallback(final MediaCallbacks callbacks) {
        this.mCallbacks = callbacks;
    }

    private void initMediaPlayer() {
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer(); //new MediaPlayer instance
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void playMedia(String path) {
        try {
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();//new MediaPlayer instance
            else
                mediaPlayer.reset();
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(path);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        if (mCallbacks != null)
            mCallbacks.onComplete();
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopMedia();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
    }
}
