package com.kelsos.mbrc.ui.navigation.main

import android.widget.SeekBar
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class SeekBarThrottler(private val action: (Int) -> Unit) : SeekBar.OnSeekBarChangeListener {

  var fromUser: Boolean = false
    private set
  private val progressRelay = PublishRelay.create<Int>()
  private var disposable: Disposable? = null

  init {
    this.fromUser = false
    disposable = progressRelay.throttleLast(600, TimeUnit.MILLISECONDS)
        .distinct()
        .subscribe { this.onProgressChange(it) }
  }

  override fun onProgressChanged(seekBar: SeekBar, value: Int, fromUser: Boolean) {
    if (fromUser) {
      progressRelay.accept(value)
    }
  }

  override fun onStartTrackingTouch(seekBar: SeekBar) {
    fromUser = true
  }

  override fun onStopTrackingTouch(seekBar: SeekBar) {
    fromUser = false
  }

  private fun onProgressChange(change: Int) {
    action.invoke(change)
  }

  fun terminate() {
    disposable?.dispose()
  }
}
