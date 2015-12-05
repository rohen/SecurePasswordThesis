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
	public static final String REQUEST_PATH_ENCRYPTED_MASTER_KEY = "/send_encrypted_master_key";
	public static final String REQUEST_PATH_RAW_MASTER_KEY = "/send_raw_master_key";

	public static final String SEND_DATA = "send_data";

	public static final String CRYPTO_ALIAS_WEAR = "crypto_alias_data";
	public static final String CRYPTO_ALIAS_MASTER = "crypto_alias_master";
}
