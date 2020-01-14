package com.kelsos.mbrc.features.library

import androidx.annotation.IdRes

interface MenuItemSelectedListener<in T> {
  fun onMenuItemSelected(@IdRes itemId: Int, item: T)
  fun onItemClicked(item: T)
}
