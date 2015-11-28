/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package hu.bme.alit.wear.securepassword.securepassword.pattern;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import hu.bme.alit.wear.securepassword.securepassword.ui.MainActivity;
import me.zhanghai.patternlock.PatternUtils;
import me.zhanghai.patternlock.PatternView;

public class SetPatternActivity extends me.zhanghai.patternlock.SetPatternActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	}

	@Override
	protected void onSetPattern(List<PatternView.Cell> pattern) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(MainActivity.RESULT_CREATE_PATTERN_DATA, PatternUtils.patternToBytes(pattern));
		setResult(MainActivity.RESULT_CREATE_PATTERN_CODE, resultIntent);
		finish();
	}
}
