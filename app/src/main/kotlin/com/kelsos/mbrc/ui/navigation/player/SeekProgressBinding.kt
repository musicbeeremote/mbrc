package com.kelsos.mbrc.ui.navigation.player

import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener

inline fun SeekBar.setOnSeekBarChangeListener(
  crossinline listener: (progress: Int) -> Unit
) {
  setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
      if (!fromUser) {
        return
      }
      listener(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

    override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
  })
}
