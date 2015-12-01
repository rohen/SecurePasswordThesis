/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package hu.bme.alit.wear.securepassword.securepassword.pattern;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import hu.bme.alit.wear.common.security.AesCryptingUtils;
import hu.bme.alit.wear.common.security.CryptoFormatUtils;
import hu.bme.alit.wear.common.security.CryptoUtils;
import hu.bme.alit.wear.common.security.RSACryptingUtils;
import hu.bme.alit.wear.common.utils.PreferenceContract;
import hu.bme.alit.wear.common.utils.PreferenceUtils;
import me.zhanghai.patternlock.PatternUtils;
import me.zhanghai.patternlock.PatternView;
import me.zhanghai.patternlock.SetPatternActivity;

public class PatternLockUtils {

	public static final int REQUEST_CODE_CONFIRM_PATTERN = 199;

	public static boolean hasPattern(Context context) {
		return !TextUtils.isEmpty(PreferenceUtils.getString(PreferenceContract.KEY_PATTERN, null, context));
	}

	public static boolean isPatternCorrect(List<PatternView.Cell> pattern, Context context, String alias) {
		String confirmPattern = PatternUtils.patternToString(pattern);
		String confirmPatternHashed = CryptoUtils.getSha1Hex(confirmPattern);
		//10. A hashhel feloldom a generált privát kulcs hashének titkosítását
		String encryptedMaster = PreferenceUtils.getString(PreferenceContract.KEY_PATTERN, null, context);
		String decryptedPrivateKey = AesCryptingUtils.decrypt(encryptedMaster, confirmPatternHashed);
		//11. Lekérem a generált kulcspár privát kulcsát, és hashelem.
		PrivateKey privateKeyFromKeyStore = RSACryptingUtils.getRSAPrivateKey(alias);
		byte[] rsaPrivateKeyInBytes = privateKeyFromKeyStore.getEncoded();
		String rsaPrivateKeyHex = CryptoUtils.getSha1Hex(CryptoFormatUtils.convertToString(rsaPrivateKeyInBytes));
		//12. Összehasonlítom az eltárolt privát kulcs hashét.
		return decryptedPrivateKey != null && rsaPrivateKeyHex != null && decryptedPrivateKey.equals(rsaPrivateKeyHex);
	}

	public static void clearPattern(Context context) {
//		PreferenceUtils.remove(PreferenceContract.KEY_PATTERN_SHA1, context);
	}

	public static void setPatternByUser(Context context) {
		context.startActivity(new Intent(context, SetPatternActivity.class));
	}

	// NOTE: Should only be called when there is a pattern for this account.
	public static void confirmPattern(Activity activity, Fragment fragment, int requestCode) {
		fragment.startActivityForResult(new Intent(activity, ConfirmPatternActivity.class),
				requestCode);
	}

	public static void confirmPattern(Activity activity, Fragment fragment) {
		confirmPattern(activity, fragment, REQUEST_CODE_CONFIRM_PATTERN);
	}

	public static void confirmPatternIfHas(Activity activity, Fragment fragment) {
		if (hasPattern(activity)) {
			confirmPattern(activity, fragment, REQUEST_CODE_CONFIRM_PATTERN);
		}
	}

	public static boolean checkConfirmPatternResult(Activity activity, int requestCode,
													int resultCode) {
		if (requestCode == REQUEST_CODE_CONFIRM_PATTERN && resultCode != Activity.RESULT_OK) {
			activity.finish();
			return true;
		} else {
			return false;
		}
	}

	public static void savePatternEncrypted(Context context, String hashPattern, String alias) {
		//3. Generálok egy privát-publikus kulcspárt.
		CryptoUtils.createKeyPair(context, alias);
		PrivateKey rsaPrivateKey = RSACryptingUtils.getRSAPrivateKey(alias);
		byte[] rsaPrivateKeyInBytes = rsaPrivateKey.getEncoded();
		//4. A privát kulcsot hashelem.
		String rsaPrivateKeyHex = CryptoUtils.getSha1Hex(CryptoFormatUtils.convertToString(rsaPrivateKeyInBytes));
		//5. A privát kulcs hashét titkosítom a kulcsminta hashével.
		String encryptedMaster = AesCryptingUtils.encrypt(rsaPrivateKeyHex, hashPattern);
		if (encryptedMaster != null) {
			//6. Eltárolom az enkriptált privát hashet.
			PreferenceUtils.putString(PreferenceContract.KEY_PATTERN, encryptedMaster, context);
		}
	}

	public static String encryptPattern(Context context, String pattern, String alias, RSAPublicKey rsaPublicKey) {
		return RSACryptingUtils.RSAEncrypt(pattern, rsaPublicKey);
	}

	public static String decryptPattern(Context context, String pattern, String alias) {
		return RSACryptingUtils.RSADecrypt(pattern, RSACryptingUtils.getRSAPrivateKey(alias));
	}

	private PatternLockUtils() {
	}
}
