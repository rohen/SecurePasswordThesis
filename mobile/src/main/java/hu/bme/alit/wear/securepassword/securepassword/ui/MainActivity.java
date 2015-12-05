package hu.bme.alit.wear.securepassword.securepassword.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.security.interfaces.RSAPublicKey;

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.helper.DefaultWearSyncHelper;
import hu.bme.alit.wear.common.helper.WearSyncHelper;
import hu.bme.alit.wear.common.security.CryptoFormatUtils;
import hu.bme.alit.wear.common.security.CryptoUtils;
import hu.bme.alit.wear.common.security.RSACryptingUtils;
import hu.bme.alit.wear.common.utils.NavigationUtils;
import hu.bme.alit.wear.common.utils.PreferenceContract;
import hu.bme.alit.wear.common.utils.PreferenceUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;
import hu.bme.alit.wear.securepassword.securepassword.communication.MobileDataLayerListenerService;
import hu.bme.alit.wear.securepassword.securepassword.pattern.MobilePatternLockUtils;
import hu.bme.alit.wear.securepassword.securepassword.pattern.SetPatternActivity;
import me.zhanghai.patternlock.PatternUtils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DataApi.DataListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	public static final int RESULT_CREATE_PATTERN_CODE = 365;
	public static final String RESULT_CREATE_PATTERN_DATA = "result_create_pattern_data";

	private WearSyncHelper wearSyncHelper;

	private DataBroadcastReceiver dataBroadcastReceiver;


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

		dataBroadcastReceiver = new DataBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MobileDataLayerListenerService.DATA_BROADCAST_ACTION);
		registerReceiver(dataBroadcastReceiver, intentFilter);

		boolean isPasswordAdded = PreferenceUtils.getBoolean(PreferenceContract.PATTERN_ADDED, false, this);
		if (!isPasswordAdded) {
			this.startActivityForResult(new Intent(this, SetPatternActivity.class), RESULT_CREATE_PATTERN_CODE);
		} else if (savedInstanceState == null) {
			NavigationUtils.navigateToFragment(this, getContentFrame(), new GreetingsFragment(), GreetingsFragment.FRAGMENT_GREETINGS_TAG, false, false);
		}

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
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
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	public WearSyncHelper getWearSyncHelper() {
		return wearSyncHelper;
	}

	public class DataBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MobileDataLayerListenerService.DATA_BROADCAST_ACTION)) {
				byte[] rawPublicKey = intent.getExtras().getByteArray(MobileDataLayerListenerService.DATA_BROADCAST_PUBLIC_KEY);
				String convertedRawPublicKey = CryptoFormatUtils.convertToString(rawPublicKey);
				PreferenceUtils.putString(PreferenceContract.WEAR_PUBLIC_KEY_DATA, convertedRawPublicKey, MainActivity.this);
				//Send the pattern if the pattern is set before getting the wear's RSA public key.
				String encryptedKeyPattern = PreferenceUtils.getString(PreferenceContract.KEY_ENCRYPTED_MASTER, null, context);
				if (encryptedKeyPattern != null) {
					sendMasterKeyToWear();
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CREATE_PATTERN_CODE) {
			String patternString = PatternUtils.bytesToString(data.getByteArrayExtra(RESULT_CREATE_PATTERN_DATA));

			String hashPattern = CryptoUtils.getSha1Hex(patternString);
			MobilePatternLockUtils.savePatternSecurityDataEncrypted(this, hashPattern, SharedData.CRYPTO_ALIAS_MASTER);

			sendMasterKeyToWear();
			PreferenceUtils.putBoolean(PreferenceContract.PATTERN_ADDED, true, this);
			NavigationUtils.navigateToFragment(this, getContentFrame(), new GreetingsFragment(), GreetingsFragment.FRAGMENT_GREETINGS_TAG, true, false);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void sendMasterKeyToWear() {
		String rawRSAPublicKey = PreferenceUtils.getString(PreferenceContract.WEAR_PUBLIC_KEY_DATA, null, this);
		if (rawRSAPublicKey != null) {
			RSAPublicKey rsaPublicKeyWear = CryptoUtils.getRSAPublicKeyFromString(rawRSAPublicKey);
			String masterKeyEncrypted = PreferenceUtils.getString(PreferenceContract.KEY_ENCRYPTED_MASTER, null, this);
			String RSAEncryptedMasterKey = RSACryptingUtils.RSAEncrypt(masterKeyEncrypted, rsaPublicKeyWear);
			wearSyncHelper.sendSimpleStringData(SharedData.REQUEST_PATH_ENCRYPTED_MASTER_KEY, RSAEncryptedMasterKey);
			String rawMesterKey = PreferenceUtils.getString(PreferenceContract.KEY_RAW_MASTER, null, this);
			String encryptedRawMasterKey = RSACryptingUtils.RSAEncrypt(rawMesterKey, rsaPublicKeyWear);
			wearSyncHelper.sendSimpleStringData(SharedData.REQUEST_PATH_RAW_MASTER_KEY, encryptedRawMasterKey);
		}
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			FragmentManager fragmentManager = getFragmentManager();
			GreetingsFragment greetingsFragment = (GreetingsFragment)fragmentManager.findFragmentByTag(GreetingsFragment.FRAGMENT_GREETINGS_TAG);
			if (greetingsFragment != null && greetingsFragment.isVisible()) {
				super.onBackPressed();
				finish();
			} else {
				NavigationUtils.navigateToFragment(this, getContentFrame(), new GreetingsFragment(), GreetingsFragment.FRAGMENT_GREETINGS_TAG, false, false);
			}
		}
	}
}
