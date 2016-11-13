package se.grace.vivian.traits.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Vivi on 2016-11-13.
 */

public class TabPagerAdapter extends FragmentPagerAdapter {
    int tabCount;

    public TabPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.tabCount = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TraitsTabFragment tab1 = new TraitsTabFragment();
                return tab1;
            case 1:
                StoriesTabFragment tab2 = new StoriesTabFragment();
                return tab2;
            case 2:
                PeopleTabFragment tab3 = new PeopleTabFragment();
                return tab3;
            case 3:
                InfoTabFragment tab4 = new InfoTabFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
