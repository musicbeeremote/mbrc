package com.kelsos.mbrc.ui.navigation.player

import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.BindingAdapter


@BindingAdapter(
  value = ["onProgressChangeByUser"]
)
fun SeekBar.setOnSeekBarChangeListener(
  listener: OnProgressChangedByUserListener
) {
  setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
      if (!fromUser) {
        return
      }
      listener.onProgressChanged(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

    override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
  })
}

interface OnProgressChangedByUserListener {
  fun onProgressChanged(progress: Int)
}