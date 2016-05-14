package com.kelsos.mbrc.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.kelsos.mbrc.ui.fragments.SearchAlbumFragment;
import com.kelsos.mbrc.ui.fragments.SearchArtistFragment;
import com.kelsos.mbrc.ui.fragments.SearchGenreFragment;
import com.kelsos.mbrc.ui.fragments.SearchTrackFragment;

public class SearchPagerAdapter extends FragmentStatePagerAdapter {
  private static final int mCount = 4;
  private final CharSequence pageTitles[] =
      new CharSequence[] {"Genre", "Artist", "Album", "Track"};

  public SearchPagerAdapter(FragmentActivity activity) {
    super(activity.getSupportFragmentManager());
  }

  @Override public Fragment getItem(int position) {

    switch (position) {
      case 0:
        return new SearchGenreFragment();
      case 1:
        return new SearchArtistFragment();
      case 2:
        return new SearchAlbumFragment();
      case 3:
        return new SearchTrackFragment();
      default:
        break;
    }
    return null;
  }

  @Override public int getCount() {
    return mCount;
  }

  @Override public CharSequence getPageTitle(int position) {
    return pageTitles[position];
  }
}
