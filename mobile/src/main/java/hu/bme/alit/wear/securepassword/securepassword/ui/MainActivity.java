package hu.bme.alit.wear.securepassword.securepassword.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;

import hu.bme.alit.wear.common.helper.DefaultWearSyncHelper;
import hu.bme.alit.wear.common.helper.WearSyncHelper;
import hu.bme.alit.wear.common.utils.NavigationUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DataApi.DataListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private WearSyncHelper wearSyncHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		wearSyncHelper = new DefaultWearSyncHelper(this, this, this, this);

		NavigationUtils.navigateToFragment(this, getContentFrame(), new GreetingsFragment(), GreetingsFragment.FRAGMENT_GREETINGS_TAG, true, false);

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		int id = item.getItemId();

		Fragment fragment = null;
		String tag = null;
		switch (id) {
			case R.id.new_password:
				fragment = new AddFragment();
				tag = AddFragment.FRAGMENT_ADD_PASSWORD_TAG;
				break;
			case R.id.list_passwords:
				fragment = new ListFragment();
				tag = ListFragment.FRAGMENT_LIST_PASSWORDS_TAG;
				break;
		}
		if (fragment != null) {
			NavigationUtils.navigateToFragment(this, getContentFrame(), fragment, tag, true, false);
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private View getContentFrame() {
		View appBarMain = findViewById(R.id.app_bar_main);
		View contentMain = appBarMain.findViewById(R.id.content_main);
		return contentMain.findViewById(R.id.content_frame);
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
//		for (DataEvent event : dataEvents) {
//			if (event.getType() == DataEvent.TYPE_CHANGED) {
//				// DataItem changed
//				DataItem item = event.getDataItem();
//				if (item.getUri().getPath().compareTo(wearSyncHelper.getRequestPath()) == 0) {
//					DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
//				}
//			} else if (event.getType() == DataEvent.TYPE_DELETED) {
//				// DataItem deleted
//			}
//		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	public WearSyncHelper getWearSyncHelper() {
		return wearSyncHelper;
	}
}
