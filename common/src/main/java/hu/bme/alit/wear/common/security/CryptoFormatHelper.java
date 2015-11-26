package hu.bme.alit.wear.common.security;

/**
 * Helper to format the text to byte and vice versa.
 */
public class CryptoFormatHelper {

	private final static String HEX = "0123456789ABCDEF";

	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}

	/**
	 * Converts text to byte codes.
	 * @param text the text.
	 * @return the code.
	 */
	public static byte[] convertToHex(String text) {
		if(text == null) {
			return null;
		}
		int len = text.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++) {
			try {
				result[i] = Integer.valueOf(text.substring(2 * i, 2 * i + 2), 16).byteValue();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return result;
	}

	/**
	 * Converts byte codes to string.
	 * @param byteCode the hex code.
	 * @return the string.
	 */
	public static String convertToString(byte[] byteCode) {
		if (byteCode == null)
			return "";
		StringBuffer result = new StringBuffer(2 * byteCode.length);
		for (int i = 0; i < byteCode.length; i++) {
			appendHex(result, byteCode[i]);
		}
		return result.toString();
	}
}
