package se.grace.vivian.traits.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import se.grace.vivian.traits.R;
import se.grace.vivian.traits.traits.PersonalityGridItem;
import se.grace.vivian.traits.traits.Trait;

/**
 * Created by Vivi on 2016-11-14.
 */

public class TraitsAdapter extends BaseAdapter {
    public static final String TAG = TraitsAdapter.class.getSimpleName();
    private final Context mContext;
    private final PersonalityGridItem mPersonalityGridItem;
    private final ArrayList<Trait> mTraits;


    public TraitsAdapter(Context context, PersonalityGridItem personalityGridItem, ArrayList<Trait> traits){
        this.mContext = context;
        this.mPersonalityGridItem = personalityGridItem;
        this.mTraits = traits;
    }
    @Override
    public int getCount() {
        return mTraits.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Trait trait = mTraits.get(position);
        if(convertView == null)
        {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.traits_grid_item, null);
        }

        ToggleButton toggleButton = (ToggleButton) convertView.findViewById(R.id.toggleButton);
        toggleButton.setText(trait.getTrait());

        toggleButton.setTextOn(trait.getTrait());
        toggleButton.setTextOff(trait.getTrait());
        if(trait.isUser()){
            toggleButton.setChecked(true);
            toggleButton.setTextColor(mPersonalityGridItem.getTypeColor());
        }
        else{
            toggleButton.setChecked(false);
            toggleButton.setTextColor(ContextCompat.getColor(mContext, R.color.buttonDefault));
        }
        toggleButton.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked)
            {
            Log.d(TAG, "Toggle button changed");
            if(isChecked){
                int c = mPersonalityGridItem.getTypeColor();
                toggleButton.setTextColor(c);
                TraitsTabFragment.updateTraits(trait, true);
            }
            else{
                toggleButton.setTextColor(ContextCompat.getColor(mContext, R.color.buttonDefault));
                TraitsTabFragment.updateTraits(trait, false);
            }

            }
        });
        return convertView;
    }
}
