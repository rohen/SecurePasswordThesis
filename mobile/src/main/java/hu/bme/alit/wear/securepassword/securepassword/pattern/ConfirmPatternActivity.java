/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package hu.bme.alit.wear.securepassword.securepassword.pattern;

import android.os.Bundle;

import java.util.List;

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.utils.PreferenceContract;
import hu.bme.alit.wear.common.utils.PreferenceUtils;
import me.zhanghai.patternlock.PatternView;

public class ConfirmPatternActivity extends me.zhanghai.patternlock.ConfirmPatternActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected boolean isStealthModeEnabled() {
        return !PreferenceUtils.getBoolean(PreferenceContract.KEY_PATTERN_VISIBLE,
                PreferenceContract.DEFAULT_PATTERN_VISIBLE, this);
    }

    @Override
    protected boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        return PatternLockUtils.isPatternCorrect(pattern, this, SharedData.CRYPTO_ALIAS_MOBILE);
    }

    @Override
    protected void onForgotPassword() {

//        startActivity(new Intent(this, ResetPatternActivity.class));

        // Finish with RESULT_FORGOT_PASSWORD.
        super.onForgotPassword();
    }
}
