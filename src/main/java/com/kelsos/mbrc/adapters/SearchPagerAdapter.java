package com.kelsos.mbrc.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.kelsos.mbrc.ui.fragments.SearchAlbumFragment;
import com.kelsos.mbrc.ui.fragments.SearchArtistFragment;
import com.kelsos.mbrc.ui.fragments.SearchGenreFragment;
import com.kelsos.mbrc.ui.fragments.SearchTrackFragment;

import java.util.ArrayList;

public class SearchPagerAdapter extends FragmentStatePagerAdapter {
    private int mCount = 4;
    private final ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
    private final CharSequence pageTitles[] = new CharSequence[]{"Genre", "Artist", "Album", "Track"};


    public SearchPagerAdapter(FragmentActivity activity) {
        super(activity.getSupportFragmentManager());
        mFragments.add(new SearchGenreFragment());
        mFragments.add(new SearchArtistFragment());
        mFragments.add(new SearchAlbumFragment());
        mFragments.add(new SearchTrackFragment());
    }

    @Override public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override public int getCount() {
        return mCount;
    }

    @Override public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }
}
