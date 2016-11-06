package se.grace.vivian.traits;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Vivi on 2016-11-06.
 */
public class SessionManager {
    private static final String TRAITS = "TRAITS";
    //method to save status
    public void setPreferences(Context context, String key, String value) {

        SharedPreferences.Editor editor = context.getSharedPreferences(TRAITS, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();

    }

    public void removePreferences(Context context, String key)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(TRAITS, Context.MODE_PRIVATE).edit();
        editor.remove(key);
        editor.commit();
    }

    public String getPreferences(Context context, String key) {

        SharedPreferences prefs = context.getSharedPreferences(TRAITS, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }
}
