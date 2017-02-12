package se.grace.vivian.traits.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import se.grace.vivian.traits.Globals;
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
    Globals g = Globals.getInstance();

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

    // Your "view holder" that holds references to each subview
    private class ViewHolder {
        private final ToggleButton traitsToggleButton;

        public ViewHolder(ToggleButton traitsToggleButton) {
            this.traitsToggleButton = traitsToggleButton;
        }
    }

    protected Trait getTraitByName(String name){
        for(int i = 0; i<mTraits.size(); i++){
            Trait t = mTraits.get(i);
            if(t.getTrait().equals(name)){
                return t;
            }
        }
        return null;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Trait trait = mTraits.get(position);
        if(convertView == null)
        {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.traits_grid_item, null);

            final ToggleButton toggleButton = (ToggleButton) convertView.findViewById(R.id.toggleButton);
            toggleButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Trait t = getTraitByName(toggleButton.getText().toString());
                    if(t != null){
                        Log.d(TAG, "Trait to change: " + t.getTrait());
                        if(toggleButton.isChecked()){
                            int c = mPersonalityGridItem.getTypeColor();
                            toggleButton.setTextColor(c);
                            TraitsTabFragment.updateTraits(t,true);
                            t.setUser(true);
                        }
                        else{
                            toggleButton.setTextColor(ContextCompat.getColor(mContext, R.color.buttonDefault));
                            TraitsTabFragment.updateTraits(t,false);
                            t.setUser(false);
                        }
                    }
                    Log.d(TAG, "Toggle button clicked! " + toggleButton.getText() +  "position: " + position);

                }
            });

            final ViewHolder viewHolder = new ViewHolder(toggleButton);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.traitsToggleButton.setText(trait.getTrait());

        viewHolder.traitsToggleButton.setTextOn(trait.getTrait());
        viewHolder.traitsToggleButton.setTextOff(trait.getTrait());

        if(trait.isUser()){
            viewHolder.traitsToggleButton.setChecked(true);
            viewHolder.traitsToggleButton.setTextColor(mPersonalityGridItem.getTypeColor());
        }
        else{
            viewHolder.traitsToggleButton.setChecked(false);
            viewHolder.traitsToggleButton.setTextColor(ContextCompat.getColor(mContext, R.color.buttonDefault));
        }

        return convertView;
    }
}
