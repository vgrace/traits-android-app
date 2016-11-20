package se.grace.vivian.traits.traits;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

import static java.lang.Integer.parseInt;

/**
 * Created by Vivi on 2016-11-06.
 */
public class UserTypePart implements Parcelable, Comparator<UserTypePart> {

    private String mPersonalityType;
    private String mPercentage;
    //private Date mLastUpdate;

    public String getPersonalityType() {
        return mPersonalityType;
    }

    public void setPersonalityType(String personalityType) {
        mPersonalityType = personalityType;
    }

    public String getPercentage() {
        return mPercentage;
    }

    public void setPercentage(String percentage) {
        mPercentage = percentage;
    }

    /*public Date getLastUpdate() {
        return mLastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        mLastUpdate = lastUpdate;
    }*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPersonalityType);
        dest.writeString(mPercentage);
    }

    public UserTypePart()
    {
    }

    private UserTypePart(Parcel in)
    {
        mPersonalityType = in.readString();
        mPercentage = in.readString();
    }

    public static final Creator<UserTypePart> CREATOR = new Creator<UserTypePart>() {
        @Override
        public UserTypePart createFromParcel(Parcel source) {
            return new UserTypePart(source);
        }

        @Override
        public UserTypePart[] newArray(int size) {
            return new UserTypePart[size];
        }
    };


    @Override
    public int compare(UserTypePart o1, UserTypePart o2) {
        return parseInt(o2.getPercentage()) - parseInt(o1.getPercentage());
    }

}
