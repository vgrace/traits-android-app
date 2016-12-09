package se.grace.vivian.traits.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import se.grace.vivian.traits.Api;
import se.grace.vivian.traits.Colors;
import se.grace.vivian.traits.Globals;
import se.grace.vivian.traits.R;
import se.grace.vivian.traits.traits.Personality;
import se.grace.vivian.traits.traits.PersonalityGridItem;
import se.grace.vivian.traits.traits.Trait;
import se.grace.vivian.traits.traits.User;
import se.grace.vivian.traits.traits.UserTraits;

public class TraitsActivity extends AppCompatActivity {

    public static final String TAG = TraitsActivity.class.getSimpleName();
    public Colors mColors = new Colors();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static PersonalityGridItem mPersonality;
    //private static String[] mUserTraits;
    private static UserTraits userTraits = new UserTraits();
    static ArrayList<Trait> mUserTraitList = new ArrayList<Trait>();
    static ArrayList<Trait> mPersonalityTraitList = new ArrayList<Trait>();
    private static int personalityScore;
    private static int personalityColor;
    private static Api mApi = new Api();
    //private static User mUser;
    private Globals g = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "TRAITS ACTIVITY CREATED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traits);
        //Context mContext = this;
        // Get User
        //mUser = PersonalitiesActivity.getUser();

        // Get Personality
        Intent intent = getIntent();
        mPersonality = g.getSelectedType(); //intent.getParcelableExtra(PersonalitiesActivity.TRAITS_PERSONALITY);

        Log.d(TAG, mPersonality.getType() + "");
        Log.d(TAG, mPersonality.getPercentage() + "");
        String selectedPersonality = mPersonality.getType();
        personalityScore = mPersonality.getPercentage();
        personalityColor = mPersonality.getTypeColor();

        //Get User Traits
        try {
            getUserTraits(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //ArrayList<UserTraits> mAllUserTraits = intent.getParcelableArrayListExtra(PersonalitiesActivity.TRAITS_USER_TRAITS);

//        // Find selected user personality traits
//        for(int i = 0; i<mAllUserTraits.size(); i++){
//            UserTraits ut = mAllUserTraits.get(i);
//            if(ut.getPersonalityType().equals(selectedPersonality)){
//                mUserTraits = ut.getTraits();
//                break;
//            }
//        }

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = this.getSupportActionBar();
        bar.setTitle(selectedPersonality);
        int pColor = mColors.getColorByType(this, selectedPersonality);
        bar.setBackgroundDrawable(new ColorDrawable(pColor));

        // Tab layout
        TabLayout tabLayout =
                (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setBackgroundColor(pColor);
        tabLayout.addTab(tabLayout.newTab().setText("Traits"));
        tabLayout.addTab(tabLayout.newTab().setText("Stories"));
        tabLayout.addTab(tabLayout.newTab().setText("People"));
        tabLayout.addTab(tabLayout.newTab().setText("Info"));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mViewPager = (ViewPager) findViewById(R.id.container);
        //mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        final PagerAdapter adapter = new TabPagerAdapter
                (getSupportFragmentManager(),
                        tabLayout.getTabCount());
        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "Tab changed");
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    // Get User Type Parts
    public void getUserTraits(final Context context) throws Exception {
        String url = "http://traits-app-api.herokuapp.com/api/usertraits/" + g.getUsername() + "/" + mPersonality.getType() + "";
        mApi.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Something went wrong
                if(e.getMessage() != null)
                    Log.d(TAG, e.getMessage());
                else
                    Log.d(TAG, "Failed to call GET");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseStr = response.body().string();
                    Log.d(TAG, responseStr);
                    try {
                        JSONObject jsonObject = new JSONObject(responseStr);
                        JSONArray jsonarray = jsonObject.getJSONArray("traits");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            Trait t = new Trait();
                            String userT = jsonarray.getString(i);
                            t.setTrait(userT);
                            t.setType(jsonObject.getString("personalitytype"));
                            t.setUser(true);
                            mUserTraitList.add(t);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Run in main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
                else {
                    // Request not successful
                    Log.d(TAG, "Request not successful: "  +response.code() +" : "+ response.message());
                }
            }
        });
    }

    public static PersonalityGridItem getPersonality(){
        return mPersonality;
    }
    public static void clearTraitsList(){
        mUserTraitList = new ArrayList<Trait>();
    }
    public static void updateUserTraitsList(Trait userTrait){
        mUserTraitList.add(userTrait);
    }
    public static ArrayList<Trait> getUserTraitsList(){
        // Loop user selected personality traits

        //        for(int j = 0; j<mUserTraits.length; j++){
        //            Log.d(TAG, mUserTraits[j] + "");
        //            Trait t = new Trait();
        //            t.setType(mPersonality.getType());
        //            t.setTrait(mUserTraits[j]);
        //            t.setUser(true);
        ////            if(mTraitList.indexOf(t) != -1){
        ////                // Set to user trait true
        ////                Log.d(TAG, "This is a user trait");
        ////                Trait pt = mTraitList.get(0);
        ////                pt.setUser(true);
        ////            }
        //            mUserTraitList.add(t);
        //        }

        return mUserTraitList;
    }

    public static int getPersonalityScore(){
        return personalityScore;
    }

    public static int getPersonalityColor(){
        return personalityColor;
    }

//    public void getPersonalityTraits(String type) throws Exception {
//        String url = "http://traits-app-api.herokuapp.com/api/trait/" + type;
//        mApi.get(url, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // Something went wrong
//                if(e.getMessage() != null)
//                    Log.d(TAG, e.getMessage());
//                else
//                    Log.d(TAG, "Failed to call GET");
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    final String responseStr = response.body().string();
//                    Log.d(TAG, responseStr);
//                    try {
//                        // Personality traits
//                        JSONArray jsonarray = new JSONArray(responseStr);
//                        for (int i = 0; i < jsonarray.length(); i++) {
//                            JSONObject jsonobject = jsonarray.getJSONObject(i);
//                            Trait t = new Trait();
//                            t.setType(jsonobject.getString("type"));
//                            t.setTrait(jsonobject.getString("trait"));
//                            t.setWeight(jsonobject.getInt("weight"));
//                            t.setUser(false);
//                            mPersonalityTraitList.add(t);
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    //Run in main thread
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d(TAG, "setupTraitsGrid");
//                            TraitsTabFragment.setupTraitsGrid(mPersonalityTraitList);
//                        }
//                    });
//                }
//                else {
//                    // Request not successful
//                    Log.d(TAG, "Request not successful: "  +response.code() +" : "+ response.message());
//                }
//            }
//        });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_traits, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String TRAITS_LIST = "TRAITS_LIST";
        private static final String PERSONALITY_SCORE = "PERSONALITY_SCORE";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_traits, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
