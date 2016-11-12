package se.grace.vivian.traits.traits;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Vivi on 2016-11-06.
 * {
         "id": "string",
         "name": "string",
         "lastname": "string",
         "email": "string",
         "username": "string",
         "passwordHash": "string",
         "salt": "string",
         "usertypeparts": [
         {
         "personalitytype": "string",
         "percentage": "string",
         "lastupdate": "2016-11-05T15:31:25.433Z"
         }
         ],
         "usertraits": [
         {
         "personalitytype": "string",
         "traits": [
         "string"
         ]
         }
         ]
         }
 */
public class User implements Parcelable {
    private String mId;
    private String mName;
    private String mLastName;
    private String mEmail;
    private String mUsername;
    private ArrayList<UserTypePart> mUserTypeParts;
    private ArrayList<UserTraits> mUserTraits;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public ArrayList<UserTraits> getUserTraits() {
        return mUserTraits;
    }

    public void setUserTraits(ArrayList<UserTraits> userTraits) {
        mUserTraits = userTraits;
    }

    public ArrayList<UserTypePart> getUserTypeParts() {
        return mUserTypeParts;
    }

    public void setUserTypeParts(ArrayList<UserTypePart> userTypeParts) {
        mUserTypeParts = userTypeParts;
    }

    /*public static Creator<User> getCREATOR() {
        return CREATOR;
    }*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mLastName);
        dest.writeString(mEmail);
        dest.writeString(mUsername);
        dest.writeList(mUserTypeParts);
        dest.writeList(mUserTraits);
        //dest.writeArray(mUserTraits);
    }

    public User(){

    }

    private User(Parcel in){
        mId = in.readString();
        mName = in.readString();
        mLastName = in.readString();
        mEmail = in.readString();
        mUsername = in.readString();
        mUserTypeParts = in.readArrayList(UserTypePart.class.getClassLoader());
        mUserTraits = in.readArrayList(UserTraits.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}


