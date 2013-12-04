package com.kelsos.mbrc.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import com.kelsos.mbrc.ui.fragments.ButtonFragment;
import com.kelsos.mbrc.ui.fragments.TrackInfoFragment;

public class InfoButtonPagerAdapter extends FragmentPagerAdapter {
    private static final int M_COUNT = 2;
    public InfoButtonPagerAdapter(FragmentActivity activity) {
        super(activity.getSupportFragmentManager());
    }

    @Override public int getCount() {
        return M_COUNT;
    }

    @Override public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ButtonFragment();
            case 1:
                return new TrackInfoFragment();
        }
        return null;
    }
}
