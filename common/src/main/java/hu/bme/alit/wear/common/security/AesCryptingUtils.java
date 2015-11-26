package hu.bme.alit.wear.common.security;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encrypting class with AES. It is uses a given seed to encrypt the text.
 */
public class AesCryptingUtils {

	private final static int AES_KEY_SIZE = 128; // the others are 192 and 256 bits

	public static String encrypt(String text, String seed) throws Exception {
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] resultText = encrypt(rawKey, text.getBytes());
		return CryptoFormatHelper.convertToString(resultText);
	}

	public static String decrypt(String text, String seed) throws Exception {
		byte[] rawKey = getRawKey(seed.getBytes());
		//convert to hexadecimals due to not lose any data.
		byte[] byteText = CryptoFormatHelper.convertToHex(text);
		byte[] result = decrypt(rawKey, byteText);
		String resumedString = new String(result, "UTF8");
		return resumedString;
	}

	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "Crypto");
		secureRandom.setSeed(seed);
		kgen.init(AES_KEY_SIZE, secureRandom);
		SecretKey secretKey = kgen.generateKey();
		byte[] rawKey = secretKey.getEncoded();
		return rawKey;
	}


	private static byte[] encrypt(byte[] rawKey, byte[] text) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encryptedText = cipher.doFinal(text);
		return encryptedText;
	}

	private static byte[] decrypt(byte[] rawKey, byte[] encryptedText) throws Exception {
		SecretKeySpec secretKeySpec = new SecretKeySpec(rawKey, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		byte[] decrypted = cipher.doFinal(encryptedText);
		return decrypted;
	}
}