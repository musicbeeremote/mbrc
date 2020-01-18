package com.kelsos.mbrc.features.library.presentation.screens

import androidx.lifecycle.LifecycleOwner
import com.kelsos.mbrc.features.library.presentation.LibraryViewHolder

interface LibraryScreen {
  fun observe(viewLifecycleOwner: LifecycleOwner)
  fun bind(viewHolder: LibraryViewHolder)
}