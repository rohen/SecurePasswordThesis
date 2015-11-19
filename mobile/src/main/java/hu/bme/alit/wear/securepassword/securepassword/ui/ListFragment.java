package hu.bme.alit.wear.securepassword.securepassword.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.bme.alit.wear.common.SharedData;
import hu.bme.alit.wear.common.helper.DefaultStoreHelper;
import hu.bme.alit.wear.common.helper.DefaultTimerHelper;
import hu.bme.alit.wear.common.helper.StoreHelper;
import hu.bme.alit.wear.common.helper.TimerHelper;
import hu.bme.alit.wear.common.helper.WearSyncHelper;
import hu.bme.alit.wear.securepassword.securepassword.R;

public class ListFragment extends Fragment implements TimerHelper.TimerCallBack {

	public final static String FRAGMENT_LIST_PASSWORDS_TAG = "fragment_list_passwords_tag";

	public final static int MAX_NUMBER_OF_TICK = 100;
	public final static int TICK_PERIOD = 100;

	private ListView listView;
	private List<String> subjects;
	private ProgressBar progressBar;
	private SubjectAdapter listAdapter;

	private StoreHelper storeHelper;

	private Dialog showPasswordDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		storeHelper = new DefaultStoreHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_list_password, container, false);
		listView = (ListView) view.findViewById(android.R.id.list);
		subjects = storeHelper.getSubjects();

		if (!subjects.isEmpty()) {
			listAdapter = new SubjectAdapter(getActivity(), android.R.layout.simple_list_item_1, subjects);
			listView.setAdapter(listAdapter);
			listView.setOnItemClickListener(getOnItemClickListeners());
			listView.setOnItemLongClickListener(getOnItemClickListeners());
		} else {
			List<String> emptyString = new ArrayList<>();
			emptyString.add(getString(R.string.list_passwords_empty_string));
			listView.setAdapter(new SubjectAdapter(getActivity(), android.R.layout.simple_list_item_1, emptyString));
		}

		return view;
	}

	private void createDialog(int position) {

		final TimerHelper timerHelper = new DefaultTimerHelper();
		timerHelper.initializeTimerTask(this, MAX_NUMBER_OF_TICK, TICK_PERIOD);

		showPasswordDialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
		showPasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_show_password, null);
		showPasswordDialog.setContentView(layout);
		showPasswordDialog.setCancelable(true);
		showPasswordDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				timerHelper.stopTimerTask();
			}
		});
		showPasswordDialog.show();

		progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
		TextView passwordText = (TextView) layout.findViewById(R.id.password);

		passwordText.setText(storeHelper.getPassword(subjects.get(position)));

		timerHelper.startTimer();
	}

	private void createContextMenu(final int itemPosition) {

		final Dialog contextDialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
		contextDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_context_menu, null);
		contextDialog.setContentView(layout);
		contextDialog.setCancelable(true);
		contextDialog.show();

		ListView contextList = (ListView) layout.findViewById(R.id.context_list_view);
		List<String> itemList = Arrays.asList(getResources().getStringArray(R.array.context_menu_items));
		contextList.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, itemList));
		contextList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
					case 0:
						String removableSubject = subjects.get(itemPosition);
						boolean isRemoved = storeHelper.removePassword(removableSubject);
						if (isRemoved) {
							subjects.remove(itemPosition);
							listAdapter.notifyDataSetChanged();
							sendMessageToWear(removableSubject);
							break;
						}
				}
				contextDialog.hide();
			}
		});
	}

	@Override
	public void timerTick(int tickNumber) {
		progressBar.setProgress(tickNumber);
	}

	@Override
	public void timerStop() {
		showPasswordDialog.hide();
	}

	private ListItemOnClickListeners getOnItemClickListeners() {
		return new ListItemOnClickListeners();
	}

	private class ListItemOnClickListeners implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			createDialog(position);
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			createContextMenu(position);
			return true;
		}
	}

	private class SubjectAdapter extends ArrayAdapter<String> {
		public SubjectAdapter(Context context, int resource, List<String> objects) {
			super(context, resource, objects);
		}
	}

	private void sendMessageToWear(String subject) {
		final WearSyncHelper wearSyncHelper = ((MainActivity) getActivity()).getWearSyncHelper();
		DataMap newPassword = new DataMap();
		newPassword.putString(SharedData.SEND_DATA, subject);
		wearSyncHelper.sendData(SharedData.REQUEST_PATH_REMOVED_DATA, newPassword);
	}
}
