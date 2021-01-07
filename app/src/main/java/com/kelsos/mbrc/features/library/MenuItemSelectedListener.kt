package com.kelsos.mbrc.features.library

import com.kelsos.mbrc.features.queue.Queue

interface MenuItemSelectedListener<in T> {

  fun onMenuItemSelected(action: Queue, item: T)

  fun onItemClicked(item: T)
}
