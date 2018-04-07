package com.kelsos.mbrc

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.support.annotation.CallSuper
import com.kelsos.mbrc.interfaces.SimpleLifecycle

abstract class LifeCycleAwareService : SimpleLifecycle,
  LifecycleOwner {

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