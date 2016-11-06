package se.grace.vivian.traits.traits;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vivi on 2016-11-06.
 */
public class UserTraits implements Parcelable {

    //"personalitytype": "string",
    //        "traits": [
    //        "string"
    //        ]

    private String mPersonalityType;
    private String[] mTraits;

    public String[] getTraits() {
        return mTraits;
    }

    public void setTraits(String[] traits) {
        mTraits = traits;
    }

    public String getPersonalityType() {
        return mPersonalityType;
    }

    public void setPersonalityType(String personalityType) {
        mPersonalityType = personalityType;
    }

    public UserTraits(){

    }

    private UserTraits(Parcel in)
    {
        mPersonalityType = in.readString();
        mTraits = in.createStringArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPersonalityType);
        dest.writeStringArray(mTraits);
    }

    public static final Creator<UserTraits> CREATOR = new Creator<UserTraits>() {
        @Override
        public UserTraits createFromParcel(Parcel source) {
            return new UserTraits(source);
        }

        @Override
        public UserTraits[] newArray(int size) {
            return new UserTraits[size];
        }
    };
}
