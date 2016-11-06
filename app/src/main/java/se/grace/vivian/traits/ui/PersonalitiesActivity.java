package se.grace.vivian.traits.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;

import se.grace.vivian.traits.Api;
import se.grace.vivian.traits.KeyStoring;
import se.grace.vivian.traits.R;
import se.grace.vivian.traits.SessionManager;
import se.grace.vivian.traits.traits.User;

public class PersonalitiesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public SessionManager manager;
    public static final String TAG = LoginActivity.class.getSimpleName();
    private Api mApi = new Api();
    private KeyStoring mKeyStoring = new KeyStoring();
    private User mUser;
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        //String message = intent.getStringExtra(LoginActivity.TRAITS_USER);
        mUser = intent.getParcelableExtra(LoginActivity.TRAITS_USER);  //getParcelableArrayExtra(LoginActivity.TRAITS_USER);
        //mUser = Parcel.obtain() .copyOf(parcelables, User.class); //Arrays.copyOf(parcelables, parcelables.length, User.class);
        Log.d(TAG, mUser.getName() + "");
        Toast.makeText(this, mUser.getName(), Toast.LENGTH_LONG).show();

        if(mUser.getUserTypeParts() != null && mUser.getUserTypeParts().size() > 0){
            Log.d(TAG, "User Type parts: " + mUser.getUserTypeParts().size());
            Log.d(TAG, "User Type parts: " + mUser.getUserTypeParts().get(0).getPersonalityType());
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
