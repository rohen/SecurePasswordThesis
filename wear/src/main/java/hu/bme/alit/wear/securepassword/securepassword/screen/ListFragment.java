package hu.bme.alit.wear.securepassword.securepassword.screen;

import android.app.Fragment;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import hu.bme.alit.wear.common.helper.StoreHelper;
import hu.bme.alit.wear.common.helper.WearSyncHelper;
import hu.bme.alit.wear.common.utils.NavigationUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;
import hu.bme.alit.wear.securepassword.securepassword.list.PasswordListAdapter;

/**
 * Created by alit on 16/11/2015.
 */
public class ListFragment extends Fragment {

	public final static String FRAGMENT_LIST_PASSWORDS_TAG = "fragment_list_passwords_tag";

	private WearableListView listView;

	private List<String> subjects;

	private StoreHelper storeHelper;
	private WearSyncHelper wearSyncHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_list, container, false);

		storeHelper = ((MainActivity) getActivity()).getStoreHelper();
		wearSyncHelper = ((MainActivity) getActivity()).getWearSyncHelper();

		final WatchViewStub stub = (WatchViewStub) view.findViewById(R.id.watch_view_stub);
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub) {
				listView = (WearableListView) stub.findViewById(R.id.password_list);
				refreshListItems();
			}
		});

		return view;
	}

	public void refreshListItems() {
		subjects = storeHelper.getSubjects();

		if (!subjects.isEmpty()) {
			listView.setAdapter(new PasswordListAdapter(getActivity(), subjects));
			listView.setClickListener(getOnClickListener());
		} else {
			List<String> emptyString = new ArrayList<>();
			emptyString.add(getString(R.string.list_passwords_empty_string));
			listView.setAdapter(new PasswordListAdapter(getActivity(), emptyString));
		}
	}

	public WearableListView.ClickListener getOnClickListener() {
		return new PasswordListClickListener();
	}

	private class PasswordListClickListener implements WearableListView.ClickListener {
		@Override
		public void onClick(WearableListView.ViewHolder viewHolder) {
			View contentFrame = getActivity().findViewById(R.id.content_frame);
			DetailsFragment detailsFragment = new DetailsFragment();
			Bundle bundle = new Bundle();
			bundle.putString(DetailsFragment.EXTRA_SUBJECT, subjects.get(viewHolder.getLayoutPosition()));
			detailsFragment.setArguments(bundle);
			NavigationUtils.navigateToFragment(getActivity(), contentFrame, detailsFragment, DetailsFragment.FRAGMENT_DETAILS_PASSWORD_TAG, true, true);
		}

		@Override
		public void onTopEmptyRegionClick() {

		}
	}
}
