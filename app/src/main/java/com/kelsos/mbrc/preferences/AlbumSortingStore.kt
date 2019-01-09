package com.kelsos.mbrc.preferences

import com.kelsos.mbrc.content.library.albums.Sorting.Fields
import com.kelsos.mbrc.content.library.albums.Sorting.Order

interface AlbumSortingStore {
  @Fields
  fun getSortingSelection(): Int

  fun setSortingSelection(@Fields sorting: Int)

  @Order
  fun getSortingOrder(): Int

  fun setSortingOrder(@Order order: Int)
}