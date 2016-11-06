package se.grace.vivian.traits;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import se.grace.vivian.traits.traits.User;
import se.grace.vivian.traits.ui.PersonalitiesActivity;

/**
 * Created by Vivi on 2016-11-06.
 */
public class Router {
    public static final String TRAITS_USER = "TRAITS_USER";

    public void GoToPersonalitiesActivity(Context context, User user)
    {
        //Go to personalities activity
        Intent intent = new Intent(context, PersonalitiesActivity.class);
        intent.putExtra(TRAITS_USER, (Parcelable) user);
        context.startActivity(intent);

    }

}
