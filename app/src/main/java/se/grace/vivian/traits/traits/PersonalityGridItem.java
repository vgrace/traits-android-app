package se.grace.vivian.traits.traits;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vivi on 2016-11-12.
 */

public class PersonalityGridItem implements Parcelable{
    private String mType;
    private int mPercentage;
    private int mTypeColor;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public int getPercentage() {
        return mPercentage;
    }

    public void setPercentage(int percentage) {
        mPercentage = percentage;
    }

    public int getTypeColor() {
        return mTypeColor;
    }

    public void setTypeColor(int typeColor) {
        mTypeColor = typeColor;
    }

    public PersonalityGridItem(){

    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mType);
        dest.writeInt(mPercentage);
        dest.writeInt(mTypeColor);
    }

    protected PersonalityGridItem(Parcel in) {
        mType = in.readString();
        mPercentage = in.readInt();
        mTypeColor = in.readInt();
    }

    public static final Creator<PersonalityGridItem> CREATOR = new Creator<PersonalityGridItem>() {
        @Override
        public PersonalityGridItem createFromParcel(Parcel in) {
            return new PersonalityGridItem(in);
        }

        @Override
        public PersonalityGridItem[] newArray(int size) {
            return new PersonalityGridItem[size];
        }
    };
}
