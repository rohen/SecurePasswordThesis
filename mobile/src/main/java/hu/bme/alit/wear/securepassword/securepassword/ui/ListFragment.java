package hu.bme.alit.wear.securepassword.securepassword.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hu.bme.alit.wear.securepassword.securepassword.R;
import hu.bme.alit.wear.securepassword.securepassword.helper.DefaultStoreHelper;
import hu.bme.alit.wear.securepassword.securepassword.helper.DefaultTimerHelper;
import hu.bme.alit.wear.securepassword.securepassword.helper.StoreHelper;
import hu.bme.alit.wear.securepassword.securepassword.helper.TimerHelper;

public class ListFragment extends Fragment implements TimerHelper.TimerCallBack {

    public final static String FRAGMENT_LIST_PASSWORDS_TAG = "fragment_list_passwords_tag";
    public final static int MAX_NUMBER_OF_TICK = 100;
    public final static int TICK_PERIOD = 100;

    private ListView listView;
    private List<String> subjects;
    private ProgressBar progressBar;

    private StoreHelper storeHelper;

    private Dialog dialog;

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
            listView.setAdapter(new SubjectAdapter(getActivity(), android.R.layout.simple_list_item_1, subjects));
            listView.setOnItemClickListener(getOnItemClickListeners());
            listView.setOnItemLongClickListener(getOnItemClickListeners());
            getActivity().registerForContextMenu(listView);
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

        dialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_show_password, null);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                timerHelper.stopTimerTask();
            }
        });
        dialog.show();

        progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        TextView passwordText = (TextView) layout.findViewById(R.id.password);

        passwordText.setText(storeHelper.getPassword(subjects.get(position)));

        timerHelper.startTimer();
    }

    @Override
    public void timerTick(int tickNumber) {
        progressBar.setProgress(tickNumber);
    }

    @Override
    public void timerStop() {
        dialog.hide();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Action 1");
        menu.add(0, v.getId(), 0, "Action 2");
        menu.add(0, v.getId(), 0, "Action 3");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Action 1") {
            Toast.makeText(getActivity(), "Action 1 invoked", Toast.LENGTH_SHORT).show();
        } else if (item.getTitle() == "Action 2") {
            Toast.makeText(getActivity(), "Action 2 invoked", Toast.LENGTH_SHORT).show();
        } else if (item.getTitle() == "Action 3") {
            Toast.makeText(getActivity(), "Action 3 invoked", Toast.LENGTH_SHORT).show();
        } else {
            return false;
        }
        return true;
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
            getActivity().openContextMenu(getView());
            return true;
        }
    }

    private class SubjectAdapter extends ArrayAdapter<String> {
        public SubjectAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }
    }
}
