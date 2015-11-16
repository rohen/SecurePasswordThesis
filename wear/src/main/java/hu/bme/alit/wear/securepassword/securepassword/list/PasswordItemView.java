package hu.bme.alit.wear.securepassword.securepassword.list;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import hu.bme.alit.wear.securepassword.securepassword.R;

/**
 * Created by alit on 15/11/2015.
 */
public class PasswordItemView extends FrameLayout implements WearableListView.OnCenterProximityListener {

	final TextView text;

	public PasswordItemView(Context context) {
		super(context);
		View.inflate(context, R.layout.wearablelistview_item, this);
		text = (TextView) findViewById(R.id.password_text);

	}


	@Override
	public void onCenterPosition(boolean b) {
		text.animate().scaleX(1f).scaleY(1f).alpha(1);

	}

	@Override
	public void onNonCenterPosition(boolean b) {
		text.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);

	}
}
