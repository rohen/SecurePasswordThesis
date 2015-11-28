package hu.bme.alit.wear.securepassword.securepassword.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import hu.bme.alit.wear.common.security.RSACryptingUtils;
import hu.bme.alit.wear.common.utils.NavigationUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;

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

			String subject = subjectEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			String passwordAgain = passwordAgainEditText.getText().toString();
			if(!password.equals(passwordAgain)) {
				showSnackBarMessage(getString(R.string.add_password_not_equal));
				return;
			}
			String encryptedPassword = encryptPassword(password);
			if (!subject.equals("") && encryptedPassword != null && storeHelper.addPassword(subject, encryptedPassword)) {
				sendMessageToWear(subject, password);
				showSnackBarMessage(getString(R.string.add_password_store_success));
				NavigationUtils.navigateToBack(getActivity());
			} else {
				showSnackBarMessage(getString(R.string.add_password_store_failed));
			}
		}
	}

	private String encryptPassword(String password) {
		return RSACryptingUtils.RSAEncrypt(password, RSACryptingUtils.getRSAPublicKey(SharedData.CRYPTO_ALIAS_MASTER));
	}

	private void sendMessageToWear(String subject, String password) {
		final WearSyncHelper wearSyncHelper = ((MainActivity) getActivity()).getWearSyncHelper();

//		String masterPassword = SharedPreferencesUtils.getStringData(getActivity(), SharedData.SHARED_PREFERENCES_PW);
		String encryptedPassword = RSACryptingUtils.RSAEncrypt(password, ((MainActivity) getActivity()).getRsaPublicKey());
		if (encryptedPassword != null) {
			DataMap newPassword = new DataMap();
			newPassword.putStringArray(SharedData.SEND_DATA, storeHelper.createStringArrayFromData(subject, encryptedPassword));
			wearSyncHelper.sendData(SharedData.REQUEST_PATH_NEW_DATA, newPassword);
		}
	}

	private void setTitle() {
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.add_password_fragment_title);
	}
}
