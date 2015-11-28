package hu.bme.alit.wear.securepassword.securepassword.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.wearable.DataMap;

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.helper.WearSyncHelper;
import hu.bme.alit.wear.common.security.CryptoUtils;
import hu.bme.alit.wear.common.security.RSACryptingUtils;
import hu.bme.alit.wear.common.utils.PreferenceContract;
import hu.bme.alit.wear.common.utils.PreferenceUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;

public class GreetingsFragment extends Fragment {

	public final static String FRAGMENT_GREETINGS_TAG = "fragment_greetings_tag";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View viewContainer = inflater.inflate(R.layout.fragment_greetings, container, false);
//		boolean isPasswordAdded = SharedPreferencesUtils.getBooleanData(getActivity(), SharedData.SHARED_PREFERENCES_MASTER_PASSWORD_ADDED);
//		if (!isPasswordAdded) {
//			getActivity().startActivity(new Intent(getActivity(), SetPatternActivity.class));
//			createContextMenu();
//		}
		return viewContainer;
	}

	private void createContextMenu() {

		final Dialog contextDialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_context_master_password, null);
		contextDialog.setContentView(layout);
		contextDialog.setCancelable(false);
		contextDialog.setTitle(R.string.master_password_title);
		contextDialog.show();

		final EditText addPasswordEditText = (EditText) layout.findViewById(R.id.add_password_edit_text);
		Button submitButton = (Button) layout.findViewById(R.id.submitButton);
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = addPasswordEditText.getText().toString();
				if (password.length() > 3) {
					encryptMasterPassword(password);
					PreferenceUtils.putBoolean(PreferenceContract.MASTER_PASSWORD_ADDED, true, getActivity());
					contextDialog.hide();
				}
			}
		});
	}

	private void encryptMasterPassword(String password) {
		CryptoUtils.createKeyPair(getActivity(), SharedData.CRYPTO_ALIAS_MASTER);
		String encryptedMasterPassword = RSACryptingUtils.RSAEncrypt(password, RSACryptingUtils.getRSAPublicKey(SharedData.CRYPTO_ALIAS_MASTER));
		PreferenceUtils.putString(PreferenceContract.MASTER_PASSWORD, encryptedMasterPassword, getActivity());
		sendMasterPasswordToWear(encryptedMasterPassword);
	}

	private void sendMasterPasswordToWear(String encryptedMasterPassword) {
		final WearSyncHelper wearSyncHelper = ((MainActivity) getActivity()).getWearSyncHelper();

		String masterPassword = PreferenceUtils.getString(PreferenceContract.MASTER_PASSWORD, null, getActivity());
		if (masterPassword != null) {
			DataMap sendMasterPassword = new DataMap();
			sendMasterPassword.putString(SharedData.SEND_DATA, encryptedMasterPassword);
			wearSyncHelper.sendData(SharedData.REQUEST_PATH_MASTER_PASSWORD, sendMasterPassword);
		}
	}
}
