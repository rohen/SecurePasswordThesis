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

import java.util.List;

import hu.bme.alit.wear.common.security.AesCryptingUtils;
import hu.bme.alit.wear.common.security.CryptoUtils;
import hu.bme.alit.wear.common.utils.PreferenceContract;
import hu.bme.alit.wear.common.utils.PreferenceUtils;
import me.zhanghai.patternlock.PatternUtils;
import me.zhanghai.patternlock.PatternView;
import me.zhanghai.patternlock.SetPatternActivity;

public class MobilePatternLockUtils {

	public static final int REQUEST_CODE_CONFIRM_PATTERN = 199;

	public static boolean hasPattern(Context context) {
		return !TextUtils.isEmpty(PreferenceUtils.getString(PreferenceContract.KEY_ENCRYPTED_MASTER, null, context));
	}

	public static boolean isPatternCorrect(List<PatternView.Cell> pattern, Context context, String alias) {
		String confirmPattern = PatternUtils.patternToString(pattern);
		String confirmPatternHashed = CryptoUtils.getSha1Hex(confirmPattern);
		//10. A hashhel feloldom a generált privát kulcs hashének titkosítását
		String encryptedMaster = PreferenceUtils.getString(PreferenceContract.KEY_ENCRYPTED_MASTER, null, context);
		String decryptedPrivateKey = AesCryptingUtils.decrypt(encryptedMaster, confirmPatternHashed);
		//11. Lekérem a generált kulcspár privát kulcsát, és hashelem.
		String rawMaster = PreferenceUtils.getString(PreferenceContract.KEY_RAW_MASTER, null, context);
		String rawMasterKeyHash = CryptoUtils.getSha1Hex(rawMaster);
		//12. Összehasonlítom az eltárolt privát kulcs hashét.
		return decryptedPrivateKey != null && rawMasterKeyHash != null && decryptedPrivateKey.equals(rawMasterKeyHash);
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

	public static void savePatternSecurityDataEncrypted(Context context, String hashPattern, String alias) {
		String generatedKey = CryptoUtils.getMasterKey();
		String generatedKeyHex = CryptoUtils.getSha1Hex(generatedKey);
		String encryptedMaster = AesCryptingUtils.encrypt(generatedKeyHex, hashPattern);
		if (encryptedMaster != null) {
			PreferenceUtils.putString(PreferenceContract.KEY_ENCRYPTED_MASTER, encryptedMaster, context);
			PreferenceUtils.putString(PreferenceContract.KEY_RAW_MASTER, generatedKey, context);
		}
	}

	private MobilePatternLockUtils() {
	}
}
