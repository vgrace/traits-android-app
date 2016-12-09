package se.grace.vivian.traits.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import se.grace.vivian.traits.Api;
import se.grace.vivian.traits.Colors;
import se.grace.vivian.traits.Globals;
import se.grace.vivian.traits.KeyStoring;
import se.grace.vivian.traits.R;
import se.grace.vivian.traits.Router;
import se.grace.vivian.traits.SessionManager;
import se.grace.vivian.traits.traits.Personality;
import se.grace.vivian.traits.traits.PersonalityGridItem;
import se.grace.vivian.traits.traits.Trait;
import se.grace.vivian.traits.traits.User;
import se.grace.vivian.traits.traits.UserTraits;
import se.grace.vivian.traits.traits.UserTypePart;

public class PersonalitiesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TRAITS_PERSONALITY = "TRAITS_PERSONALITY";
    public static final String TRAITS_USER_TRAITS = "TRAITS_USER_TRAITS";

    public SessionManager manager;
    public static final String TAG = PersonalitiesActivity.class.getSimpleName();
    private static Api mApi = new Api();
    private KeyStoring mKeyStoring = new KeyStoring();
    private static User mUser;
    private NavigationView navigationView;
    ArrayList<PersonalityGridItem> mPersonalityGridItems = new ArrayList<PersonalityGridItem>();
    private Personality[] mPersonalities = new Personality[16];
    private Router mRouter = new Router();
    private Colors mColors = new Colors();
    private static ArrayList<UserTraits> mUserTraitsList;
    private static  ArrayList<UserTypePart> mUserTypeParts;
    private Globals g = Globals.getInstance();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context mContext = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        manager = new SessionManager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Get User
        Intent intent = getIntent();
        mUser = intent.getParcelableExtra(LoginActivity.TRAITS_USER);
        g.setUsername(mUser.getUsername());
        Log.d(TAG, mUser.getName() + "");

        // Check if user object is available, otherwise get it
        checkUser();

        // Create PersonalityGridItem list
        //mPersonalityGridItems = g.getPersonalityGridItems();
        //if(mPersonalityGridItems == null) {
            try {
                getUserTypeParts(mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        //}
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "ON RESUME!");
        //

        super.onResume();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "ON RESTART");

        mPersonalityGridItems = g.getPersonalityGridItems();
        setGridViewAdapter(this);
        super.onRestart();
    }

    protected void checkUser(){
        if(mUser.getUsername() == null) {
            String status = manager.getPreferences(PersonalitiesActivity.this,"status");

            //if (status.equals("1") && message.length() <= 16) {
            if (status.equals("1")) {
                //Log.d(TAG, "User already signed in!");
                //Get user
                String username = manager.getPreferences(PersonalitiesActivity.this, "username");
                //Log.d(TAG, username);
                String encryptedPassword = manager.getPreferences(PersonalitiesActivity.this, "password");
                //Log.d(TAG, encryptedPassword);
                String decryptedPassword = mKeyStoring.decryptString("TraitsPassword", encryptedPassword);
                //Log.d(TAG, decryptedPassword);

                try {
                    //Log.d(TAG, "Get signed in user info");
                    //postLogin(username, decryptedPassword);
                    mApi.postLogin(username, decryptedPassword, PersonalitiesActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static User getUser(){
        return mUser;
    }

    // Get User Type Parts
    public void getUserTypeParts(final Context context) throws Exception {
        Log.d(TAG, "In Get User Type Parts");
        String url = "http://traits-app-api.herokuapp.com/api/userTypePart/" + mUser.getUsername();
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
                        JSONArray jsonarray = jsonObject.getJSONArray("usertypeparts");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            PersonalityGridItem item = new PersonalityGridItem();
                            String type = jsonobject.getString("personalitytype");
                            item.setType(type);
                            item.setPercentage(jsonobject.getInt("percentage"));
                            item.setTypeColor(mColors.getColorByType(context, type));
                            mPersonalityGridItems.add(item);

                            //UserTypePart utp = new UserTypePart();
                            //utp.setPersonalityType(jsonobject.getString("personalitytype"));
                            //utp.setPercentage(jsonobject.getString("percentage"));
                            //mUserTypeParts.add(utp);
                        }
                        g.setPersonalityGridItems(mPersonalityGridItems);

                        //Get att personalities
                        if(mPersonalityGridItems.size() < 16) {
                            try {
                                getPersonalities(context);
                                Log.d(TAG, mPersonalities.length + "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    //public void setup
    public void setGridViewAdapter(final Context context){
        //Add types to GridView
        GridView gridView = (GridView)findViewById(R.id.gridview);
        mPersonalityGridItems = g.getPersonalityGridItems();
        final PersonalityAdapter personalityAdapter = new PersonalityAdapter(this, mPersonalityGridItems);
        gridView.setAdapter(personalityAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                TraitsActivity.clearTraitsList();
                PersonalityGridItem personalityGridItem = mPersonalityGridItems.get(position);
                Log.d(TAG, personalityGridItem.getType() + " was clicked!");
                g.setSelectedType(getPersonalityByType(personalityGridItem.getType()));
                personalityAdapter.notifyDataSetChanged();
                mRouter.GoToTraitsActivity(context);
            }
        });
    }

    public PersonalityGridItem getPersonalityByType(String type){
        for(int i = 0; i < mPersonalityGridItems.size(); i++){
            if(mPersonalityGridItems.get(i).getType().equals(type)){
                return mPersonalityGridItems.get(i);
            }
        }
        return null;
    }

    public void getPersonalities(final Context context) throws Exception {
        Log.d(TAG, "In Get Personalities");
        String url = "http://traits-app-api.herokuapp.com/api/personality";
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

                        JSONArray jsonarray = new JSONArray(responseStr);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            Personality p = new Personality();
                            p.setType(jsonobject.getString("type"));
                            p.setTotalFrequency(jsonobject.getString("totalfrequency"));
                            p.setMaleFrequency(jsonobject.getString("malefrequency"));
                            p.setFemaleFrequency(jsonobject.getString("femalefrequency"));
                            p.setDescription(jsonobject.getString("description"));
                            p.setTypeFull(jsonobject.getString("typefull"));
                            //p.setTraits(parseUserTraits(jsonobject));
                            mPersonalities[i] = p;
                        }

                        // Update grid with missing personalities
                        if(mPersonalityGridItems.size() < 16){

                            for(int i = 0; i<mPersonalities.length; i++){
                                PersonalityGridItem pgi = new PersonalityGridItem();
                                String pType = mPersonalities[i].getType();
                                if(!typeExistsInPersonalityGridList(pType)){
                                    pgi.setType(pType);
                                    pgi.setPercentage(0);
                                    pgi.setTypeColor(mColors.getColorByType(context, pType));
                                    mPersonalityGridItems.add(pgi);
                                }
                            }
                        }
                        Log.d(TAG, "Personality grid items after add: " + mPersonalityGridItems.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Run in main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setGridViewAdapter(context);
                            // Setup menu drawer
                            Log.d(TAG, "Setting username in menu");
                            setMenuUsername(mUser.getUsername());
                            setMenuType(mPersonalityGridItems.get(0).getType()); //Already sorted
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

    private boolean typeExistsInPersonalityGridList(String type){
        Boolean typeExists = false;
        for(int j = 0; j<mPersonalityGridItems.size(); j++){
            String gType = mPersonalityGridItems.get(j).getType();
            if(type.trim().equals(gType.trim())){
                return true;
            }
        }
        return typeExists;
    }

    private ArrayList<Trait> parseUserTraits(JSONObject personalityJson) throws JSONException{
        JSONArray traitsJson = personalityJson.getJSONArray("traits");
        ArrayList<Trait> traits = new ArrayList<Trait>();
        for(int i = 0; i<traitsJson.length(); i++){
            Trait t = new Trait();
            JSONObject jsonTrait = traitsJson.getJSONObject(i);
            t.setTrait(jsonTrait.getString("trait"));
            t.setWeight(jsonTrait.getInt("weight"));
        }
        return traits;
    }

    public static void updateUserTypePart(String type, int typeScore){
        try {
            putUserTypePart(mUser.getUsername(), type, typeScore);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void putUserTypePart(String username, String type, int percentage) throws Exception {
        Log.d(TAG, "IN API!! " + percentage);
        String url = "http://traits-app-api.herokuapp.com/api/userTypePart/" + username;

        String bodyJson = "{\n" +
                "  \"personalitytype\": \"" + type + "\",\n" +
                "  \"percentage\": \"" + percentage + "\",\n" +
                "  \"lastupdate\": \"\"\n" +
                "}";
        Log.d(TAG, bodyJson);
        mApi.put(url, bodyJson, new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // Something went wrong
                if(e.getMessage() != null)
                    Log.d(TAG, e.getMessage());
                else
                    Log.d(TAG, "Failed to call PUT");
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseStr = response.body().string();
                    Log.d(TAG, responseStr);

                } else {
                    // Request not successful
                    Log.d(TAG, "Request not successful: "  +response.code() +" : "+ response.message());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.personalities, menu);

        return true;
    }
    public void setMenuUsername(String username){
        View header=navigationView.getHeaderView(0);
        TextView navUsernameTextView = (TextView)header.findViewById(R.id.navTextView);
        if(navUsernameTextView != null)
        {
            navUsernameTextView.setText(username);
        }
    }

    public void setMenuType(String type){
        View header=navigationView.getHeaderView(0);
        LinearLayout headerLayout = (LinearLayout) header.findViewById(R.id.navHeadLayout);
        //headerLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
        headerLayout.setBackgroundColor(mColors.getColorByType(this, type));
        TextView navTypeTextView = (TextView)header.findViewById(R.id.navTypeTextView);
        if(navTypeTextView != null)
        {
            navTypeTextView.setText(type);
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        if(id == R.id.logout){

            manager.setPreferences(PersonalitiesActivity.this, "status", "0");
            // Remove stored preferences
            manager.removePreferences(PersonalitiesActivity.this, "username");
            manager.removePreferences(PersonalitiesActivity.this, "password");

            //Go to sign in activity
            Intent intent = new Intent(PersonalitiesActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
