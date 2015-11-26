package hu.bme.alit.wear.common.security;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tamasali on 2015.11.26..
 */
public class SharedPreferencesUtils {

	public static boolean putStringData(Activity activity, String key, String stringData) {
		SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, stringData);
		return editor.commit();
	}

	public static String getStringData(Activity activity, String key) {
		SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
		return sharedPref.getString(key, "");
	}
}
