package olala.gq.com.remixaudio;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = "MainActivity";
    private ListView mListView;
    private Button record, over;
    private View header;
    private FloatingActionButton remix, delete;
    private MyAdapter mMyAdapter;
    private MediaRecorder mMediaRecorder;
    private AudioBean mAudioBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        remix = (FloatingActionButton) findViewById(R.id.remix);
        remix.setOnClickListener(this);
        delete = (FloatingActionButton) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        initUi();
    }

    private void initUi() {
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnItemLongClickListener(this);
        header = LayoutInflater.from(this).inflate(R.layout.header, null, false);
        record = (Button) header.findViewById(R.id.record);
        over = (Button) header.findViewById(R.id.over);
        if (record != null) record.setOnClickListener(this);
        if (over != null) over.setOnClickListener(this);
        setData();
    }

    private void setData() {
        mListView.addHeaderView(header);
        if (mMyAdapter == null) {
            mMyAdapter = new MyAdapter(this);
            mListView.setAdapter(mMyAdapter);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == record) {
            record();
        } else if (view == remix) {
            Snackbar snackbar = Snackbar.make(remix, "remix the audios", Snackbar.LENGTH_SHORT).setAction("Remix", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mMyAdapter!=null)mMyAdapter.action("remix");
                }
            });
            snackbar.show();
        } else if (view == delete) {
            Snackbar snackbar = Snackbar.make(delete, "delete the audios", Snackbar.LENGTH_SHORT).setAction("Delete", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mMyAdapter != null) {
                        if (mMyAdapter!=null)mMyAdapter.action("delete");
                    }
                }
            });
            snackbar.show();
        } else if (view == over) {
            stopRecord();
        }
    }

    //start to record
    private void record() {
        if (mMediaRecorder == null) {
            Log.d(TAG, "record.......");
            mAudioBean = new AudioBean();
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.reset();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置声音来源
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//设置录制音频的格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//编码格式
            mMediaRecorder.setAudioEncodingBitRate(96000);//比特率
            mMediaRecorder.setAudioChannels(2);//
            mMediaRecorder.setAudioSamplingRate(44100);//采样率
            String audioFile = fetchAudioUri() + "/" + "audio_" + System.currentTimeMillis();
            mMediaRecorder.setOutputFile(audioFile);
            mAudioBean.setAudioName("audio_" + System.currentTimeMillis());
            try {
                mMediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaRecorder.start();
        }
    }

    //stop to record
    private void stopRecord() {
        Log.d(TAG, "stopRecord......");
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder = null;
            if (mMyAdapter != null) {
                mMyAdapter.addAudio(mAudioBean);
            }
        }
    }

    public String fetchAudioUri() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String audioCacheUri = getExternalCacheDir().getPath() + "/audios";
            File audioCacheFile = new File(audioCacheUri);
            if (!audioCacheFile.exists()) {
                audioCacheFile.mkdir();
            }
            return audioCacheFile.getPath();
        } else {
            String audioCacheUri = getCacheDir().getPath() + "/audios/";
            File audioCacheFile = new File(audioCacheUri);
            if (!audioCacheFile.exists()) {
                audioCacheFile.mkdir();
            }
            return audioCacheFile.getPath();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemLongClick.....");
        if (mMyAdapter != null && !mMyAdapter.isRadioButtonShowing()) {
            mMyAdapter.showRadioButton();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mMyAdapter != null && mMyAdapter.isRadioButtonShowing()) {
            mMyAdapter.hideRadioButton();
        } else {
            super.onBackPressed();
        }
    }

}
