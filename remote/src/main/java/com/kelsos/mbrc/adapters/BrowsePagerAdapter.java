package com.kelsos.mbrc.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;
import com.kelsos.mbrc.ui.fragments.browse.BrowseAlbumFragment;
import com.kelsos.mbrc.ui.fragments.browse.BrowseArtistFragment;
import com.kelsos.mbrc.ui.fragments.browse.BrowseGenreFragment;
import com.kelsos.mbrc.ui.fragments.browse.BrowseTrackFragment;

public class BrowsePagerAdapter extends FragmentStatePagerAdapter {
  public static final int TOTAL_FRAGMENTS = 4;
  public static final int GENRES_FRAGMENT = 0;
  public static final int ARTISTS_FRAGMENT = 1;
  public static final int ALBUMS_FRAGMENT = 2;
  public static final int TRACKS_FRAGMENT = 3;
  private final CharSequence[] pageTitles =
      new CharSequence[] {"Genre", "Artist", "Album", "Track"};

  public BrowsePagerAdapter(Activity activity) {
    super(activity.getFragmentManager());
  }

  @Override public Fragment getItem(int position) {

    switch (position) {
      case GENRES_FRAGMENT:
        return new BrowseGenreFragment();
      case ARTISTS_FRAGMENT:
        return new BrowseArtistFragment();
      case ALBUMS_FRAGMENT:
        return new BrowseAlbumFragment();
      case TRACKS_FRAGMENT:
        return new BrowseTrackFragment();
      default:
        return null;
    }
  }

  @Override public int getCount() {
    return TOTAL_FRAGMENTS;
  }

  @Override public CharSequence getPageTitle(int position) {
    return pageTitles[position];
  }
}
