package se.grace.vivian.traits;

import android.widget.GridView;

import java.util.ArrayList;

import se.grace.vivian.traits.traits.PersonalityGridItem;
import se.grace.vivian.traits.traits.UserTypePart;

/**
 * Created by Vivi on 2016-12-09.
 */

public class Globals {
    private static Globals instance;

    // Global variable
    //private int data;
    private String username;
    private PersonalityGridItem selectedType;
    private ArrayList<PersonalityGridItem> personalityGridItems;

    // Restrict the constructor from being instantiated
    private Globals(){}

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public void setSelectedType(PersonalityGridItem selType){
        this.selectedType = selType;
    }

    public PersonalityGridItem getSelectedType(){
        return this.selectedType;
    }

    public void setPersonalityGridItem(String type, int percent){
        for(int i = 0; i<personalityGridItems.size(); i++){
            PersonalityGridItem utp = personalityGridItems.get(i);
            if(utp.getType().equals(type)){
                utp.setPercentage(percent);
                break;
            }
        }
    }

    public PersonalityGridItem getPersonalityGridItem(String type){
        for(int i = 0; i<personalityGridItems.size(); i++) {
            PersonalityGridItem utp = personalityGridItems.get(i);
            if(utp.getType().equals(type)) {
                return utp;
            }
        }
        return null;
    }

    public void setPersonalityGridItems(ArrayList<PersonalityGridItem> griditems){
        this.personalityGridItems = griditems;
    }

    public ArrayList<PersonalityGridItem> getPersonalityGridItems(){
        return personalityGridItems;
    }

//    public void setData(int d){
//        this.data=d;
//    }
//    public int getData(){
//        return this.data;
//    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
