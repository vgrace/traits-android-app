package se.grace.vivian.traits.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import se.grace.vivian.traits.R;
import se.grace.vivian.traits.traits.PersonalityGridItem;
import se.grace.vivian.traits.traits.UserTypePart;

/**
 * Created by Vivi on 2016-11-12.
 */

public class PersonalityAdapter extends BaseAdapter {
    public static final String TAG = PersonalityAdapter.class.getSimpleName();
    private final Context mContext;
    private final ArrayList<PersonalityGridItem> mTypes;

    public PersonalityAdapter(Context context, ArrayList<PersonalityGridItem> types)
    {
        this.mContext = context;
        this.mTypes = types;

    }
    @Override
    public int getCount() {
        return mTypes.size();
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
        final PersonalityGridItem typePart = mTypes.get(position);

        if(convertView == null)
        {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.personality_grid_item, null);
        }

        final LinearLayout itemLayout = (LinearLayout) convertView.findViewById(R.id.itemGridLayout);
        final TextView typeTextView = (TextView) convertView.findViewById(R.id.personality_type);
        final TextView percTextView = (TextView) convertView.findViewById(R.id.personality_perc);
        typeTextView.setText(typePart.getType());
        percTextView.setText(typePart.getPercentage() + " %");
        itemLayout.setBackgroundColor(typePart.getTypeColor());

        return convertView;
    }

}
