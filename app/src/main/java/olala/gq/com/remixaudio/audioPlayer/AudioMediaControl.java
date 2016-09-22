package olala.gq.com.remixaudio.audioPlayer;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;

/**
 * author：gaoqun on 2016/9/21 09:59
 */
public interface AudioMediaControl {

    //play audio with url
    void playAudio(@NonNull String url);

    //audioMedia pause
    void pause();

    //recovery the music
    void recovery();

    //audioMedia stop
    void stop();

    //audioMedia seek to anywhere
    void seekTo(int percentage);

    //audioMedia resource destroyed
    void destroyedAllResource();

    //check AudioPlayer
    boolean checkAudioPlay(@NonNull MediaPlayer mediaPlayer);

    //transparent the current percentage
    interface PercentCallback{
        void setPercent(int percentage);
    }

}
