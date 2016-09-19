package com.kelsos.mbrc.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentStatePagerAdapter
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.Search
import com.kelsos.mbrc.annotations.Search.Section
import com.kelsos.mbrc.ui.fragments.BrowseAlbumFragment
import com.kelsos.mbrc.ui.fragments.BrowseArtistFragment
import com.kelsos.mbrc.ui.fragments.BrowseGenreFragment
import com.kelsos.mbrc.ui.fragments.BrowseTrackFragment

class LibraryPagerAdapter(activity: FragmentActivity) : FragmentStatePagerAdapter(activity.supportFragmentManager) {
  private val context: Context

  init {
    context = activity.applicationContext
  }

  override fun getItem(@Section position: Int): Fragment? {
    when (position) {
      Search.SECTION_GENRE -> return BrowseGenreFragment()
      Search.SECTION_ARTIST -> return BrowseArtistFragment()
      Search.SECTION_ALBUM -> return BrowseAlbumFragment()
      Search.SECTION_TRACK -> return BrowseTrackFragment()
      else -> {
      }
    }
    return null
  }

  override fun getCount(): Int {
    return count
  }

  override fun getPageTitle(@Section position: Int): CharSequence {
    when (position) {
      Search.SECTION_ALBUM -> return context.getString(R.string.label_albums)
      Search.SECTION_ARTIST -> return context.getString(R.string.label_artists)
      Search.SECTION_GENRE -> return context.getString(R.string.label_genres)
      Search.SECTION_TRACK -> return context.getString(R.string.label_tracks)
      else -> return ""
    }
  }

  companion object {
    private val count = 4
  }
}
