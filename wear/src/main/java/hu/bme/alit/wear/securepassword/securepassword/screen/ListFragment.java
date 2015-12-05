package hu.bme.alit.wear.securepassword.securepassword.screen;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import hu.bme.alit.wear.common.helper.StoreHelper;
import hu.bme.alit.wear.common.utils.NavigationUtils;
import hu.bme.alit.wear.securepassword.securepassword.R;
import hu.bme.alit.wear.securepassword.securepassword.list.PasswordListAdapter;
import hu.bme.alit.wear.securepassword.securepassword.pattern.WearPatternLockUtils;
import me.zhanghai.patternlock.ConfirmPatternActivity;

/**
 * Created by alit on 16/11/2015.
 */
public class ListFragment extends Fragment {

	public final static String FRAGMENT_LIST_PASSWORDS_TAG = "fragment_list_passwords_tag";

	private WearableListView listView;

	private List<String> subjects;

	private StoreHelper storeHelper;

	private PasswordListAdapter passwordListAdapter;

	private int clickedPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_list, container, false);

		storeHelper = ((MainActivity) getActivity()).getStoreHelper();

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
			passwordListAdapter = new PasswordListAdapter(getActivity(), subjects);
			listView.setAdapter(passwordListAdapter);
			listView.setClickListener(getOnClickListener());
		} else {
			List<String> emptyString = new ArrayList<>();
			emptyString.add(getString(R.string.list_passwords_empty_string));
			passwordListAdapter = new PasswordListAdapter(getActivity(), emptyString);
			listView.setAdapter(passwordListAdapter);
		}
		passwordListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			View contentFrame = getActivity().findViewById(R.id.content_frame);
			DetailsFragment detailsFragment = new DetailsFragment();
			Bundle bundle = new Bundle();
			bundle.putString(DetailsFragment.EXTRA_SUBJECT, subjects.get(clickedPosition));
			bundle.putString(DetailsFragment.EXTRA_KEY, data.getStringExtra(ConfirmPatternActivity.EXTRA_KEY_PATTERN_HEXED));
			detailsFragment.setArguments(bundle);
			NavigationUtils.navigateToFragment(getActivity(), contentFrame, detailsFragment, DetailsFragment.FRAGMENT_DETAILS_PASSWORD_TAG, true, false);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public WearableListView.ClickListener getOnClickListener() {
		return new PasswordListClickListener();
	}

	private class PasswordListClickListener implements WearableListView.ClickListener {
		@Override
		public void onClick(WearableListView.ViewHolder viewHolder) {
			WearPatternLockUtils.confirmPattern(getActivity(), ListFragment.this);
			clickedPosition = viewHolder.getLayoutPosition();
		}

		@Override
		public void onTopEmptyRegionClick() {

		}
	}
}
