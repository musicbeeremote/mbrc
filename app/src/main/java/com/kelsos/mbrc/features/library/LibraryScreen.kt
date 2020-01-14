package com.kelsos.mbrc.features.library

import androidx.lifecycle.LifecycleOwner

interface LibraryScreen {
  fun observe(viewLifecycleOwner: LifecycleOwner)
  fun bind(viewHolder: LibraryViewHolder)
}
