package se.grace.vivian.traits;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Vivi on 2016-11-06.
 */
public class Router {
    public static final String TRAITS_USER = "TRAITS_USER";

    protected void GoToPersonalitiesActivity(Context context, String username)
    {
        //Go to personalities activity
        Intent intent = new Intent(context, PersonalitiesActivity.class);
        intent.putExtra(TRAITS_USER, "The user was signed in: " + username);
        context.startActivity(intent);

    }

}
