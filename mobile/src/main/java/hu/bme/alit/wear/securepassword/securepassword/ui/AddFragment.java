package hu.bme.alit.wear.securepassword.securepassword.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import hu.bme.alit.wear.securepassword.securepassword.R;
import hu.bme.alit.wear.securepassword.securepassword.helper.DefaultStoreHelper;
import hu.bme.alit.wear.securepassword.securepassword.helper.StoreHelper;

public class AddFragment extends Fragment {

    private EditText subjectEditText;
    private EditText passwordEditText;
    private Button submitButton;

    private StoreHelper storeHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storeHelper = new DefaultStoreHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewContainer = inflater.inflate(R.layout.fragment_add_password, container, false);

        subjectEditText = (EditText) viewContainer.findViewById(R.id.input_subject);
        passwordEditText = (EditText) viewContainer.findViewById(R.id.input_value);
        submitButton = (Button) viewContainer.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(getSubmitButtonOnClickListener());

        return viewContainer;
    }

    private View.OnClickListener getSubmitButtonOnClickListener() {
        return new SubmitButtonOnClickListener();
    }

    private class SubmitButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String subject = subjectEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (subject.equals("") || password.equals("")) {
                return;
            }
            storeHelper.addPassword(subject, password);
        }
    }

}
