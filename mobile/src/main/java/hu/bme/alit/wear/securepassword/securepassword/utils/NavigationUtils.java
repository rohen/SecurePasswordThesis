package hu.bme.alit.wear.securepassword.securepassword.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import hu.bme.alit.wear.securepassword.securepassword.R;

/**
 * Utils class for navigation beetween {@link Fragment} and {@link Activity} classes.
 */
public class NavigationUtils {

	/**
	 * Navigate to a fragment.
	 *
	 * @param activity       the activity.
	 * @param fragment       the fragment.
	 * @param tag            the tag.
	 * @param addToBackStack boolean, {@code true}, if the current page needs to add to the navigation back stack, {@code false} otherwise.
	 */
	public static void navigateToFragment(Activity activity, Fragment fragment, String tag, boolean addToBackStack) {
		FragmentManager fragmentManager = activity.getFragmentManager();
		View appBarMain = activity.findViewById(R.id.app_bar_main);
		View contentMain = appBarMain.findViewById(R.id.content_main);
		View contentFrame = contentMain.findViewById(R.id.content_frame);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(contentFrame.getId(), fragment, tag);
		if (addToBackStack) {
			fragmentTransaction.addToBackStack(null);
		}
		fragmentTransaction.commit();
	}

	/**
	 * Navigate the last added page in the back stack.
	 *
	 * @param activity the activity.
	 */
	public static void navigateToBack(Activity activity) {
		FragmentManager fragmentManager = activity.getFragmentManager();
		fragmentManager.popBackStack();
	}

	/**
	 * Navigates to an activity.
	 *
	 * @param context     the context.
	 * @param newActivity the new activity.
	 */
	public static void navigateToActivity(Context context, Activity newActivity) {
		Intent intent = new Intent(context, newActivity.getClass());
		context.startActivity(intent);
	}

	/**
	 * Navigates to new activity, and destroys the old.
	 *
	 * @param context     the context.
	 * @param newActivity the new activity.
	 */
	public static void navigateToActivityWithoutBackStack(Context context, Activity newActivity) {
		Intent intent = new Intent(context, newActivity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}