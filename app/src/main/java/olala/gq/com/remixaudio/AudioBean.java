package olala.gq.com.remixaudio;

/**
 * Created by gaoqun on 2016/9/5.
 */
public class AudioBean {

    private String audioName = "default_name";
    private long audioLength;
    private boolean showCheckBox;
    private boolean checkBox;
    private String decodeName;

    public String getDecodeName() {
        return decodeName;
    }

    public void setDecodeName(String decodeName) {
        this.decodeName = decodeName;
    }

    public boolean isCheckBox() {
        return checkBox;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }

    public boolean isShowCheckBox() {
        return showCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        this.showCheckBox = showCheckBox;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public long getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(long audioLength) {
        this.audioLength = audioLength;
    }
}
