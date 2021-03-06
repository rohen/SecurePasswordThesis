package hu.bme.alit.wear.securepassword.securepassword.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.wearable.DataMap;

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.helper.DefaultStoreHelper;
import hu.bme.alit.wear.common.helper.StoreHelper;
import hu.bme.alit.wear.common.helper.WearSyncHelper;
import hu.bme.alit.wear.common.security.AesCryptingUtils;
import hu.bme.alit.wear.common.security.CryptoUtils;
import hu.bme.alit.wear.common.security.RSACryptingUtils;
import hu.bme.alit.wear.common.utils.NavigationUtils;
import hu.bme.alit.wear.common.utils.PreferenceContract;
import hu.bme.alit.wear.common.utils.PreferenceUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;
import hu.bme.alit.wear.securepassword.securepassword.pattern.MobilePatternLockUtils;
import me.zhanghai.patternlock.ConfirmPatternActivity;

public class AddFragment extends Fragment {

	public final static String FRAGMENT_ADD_PASSWORD_TAG = "fragment_add_password_tag";

	private EditText subjectEditText;
	private EditText passwordEditText;
	private EditText passwordAgainEditText;
	private Button submitButton;

	private StoreHelper storeHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		storeHelper = new DefaultStoreHelper(getActivity());

		setTitle();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View viewContainer = inflater.inflate(R.layout.fragment_add_password, container, false);

		subjectEditText = (EditText) viewContainer.findViewById(R.id.input_subject);
		passwordEditText = (EditText) viewContainer.findViewById(R.id.input_value);
		passwordAgainEditText = (EditText) viewContainer.findViewById(R.id.input_value_again);
		submitButton = (Button) viewContainer.findViewById(R.id.submitButton);

		submitButton.setOnClickListener(getSubmitButtonOnClickListener());

		return viewContainer;
	}

	@Override
	public void onPause() {
		super.onPause();

		hideKeyboard();
	}

	private View.OnClickListener getSubmitButtonOnClickListener() {
		return new SubmitButtonOnClickListener();
	}

	private void showSnackBarMessage(String message) {
		Snackbar snackbar = Snackbar
				.make(getActivity().findViewById(R.id.app_bar_main), message, Snackbar.LENGTH_LONG);
		snackbar.show();
	}

	public void hideKeyboard() {
		View view = getActivity().getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	private class SubmitButtonOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {

			hideKeyboard();

			String password = passwordEditText.getText().toString();
			String passwordAgain = passwordAgainEditText.getText().toString();
			if (!password.equals(passwordAgain)) {
				showSnackBarMessage(getString(R.string.add_password_not_equal));
				return;
			}
			MobilePatternLockUtils.confirmPattern(getActivity(), AddFragment.this);

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			String hexedPattern = data.getStringExtra(ConfirmPatternActivity.EXTRA_KEY_PATTERN_HEXED);
			String subject = subjectEditText.getText().toString();
			String password = passwordEditText.getText().toString();

			String encryptedPassword = AesCryptingUtils.encrypt(password, hexedPattern);
			if (!subject.equals("") && encryptedPassword != null && storeHelper.addPassword(subject, encryptedPassword)) {
				sendMessageToWear(subject, encryptedPassword);
				showSnackBarMessage(getString(R.string.add_password_store_success));
				NavigationUtils.navigateToBack(getActivity());
			} else {
				showSnackBarMessage(getString(R.string.add_password_store_failed));
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void sendMessageToWear(String subject, String password) {
		final WearSyncHelper wearSyncHelper = ((MainActivity) getActivity()).getWearSyncHelper();

		String rawRSAPublicKey = PreferenceUtils.getString(PreferenceContract.WEAR_PUBLIC_KEY_DATA, null, getActivity());
		String encryptedPassword = RSACryptingUtils.RSAEncrypt(password, CryptoUtils.getRSAPublicKeyFromString(rawRSAPublicKey));
		if (encryptedPassword != null) {
			DataMap newPassword = new DataMap();
			newPassword.putStringArray(SharedData.SEND_DATA, storeHelper.createStringArrayFromData(subject, encryptedPassword));
			wearSyncHelper.sendData(SharedData.REQUEST_PATH_NEW_DATA, newPassword);
		}
	}

	private void setTitle() {
		ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (actionBar != null) {
			actionBar.setTitle(R.string.add_password_fragment_title);
		}
	}
}
