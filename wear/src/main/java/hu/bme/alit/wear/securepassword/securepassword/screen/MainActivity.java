package hu.bme.alit.wear.securepassword.securepassword.screen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.helper.DefaultStoreHelper;
import hu.bme.alit.wear.common.helper.DefaultWearSyncHelper;
import hu.bme.alit.wear.common.helper.StoreHelper;
import hu.bme.alit.wear.common.helper.WearSyncHelper;
import hu.bme.alit.wear.common.utils.NavigationUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;

public class MainActivity extends Activity implements DataApi.DataListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private WearSyncHelper wearSyncHelper;
	private StoreHelper storeHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		wearSyncHelper = new DefaultWearSyncHelper(this, this, this, this);
		storeHelper = new DefaultStoreHelper(this);

		View contentFrame = findViewById(R.id.content_frame);
		NavigationUtils.navigateToFragment(this, contentFrame, new ListFragment(), ListFragment.FRAGMENT_LIST_PASSWORDS_TAG, true, true);
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
					//send data to the ListFragment
					ListFragment listFragment = (ListFragment) getFragmentManager().findFragmentByTag(ListFragment.FRAGMENT_LIST_PASSWORDS_TAG);
					if(listFragment != null) {
						listFragment.refreshListItems();
					}
				}
			} else if (event.getType() == DataEvent.TYPE_DELETED) {
				// DataItem deleted
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	public StoreHelper getStoreHelper() {
		return storeHelper;
	}

	public WearSyncHelper getWearSyncHelper() {
		return wearSyncHelper;
	}
}
