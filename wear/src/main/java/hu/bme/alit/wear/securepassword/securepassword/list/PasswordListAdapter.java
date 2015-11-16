package hu.bme.alit.wear.securepassword.securepassword.list;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hu.bme.alit.wear.securepassword.securepassword.R;

/**
 * Created by alit on 15/11/2015.
 */
public class PasswordListAdapter extends WearableListView.Adapter {

	private final Context context;
	private final List<String> items;

	public PasswordListAdapter(Context context, List<String> items) {
		this.context = context;
		this.items = items;
	}

	@Override
	public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		return new WearableListView.ViewHolder(new PasswordItemView(context));
	}

	@Override
	public void onBindViewHolder(WearableListView.ViewHolder viewHolder, final int position) {
		PasswordItemView passwordItemView = (PasswordItemView) viewHolder.itemView;
		final String item = items.get(position);

		TextView textView = (TextView) passwordItemView.findViewById(R.id.password_text);
		textView.setText(item);
	}

	@Override
	public int getItemCount() {
		return items.size();
	}
}
