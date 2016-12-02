package com.kelsos.mbrc.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentStatePagerAdapter
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.Search
import com.kelsos.mbrc.annotations.Search.Section
import com.kelsos.mbrc.ui.navigation.library.albums.BrowseAlbumFragment
import com.kelsos.mbrc.ui.navigation.library.artists.BrowseArtistFragment
import com.kelsos.mbrc.ui.navigation.library.gernes.BrowseGenreFragment
import com.kelsos.mbrc.ui.navigation.library.tracks.BrowseTrackFragment

class LibraryPagerAdapter(activity: FragmentActivity) : FragmentStatePagerAdapter(activity.supportFragmentManager) {
  private val context: Context

  init {
    context = activity.applicationContext
  }

  override fun getItem(@Section position: Int): Fragment? {
    return when (position) {
      Search.SECTION_GENRE -> BrowseGenreFragment()
      Search.SECTION_ARTIST -> BrowseArtistFragment()
      Search.SECTION_ALBUM -> BrowseAlbumFragment()
      Search.SECTION_TRACK -> BrowseTrackFragment()
      else -> null
    }
  }

  override fun getCount(): Int {
    return COUNT
  }

  override fun getPageTitle(@Section position: Int): CharSequence {
    return when (position) {
      Search.SECTION_ALBUM -> context.getString(R.string.label_albums)
      Search.SECTION_ARTIST -> context.getString(R.string.label_artists)
      Search.SECTION_GENRE -> context.getString(R.string.label_genres)
      Search.SECTION_TRACK -> context.getString(R.string.label_tracks)
      else -> ""
    }
  }

  companion object {
    const val COUNT = 4
  }
}
