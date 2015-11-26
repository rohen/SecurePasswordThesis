package hu.bme.alit.wear.common.security;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.Cipher;
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
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);

                KeyPair keyPair = generator.generateKeyPair();
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

    public static RSAPublicKey getRSAPublicKey(String alias) {
        KeyStore.PrivateKeyEntry privateKeyEntry = null;
        try {
            privateKeyEntry = (KeyStore.PrivateKeyEntry) getKeyStore().getEntry(alias, null);
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

    public static KeyStore getKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return keyStore;
    }

    public static String RSAEncrypt(String text, PublicKey publicKey) throws Exception {
        byte[] dataToEncrypt = text.getBytes();

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = cipher.doFinal(dataToEncrypt);

        return CryptoFormatHelper.convertToString(encryptedData);
    }

    public static String RSADecrypt(String text, PrivateKey privateKey) throws Exception {
        byte[] byteText = CryptoFormatHelper.convertToHex(text);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(byteText);

        return new String(result, "UTF8");
    }
}
