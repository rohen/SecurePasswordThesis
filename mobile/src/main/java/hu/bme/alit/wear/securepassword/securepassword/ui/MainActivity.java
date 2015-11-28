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
import com.google.android.gms.wearable.DataMap;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.helper.DefaultWearSyncHelper;
import hu.bme.alit.wear.common.helper.WearSyncHelper;
import hu.bme.alit.wear.common.security.CryptoUtils;
import hu.bme.alit.wear.common.utils.NavigationUtils;
import hu.bme.alit.wear.common.utils.PreferenceContract;
import hu.bme.alit.wear.common.utils.PreferenceUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;
import hu.bme.alit.wear.securepassword.securepassword.communication.MobileDataLayerListenerService;
import hu.bme.alit.wear.securepassword.securepassword.pattern.PatternLockUtils;
import hu.bme.alit.wear.securepassword.securepassword.pattern.SetPatternActivity;
import me.zhanghai.patternlock.PatternUtils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DataApi.DataListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	public static final int RESULT_CREATE_PATTERN_CODE = 365;
	public static final String RESULT_CREATE_PATTERN_DATA = "result_create_pattern_data";

	private WearSyncHelper wearSyncHelper;

	private DataBroadcastReceiver dataBroadcastReceiver;

	private RSAPublicKey rsaPublicKeyWear;


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
			NavigationUtils.navigateToFragment(this, getContentFrame(), new GreetingsFragment(), GreetingsFragment.FRAGMENT_GREETINGS_TAG, true, false);
		}

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			FragmentManager fragmentManager = getFragmentManager();
			if (fragmentManager.getBackStackEntryCount() > 0) {
				NavigationUtils.navigateToFragment(this, getContentFrame(), new GreetingsFragment(), GreetingsFragment.FRAGMENT_GREETINGS_TAG, true, false);
			} else {
				super.onBackPressed();
			}
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
				try {
					rsaPublicKeyWear =
							(RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(rawPublicKey));
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public RSAPublicKey getRsaPublicKeyWear() {
		return rsaPublicKeyWear;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CREATE_PATTERN_CODE) {
			String patternString = PatternUtils.bytesToString(data.getByteArrayExtra(RESULT_CREATE_PATTERN_DATA));
			PatternLockUtils.savePatternEncrypted(this, patternString, SharedData.CRYPTO_ALIAS_MOBILE);
			sendPatternEncrypted(patternString, SharedData.CRYPTO_ALIAS_WEAR);
			PreferenceUtils.putBoolean(PreferenceContract.PATTERN_ADDED, true, this);
			NavigationUtils.navigateToFragment(this, getContentFrame(), new GreetingsFragment(), GreetingsFragment.FRAGMENT_GREETINGS_TAG, true, false);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void sendPatternEncrypted(String patternString, String alias) {
		CryptoUtils.createKeyPair(this, alias);
		String encryptedPattern = PatternLockUtils.encryptPattern(this, patternString, alias, rsaPublicKeyWear);
		if (encryptedPattern != null) {
			DataMap sendMasterPassword = new DataMap();
			sendMasterPassword.putString(SharedData.SEND_DATA, encryptedPattern);
			wearSyncHelper.sendData(SharedData.REQUEST_PATH_PATTERN, sendMasterPassword);
		}
	}


}
