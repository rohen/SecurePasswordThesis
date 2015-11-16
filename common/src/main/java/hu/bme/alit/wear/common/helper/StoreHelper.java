package hu.bme.alit.wear.common.helper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tamasali on 2015.11.12..
 */
public interface StoreHelper {

	boolean addPassword(String subject, String password);

	boolean removePassword(String subject);

	ArrayList<String> getSubjects();

	String getPassword(String subject);

	HashMap<String, String> createMapFromStringArray(String[] stringArray);

	String[] createStringArrayFromData(String subject, String password);
}
