@file:Suppress("unused")

package com.kelsos.mbrc.di.modules

import com.facebook.stetho.okhttp3.StethoInterceptor
import toothpick.config.Module

class DebugModule : Module() {
  init {
    bind(okhttp3.Interceptor::class.java).to(StethoInterceptor::class.java).singletonInScope()
  }
}
