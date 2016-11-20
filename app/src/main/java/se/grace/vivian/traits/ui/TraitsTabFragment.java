package se.grace.vivian.traits.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import se.grace.vivian.traits.Api;
import se.grace.vivian.traits.R;
import se.grace.vivian.traits.traits.PersonalityGridItem;
import se.grace.vivian.traits.traits.Trait;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TraitsTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TraitsTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TraitsTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = TraitsTabFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Api mApi = new Api();
    private static PersonalityGridItem mPersonality;
    private static TextView scoresTextView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    static ArrayList<Trait> mTraitList = new ArrayList<Trait>();
    static View v;

    private OnFragmentInteractionListener mListener;

    public TraitsTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TraitsTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TraitsTabFragment newInstance(String param1, String param2) {
        TraitsTabFragment fragment = new TraitsTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mTraitList = new ArrayList<Trait>(); // Clear traitslist
        mPersonality = TraitsActivity.getPersonality();
        try {
            getPersonalityTraits(mPersonality.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_traits_tab, container, false);

        scoresTextView = (TextView) v.findViewById(R.id.traitsTabScoreValue);
        scoresTextView.setText(TraitsActivity.getPersonalityScore()  + " %");

        //setupTraitsGrid(TraitsActivity.getTraitsList());

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_traits_tab, container, false);
        return v;
    }

    public static void updateScore(){
        int newScore = (int) Math.round(calculateScore() * 100);
        scoresTextView.setText( newScore + " %");
        PersonalitiesActivity.updateUserTypePart(mPersonality.getType(), newScore);
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
    public static void updateTraits(Trait trait, boolean isUser){
        for(int i = 0; i<mTraitList.size(); i++){
            Trait t = mTraitList.get(i);
            if(t.getTrait().equals(trait.getTrait())){
                Log.d(TAG, "set trait: " + trait.getTrait() + " to user trait");
                t.setUser(isUser);
                TraitsActivity.updateUserTraitsList(t);
            }
        }
        try {
            putUserTrait("apitest@mail.com", trait.getType(), getUserTraitsForPut());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateScore();
    }

    protected static String getUserTraitsForPut(){
        String traitsStr = "";
        for(int i = 0; i<mTraitList.size(); i++){
            Trait t = mTraitList.get(i);
            if(t.isUser()){
                traitsStr += "\""+t.getTrait()+"\",";
            }
        }
        if (traitsStr != null && traitsStr.length() > 1) {
            traitsStr = traitsStr.substring(0, traitsStr.length() - 1);
        }
        return traitsStr;
    }
    public static void putUserTrait(String username, String type, String traitStr) throws Exception {
        Log.d(TAG, "IN API!! " + traitStr);
        String url = "http://traits-app-api.herokuapp.com/api/userTraits/";

        String bodyJson = "{\n" +
                "  \"username\": \"" + username + "\",\n" +
                "  \"personalitytype\": \"" + type + "\",\n" +
                "  \"traits\": [\n" +
                "    " + traitStr + "\n" +
                "  ]\n" +
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

    public void getPersonalityTraits(String type) throws Exception {
        String url = "http://traits-app-api.herokuapp.com/api/trait/" + type;
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
                        // Personality traits
                        JSONArray jsonarray = new JSONArray(responseStr);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            Trait t = new Trait();
                            t.setType(jsonobject.getString("type"));
                            t.setTrait(jsonobject.getString("trait"));
                            t.setWeight(jsonobject.getInt("weight"));
                            t.setUser(false);
                            mTraitList.add(t);
                        }
                        ArrayList<Trait> userTraitsList = TraitsActivity.getUserTraitsList();
                        Log.d(TAG, "User traits: " + userTraitsList.size());
                        for(int i = 0; i<userTraitsList.size(); i++) {
                            Trait ut = userTraitsList.get(i);
                            for(int j = 0; j< mTraitList.size(); j++){
                                Trait pt = mTraitList.get(j);
                                if(ut.getTrait().equals(pt.getTrait())){
                                    pt.setUser(true);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Run in main thread
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "setupTraitsGrid!");
                            setupTraitsGrid(mTraitList);
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

    protected static double calculateScore(){
        // sum weight / total weight
        double totalWeight = 0;
        double userWeight = 0;
        for(int i = 0; i<mTraitList.size(); i++){
            Trait t = mTraitList.get(i);
            if(t.isUser()){
                userWeight += t.getWeight();
            }
            totalWeight += t.getWeight();
        }
        return userWeight / totalWeight;
    }

    public static void setupTraitsGrid(ArrayList<Trait> traitList){
        //Setup traits grid
        GridView traitsGridView = (GridView) v.findViewById(R.id.traitsGrid);
        final TraitsAdapter traitsAdapter = new TraitsAdapter(v.getContext(), TraitsActivity.getPersonality(), traitList);
        traitsGridView.setAdapter(traitsAdapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    /*@Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
