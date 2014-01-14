package com.kelsos.mbrc.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.kelsos.mbrc.ui.fragments.ButtonFragment;
import com.kelsos.mbrc.ui.fragments.TrackInfoFragment;

public class InfoButtonPagerAdapter extends FragmentPagerAdapter {
    private static final int M_COUNT = 2;
    public static final int BUTTONS = 0;
    public static final int INFO = 1;

    public InfoButtonPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override public int getCount() {
        return M_COUNT;
    }

    @Override public Fragment getItem(int position) {
        switch (position) {
            case BUTTONS:
                return new ButtonFragment();
            case INFO:
                return new TrackInfoFragment();
            default:
                return null;
        }
    }
}
