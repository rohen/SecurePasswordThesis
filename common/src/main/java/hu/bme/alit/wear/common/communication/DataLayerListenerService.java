package hu.bme.alit.wear.common.communication;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.helper.DefaultStoreHelper;
import hu.bme.alit.wear.common.helper.DefaultWearSyncHelper;
import hu.bme.alit.wear.common.helper.StoreHelper;
import hu.bme.alit.wear.common.helper.WearSyncHelper;

/**
 * Created by alit on 18/11/2015.
 */
public class DataLayerListenerService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	public final static String DATA_BROADCAST_ACTION = "hu.bme.alit.databroadcastreceiver";
	public final static String DATA_BROADCAST_CHANGED = "data_broadcast_changed";

	private WearSyncHelper wearSyncHelper;
	private StoreHelper storeHelper;

	@Override
	public void onCreate() {
		super.onCreate();

		storeHelper = new DefaultStoreHelper(this);

		wearSyncHelper = new DefaultWearSyncHelper(this, this, this, null);

	}

	@Override
	public void onConnected(Bundle bundle) {
		wearSyncHelper.connectedGoogleApiClient();
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	public void onDataChanged(DataEventBuffer dataEvents) {
		for (DataEvent event : dataEvents) {
			if (event.getType() == DataEvent.TYPE_CHANGED) {
				// DataItem changed
				DataItem item = event.getDataItem();
				if (item.getUri().getPath().compareTo(wearSyncHelper.getRequestPath()) == 0) {
					DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
					DataMap receivedDataMap = dataMap.get(SharedData.SEND_ADDED_PASSWORD);
					String[] receivedData = receivedDataMap.getStringArray(SharedData.SEND_DATA);
					boolean dataChanged = storeHelper.addPassword(receivedData[0], receivedData[1]);
					if (dataChanged) {
						//send data to the ListFragment
						Intent intent = new Intent();
						intent.setAction(DATA_BROADCAST_ACTION);
						intent.putExtra(DATA_BROADCAST_CHANGED, true);
						sendBroadcast(intent);
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


}