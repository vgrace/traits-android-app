package se.grace.vivian.traits;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import se.grace.vivian.traits.traits.User;
import se.grace.vivian.traits.traits.UserTraits;
import se.grace.vivian.traits.traits.UserTypePart;
import se.grace.vivian.traits.ui.LoginActivity;

/**
 * Created by Vivi on 2016-11-06.
 */
public class Api {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    public static final String TAG = LoginActivity.class.getSimpleName();
    public static final String TRAITS_USER = "TRAITS_USER";
    private Router mRouter = new Router();
    private User mUser;

    public okhttp3.Call get(String url, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        okhttp3.Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private okhttp3.Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public okhttp3.Call put(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
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

        //Log.d(TAG, bodyJson);

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


                    try {
                        mUser = parseUser(responseStr);
                        mRouter.GoToPersonalitiesActivity(context, mUser);
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

                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                    Log.d(TAG, "Request not successful: "  +response.code() +" : "+ response.message());

                }
            }
        });
    }

    private User parseUser (String jsonData) throws JSONException {
        JSONObject userJson = new JSONObject(jsonData);
        User user = new User();
        user.setId(userJson.getString("_id"));
        Log.d(TAG, "id: " + userJson.getString("_id"));
        user.setName(userJson.getString("name"));
        Log.d(TAG, "Name: " + userJson.getString("name"));
        user.setLastName(userJson.getString("lastname"));
        Log.d(TAG, "Lastname: " + userJson.getString("lastname"));
        user.setEmail(userJson.getString("email"));
        Log.d(TAG, "email: " + userJson.getString("email"));
        user.setUsername(userJson.getString("username"));
        Log.d(TAG, "Username: " + userJson.getString("username"));
        //user.setUserTypeParts(parseUserTypeParts(jsonData));
        //user.setUserTraits(parseUserTraits(jsonData));
        return user;
    }

    private ArrayList<UserTypePart> parseUserTypeParts(String jsonData) throws JSONException{
        JSONObject userJson = new JSONObject(jsonData);
        JSONArray usertypepartsJson = userJson.getJSONArray("usertypeparts");
        ArrayList<UserTypePart> userTypeParts = new ArrayList<UserTypePart>();
        for(int i = 0; i<usertypepartsJson.length(); i++){
            UserTypePart userTypePart = new UserTypePart();
            JSONObject jsonUserTypePart = usertypepartsJson.getJSONObject(i);
            userTypePart.setPersonalityType(jsonUserTypePart.getString("personalitytype"));
            userTypePart.setPercentage(jsonUserTypePart.getString("percentage"));
            userTypeParts.add(userTypePart);
        }
        return userTypeParts;
    }

    private ArrayList<UserTraits> parseUserTraits(String jsonData) throws JSONException{
        JSONObject userJson = new JSONObject(jsonData);
        JSONArray usertraitsJson = userJson.getJSONArray("usertraits");
        ArrayList<UserTraits> userTraits = new ArrayList<UserTraits>();
        Log.d(TAG, "Usertraits size: " + usertraitsJson.length());
        for(int i = 0; i<usertraitsJson.length(); i++){
            UserTraits userTrait = new UserTraits();
            JSONObject jsonUserTraits = usertraitsJson.getJSONObject(i);
            userTrait.setPersonalityType(jsonUserTraits.getString("personalitytype"));
            Log.d(TAG, jsonUserTraits.getString("personalitytype"));
            JSONArray traitsList = jsonUserTraits.getJSONArray("traits");
            Log.d(TAG, "Traits size: " + traitsList.length());
            String[] traitsArr = new String[traitsList.length()];
            // loop through the array itemList and get the items
            for(int j=0;j<traitsList.length();j++)
            {
                traitsArr[j] = traitsList.getString(j); // item at index i
            }
            userTrait.setTraits(traitsArr);
            userTraits.add(userTrait);
        }
        return userTraits;
    }

    /*
    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");
        Day[] days = new Day[data.length()];
        for(int i = 0; i< data.length(); i++){
            Day day = new Day();
            JSONObject jsonDay = data.getJSONObject(i);

            day.setSummary(jsonDay.getString("summary"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimezone(timezone);

            days[i] = day;
        }
        return days;
    }
    }*/
}
