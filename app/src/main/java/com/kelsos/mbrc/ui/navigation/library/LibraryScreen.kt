package com.kelsos.mbrc.ui.navigation.library

import androidx.lifecycle.LifecycleOwner

interface LibraryScreen {
  fun observe(viewLifecycleOwner: LifecycleOwner)
  fun bind(viewHolder: LibraryViewHolder)
}