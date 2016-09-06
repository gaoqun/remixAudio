package olala.gq.com.remixaudio;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import olala.gq.com.remixaudio.util.AudioDecoder;
import olala.gq.com.remixaudio.util.AudioEncoder;
import olala.gq.com.remixaudio.util.MultiAudioMixer;

/**
 * Created by gaoqun on 2016/9/5.
 */
public class MyAdapter extends BaseAdapter {
    private List<AudioBean> mAudioBeanList;
    private Context mContext;
    private static final String TAG = "MyAdapter";
    private MediaPlayer mMediaPlayer;
    private HandlerThread mHandlerThread = new HandlerThread("workThread");
    private Handler mHandler;
    private Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DECODE_ERROR:
                    Toast.makeText(mContext, "解码出错！", Toast.LENGTH_SHORT).show();
                    break;
                case DECODE_SUCCESS:
                    Toast.makeText(mContext, "解码成功！", Toast.LENGTH_SHORT).show();
                    break;
                case DECODE_OVER:
                    Toast.makeText(mContext, "解码完成！", Toast.LENGTH_SHORT).show();
                    break;
                case REMIX_OVER:
                    Toast.makeText(mContext, "合成完成！", Toast.LENGTH_SHORT).show();
                    //刷新列表
                    addAudios();
                    MyAdapter.this.notifyDataSetChanged();
                    break;
            }
        }
    };
    private static final int DECODE_ERROR = 0;
    private static final int DECODE_SUCCESS = 1;
    private static final int DECODE_OVER = 2;
    private static final int REMIX_OVER = 3;

    public MyAdapter(Context context) {
        mContext = context;
        addAudios();
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public int getCount() {
        return mAudioBeanList == null ? 0 : mAudioBeanList.size();
    }

    @Override
    public AudioBean getItem(int i) {
        return mAudioBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final AudioBean audioBean = mAudioBeanList.get(i);
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_listview, viewGroup, false);
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);
            viewHolder.audioName = (TextView) view.findViewById(R.id.audio_name);
            viewHolder.play = (Button) view.findViewById(R.id.play_audio);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (!TextUtils.isEmpty(audioBean.getAudioName())) {
            viewHolder.audioName.setText(audioBean.getAudioName());
        }
        viewHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "position:" + i + "play.....");
                playAudio(audioBean);
            }
        });
        if (audioBean.isShowCheckBox()) {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checkBox.setVisibility(View.GONE);
        }
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    audioBean.setCheckBox(true);
                } else {
                    audioBean.setCheckBox(false);
                }
            }
        });

        return view;
    }

    private static class ViewHolder {
        private TextView audioName;
        private Button play;
        private CheckBox checkBox;
    }

    /**
     * add fetched audios
     */
    private void addAudios() {
        if (mAudioBeanList == null) mAudioBeanList = new ArrayList<>();
        mAudioBeanList.clear();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String audioFileUri = ((MainActivity) mContext).fetchAudioUri();
            File audiosPath = new File(audioFileUri);
            File[] audios = audiosPath.listFiles();
            if (audios != null) {
                for (File audio : audios) {
                    AudioBean audioBean = new AudioBean();
                    audioBean.setAudioName(audio.getName());
                    mAudioBeanList.add(audioBean);
                }
            }
        }
    }

    /**
     * simple play audio
     *
     * @param audioBean
     */
    private void playAudio(AudioBean audioBean) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(((MainActivity) mContext).fetchAudioUri() + "/" + audioBean.getAudioName());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * add audio
     *
     * @param audioBean
     */
    public void addAudio(AudioBean audioBean) {
        mAudioBeanList.add(audioBean);
        this.notifyDataSetChanged();
    }

    /**
     * radioButtons show
     */
    public void showRadioButton() {
        for (AudioBean audioBean : mAudioBeanList) {
            audioBean.setShowCheckBox(true);
        }
    }

    /**
     * radioButtons hide
     */
    public void hideRadioButton() {
        for (AudioBean audioBean : mAudioBeanList) {
            audioBean.setShowCheckBox(false);
        }
        this.notifyDataSetChanged();
    }

    /**
     * is the radioButtons showing
     *
     * @return
     */
    public boolean isRadioButtonShowing() {
        for (AudioBean audioBean : mAudioBeanList) {
            if (audioBean.isShowCheckBox()) {
                return true;
            }
        }
        this.notifyDataSetChanged();
        return false;
    }

    /**
     * choose action type
     *
     * @param type
     */
    public void action(String type) {
        if (type.equals("remix")) {
            remix();
        } else if (type.equals("delete")) {
            delete();
        }
    }

    /**
     * remix
     */
    private void remix() {
        int i = 0;
        final ArrayList<File> audios = new ArrayList<>();
        for (AudioBean audioBean : mAudioBeanList) {
            if (audioBean.isShowCheckBox() && audioBean.isCheckBox()) {
                i++;
                String audioName = audioBean.getAudioName();
                if (fetchFiles() != null) {
                    for (File audio : fetchFiles()) {
                        if (audio.getName().equals(audioName)) {
                            audios.add(audio);
                        }
                    }
                }
            }
        }
        if (i == 0) {
            Toast.makeText(mContext, "请选择要合成的音频！", Toast.LENGTH_SHORT).show();
        } else if (i == 1) {
            Toast.makeText(mContext, "要合成的音频来源必须大于1个！", Toast.LENGTH_SHORT).show();
        } else {
            decodeAudio(audios, new Callback<Boolean, Integer, String>() {
                @Override
                public void result(Boolean aBoolean, Integer integer, final String path) {
                    File mFile = new File(path + "/");
                    if (!mFile.exists()) {
                        mFile.mkdir();
                    }
                    final File file = new File(mFile,"mixfile");
                    //解码成功
                    if (aBoolean) {
                        //完全解码成功
                        if (integer == audios.size()) {
                            mainHandler.sendEmptyMessage(DECODE_SUCCESS);
                            File[] files1 = mFile.listFiles();
                            if (files1==null)return;
                            ArrayList<File> arrayList = new ArrayList<File>();
                            for (File decoderFile:files1)
                            {
                                if (decoderFile.getName().startsWith("decode_")){
                                    arrayList.add(decoderFile);
                                }
                            }
                            File[] files = new File[arrayList.size()];
                            for (int i=0;i<arrayList.size();i++)
                            {
                                files[i] = arrayList.get(i);
                            }
                            try {
                                //combine audios
                                MultiAudioMixer audioMixer = MultiAudioMixer.createAudioMixer();
                                audioMixer.setOnAudioMixListener(new MultiAudioMixer.OnAudioMixListener() {
                                    FileOutputStream fosRawMixAudio = new FileOutputStream(file.getPath());

                                    @Override
                                    public void onMixing(byte[] mixBytes) throws IOException {
                                        Log.d(TAG,"onMixing........");
                                        fosRawMixAudio.write(mixBytes);
                                    }

                                    @Override
                                    public void onMixError(int errorCode) {
                                        Log.d(TAG,"onMixError/......");
                                        try {
                                            if (fosRawMixAudio != null)
                                                fosRawMixAudio.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onMixComplete() {
                                        Log.d(TAG,"onMixComplete........");
                                        try {
                                            if (fosRawMixAudio != null)
                                                fosRawMixAudio.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                audioMixer.mixAudios(files);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            AudioEncoder accEncoder = AudioEncoder
                                    .createAccEncoder(file.getPath());
                            String mixPath = mFile.getAbsolutePath() + "/remix_" + System.currentTimeMillis();
                            accEncoder.encodeToFile(mixPath);
                            //删除解码的文件
                            for (File audio:mFile.listFiles()){
                                if (audio.getName().startsWith("decode")||audio.getName().startsWith("mixfile")){
                                    audio.delete();
                                }
                            }
                            mainHandler.sendEmptyMessage(REMIX_OVER);
                        } else if (audios.size() < 2) {
                            //当有文件解码失败时删除解码的文件
                            mainHandler.sendEmptyMessage(DECODE_ERROR);
                        }
                    }
                }
            });
        }
        i = 0;
    }

    /**
     * delete
     */
    private void delete() {
        Log.d(TAG, "delete......");
        int i = 0;
        for (Iterator<AudioBean> it = mAudioBeanList.iterator(); it.hasNext(); ) {
            AudioBean audioBean = it.next();
            if (audioBean.isShowCheckBox() && audioBean.isCheckBox()) {
                i++;
                String audioName = audioBean.getAudioName();
                if (fetchFiles() != null) {
                    for (File audio : fetchFiles()) {
                        if (audio.getName().equals(audioName)) {
                            audio.delete();
                        }
                    }
                }
                it.remove();
            }
        }
        if (i == 0) {
            Toast.makeText(mContext, "请选择要删除的音频！", Toast.LENGTH_SHORT).show();
        }
        i = 0;
        this.notifyDataSetChanged();
    }

    /**
     * fetch the audio files collection
     *
     * @return
     */
    private File[] fetchFiles() {
        String audiosUri = ((MainActivity) mContext).fetchAudioUri();
        File file = new File(audiosUri);
        if (file.exists()) {
            return file.listFiles();
        } else {
            return null;
        }
    }

    /**
     * decode audio file
     *
     * @return
     */
    private void decodeAudio(final ArrayList<File> arrayList, final Callback<Boolean, Integer, String> callback) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                ArrayList<AudioBean> audioBeans = new ArrayList<AudioBean>();
                int decodeSuccess = 0;
                String filePath = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    File audio = arrayList.get(i);
                    AudioBean audioBean = new AudioBean();
                    String decoderName = "decode_" + audio.getName();
                    audioBean.setDecodeName(decoderName);
                    audioBeans.add(audioBean);
                    Log.d("audio.getPath()",audio.getPath());
                    AudioDecoder audioDecoder = AudioDecoder.createDefualtDecoder(audio.getPath());
                    try {
                        String decodeFilePath = audio.getParent()+"/"+decoderName;
                        Log.d("decodeFilePath",decodeFilePath);
                        audioDecoder.decodeToFile(decodeFilePath);
                        decodeSuccess++;
                        filePath = audio.getParent() + "/";
                        Log.d(TAG, filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                callback.result(true, decodeSuccess, filePath);
            }
        });
    }
}
