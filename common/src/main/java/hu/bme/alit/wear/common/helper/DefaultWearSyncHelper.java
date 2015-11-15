package hu.bme.alit.wear.common.helper;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;

/**
 * Default implementation of {@link WearSyncHelper}.
 */
public class DefaultWearSyncHelper implements WearSyncHelper {

	private final static String REQUEST_PATH = "/send_data";

	private GoogleApiClient googleApiClient;

	private DataApi.DataListener dataListener;

	public DefaultWearSyncHelper(Context context, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener, DataApi.DataListener dataListener) {
		googleApiClient = new GoogleApiClient.Builder(context)
				.addApi(Wearable.API)
				.addConnectionCallbacks(connectionCallbacks)
				.addOnConnectionFailedListener(onConnectionFailedListener)
				.build();

		this.dataListener = dataListener;
	}

	@Override
	public void connectGoogleApiClient() {
		googleApiClient.connect();
	}

	@Override
	public void disconnectGoogleApiClient() {
		Wearable.DataApi.removeListener(googleApiClient, dataListener);
		googleApiClient.disconnect();
	}

	@Override
	public void connectedGoogleApiClient() {
		Wearable.DataApi.addListener(googleApiClient, dataListener);
	}

	@Override
	public void sendData(String key, DataMap dataMap) {
		dataMap.putLong("time", new Date().getTime());
		PutDataMapRequest putDataMapReq = PutDataMapRequest.create(REQUEST_PATH);
		putDataMapReq.getDataMap().putDataMap(key, dataMap);
		PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
		PendingResult<DataApi.DataItemResult> pendingResult =
				Wearable.DataApi.putDataItem(googleApiClient, putDataReq);

		pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
			@Override
			public void onResult(final DataApi.DataItemResult result) {
				if (result.getStatus().isSuccess()) {
					Log.d("DataMapRequestResult", "Data item set: " + result.getDataItem().getUri());
				}
			}
		});
	}

	@Override
	public String getRequestPath() {
		return REQUEST_PATH;
	}
}
