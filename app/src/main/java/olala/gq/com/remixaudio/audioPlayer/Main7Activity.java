package olala.gq.com.remixaudio.audioPlayer;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Main7Activity extends AppCompatActivity {
    private static final int PARSE_LRC_INFO = 1;

    private static TextView lrcText;
    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PARSE_LRC_INFO) {
                LrcInfo lrcInfo = (LrcInfo) msg.obj;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(lrcInfo.getTitle())
                        .append("\n")
                        .append(lrcInfo.getSinger())
                        .append("\n")
                        .append(lrcInfo.getAlbum())
                        .append("\n");
                HashMap<Long, String> lrcInfos = lrcInfo.getInfos();
                Iterator<Map.Entry<Long, String>> iterator = lrcInfos.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Long, String> lrc = iterator.next();
                    stringBuilder.append(lrc.getKey()).append(lrc.getValue())
                            .append("\n");
                }
                lrcText.setText(stringBuilder.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        lrcText = (TextView) findViewById(R.id.lrc_info);
        String dirDctionary = Environment.getExternalStorageDirectory().getPath();
        File file = new File(dirDctionary + "/abc.lrc");
        new Thread(new ParseLrcInfoTask(file.getPath())).start();
    }

    private class ParseLrcInfoTask implements Runnable {

        private String path;

        public ParseLrcInfoTask(@NonNull String path) {
            this.path = path;
        }

        @Override
        public void run() {
            try {
                sHandler.obtainMessage(PARSE_LRC_INFO, new LrcParser().parser(path)).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
