package com.kelsos.mbrc.preferences

import android.content.SharedPreferences
import com.kelsos.mbrc.content.library.albums.Sorting
import javax.inject.Inject

class AlbumSortingStoreImpl
@Inject
constructor(private val sharedPreferences: SharedPreferences) : AlbumSortingStore {
  @Sorting.Fields
  override fun getSortingSelection(): Long {
    return sharedPreferences.getLong(ALBUM_SORTING_KEY, Sorting.ALBUM_ARTIST__ALBUM)
  }

  override fun setSortingSelection(@Sorting.Fields sorting: Long) {
    sharedPreferences.edit()
        .putLong(ALBUM_SORTING_KEY, sorting)
        .apply()
  }

  @Sorting.Order
  override fun getSortingOrder(): Long {
    return sharedPreferences.getLong(ALBUM_SORTING_ORDER_KEY, Sorting.ORDER_ASCENDING)
  }

  override fun setSortingOrder(@Sorting.Order order: Long) {
    sharedPreferences.edit()
        .putLong(ALBUM_SORTING_ORDER_KEY, order)
        .apply()
  }

  companion object {
    private const val ALBUM_SORTING_KEY = "com.kelsos.mbrc.preferences.ALBUM_SORTING"
    private const val ALBUM_SORTING_ORDER_KEY = "com.kelsos.mbrc.preferences.ALBUM_SORTING_ORDER"
  }
}
