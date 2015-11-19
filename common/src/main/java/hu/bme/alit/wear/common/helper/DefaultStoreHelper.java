package hu.bme.alit.wear.common.helper;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by tamasali on 2015.11.12..
 */
public class DefaultStoreHelper implements StoreHelper {

	private static final String LOG_TAG = "SECURE_LOG";

	private static final String FILE_NAME = "SPW";

	private Context context;

	private HashMap<String, String> passwordMap;

	public DefaultStoreHelper(Context context) {
		this.context = context;
		loadData();
	}

	@Override
	public boolean addPassword(String subject, String password) {
		return passwordMap.put(subject, password) == null && saveData();
	}

	@Override
	public boolean removePassword(String subject) {
		return passwordMap.remove(subject) == null && saveData();
	}

	@Override
	public ArrayList<String> getSubjects() {
		loadData();
		Set<String> subjectSet = passwordMap.keySet();
		return new ArrayList<>(subjectSet);
	}

	@Override
	public String getPassword(String subject) {
		return passwordMap.get(subject);
	}

	@Override
	public HashMap<String, String> createMapFromStringArray(String[] stringArray) {
		HashMap<String, String> newMap = new HashMap<>();
		newMap.put(stringArray[0], stringArray[1]);
		return newMap;
	}

	@Override
	public String[] createStringArrayFromData(String subject, String password) {
		String[] newStringArray = new String[2];
		newStringArray[0] = subject;
		newStringArray[1] = password;
		return newStringArray;
	}

	private boolean loadData() {
		try {
			FileInputStream fileInputStream = context.openFileInput(FILE_NAME);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			passwordMap = (HashMap<String, String>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (FileNotFoundException e) {
			Log.d(LOG_TAG, getClass().getName() + ": No file exist, creating a new.");
			passwordMap = new HashMap<>();
		} catch (Exception e) {
			Log.d(LOG_TAG, getClass().getName() + ": Error while loading stored data. Error: " + e.getMessage());
			return false;
		}
		return passwordMap != null;
	}

	private boolean saveData() {
		try {
			FileOutputStream fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(passwordMap);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (IOException e) {
			Log.d(LOG_TAG, getClass().getName() + ": Error while storing data. Error: " + e.getMessage());
			return false;
		}
		return true;
	}
}
