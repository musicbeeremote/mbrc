package com.kelsos.mbrc.core

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.kelsos.mbrc.interfaces.SimpleLifecycle

abstract class LifeCycleAwareService : SimpleLifecycle, LifecycleOwner {

  @Suppress("LeakingThis")
  private val lifecycleRegistry = LifecycleRegistry(this)

  override fun getLifecycle(): Lifecycle = lifecycleRegistry

  @CallSuper
  override fun stop() {
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
  }

  @CallSuper
  override fun start() {
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
  }
}
