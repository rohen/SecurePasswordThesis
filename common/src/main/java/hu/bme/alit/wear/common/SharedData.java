package hu.bme.alit.wear.common;

/**
 * Created by alit on 14/11/2015.
 */
public class SharedData {

	public static final String TAG = "SecurePasswordTag";

	public static final String TEST_SEND_STRING = "test_send_string";
	public static final String TEST_SEND_DATA = "test_send_data";

	public final static String REQUEST_PATH_NEW_DATA = "/send_data_new";
	public final static String REQUEST_PATH_REMOVED_DATA = "/send_data_removed";
	public final static String REQUEST_PATH_PUBLIC_KEY_RECEIVED = "/public_key_received";
	public static final String REQUEST_PATH_MASTER_PASSWORD = "/send_master_password";

	public static final String SEND_DATA = "send_data";

	public static final String CRYPTO_ALIAS_DATA = "crypto_alias_data";
	public static final String CRYPTO_ALIAS_MASTER = "crypto_alias_master";

	public static final String SHARED_PREFERENCES_PW = "shared_preferences_pw";
	public static final String SHARED_PREFERENCES_MASTER_PASSWORD_ADDED = "shared_preferences_master_password_added";
	public static final String SHARED_PREFERENCES_WEAR = "shared_preferences_wear";
}
