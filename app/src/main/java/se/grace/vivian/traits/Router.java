package se.grace.vivian.traits;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

import se.grace.vivian.traits.traits.Personality;
import se.grace.vivian.traits.traits.PersonalityGridItem;
import se.grace.vivian.traits.traits.User;
import se.grace.vivian.traits.traits.UserTraits;
import se.grace.vivian.traits.ui.PersonalitiesActivity;
import se.grace.vivian.traits.ui.TraitsActivity;

/**
 * Created by Vivi on 2016-11-06.
 */
public class Router {
    public static final String TRAITS_USER = "TRAITS_USER";
    public static final String TRAITS_PERSONALITY = "TRAITS_PERSONALITY";
    public static final String TRAITS_USER_TRAITS = "TRAITS_USER_TRAITS";
    public static final String TAG = Router.class.getSimpleName();

    public void GoToPersonalitiesActivity(Context context, User user)
    {
        //Go to personalities activity
        Intent intent = new Intent(context, PersonalitiesActivity.class);
        intent.putExtra(TRAITS_USER, (Parcelable) user);
        context.startActivity(intent);

    }

    public void GoToTraitsActivity(Context context, PersonalityGridItem personalityGridItem){
        Log.d(TAG, personalityGridItem.getType());
        //Go to traits activity
        Intent intent = new Intent(context, TraitsActivity.class);
        intent.putExtra(TRAITS_PERSONALITY, personalityGridItem);
        //intent.putExtra(TRAITS_USER_TRAITS, userTraits);
        context.startActivity(intent);
    }

}
