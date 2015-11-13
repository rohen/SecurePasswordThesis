package hu.bme.alit.wear.securepassword.securepassword.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import hu.bme.alit.wear.securepassword.securepassword.R;
import hu.bme.alit.wear.securepassword.securepassword.helper.DefaultStoreHelper;
import hu.bme.alit.wear.securepassword.securepassword.helper.StoreHelper;
import hu.bme.alit.wear.securepassword.securepassword.utils.NavigationUtils;

public class AddFragment extends Fragment {

    public final static String FRAGMENT_ADD_PASSWORD_TAG = "fragment_add_password_tag";

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

    @Override
    public void onPause() {
        super.onPause();

        hideKeyboard();


    }

    private View.OnClickListener getSubmitButtonOnClickListener() {
        return new SubmitButtonOnClickListener();
    }

    private void showSnackBarMessage(String message) {
        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(R.id.app_bar_main), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private class SubmitButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            hideKeyboard();

            String subject = subjectEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (!subject.equals("") && !password.equals("") && storeHelper.addPassword(subject, password)) {
                showSnackBarMessage(getString(R.string.add_password_store_success));
                NavigationUtils.navigateToBack(getActivity());
            } else {
                showSnackBarMessage(getString(R.string.add_password_store_failed));
            }
        }
    }
}
