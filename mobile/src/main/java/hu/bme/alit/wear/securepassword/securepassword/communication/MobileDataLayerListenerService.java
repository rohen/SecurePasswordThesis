package hu.bme.alit.wear.securepassword.securepassword.communication;

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
import hu.bme.alit.wear.common.helper.DefaultWearSyncHelper;
import hu.bme.alit.wear.common.helper.WearSyncHelper;

/**
 * Created by alit on 18/11/2015.
 */
public class MobileDataLayerListenerService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public final static String DATA_BROADCAST_ACTION = "hu.bme.alit.databroadcastreceiver";
    public final static String DATA_BROADCAST_PUBLIC_KEY = "data_broadcast_public_password";

    private WearSyncHelper wearSyncHelper;

    @Override
    public void onCreate() {
        super.onCreate();

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
                if (item.getUri().getPath().compareTo(SharedData.REQUEST_PATH_PUBLIC_KEY_RECEIVED) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    DataMap receivedDataMap = dataMap.get(SharedData.REQUEST_PATH_PUBLIC_KEY_RECEIVED);
                    byte[] receivedData = receivedDataMap.getByteArray(SharedData.SEND_DATA);
                    Intent intent = new Intent();
                    intent.setAction(DATA_BROADCAST_ACTION);
                    intent.putExtra(DATA_BROADCAST_PUBLIC_KEY, receivedData);
                    sendBroadcast(intent);
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