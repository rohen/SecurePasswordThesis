package hu.bme.alit.wear.securepassword.securepassword.helper;

import java.util.ArrayList;

/**
 * Created by tamasali on 2015.11.12..
 */
public interface StoreHelper {

    boolean addPassword(String subject, String passwor);

    boolean removePassword(String subject);

    ArrayList<String> getSubjects();

    String getPassword(String subject);

}
