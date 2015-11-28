package hu.bme.alit.wear.securepassword.securepassword.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bme.alit.wear.securepassword.securepassword.R;

public class GreetingsFragment extends Fragment {

	public final static String FRAGMENT_GREETINGS_TAG = "fragment_greetings_tag";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_greetings, container, false);
	}

	private void setTitle() {
		ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (actionBar != null) {
			actionBar.setTitle(R.string.greetings_fragment_title);
		}
	}
}
