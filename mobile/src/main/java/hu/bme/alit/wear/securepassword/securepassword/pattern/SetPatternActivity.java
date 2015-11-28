/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package hu.bme.alit.wear.securepassword.securepassword.pattern;

import android.os.Bundle;

import java.util.List;

import me.zhanghai.patternlock.PatternView;

public class SetPatternActivity extends me.zhanghai.patternlock.SetPatternActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	}

	@Override
	protected void onSetPattern(List<PatternView.Cell> pattern) {
		PatternLockUtils.setPattern(pattern, this);
	}
}
