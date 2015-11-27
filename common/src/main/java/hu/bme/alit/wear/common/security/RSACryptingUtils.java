package hu.bme.alit.wear.common.security;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Tamás on 27/11/2015.
 */
public class RSACryptingUtils {

	public static RSAPublicKey getRSAPublicKey(String alias) {
		KeyStore.PrivateKeyEntry privateKeyEntry = null;
		try {
			privateKeyEntry = (KeyStore.PrivateKeyEntry) CryptoUtils.getKeyStore().getEntry(alias, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnrecoverableEntryException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
	}

	public static
	@Nullable
	RSAPrivateKey getRSAPrivateKey(String alias) {
		KeyStore.PrivateKeyEntry privateKeyEntry = null;
		try {
			privateKeyEntry = (KeyStore.PrivateKeyEntry) CryptoUtils.getKeyStore().getEntry(alias, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (UnrecoverableEntryException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (RSAPrivateKey) privateKeyEntry.getPrivateKey();
	}

	public static String RSAEncrypt(String text, PublicKey publicKey) throws Exception {
		byte[] dataToEncrypt = text.getBytes();

		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedData = cipher.doFinal(dataToEncrypt);

		return CryptoFormatUtils.convertToString(encryptedData);
	}

	public static String RSADecrypt(String text, PrivateKey privateKey) {
		byte[] byteText = CryptoFormatUtils.convertToHex(text);
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] result = cipher.doFinal(byteText);
			return new String(result, "UTF8");
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		return null;
	}
}
