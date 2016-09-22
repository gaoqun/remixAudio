package olala.gq.com.remixaudio.audioPlayer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AudioMediaPlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, AdapterView.OnItemClickListener {
    private SeekBar mSeekBar;
    private Button start, pause, stop;
    private ListView mListView;
    private static final String TAG = "AudioMediaPlayerActivity";
    private String[] musicUrls = new String[]{"http://yinyueshiting.baidu.com/data2/music/0bb054cfccca36661cc0ed26513bd0a7/261813945/261813857147600128.mp3?xcode=63bf847132c7b0ac52d17b2d9b423fd1",
            "http://yinyueshiting.baidu.com/data2/music/462f14d307624cc6d3b4f1fd67e7e3f4/262903700/7356091241200128.mp3?xcode=63bf847132c7b0ac9614b4474386abae",
            "http://yinyueshiting.baidu.com/data2/music/d9f57b791f521e46cb0f8cff3c0364a4/42059027/13844864201600128.mp3?xcode=bc0d590be99a58cf08a65907a49a17b0",
            "http://yinyueshiting.baidu.com/data2/music/5ebf9e97d4b35d0cc147639a50336712/123947917/211199361200128.mp3?xcode=bc0d590be99a58cf0d9b8f0c677c76d2",
            "http://yinyueshiting.baidu.com/data2/music/239130192/12267411954000128.mp3?xcode=c7b3f66e0006cdba0fd1a9e14bd081d7"};

    private AudioPlayerManager mAudioPlayerManager;
    private ArrayList<Map<String, String>> mArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_music_list_activity);
        mSeekBar = (SeekBar) findViewById(R.id.audio_media_seekBar);
        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);
        mListView = (ListView) findViewById(R.id.list_item);
        mListView.setOnItemClickListener(this);
        for (int i = 0; i < musicUrls.length; i++) {
            Map<String, String> musics = new HashMap<>();
            musics.put("name", musicUrls[i]);
            mArrayList.add(musics);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(AudioMediaPlayerActivity.this, mArrayList, R.layout.item_audio_music, new String[]{"name"}, new int[]{R.id.music_name});
        mListView.setAdapter(simpleAdapter);
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
            if (!TextUtils.isEmpty(musicName))
                mAudioPlayerManager.playAudio(musicName);
        } else if (view == pause) {
            if (pause.getText().toString().equals("pause")){
                pause.setText("recovery");
                mAudioPlayerManager.pause();
            }else {
                pause.setText("pause");
                mAudioPlayerManager.recovery();
            }
        } else if (view == stop) {
            mAudioPlayerManager.stop();
        }
    }

    private String musicName;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        musicName = musicUrls[i];
        if (!TextUtils.isEmpty(musicName))
        {
            mAudioPlayerManager.playAudio(musicName);
            if (!pause.getText().toString().equals("pause")){
                pause.setText("pause");
            }
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
