package me.zhanghai.patternlock;

import android.os.Bundle;

import java.util.List;

/**
 * Created by Tamás on 28/11/2015.
 */
public class ConfirmWearPatternActivity extends BaseWearPatternActivity
		implements PatternView.OnPatternListener {

	private static final String KEY_NUM_FAILED_ATTEMPTS = "num_failed_attempts";

	public static final int RESULT_FORGOT_PASSWORD = RESULT_FIRST_USER;

	protected int numFailedAttempts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		patternView.setInStealthMode(isStealthModeEnabled());
		patternView.setOnPatternListener(this);

		if (savedInstanceState == null) {
			numFailedAttempts = 0;
		} else {
			numFailedAttempts = savedInstanceState.getInt(KEY_NUM_FAILED_ATTEMPTS);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(KEY_NUM_FAILED_ATTEMPTS, numFailedAttempts);
	}

	@Override
	public void onPatternStart() {

		removeClearPatternRunnable();

		// Set display mode to correct to ensure that pattern can be in stealth mode.
		patternView.setDisplayMode(PatternView.DisplayMode.Correct);
	}

	@Override
	public void onPatternCellAdded(List<PatternView.Cell> pattern) {
	}

	@Override
	public void onPatternDetected(List<PatternView.Cell> pattern) {
		if (isPatternCorrect(pattern)) {
			onConfirmed();
		} else {
			patternView.setDisplayMode(PatternView.DisplayMode.Wrong);
			postClearPatternRunnable();
			onWrongPattern();
		}
	}

	@Override
	public void onPatternCleared() {
		removeClearPatternRunnable();
	}

	protected boolean isStealthModeEnabled() {
		return false;
	}

	protected boolean isPatternCorrect(List<PatternView.Cell> pattern) {
		return true;
	}

	protected void onConfirmed() {
		setResult(RESULT_OK);
		finish();
	}

	protected void onWrongPattern() {
		++numFailedAttempts;
	}

	protected void onCancel() {
		setResult(RESULT_CANCELED);
		finish();
	}
}
