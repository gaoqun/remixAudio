package olala.gq.com.remixaudio.util;



public abstract class AudioEncoder {

	String rawAudioFile;

	AudioEncoder(String rawAudioFile) {
		this.rawAudioFile = rawAudioFile;
	}

	public static AudioEncoder createAccEncoder(String rawAudioFile) {
		return new AACAudioEncoder(rawAudioFile);
	}

	public abstract void encodeToFile(String outEncodeFile);
}
