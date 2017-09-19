package com.kelsos.mbrc.preferences

import com.kelsos.mbrc.content.library.albums.Sorting.Fields
import com.kelsos.mbrc.content.library.albums.Sorting.Order

interface AlbumSortingStore {
  @Fields
  fun getSortingSelection(): Long

  fun setSortingSelection(@Fields sorting: Long)

  @Order
  fun getSortingOrder(): Long

  fun setSortingOrder(@Order order: Long)
}
