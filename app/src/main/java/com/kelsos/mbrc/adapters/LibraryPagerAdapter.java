package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.Search;
import com.kelsos.mbrc.annotations.Search.Section;
import com.kelsos.mbrc.ui.fragments.BrowseAlbumFragment;
import com.kelsos.mbrc.ui.fragments.BrowseArtistFragment;
import com.kelsos.mbrc.ui.fragments.BrowseGenreFragment;
import com.kelsos.mbrc.ui.fragments.BrowseTrackFragment;

public class LibraryPagerAdapter extends FragmentStatePagerAdapter {
  private static final int count = 4;
  private final Context context;

  public LibraryPagerAdapter(FragmentActivity activity) {
    super(activity.getSupportFragmentManager());
    context = activity.getApplicationContext();
  }

  @Override
  public Fragment getItem(@Section int position) {
    switch (position) {
      case Search.SECTION_GENRE:
        return new BrowseGenreFragment();
      case Search.SECTION_ARTIST:
        return new BrowseArtistFragment();
      case Search.SECTION_ALBUM:
        return new BrowseAlbumFragment();
      case Search.SECTION_TRACK:
        return new BrowseTrackFragment();
      default:
        break;
    }
    return null;
  }

  @Override
  public int getCount() {
    return count;
  }

  @Override
  public CharSequence getPageTitle(@Section int position) {
    switch (position) {
      case Search.SECTION_ALBUM:
        return context.getString(R.string.label_albums);
      case Search.SECTION_ARTIST:
        return context.getString(R.string.label_artists);
      case Search.SECTION_GENRE:
        return context.getString(R.string.label_genres);
      case Search.SECTION_TRACK:
        return context.getString(R.string.label_tracks);
      default:
        return "";
    }
  }
}
