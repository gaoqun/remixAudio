package olala.gq.com.remixaudio.util;

import java.io.IOException;

/**
 * ��Ƶ������
 * 
 */
public abstract class AudioDecoder {

	String mEncodeFile;

	OnAudioDecoderListener mOnAudioDecoderListener;

	AudioDecoder(String encodefile) {
		this.mEncodeFile = encodefile;
	}

	public static AudioDecoder createDefualtDecoder(String encodefile) {
		return new AndroidAudioDecoder(encodefile);
	}

	public void setOnAudioDecoderListener(OnAudioDecoderListener l) {
		this.mOnAudioDecoderListener = l;
	}

	/**
	 * ����
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract RawAudioInfo decodeToFile(String outFile)
			throws IOException;

	public static class RawAudioInfo {
		public String tempRawFile;
		public int size;
		public long sampleRate;
		public int channel;
	}

	public interface OnAudioDecoderListener {
		/**
		 * monitor when processing decode
		 * 
		 * @param decodedBytes
		 * @param progress
		 *            range 0~1
		 * @throws IOException
		 */
		void onDecode(byte[] decodedBytes, double progress) throws IOException;
	}
}
