package se.grace.vivian.traits;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor>{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    public static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_READ_CONTACTS = 0;
    public static final String TRAITS_USER = "TRAITS_USER";
    public SessionManager manager;
    private Api mApi = new Api();
    private Router mRouter = new Router();
    KeyStore keyStore;
    List<String> keyAliases;
    KeyStoring mKeyStoring = new KeyStoring();

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //protected OkHttpClient client = new OkHttpClient();
    private final OkHttpClient client = new OkHttpClient();
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        //Safe store user creds
//        try {
//            keyStore = KeyStore.getInstance("AndroidKeyStore");
//            keyStore.load(null);
//        }
//        catch(Exception e) {}
//        mKeyStoring.refreshKeys();
//
//        // Key to encrypt password
//        mKeyStoring.createNewKeys("TraitsPassword");
//        //deleteKey("TraitsPassword");

        setContentView(R.layout.activity_login);

        //Save user signed in
        manager = new SessionManager();

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        //Check if user is signed in
        String status = manager.getPreferences(LoginActivity.this,"status");
        Log.d("status",status);
        if (status.equals("1")){
            Log.d(TAG, "User already signed in!");
            //Get user
            String username = manager.getPreferences(LoginActivity.this, "username");
            Log.d(TAG, username);
            String encryptedPassword = manager.getPreferences(LoginActivity.this, "password");
            Log.d(TAG, encryptedPassword);
            String decryptedPassword = mKeyStoring.decryptString("TraitsPassword", encryptedPassword);
            Log.d(TAG, decryptedPassword);
            /*try {
                Log.d(TAG, "Get signed in user info");
                //postLogin(username, decryptedPassword);
                mApi.postLogin(username, decryptedPassword, LoginActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            // Go to personalities activity
            mRouter.GoToPersonalitiesActivity(LoginActivity.this, username);
        }
    }

    /* KEYSTORE */
//    private void refreshKeys() {
//        keyAliases = new ArrayList<>();
//        try {
//            Enumeration<String> aliases = keyStore.aliases();
//            while (aliases.hasMoreElements()) {
//                keyAliases.add(aliases.nextElement());
//            }
//            Log.d(TAG, keyAliases.toString());
//        }
//        catch(Exception e) {}
//
//        //if(listAdapter != null)
//            //listAdapter.notifyDataSetChanged();
//    }
//
//    public void createNewKeys(String alias) {
//        //String alias = ""; //aliasText.getText().toString();
//        try {
//            // Create new key if needed
//            if (!keyStore.containsAlias(alias)) {
//                Log.d(TAG, "Create key");
//                Calendar start = Calendar.getInstance();
//                Calendar end = Calendar.getInstance();
//                end.add(Calendar.YEAR, 1);
//
//                KeyPairGenerator kpg = KeyPairGenerator.getInstance(
//                        KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
//
//                kpg.initialize(new KeyGenParameterSpec.Builder(
//                        alias, KeyProperties.PURPOSE_DECRYPT)
//                        .setDigests(KeyProperties.DIGEST_SHA256)
//                        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
//                        .build());
//                KeyPair keyPair = kpg.generateKeyPair();
//
//                /*KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(this)
//                        .setAlias(alias)
//                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
//                        .setSerialNumber(BigInteger.ONE)
//                        .setStartDate(start.getTime())
//                        .setEndDate(end.getTime())
//                        .build();
//
//                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
//                generator.initialize(spec);
//                KeyPair keyPair = generator.generateKeyPair();*/
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
//            Log.e(TAG, Log.getStackTraceString(e));
//        }
//        refreshKeys();
//    }
//
//    public void deleteKey(String alias) {
//        try {
//            keyStore.deleteEntry(alias);
//            refreshKeys();
//        } catch (KeyStoreException e) {
//            Log.e(TAG, Log.getStackTraceString(e));
//        }
//    }
//
//    public String encryptString(String alias, String initialText) {
//        try {
//            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
//            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
//
//            //String initialText = "Text to encrypt"; //startText.getText().toString();
//            if(initialText.isEmpty()) {
//                Toast.makeText(this, "Enter text in the 'Initial Text' widget", Toast.LENGTH_LONG).show();
//                return "";
//            }
//
//            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
//            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);
//
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            CipherOutputStream cipherOutputStream = new CipherOutputStream(
//                    outputStream, inCipher);
//            cipherOutputStream.write(initialText.getBytes("UTF-8"));
//            cipherOutputStream.close();
//
//            byte [] vals = outputStream.toByteArray();
//            String encryptedText = Base64.encodeToString(vals, Base64.DEFAULT);
//            Log.d(TAG, encryptedText);
//            return  encryptedText;
//            //encryptedText.setText(Base64.encodeToString(vals, Base64.DEFAULT));
//        } catch (Exception e) {
//            Toast.makeText(this, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
//            Log.e(TAG, Log.getStackTraceString(e));
//            return "";
//        }
//    }
//
//    public String decryptString(String alias, String cipherText) {
//        try {
//            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
//            //RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
//
//            //Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
//            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//            //output.init(Cipher.DECRYPT_MODE, privateKey);
//            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
//
//            //String cipherText = encryptedText.getText().toString();
//            CipherInputStream cipherInputStream = new CipherInputStream(
//                    new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
//            ArrayList<Byte> values = new ArrayList<>();
//            int nextByte;
//            while ((nextByte = cipherInputStream.read()) != -1) {
//                values.add((byte)nextByte);
//            }
//
//            byte[] bytes = new byte[values.size()];
//            for(int i = 0; i < bytes.length; i++) {
//                bytes[i] = values.get(i).byteValue();
//            }
//
//            String finalText = new String(bytes, 0, bytes.length, "UTF-8");
//            //Log.d(TAG, finalText);
//            return finalText;
//            //decryptedText.setText(finalText);
//
//        } catch (Exception e) {
//            Toast.makeText(this, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
//            Log.e(TAG, Log.getStackTraceString(e));
//            return "";
//        }
//    }

    /* END KEYSTORE*/


    protected void GoToPersonalitiesActivity(String username)
    {
        //Go to personalities activity
        Intent intent = new Intent(LoginActivity.this, PersonalitiesActivity.class);
        intent.putExtra(TRAITS_USER, "The user was signed in: " + username);
        startActivity(intent);

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    //private final OkHttpClient client = new OkHttpClient();
    /*public void test() throws Exception
    {
        RequestBody formBody = new FormBody.Builder().add("email", "test").build();
        Request request = new Request.Builder().url("http://traits-app-api.herokuapp.com/api/usersignin").post(formBody).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException
                ("Unexpected code " + response); System.out.println(response.body().string());
    }

    String getTest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    protected okhttp3.Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private void postLogin(String username, String password) throws Exception {
        //Log.d(TAG, getTest("http://traits-app-api.herokuapp.com/api/personality"));
        String url = "http://traits-app-api.herokuapp.com/api/usersignin";
        String bodyJson = "{\n" +
                "  \"email\": \""+username+"\",\n" +
                "  \"password\": \""+password+"\"\n" +
                "}";

        Log.d(TAG, bodyJson);

        post(url, bodyJson, new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // Something went wrong
                if(e.getMessage() != null)
                    Log.d(TAG, e.getMessage());
                else
                    Log.d(TAG, "Failed to call POST");
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseStr = response.body().string();
                    Log.d(TAG, responseStr);


                    //Run in main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GoToPersonalitiesActivity(responseStr);

                            }
                        });

                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                    Log.d(TAG, "Request not successful: "  +response.code() +" : "+ response.message());

                }
            }
        });
    }*/

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            // Call signin from API
            try {
                //postLogin(mEmail, mPassword);
                mApi.postLogin(mEmail, mPassword, LoginActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }*/

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }



        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //finish();
                // Go to main activity
                /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(TRAITS_USER, "The user was signed in: " + mEmailView.getText().toString());
                startActivity(intent);*/

                // For auto login user in future
                manager.setPreferences(LoginActivity.this, "status", "1");
                manager.setPreferences(LoginActivity.this, "username", mEmail);
                String encrPassword = mKeyStoring.encryptString("TraitsPassword", mPassword);
                manager.setPreferences(LoginActivity.this, "password", encrPassword);
                Log.d(TAG, "ON POST EXECUTE!");
                //Go to personalities activity
                //GoToPersonalitiesActivity(mEmailView.getText().toString());

                /*Intent intent = new Intent(LoginActivity.this, PersonalitiesActivity.class);
                intent.putExtra(TRAITS_USER, "The user was signed in: " + mEmailView.getText().toString());
                startActivity(intent);*/

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

