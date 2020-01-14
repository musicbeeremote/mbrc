package com.kelsos.mbrc.features.library

import com.kelsos.mbrc.features.queue.LibraryPopup

interface MenuItemSelectedListener<in T> {

  fun onMenuItemSelected(@LibraryPopup.Action action: String, item: T)

  fun onItemClicked(item: T)
}