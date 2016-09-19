package com.kelsos.mbrc.helper

import android.widget.SeekBar
import com.jakewharton.rxrelay.PublishRelay
import rx.functions.Action1
import java.util.concurrent.TimeUnit

class VolumeChangeHelper(private val action: Action1<Int>?) : SeekBar.OnSeekBarChangeListener {

  var isUserChangingVolume: Boolean = false
    private set
  private val volumeRelay = PublishRelay.create<Int>()

  init {
    this.isUserChangingVolume = false
    volumeRelay.throttleLast(600, TimeUnit.MILLISECONDS).subscribe { this.onVolumeChange(it) }
  }

  override fun onProgressChanged(seekBar: SeekBar, value: Int, fromUser: Boolean) {
    if (fromUser) {
      volumeRelay.call(value)
    }
  }

  override fun onStartTrackingTouch(seekBar: SeekBar) {
    isUserChangingVolume = true
  }

  override fun onStopTrackingTouch(seekBar: SeekBar) {
    isUserChangingVolume = false
  }

  private fun onVolumeChange(change: Int) {
    action?.call(change)
  }
}
