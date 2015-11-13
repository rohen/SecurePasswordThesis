package hu.bme.alit.wear.securepassword.securepassword.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.alit.wear.securepassword.securepassword.R;
import hu.bme.alit.wear.securepassword.securepassword.helper.DefaultStoreHelper;
import hu.bme.alit.wear.securepassword.securepassword.helper.StoreHelper;

public class ListFragment extends Fragment {

    public final static String FRAGMENT_LIST_PASSWORDS_TAG = "fragment_list_passwords_tag";

    private ListView listView;

    private StoreHelper storeHelper;

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
        List<String> subjects = storeHelper.getSubjects();
        if (!subjects.isEmpty()) {
            listView.setAdapter(new SubjectAdapter(getActivity(), android.R.layout.simple_list_item_1, subjects));
            listView.setOnItemClickListener(getOnItemClickListeners());
            listView.setOnItemLongClickListener(getOnItemClickListeners());
        } else {
            List<String> emptyString = new ArrayList<>();
            emptyString.add(getString(R.string.list_passwords_empty_string));
            listView.setAdapter(new SubjectAdapter(getActivity(), android.R.layout.simple_list_item_1, emptyString));
        }

        return view;
    }

    private ListItemOnClickListeners getOnItemClickListeners() {
        return new ListItemOnClickListeners();
    }

    private class SubjectAdapter extends ArrayAdapter<String> {
        public SubjectAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }
    }

    private class ListItemOnClickListeners implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_show_password, null);
            dialog.setContentView(layout);
            dialog.setCancelable(true);
            //dialog.setOnCancelListener(cancelListener);
            dialog.show();
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            return false;
        }
    }
}
