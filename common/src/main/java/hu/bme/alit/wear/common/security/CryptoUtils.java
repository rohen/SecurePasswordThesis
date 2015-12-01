package hu.bme.alit.wear.common.security;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import hu.bme.alit.wear.common.SharedData;

/**
 * Created by tamasali on 2015.11.26..
 */
public class CryptoUtils {
	public static void createKeyPair(Context context, String alias) {
		try {
			// Create new key if needed
			if (!getKeyStore().containsAlias(alias)) {
				Calendar start = Calendar.getInstance();
				Calendar end = Calendar.getInstance();
				end.add(Calendar.YEAR, 1);
				//TODO recode to KeyGenParameterSpec
				KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
						.setAlias(alias)
						.setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
						.setSerialNumber(BigInteger.ONE)
						.setStartDate(start.getTime())
						.setEndDate(end.getTime())
						.build();
				KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
				generator.initialize(spec);

				generator.generateKeyPair();
			}
		} catch (Exception e) {
			Toast.makeText(context, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
			Log.e(SharedData.TAG, Log.getStackTraceString(e));
		}
		refreshKeys();
	}

	private static void refreshKeys() {
		List<String> keyAliases = new ArrayList<>();
		try {
			Enumeration<String> aliases = getKeyStore().aliases();
			while (aliases.hasMoreElements()) {
				keyAliases.add(aliases.nextElement());
			}
		} catch (Exception e) {
		}
	}


	public static KeyStore getKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
		KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
		keyStore.load(null);
		return keyStore;
	}

	public static @Nullable RSAPublicKey getRSAPublicKeyFromString(String rawRSAPublicKey) {
		byte[] rsaPublicKeyInBytes = CryptoFormatUtils.convertToHex(rawRSAPublicKey);
		try {
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKeyInBytes);
			return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getSha1Hex(String clearString)
	{
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			messageDigest.update(clearString.getBytes());
			byte[] bytes = messageDigest.digest();
			StringBuilder buffer = new StringBuilder();
			for (byte b : bytes)
			{
				buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			}
			return buffer.toString();
		}
		catch (Exception ignored)
		{
			ignored.printStackTrace();
			return null;
		}
	}
}
