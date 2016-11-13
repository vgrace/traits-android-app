package se.grace.vivian.traits.traits;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Vivi on 2016-11-12.
 */

public class Personality implements Parcelable{

    private String mId;
    private String mType;
    private String mTotalFrequency;
    private String mMaleFrequency;
    private String mFemaleFrequency;
    private String mDescription;
    private String mTypeFull;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getTotalFrequency() {
        return mTotalFrequency;
    }

    public void setTotalFrequency(String totalFrequency) {
        mTotalFrequency = totalFrequency;
    }

    public String getMaleFrequency() {
        return mMaleFrequency;
    }

    public void setMaleFrequency(String maleFrequency) {
        mMaleFrequency = maleFrequency;
    }

    public String getFemaleFrequency() {
        return mFemaleFrequency;
    }

    public void setFemaleFrequency(String femaleFrequency) {
        mFemaleFrequency = femaleFrequency;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getTypeFull() {
        return mTypeFull;
    }

    public void setTypeFull(String typeFull) {
        mTypeFull = typeFull;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mType);
        dest.writeString(mTotalFrequency);
        dest.writeString(mMaleFrequency);
        dest.writeString(mFemaleFrequency);
        dest.writeString(mDescription);
        dest.writeString(mTypeFull);
    }

    protected Personality(Parcel in) {
        mId = in.readString();
        mType = in.readString();
        mTotalFrequency = in.readString();
        mMaleFrequency = in.readString();
        mFemaleFrequency = in.readString();
        mDescription = in.readString();
        mTypeFull = in.readString();
    }

    public Personality(){}

    public static final Creator<Personality> CREATOR = new Creator<Personality>() {
        @Override
        public Personality createFromParcel(Parcel in) {
            return new Personality(in);
        }

        @Override
        public Personality[] newArray(int size) {
            return new Personality[size];
        }
    };
}
