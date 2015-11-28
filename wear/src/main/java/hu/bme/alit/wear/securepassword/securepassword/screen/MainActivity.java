package hu.bme.alit.wear.securepassword.securepassword.screen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;

import java.security.interfaces.RSAPublicKey;

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.helper.DefaultStoreHelper;
import hu.bme.alit.wear.common.helper.DefaultWearSyncHelper;
import hu.bme.alit.wear.common.helper.StoreHelper;
import hu.bme.alit.wear.common.helper.WearSyncHelper;
import hu.bme.alit.wear.common.security.CryptoUtils;
import hu.bme.alit.wear.common.security.RSACryptingUtils;
import hu.bme.alit.wear.common.utils.NavigationUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;
import hu.bme.alit.wear.securepassword.securepassword.communication.WearDataLayerListenerService;

public class MainActivity extends Activity implements DataApi.DataListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private StoreHelper storeHelper;

	private WearSyncHelper wearSyncHelper;

	private DataBroadcastReceiver dataBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		storeHelper = new DefaultStoreHelper(this);

		wearSyncHelper = new DefaultWearSyncHelper(this, this, this, this);
		sendMessageToHandheld();

		dataBroadcastReceiver = new DataBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WearDataLayerListenerService.DATA_BROADCAST_ACTION);
		registerReceiver(dataBroadcastReceiver, intentFilter);

		View contentFrame = findViewById(R.id.content_frame);
		NavigationUtils.navigateToFragment(this, contentFrame, new ListFragment(), ListFragment.FRAGMENT_LIST_PASSWORDS_TAG, true, false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		wearSyncHelper.connectGoogleApiClient();
	}

	@Override
	protected void onPause() {
		super.onPause();
		wearSyncHelper.disconnectGoogleApiClient();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(dataBroadcastReceiver);
		super.onStop();
	}

	public StoreHelper getStoreHelper() {
		return storeHelper;
	}

	@Override
	public void onConnected(Bundle bundle) {
		wearSyncHelper.connectedGoogleApiClient();
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onDataChanged(DataEventBuffer dataEventBuffer) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	public class DataBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(WearDataLayerListenerService.DATA_BROADCAST_ACTION)) {
				ListFragment listFragment = (ListFragment) getFragmentManager().findFragmentByTag(ListFragment.FRAGMENT_LIST_PASSWORDS_TAG);
				if (listFragment != null) {
					listFragment.refreshListItems();
				}
			}
		}
	}

	private void sendMessageToHandheld() {
		CryptoUtils.createKeyPair(this, SharedData.CRYPTO_ALIAS_WEAR);
		RSAPublicKey rsaPublicKey = RSACryptingUtils.getRSAPublicKey(SharedData.CRYPTO_ALIAS_WEAR);
		byte[] keyBytes = rsaPublicKey.getEncoded();
		DataMap sendPublicKey = new DataMap();
		sendPublicKey.putByteArray(SharedData.SEND_DATA, keyBytes);
		wearSyncHelper.sendData(SharedData.REQUEST_PATH_PUBLIC_KEY_RECEIVED, sendPublicKey);
	}
}
