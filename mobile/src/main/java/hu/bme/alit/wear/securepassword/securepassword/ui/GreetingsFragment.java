package hu.bme.alit.wear.securepassword.securepassword.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.wearable.DataMap;

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.helper.WearSyncHelper;
import hu.bme.alit.wear.securepassword.securepassword.R;

public class GreetingsFragment extends Fragment {

	public final static String FRAGMENT_GREETINGS_TAG = "fragment_greetings_tag";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View viewContainer = inflater.inflate(R.layout.fragment_greetings, container, false);

		final WearSyncHelper wearSyncHelper = ((MainActivity) getActivity()).getWearSyncHelper();

		Button sendDataButton = (Button) viewContainer.findViewById(R.id.send_data_button);
		sendDataButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DataMap dataMap = new DataMap();
				dataMap.putString(SharedData.TEST_SEND_STRING,"Message!!!");
				wearSyncHelper.sendData(SharedData.TEST_SEND_DATA, dataMap);

			}
		});

		return viewContainer;
	}
}
