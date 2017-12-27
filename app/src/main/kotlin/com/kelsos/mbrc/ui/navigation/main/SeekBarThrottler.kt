package com.kelsos.mbrc.ui.navigation.main

import android.widget.SeekBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SeekBarThrottler(private val action: (Int) -> Unit) : SeekBar.OnSeekBarChangeListener {

  var fromUser: Boolean = false
    private set
  private val progressRelay = MutableStateFlow(0)
  private val job = SupervisorJob()
  private val scope = CoroutineScope(job + Dispatchers.IO)
  private var progressJob: Job? = null

  init {
    this.fromUser = false
    progressJob = progressRelay.sample(600)
      .distinctUntilChanged()
      .onEach { this.onProgressChange(it) }
      .launchIn(scope)
  }

  override fun onProgressChanged(seekBar: SeekBar, value: Int, fromUser: Boolean) {
    if (fromUser) {
      progressRelay.tryEmit(value)
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
    scope.launch { progressJob?.cancelAndJoin() }
  }
}
