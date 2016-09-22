package olala.gq.com.remixaudio.audioPlayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * author：gaoqun on 2016/9/21 09:56
 */
public class AudioPlayerManager implements AudioMediaControl, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener {
    private static MediaPlayer mMediaPlayer;
    private static final String TAG = "AudioPlayerManager";
    private static final int RECORD_CURRENT_POSITION = 1;
    private static PercentCallback percentCallback;
    private int duration = 0;
    private java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
    private int currentPosition;

    private enum State {
        PLAYING, PAUSE, RECOVERY, STOP
    }

    private State mState;


    private AudioPlayerManager() {
    }

    private static class SingleInstance {
        private static AudioPlayerManager sAudioPlayerManager = new AudioPlayerManager();
    }

    public static AudioPlayerManager getInstance() {
        return SingleInstance.sAudioPlayerManager;
    }

    private static Handler sHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RECORD_CURRENT_POSITION) {
                int position = (int) msg.obj;
                Log.d("position=", position + "");
                percentCallback.setPercent(position);
            }
        }
    };

    private void initAudioMedia() {
        if (checkAudioPlay(mMediaPlayer)) {
            duration = 0;
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    /**
     * async invoke when the resource load over
     *
     * @param mediaPlayer
     */
    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        if (!checkAudioPlay(mediaPlayer)) {
            mediaPlayer.start();
        }
    }

    private Thread caculatePostion;

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        if (!checkAudioPlay(mediaPlayer) && i > 0) {
            duration = mediaPlayer.getDuration();
            if (caculatePostion == null) {
                caculatePostion = new Thread(new RecordCurrentPositionTask());
                caculatePostion.start();
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (!checkAudioPlay(mediaPlayer)) {
            mediaPlayer.release();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (checkAudioPlay(mediaPlayer)) return;
        mediaPlayer.start();
    }

    @Override
    public void playAudio(@NonNull String url) {
        initAudioMedia();
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync();
            mState = State.PLAYING;
        } catch (IOException | IllegalStateException | SecurityException | IllegalArgumentException e) {
            e.printStackTrace();
            if (e != null)
                showErrorMessageLog("playAudio_exception", e.getMessage());
            mMediaPlayer.release();
        }
    }

    @Override
    public void pause() {
        if (checkAudioPlay(mMediaPlayer)) return;
        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mState = State.PAUSE;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            if (e != null)
                showErrorMessageLog("pause_error", e.getMessage());
            mMediaPlayer.release();
        }
    }

    @Override
    public void recovery() {
        if (checkAudioPlay(mMediaPlayer)) return;
        if (mState == State.PAUSE) {
            try {
                mMediaPlayer.start();
            } catch (IllegalStateException e) {

            }
        }
    }

    @Override
    public void stop() {
        if (checkAudioPlay(mMediaPlayer)) return;
        mMediaPlayer.stop();
        mState = State.STOP;
        mMediaPlayer.release();
        duration = 0;
        sHandler.obtainMessage(RECORD_CURRENT_POSITION, 0).sendToTarget();
        mMediaPlayer = null;
    }


    @Override
    public void seekTo(int percentage) {
        if (!checkAudioPlay(mMediaPlayer)) {
            currentPosition = (int) (duration * (percentage / 100f));
            mMediaPlayer.seekTo((int) currentPosition);
        }
    }

    @Override
    public void destroyedAllResource() {
        if (sHandler != null) sHandler.removeCallbacksAndMessages(null);
        stop();
        duration = 0;
        mMediaPlayer = null;
        System.gc();
    }

    @Override
    public boolean checkAudioPlay(@NonNull MediaPlayer mediaPlayer) {
        return mediaPlayer == null;
    }

    public void percentCallback(PercentCallback percentCallback) {
        this.percentCallback = percentCallback;
    }

    private class RecordCurrentPositionTask implements Runnable {

        @Override
        public void run() {

            while (!checkAudioPlay(mMediaPlayer) && duration > 0) {
                try {
                    currentPosition = mMediaPlayer.getCurrentPosition();
                    float percent = (float) currentPosition / (float) duration;
                    String str = String.format(String.valueOf(percent), df);
                    percent = Float.valueOf(str);
                    currentPosition = (int) (percent * 100);
                    sHandler.obtainMessage(RECORD_CURRENT_POSITION, currentPosition).sendToTarget();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            caculatePostion = null;
        }
    }

    private void showErrorMessageLog(@Nullable String key, @NonNull String message) {
        if (key == null) {
            Log.d(TAG, message);
        } else {
            Log.d(key, message);
        }
    }
}
