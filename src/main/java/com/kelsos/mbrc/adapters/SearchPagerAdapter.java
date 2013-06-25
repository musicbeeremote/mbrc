package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import com.kelsos.mbrc.ui.fragments.SearchAlbumFragment;
import com.kelsos.mbrc.ui.fragments.SearchArtistFragment;
import com.kelsos.mbrc.ui.fragments.SearchGenreFragment;
import com.kelsos.mbrc.ui.fragments.SearchTrackFragment;

import java.util.ArrayList;

public class SearchPagerAdapter extends FragmentStatePagerAdapter {

    private int mCount = 4;
    private final Context mContext;
    private final ArrayList<FragmentInfo> mFragments = new ArrayList<FragmentInfo>();
    private final CharSequence pageTitles[] = new CharSequence[] {"Genre","Artist","Album","Track"};


    static final class FragmentInfo {
        private final Class<?> clss;

        FragmentInfo(Class<?> clss) {
            this.clss = clss;
        }
    }

    public SearchPagerAdapter(FragmentActivity activity) {
        super(activity.getSupportFragmentManager());
        mContext = activity;
        mFragments.add(new FragmentInfo(SearchGenreFragment.class));
        mFragments.add(new FragmentInfo(SearchArtistFragment.class));
        mFragments.add(new FragmentInfo(SearchAlbumFragment.class));
        mFragments.add(new FragmentInfo(SearchTrackFragment.class));
    }

    @Override public Fragment getItem(int position) {
        FragmentInfo fInfo = mFragments.get(position);
        Fragment fragment = Fragment.instantiate(mContext, fInfo.clss.getName());
        Log.d("MBRC", "instantiate: " + fInfo.clss.getName());
        return fragment;
    }

    @Override public int getCount() {
        return mCount;
    }

    @Override public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }
}
