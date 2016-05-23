package com.kelsos.mbrc.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.kelsos.mbrc.ui.fragments.BrowseAlbumFragment;
import com.kelsos.mbrc.ui.fragments.BrowseArtistFragment;
import com.kelsos.mbrc.ui.fragments.BrowseGenreFragment;
import com.kelsos.mbrc.ui.fragments.BrowseTrackFragment;

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
        return new BrowseGenreFragment();
      case 1:
        return new BrowseArtistFragment();
      case 2:
        return new BrowseAlbumFragment();
      case 3:
        return new BrowseTrackFragment();
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
