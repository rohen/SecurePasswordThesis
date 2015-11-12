package hu.bme.alit.wear.securepassword.securepassword.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import hu.bme.alit.wear.securepassword.securepassword.R;
import hu.bme.alit.wear.securepassword.securepassword.helper.DefaultStoreHelper;
import hu.bme.alit.wear.securepassword.securepassword.helper.StoreHelper;

public class ListFragment extends Fragment {

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
        listView.setAdapter(new SubjectAdapter(getActivity(), android.R.layout.simple_list_item_1, storeHelper.getSubjects()));

        return view;
    }

    private class SubjectAdapter extends ArrayAdapter<String> {
        public SubjectAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }
    }
}
