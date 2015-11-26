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

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.security.CryptoUtils;
import hu.bme.alit.wear.common.security.SharedPreferencesUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;

public class GreetingsFragment extends Fragment {

	public final static String FRAGMENT_GREETINGS_TAG = "fragment_greetings_tag";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View viewContainer = inflater.inflate(R.layout.fragment_greetings, container, false);

		boolean isPasswordAdded = false;
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
						String encryptedPassword = CryptoUtils.RSAEncrypt(password, ((MainActivity) getActivity()).getRsaPublicKey());
						if(!encryptedPassword.equals("") ) {
							SharedPreferencesUtils.putStringData(getActivity(), SharedData.SHARED_PREFERENCES_PW, encryptedPassword);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					contextDialog.hide();
				}
			}
		});
	}
}
