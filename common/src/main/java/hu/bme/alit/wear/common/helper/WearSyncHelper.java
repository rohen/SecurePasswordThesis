package hu.bme.alit.wear.common.helper;

import com.google.android.gms.wearable.DataMap;

import java.io.Serializable;

/**
 * Helper class for syncing data between wear and handheld.
 */
public interface WearSyncHelper extends Serializable {

	void connectGoogleApiClient();

	void disconnectGoogleApiClient();

	void connectedGoogleApiClient();

	void sendData(String key, DataMap dataMap);

	String getRequestPath();
}
