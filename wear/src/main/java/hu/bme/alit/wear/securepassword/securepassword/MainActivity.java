package hu.bme.alit.wear.securepassword.securepassword;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.helper.DefaultWearSyncHelper;
import hu.bme.alit.wear.common.helper.WearSyncHelper;

public class MainActivity extends Activity implements DataApi.DataListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener{

	private TextView textView;

	private WearSyncHelper wearSyncHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		wearSyncHelper = new DefaultWearSyncHelper(this, this, this, this);

		final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub) {
				textView = (TextView) stub.findViewById(R.id.text);
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
					DataMap receivedDataMap = dataMap.get(SharedData.TEST_SEND_DATA);
					String receivedString = receivedDataMap.getString(SharedData.TEST_SEND_STRING);
					textView.setText(receivedString);
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
