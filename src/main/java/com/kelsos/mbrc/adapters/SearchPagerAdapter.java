package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.kelsos.mbrc.fragments.SearchAlbumFragment;
import com.kelsos.mbrc.fragments.SearchArtistFragment;
import com.kelsos.mbrc.fragments.SearchGenreFragment;
import com.kelsos.mbrc.fragments.SearchTrackFragment;

import java.util.*;

public class SearchPagerAdapter extends FragmentPagerAdapter {

    private int mCount = 4;
    private final Context mContext;
    private final ArrayList<FragmentInfo> mFragments = new ArrayList<FragmentInfo>();
    private final CharSequence pageTitles[] = new CharSequence[] {"Genre","Artist","Album","Track"};
    private Map<Integer, Fragment> fragmentMap;

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
        fragmentMap = new HashMap<Integer, Fragment>();
    }

    @Override public Fragment getItem(int position) {
        FragmentInfo fInfo = mFragments.get(position);
        Fragment fragment = Fragment.instantiate(mContext, fInfo.clss.getName());
        fragmentMap.put(position, fragment);
        return fragment;
    }

    @Override public int getCount() {
        return mCount;
    }

    @Override public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        fragmentMap.remove(position);
    }

    public Fragment getFragment(int position) {
        return fragmentMap.get(position);
    }
}
