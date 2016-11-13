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
import se.grace.vivian.traits.KeyStoring;
import se.grace.vivian.traits.R;
import se.grace.vivian.traits.SessionManager;
import se.grace.vivian.traits.traits.Personality;
import se.grace.vivian.traits.traits.PersonalityGridItem;
import se.grace.vivian.traits.traits.Trait;
import se.grace.vivian.traits.traits.User;
import se.grace.vivian.traits.traits.UserTypePart;

public class PersonalitiesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public SessionManager manager;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    public static final String TAG = PersonalitiesActivity.class.getSimpleName();
    private Api mApi = new Api();
    private KeyStoring mKeyStoring = new KeyStoring();
    private User mUser;
    private NavigationView navigationView;
    final ArrayList<PersonalityGridItem> mPersonalityGridItems = new ArrayList<PersonalityGridItem>();
    private Personality[] mPersonalities = new Personality[16];
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        manager = new SessionManager();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Intent intent = getIntent();
        //String message = intent.getStringExtra(LoginActivity.TRAITS_USER);
        mUser = intent.getParcelableExtra(LoginActivity.TRAITS_USER);  //getParcelableArrayExtra(LoginActivity.TRAITS_USER);
        //mUser = Parcel.obtain() .copyOf(parcelables, User.class); //Arrays.copyOf(parcelables, parcelables.length, User.class);
        Log.d(TAG, mUser.getName() + "");
        Toast.makeText(this, mUser.getName(), Toast.LENGTH_LONG).show();

        if(mUser.getUserTypeParts() != null && mUser.getUserTypeParts().size() > 0){

            //Get User type parts
            Collections.sort(mUser.getUserTypeParts(), new UserTypePart());
            Log.d(TAG, "User Type parts: " + "After sort");
            Log.d(TAG, "User Type parts: " + mUser.getUserTypeParts().size());
            Log.d(TAG, "User Type parts: " + mUser.getUserTypeParts().get(0).getPersonalityType());

            // Get user type parts
            ArrayList<UserTypePart> mUserTypeParts = mUser.getUserTypeParts();
            for(int i = 0; i< mUserTypeParts.size(); i++)
            {
                PersonalityGridItem item = new PersonalityGridItem();
                item.setType(mUserTypeParts.get(i).getPersonalityType());
                item.setPercentage(Integer.parseInt(mUserTypeParts.get(i).getPercentage()));
                item.setTypeColor(getColorByType(this, mUserTypeParts.get(i).getPersonalityType()));
                mPersonalityGridItems.add(item);
            }

            //Get att personalities
            if(mPersonalityGridItems.size() < 16) {
                try {
                    getPersonalities(this);
                    Log.d(TAG, mPersonalities.length + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Add types to GridView
            GridView gridView = (GridView)findViewById(R.id.gridview);
            final PersonalityAdapter personalityAdapter = new PersonalityAdapter(this, mPersonalityGridItems);
            gridView.setAdapter(personalityAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    PersonalityGridItem personalityGridItem = mPersonalityGridItems.get(position);
                    Log.d(TAG, personalityGridItem.getType() + " was clicked!");
                    personalityAdapter.notifyDataSetChanged();
                }
            });
        }

        if(mUser.getUserTraits() != null && mUser.getUserTraits().size() > 0){
            Log.d(TAG, "User Traits: " + mUser.getUserTraits().size());
            Log.d(TAG, "User Traits: " + mUser.getUserTraits().get(0).getPersonalityType());
        }


        if(mUser.getUsername() == null) {
            String status = manager.getPreferences(PersonalitiesActivity.this,"status");

            //if (status.equals("1") && message.length() <= 16) {
            if (status.equals("1")) {
                Log.d(TAG, "User already signed in!");
                //Get user
                String username = manager.getPreferences(PersonalitiesActivity.this, "username");
                Log.d(TAG, username);
                String encryptedPassword = manager.getPreferences(PersonalitiesActivity.this, "password");
                Log.d(TAG, encryptedPassword);
                String decryptedPassword = mKeyStoring.decryptString("TraitsPassword", encryptedPassword);
                Log.d(TAG, decryptedPassword);

                try {
                    Log.d(TAG, "Get signed in user info");
                    //postLogin(username, decryptedPassword);
                    mApi.postLogin(username, decryptedPassword, PersonalitiesActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.d(TAG, "status: " + status);
            }
        }
        else
        {
            Log.d(TAG, "Setting username in menu");
            setMenuUsername(mUser.getUsername());
            setMenuType(mUser.getUserTypeParts().get(0).getPersonalityType()); //Already sorted
        }


    }

    protected okhttp3.Call get(String url, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        okhttp3.Call call = client.newCall(request); //.execute();
        call.enqueue(callback);
        return call;
    }

    public void getPersonalities(final Context context) throws Exception {
        Log.d(TAG, "In Get Personalities");
        String url = "http://traits-app-api.herokuapp.com/api/personality";
        get(url, new Callback() {
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
                            p.setTraits(parseUserTraits(jsonobject));
                            mPersonalities[i] = p;
                        }

                        // Update grid with missing personalities
                        if(mPersonalityGridItems.size() < 16){

                            for(int i = 0; i<mPersonalities.length; i++){
                                PersonalityGridItem pgi = new PersonalityGridItem();
                                String pType = mPersonalities[i].getType();
                                if(!typeExistsInPersonalityGridList(pType)){
                                    //Log.d(TAG, "Add: " + pType);
                                    pgi.setType(pType);
                                    pgi.setPercentage(0);
                                    pgi.setTypeColor(getColorByType(context, pType));
                                    mPersonalityGridItems.add(pgi);
                                }
                                else{
                                    //Log.d(TAG, "Not add: " + pType);
                                }
                            }
                        }
                        Log.d(TAG, "Personality grid items after add: " + mPersonalityGridItems.size());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Run in main thread
                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GoToPersonalitiesActivity(responseStr);

                        }
                    });*/
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
            Log.d(TAG, type + "==" + gType);
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
        headerLayout.setBackgroundColor(getColorByType(this, type));
        TextView navTypeTextView = (TextView)header.findViewById(R.id.navTypeTextView);
        if(navTypeTextView != null)
        {
            navTypeTextView.setText(type);
        }
    }

    protected int getColorByType(Context context, String type){
        if(type.equals("INTJ")){
            return ContextCompat.getColor(context, R.color.colorIntj);
        }
        else if(type.equals("ESTJ")){
            return ContextCompat.getColor(context, R.color.colorEstj);
        }
        else if(type.equals("ESFJ")){
            return ContextCompat.getColor(context, R.color.colorEsfj);
        }
        else if(type.equals("ISTJ")){
            return ContextCompat.getColor(context, R.color.colorIstj);
        }
        else if(type.equals("ISFJ")){
            return ContextCompat.getColor(context, R.color.colorIsfj);
        }
        else if(type.equals("ESTP")){
            return ContextCompat.getColor(context, R.color.colorEstp);
        }
        else if(type.equals("ESFP")){
            return ContextCompat.getColor(context, R.color.colorEsfp);
        }
        else if(type.equals("ISFP")){
            return ContextCompat.getColor(context, R.color.colorIsfp);
        }
        else if(type.equals("ISTP")){
            return ContextCompat.getColor(context, R.color.colorIstp);
        }
        else if(type.equals("ENTJ")){
            return ContextCompat.getColor(context, R.color.colorEntj);
        }
        else if(type.equals("ENTP")){
            return ContextCompat.getColor(context, R.color.colorEntp);
        }
        else if(type.equals("INTP")){
            return ContextCompat.getColor(context, R.color.colorIntp);
        }
        else if(type.equals("INFJ")){
            return ContextCompat.getColor(context, R.color.colorInfj);
        }
        else if(type.equals("ENFP")){
            return ContextCompat.getColor(context, R.color.colorEnfp);
        }
        else if(type.equals("INFP")){
            return ContextCompat.getColor(context, R.color.colorInfp);
        }
        else if(type.equals("ENFJ")){
            return ContextCompat.getColor(context, R.color.colorEnfj);
        }
        return ContextCompat.getColor(context, R.color.colorIntj);
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
