@file:Suppress("unused")

package com.kelsos.mbrc.di.modules

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.inject.AbstractModule

class DebugModule : AbstractModule() {
  override fun configure() {
    bind(okhttp3.Interceptor::class.java).to(StethoInterceptor::class.java)
  }
}
