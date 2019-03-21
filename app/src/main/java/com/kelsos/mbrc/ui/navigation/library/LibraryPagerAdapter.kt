package com.kelsos.mbrc.ui.navigation.library

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kelsos.mbrc.ui.navigation.library.albums.AlbumFragment
import com.kelsos.mbrc.ui.navigation.library.artists.ArtistFragment
import com.kelsos.mbrc.ui.navigation.library.genres.GenreFragment
import com.kelsos.mbrc.ui.navigation.library.tracks.BrowseTrackFragment

class LibraryPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

  override fun createFragment(position: Int): Fragment = when (position) {
    Category.SECTION_GENRE -> GenreFragment()
    Category.SECTION_ARTIST -> ArtistFragment()
    Category.SECTION_ALBUM -> AlbumFragment()
    Category.SECTION_TRACK -> BrowseTrackFragment()
    else -> throw IllegalStateException("invalid position")
  }

  override fun getItemCount(): Int = COUNT

  companion object {
    const val COUNT = 4
  }
}
