package se.grace.vivian.traits.traits;

import java.util.ArrayList;

/**
 * Created by Vivi on 2016-11-12.
 */

public class Personality {
    /*
    * "personalities": [
    {
      "id": "string",
      "type": "string",
      "totalfrequency": "string",
      "malefrequency": "string",
      "femalefrequency": "string",
      "description": "string",
      "typefull": "string",
      "color": "string"
    }
  ]
    * */
    private String mId;
    private String mType;
    private String mTotalFrequency;
    private String mMaleFrequency;
    private String mFemaleFrequency;
    private String mDescription;
    private String mTypeFull;
    private ArrayList<Trait> mTraits;

    public ArrayList<Trait> getTraits() {
        return mTraits;
    }

    public void setTraits(ArrayList<Trait> traits) {
        mTraits = traits;
    }

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
}
