package olala.gq.com.remixaudio.audioPlayer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class AudioMediaPlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        View.OnClickListener {
    private SeekBar mSeekBar;
    private Button start, pause, stop;
    private static final String TAG = "AudioMediaPlayerActivity";
    private static String musicName = "http://yinyueshiting.baidu.com/data2/music/d2d89f20d9db974f7823740b622d2d31/123680493/11373853600128.mp3?xcode=20ad1af71a108670bdfb5657fcf2438e";
    private AudioPlayerManager mAudioPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_media_player);
        mSeekBar = (SeekBar) findViewById(R.id.audio_media_seekBar);
        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);
        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        mAudioPlayerManager = AudioPlayerManager.getInstance();
        mAudioPlayerManager.percentCallback(new AudioMediaControl.PercentCallback() {
            @Override
            public void setPercent(int percentage) {
                mSeekBar.setProgress(percentage);
            }
        });
        TelephonyManager telephony = (TelephonyManager) getSystemService(
                Context.TELEPHONY_SERVICE);
        telephony.listen(new CallPhoneListener(),
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b) {
            mAudioPlayerManager.seekTo(i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAudioPlayerManager.destroyedAllResource();
    }

    @Override
    public void onClick(View view) {
        if (view == start) {
            mAudioPlayerManager.playAudio(musicName);
        } else if (view == pause) {
            mAudioPlayerManager.pause();
        } else if (view == stop) {
            mAudioPlayerManager.stop();
        }
    }

    private class CallPhoneListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d("1", "CALL_STATE_RINGING");
                    mAudioPlayerManager.pause();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d("2", "CALL_STATE_OFFHOOK");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d("3", "CALL_STATE_IDLE");
                    mAudioPlayerManager.pause();
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

}
