package de.fs.fintech.geogame.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

/**
 * Created by axel on 28.05.17.
 */
public class GeoFirebaseStructure {

    private final String email;
    private final String pseudonym;

    public GeoFirebaseStructure(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        email = prefs.getString("google.plus.email", null);
        pseudonym = email.replace('.', '°');
    }
    @NonNull
    public String getUniquePortalId(String portalId) {
        return "geogame/users/" + pseudonym + "/uniques/" + portalId;
    }

    @NonNull
    public String getPortalInfo(String portalId) {
        return "geogame/portals/" + portalId;
    }


}
