package olala.gq.com.remixaudio.util;

import java.security.MessageDigest;

public class MD5Util {

	public static String getMD5Str(String src) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(src.getBytes());
			return toHexString(messageDigest.digest());
		} catch (Exception e) {
		}
		return null;
	}

	private static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(Digit[(b[i] & 0xf0) >>> 4]);
			sb.append(Digit[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	private static char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

}