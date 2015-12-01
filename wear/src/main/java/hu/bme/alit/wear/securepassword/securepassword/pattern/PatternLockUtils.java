/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package hu.bme.alit.wear.securepassword.securepassword.pattern;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import hu.bme.alit.wear.common.security.CryptoUtils;
import hu.bme.alit.wear.common.security.RSACryptingUtils;
import hu.bme.alit.wear.common.utils.PreferenceContract;
import hu.bme.alit.wear.common.utils.PreferenceUtils;
import me.zhanghai.patternlock.PatternUtils;
import me.zhanghai.patternlock.PatternView;

public class PatternLockUtils {

	public static final int REQUEST_CODE_CONFIRM_PATTERN = 199;

	public static boolean isPatternCorrect(List<PatternView.Cell> pattern, Context context, String alias) {
		String confirmPattern = PatternUtils.patternToString(pattern);
		String confirmPatternHexed = CryptoUtils.getSha1Hex(confirmPattern);
		String encryptedCorrectPattern = PreferenceUtils.getString(PreferenceContract.KEY_PATTERN, null, context);
		String correctPattern = decryptPattern(encryptedCorrectPattern, alias);
		return !(correctPattern == null || confirmPatternHexed == null) && confirmPatternHexed.equals(correctPattern);
	}

	// NOTE: Should only be called when there is a pattern for this account.
	public static void confirmPattern(Activity activity, Fragment fragment, int requestCode) {
		fragment.startActivityForResult(new Intent(activity, ConfirmPatternActivity.class),
				requestCode);
	}

	public static void confirmPattern(Activity activity, Fragment fragment) {
		confirmPattern(activity, fragment, REQUEST_CODE_CONFIRM_PATTERN);
	}

	public static String decryptPattern(String pattern, String alias) {
		return RSACryptingUtils.RSADecrypt(pattern, RSACryptingUtils.getRSAPrivateKey(alias));
	}

	private PatternLockUtils() {
	}
}
