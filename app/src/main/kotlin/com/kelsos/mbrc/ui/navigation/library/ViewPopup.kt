package com.kelsos.mbrc.ui.navigation.library

import android.support.annotation.MenuRes
import android.support.v7.widget.PopupMenu
import android.view.View

fun View.popup(@MenuRes menu: Int, onSelect: (itemId: Int) -> Unit) {
  val popupMenu = PopupMenu(this.context, this)

  popupMenu.inflate(menu)
  popupMenu.setOnMenuItemClickListener {
    onSelect(it.itemId)
    true
  }
  popupMenu.show()
}