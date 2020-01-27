package com.kelsos.mbrc.features.library

import com.kelsos.mbrc.features.queue.Queue

interface MenuItemSelectedListener<in T> {

  fun onMenuItemSelected(@Queue.Action action: String, item: T)

  fun onItemClicked(item: T)
}