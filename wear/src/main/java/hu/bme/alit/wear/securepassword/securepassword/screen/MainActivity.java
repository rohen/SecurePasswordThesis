package hu.bme.alit.wear.securepassword.securepassword.screen;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;

import java.util.ArrayList;
import java.util.List;

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.helper.DefaultStoreHelper;
import hu.bme.alit.wear.common.helper.DefaultWearSyncHelper;
import hu.bme.alit.wear.common.helper.StoreHelper;
import hu.bme.alit.wear.common.helper.WearSyncHelper;
import hu.bme.alit.wear.securepassword.securepassword.R;
import hu.bme.alit.wear.securepassword.securepassword.list.PasswordListAdapter;

public class MainActivity extends Activity implements DataApi.DataListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private WearableListView listView;

	private WearSyncHelper wearSyncHelper;
	private StoreHelper storeHelper;

	private List<String> subjects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		wearSyncHelper = new DefaultWearSyncHelper(this, this, this, this);
		storeHelper = new DefaultStoreHelper(this);

		final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub) {
				listView = (WearableListView) stub.findViewById(R.id.password_list);
				refreshListItems();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		wearSyncHelper.connectGoogleApiClient();
	}

	@Override
	public void onConnected(Bundle bundle) {
		wearSyncHelper.connectedGoogleApiClient();
	}

	@Override
	protected void onPause() {
		super.onPause();
		wearSyncHelper.disconnectGoogleApiClient();
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		for (DataEvent event : dataEvents) {
			if (event.getType() == DataEvent.TYPE_CHANGED) {
				// DataItem changed
				DataItem item = event.getDataItem();
				if (item.getUri().getPath().compareTo(wearSyncHelper.getRequestPath()) == 0) {
					DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
					DataMap receivedDataMap = dataMap.get(SharedData.SEND_ADDED_PASSWORD);
					String[] receivedData = receivedDataMap.getStringArray(SharedData.SEND_DATA);
					storeHelper.addPassword(receivedData[0], receivedData[1]);
					refreshListItems();

				}
			} else if (event.getType() == DataEvent.TYPE_DELETED) {
				// DataItem deleted
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	private void refreshListItems() {
		subjects = storeHelper.getSubjects();

		if (!subjects.isEmpty()) {
			listView.setAdapter(new PasswordListAdapter(this, subjects));
			listView.setOnClickListener(getOnClickListener());
		} else {
			List<String> emptyString = new ArrayList<>();
			emptyString.add(getString(R.string.list_passwords_empty_string));
			listView.setAdapter(new PasswordListAdapter(this, emptyString));
		}
	}

	public View.OnClickListener getOnClickListener() {
		return new PasswordListClickListener();
	}

	private class PasswordListClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {

		}
	}
}
