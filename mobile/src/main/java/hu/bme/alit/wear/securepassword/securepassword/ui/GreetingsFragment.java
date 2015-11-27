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
import hu.bme.alit.wear.common.security.RSACryptingUtils;
import hu.bme.alit.wear.common.security.SharedPreferencesUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;

public class GreetingsFragment extends Fragment {

	public final static String FRAGMENT_GREETINGS_TAG = "fragment_greetings_tag";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View viewContainer = inflater.inflate(R.layout.fragment_greetings, container, false);

		boolean isPasswordAdded = SharedPreferencesUtils.getBooleanData(getActivity(), SharedData.SHARED_PREFERENCES_MASTER_PASSWORD_ADDED);
		if (!isPasswordAdded) {
			createContextMenu();
		}
		return viewContainer;
	}

	private void createContextMenu() {

		final Dialog contextDialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_context_master_password, null);
		contextDialog.setContentView(layout);
		contextDialog.setCancelable(false);
		contextDialog.show();

		final EditText addPasswordEditText = (EditText) layout.findViewById(R.id.add_password_edit_text);
		Button submitButton = (Button) layout.findViewById(R.id.submitButton);
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = addPasswordEditText.getText().toString();
				if (password.length() > 3) {
					try {
						String encryptedMasterPassword = RSACryptingUtils.RSAEncrypt(password, ((MainActivity) getActivity()).getRsaPublicKey());
						SharedPreferencesUtils.putStringData(getActivity(), SharedData.SHARED_PREFERENCES_PW, encryptedMasterPassword);
						sendMasterPasswordToWear(encryptedMasterPassword);
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					SharedPreferencesUtils.putBooleanData(getActivity(), SharedData.SHARED_PREFERENCES_MASTER_PASSWORD_ADDED, true);
					contextDialog.hide();
				}
			}
		});
	}

	private void sendMasterPasswordToWear(String encryptedMasterPassword) {
		final WearSyncHelper wearSyncHelper = ((MainActivity) getActivity()).getWearSyncHelper();

		String masterPassword = SharedPreferencesUtils.getStringData(getActivity(), SharedData.SHARED_PREFERENCES_PW);
		if (masterPassword != null) {
			DataMap sendMasterPassword = new DataMap();
			sendMasterPassword.putString(SharedData.SEND_DATA, encryptedMasterPassword);
			wearSyncHelper.sendData(SharedData.REQUEST_PATH_MASTER_PASSWORD, sendMasterPassword);
		}
	}
}
