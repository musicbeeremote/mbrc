package com.kelsos.mbrc.ui.navigation.library

import android.content.Context
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.navigation.library.Category.Section
import com.kelsos.mbrc.ui.navigation.library.albums.BrowseAlbumFragment
import com.kelsos.mbrc.ui.navigation.library.artists.BrowseArtistFragment
import com.kelsos.mbrc.ui.navigation.library.genres.BrowseGenreFragment
import com.kelsos.mbrc.ui.navigation.library.tracks.BrowseTrackFragment

class LibraryPagerAdapter(
  activity: androidx.fragment.app.FragmentActivity
) : androidx.fragment.app.FragmentStatePagerAdapter(activity.supportFragmentManager) {
  private val context: Context = activity.applicationContext

  override fun getItem(@Section position: Int): androidx.fragment.app.Fragment? {
    when (position) {
      Category.SECTION_GENRE -> return BrowseGenreFragment()
      Category.SECTION_ARTIST -> return BrowseArtistFragment()
      Category.SECTION_ALBUM -> return BrowseAlbumFragment()
      Category.SECTION_TRACK -> return BrowseTrackFragment()
      else -> {
      }
    }
    return null
  }

  override fun getCount(): Int = COUNT

  override fun getPageTitle(@Section position: Int): CharSequence {
    return when (position) {
      Category.SECTION_ALBUM -> context.getString(R.string.label_albums)
      Category.SECTION_ARTIST -> context.getString(R.string.label_artists)
      Category.SECTION_GENRE -> context.getString(R.string.label_genres)
      Category.SECTION_TRACK -> context.getString(R.string.label_tracks)
      else -> ""
    }
  }

  companion object {
    const val COUNT = 4
  }
}