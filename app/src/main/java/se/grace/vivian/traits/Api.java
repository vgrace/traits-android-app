package se.grace.vivian.traits;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Vivi on 2016-11-06.
 */
public class Api {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    public static final String TAG = LoginActivity.class.getSimpleName();
    public static final String TRAITS_USER = "TRAITS_USER";
    private Router mRouter = new Router();
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

    public void postLogin(String username, String password, final Context context) throws Exception {
        Log.d(TAG, "IN API!!");
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
                    mRouter.GoToPersonalitiesActivity(context, responseStr);

                    //Run in main thread
                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GoToPersonalitiesActivity(responseStr);

                        }
                    });*/

                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                    Log.d(TAG, "Request not successful: "  +response.code() +" : "+ response.message());

                }
            }
        });
    }

    /*protected void GoToPersonalitiesActivity(Context context, String username)
    {
        //Go to personalities activity
        Intent intent = new Intent(context, PersonalitiesActivity.class);
        intent.putExtra(TRAITS_USER, "The user was signed in: " + username);
        context.startActivity(intent);

    }*/
}
