package hu.bme.alit.wear.securepassword.securepassword.screen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import hu.bme.alit.wear.common.helper.DefaultStoreHelper;
import hu.bme.alit.wear.common.helper.StoreHelper;
import hu.bme.alit.wear.common.utils.NavigationUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;
import hu.bme.alit.wear.securepassword.securepassword.communication.DataLayerListenerService;

public class MainActivity extends Activity {

	private StoreHelper storeHelper;

	private DataBroadcastReceiver dataBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		storeHelper = new DefaultStoreHelper(this);

		dataBroadcastReceiver = new DataBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DataLayerListenerService.DATA_BROADCAST_ACTION);
		registerReceiver(dataBroadcastReceiver, intentFilter);

		View contentFrame = findViewById(R.id.content_frame);
		NavigationUtils.navigateToFragment(this, contentFrame, new ListFragment(), ListFragment.FRAGMENT_LIST_PASSWORDS_TAG, true, false);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(dataBroadcastReceiver);
		super.onStop();
	}

	public StoreHelper getStoreHelper() {
		return storeHelper;
	}

	public class DataBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(DataLayerListenerService.DATA_BROADCAST_ACTION)) {
				ListFragment listFragment = (ListFragment) getFragmentManager().findFragmentByTag(ListFragment.FRAGMENT_LIST_PASSWORDS_TAG);
				if (listFragment != null) {
					listFragment.refreshListItems();
				}
			}
		}
	}
}
