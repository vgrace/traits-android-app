package se.grace.vivian.traits;

import android.content.Context;
import android.support.v4.content.ContextCompat;

/**
 * Created by Vivi on 2016-11-13.
 */

public class Colors {

    public int getColorByType(Context context, String type){
        if(type.equals("INTJ")){
            return ContextCompat.getColor(context, R.color.colorIntj);
        }
        else if(type.equals("ESTJ")){
            return ContextCompat.getColor(context, R.color.colorEstj);
        }
        else if(type.equals("ESFJ")){
            return ContextCompat.getColor(context, R.color.colorEsfj);
        }
        else if(type.equals("ISTJ")){
            return ContextCompat.getColor(context, R.color.colorIstj);
        }
        else if(type.equals("ISFJ")){
            return ContextCompat.getColor(context, R.color.colorIsfj);
        }
        else if(type.equals("ESTP")){
            return ContextCompat.getColor(context, R.color.colorEstp);
        }
        else if(type.equals("ESFP")){
            return ContextCompat.getColor(context, R.color.colorEsfp);
        }
        else if(type.equals("ISFP")){
            return ContextCompat.getColor(context, R.color.colorIsfp);
        }
        else if(type.equals("ISTP")){
            return ContextCompat.getColor(context, R.color.colorIstp);
        }
        else if(type.equals("ENTJ")){
            return ContextCompat.getColor(context, R.color.colorEntj);
        }
        else if(type.equals("ENTP")){
            return ContextCompat.getColor(context, R.color.colorEntp);
        }
        else if(type.equals("INTP")){
            return ContextCompat.getColor(context, R.color.colorIntp);
        }
        else if(type.equals("INFJ")){
            return ContextCompat.getColor(context, R.color.colorInfj);
        }
        else if(type.equals("ENFP")){
            return ContextCompat.getColor(context, R.color.colorEnfp);
        }
        else if(type.equals("INFP")){
            return ContextCompat.getColor(context, R.color.colorInfp);
        }
        else if(type.equals("ENFJ")){
            return ContextCompat.getColor(context, R.color.colorEnfj);
        }
        return ContextCompat.getColor(context, R.color.colorIntj);
    }
}
