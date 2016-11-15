package se.grace.vivian.traits.traits;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vivi on 2016-11-13.
 */

public class Trait implements Parcelable {
    private String mType;
    private String mTrait;
    private int mWeight;
    private boolean mIsUser;

    public String getTrait() {
        return mTrait;
    }

    public void setTrait(String trait) {
        mTrait = trait;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        mWeight = weight;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public boolean isUser() {
        return mIsUser;
    }

    public void setUser(boolean user) {
        mIsUser = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Trait(){

    }

    protected Trait(Parcel in) {
        mType = in.readString();
        mTrait = in.readString();
        mWeight = in.readInt();
        mIsUser = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mType);
        dest.writeString(mTrait);
        dest.writeInt(mWeight);
        dest.writeByte((byte) (mIsUser ? 1 : 0));
    }

    public static final Creator<Trait> CREATOR = new Creator<Trait>() {
        @Override
        public Trait createFromParcel(Parcel in) {
            return new Trait(in);
        }

        @Override
        public Trait[] newArray(int size) {
            return new Trait[size];
        }
    };
}
