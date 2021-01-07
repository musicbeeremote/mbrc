package com.kelsos.mbrc.features.minicontrol

import androidx.fragment.app.FragmentManager

interface MiniControlFactory {
  fun attach(fragmentManager: FragmentManager)
}
