package me.zhanghai.patternlock;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Tamás on 28/11/2015.
 */
public class BaseWearPatternActivity extends Activity {

	private static final int CLEAR_PATTERN_DELAY_MILLI = 2000;

	protected PatternView patternView;

	private final Runnable clearPatternRunnable = new Runnable() {
		public void run() {
			// clearPattern() resets display mode to DisplayMode.Correct.
			patternView.clearPattern();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.base_wear_pattern_activity);
		patternView = (PatternView) findViewById(R.id.pl_pattern);
	}

	protected void removeClearPatternRunnable() {
		patternView.removeCallbacks(clearPatternRunnable);
	}

	protected void postClearPatternRunnable() {
		removeClearPatternRunnable();
		patternView.postDelayed(clearPatternRunnable, CLEAR_PATTERN_DELAY_MILLI);
	}
}
