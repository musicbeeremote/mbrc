package com.kelsos.mbrc.features.minicontrol

import androidx.fragment.app.FragmentManager
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.minicontrol.MiniControlFragment

object MiniControlFactoryImpl : MiniControlFactory {
  override fun attach(fragmentManager: FragmentManager) {
    val fragmentTransaction = fragmentManager.beginTransaction()
    val fragment = MiniControlFragment()
    fragmentTransaction.add(R.id.mini_control, fragment)
    fragmentTransaction.commit()
  }
}