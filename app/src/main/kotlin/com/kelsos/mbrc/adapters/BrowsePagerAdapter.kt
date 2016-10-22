package com.kelsos.mbrc.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import javax.inject.Inject
import com.kelsos.mbrc.ui.fragments.browse.BrowseAlbumFragment
import com.kelsos.mbrc.ui.fragments.browse.BrowseArtistFragment
import com.kelsos.mbrc.ui.fragments.browse.BrowseGenreFragment
import com.kelsos.mbrc.ui.fragments.browse.BrowseTrackFragment

class BrowsePagerAdapter
@Inject
constructor(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
  private val pageTitles = arrayOf<CharSequence>("Genre", "Artist", "Album", "Track")

  override fun getItem(position: Int): Fragment? {

    when (position) {
      GENRES_FRAGMENT -> return BrowseGenreFragment()
      ARTISTS_FRAGMENT -> return BrowseArtistFragment()
      ALBUMS_FRAGMENT -> return BrowseAlbumFragment()
      TRACKS_FRAGMENT -> return BrowseTrackFragment()
      else -> return null
    }
  }

  override fun getCount(): Int {
    return TOTAL_FRAGMENTS
  }

  override fun getPageTitle(position: Int): CharSequence {
    return pageTitles[position]
  }

  companion object {
    val TOTAL_FRAGMENTS = 4
    val GENRES_FRAGMENT = 0
    val ARTISTS_FRAGMENT = 1
    val ALBUMS_FRAGMENT = 2
    val TRACKS_FRAGMENT = 3
  }
}
