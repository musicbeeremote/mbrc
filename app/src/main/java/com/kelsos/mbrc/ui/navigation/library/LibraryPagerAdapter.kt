package com.kelsos.mbrc.ui.navigation.library

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.navigation.library.Category.Section
import com.kelsos.mbrc.ui.navigation.library.albums.AlbumFragment
import com.kelsos.mbrc.ui.navigation.library.artists.ArtistFragment
import com.kelsos.mbrc.ui.navigation.library.genres.BrowseGenreFragment
import com.kelsos.mbrc.ui.navigation.library.tracks.BrowseTrackFragment

class LibraryPagerAdapter(
  activity: FragmentActivity
) : FragmentStatePagerAdapter(activity.supportFragmentManager) {
  private val context: Context = activity.applicationContext

  override fun getItem(@Section position: Int): Fragment {
    return when (position) {
      Category.SECTION_GENRE -> BrowseGenreFragment()
      Category.SECTION_ARTIST -> ArtistFragment()
      Category.SECTION_ALBUM -> AlbumFragment()
      Category.SECTION_TRACK -> BrowseTrackFragment()
      else -> error("Invalid position $position")
    }
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