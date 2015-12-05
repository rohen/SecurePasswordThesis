package hu.bme.alit.wear.securepassword.securepassword.screen;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.helper.StoreHelper;
import hu.bme.alit.wear.common.security.AesCryptingUtils;
import hu.bme.alit.wear.common.security.RSACryptingUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;

/**
 * Created by alit on 16/11/2015.
 */
public class DetailsFragment extends Fragment {

	public final static String FRAGMENT_DETAILS_PASSWORD_TAG = "fragment_details_password_tag";

	public final static String EXTRA_SUBJECT = "extra_subject";
	public static final String EXTRA_KEY = "extra_key";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		Bundle bundle = this.getArguments();
		String selectedSubject = bundle.getString(EXTRA_SUBJECT);
		String encryptionKey = bundle.getString(EXTRA_KEY);

		View view = inflater.inflate(R.layout.fragment_details, container, false);

		TextView passwordText = (TextView) view.findViewById(R.id.password_text);

		StoreHelper storeHelper = ((MainActivity) getActivity()).getStoreHelper();

		String rsaEncryptedPassword = storeHelper.getPassword(selectedSubject);
		String aesEncryptedPassword = RSACryptingUtils.RSADecrypt(rsaEncryptedPassword, RSACryptingUtils.getRSAPrivateKey(SharedData.CRYPTO_ALIAS_WEAR));
		String password = AesCryptingUtils.decrypt(aesEncryptedPassword, encryptionKey);

		passwordText.setText(password);

		return view;
	}
}
