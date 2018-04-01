package com.kelsos.mbrc.utilities

import timber.log.Timber

class CustomLoggingTree : Timber.DebugTree() {
  override fun createStackElementTag(element: StackTraceElement): String {
    return with(element) {
      val currentThread = Thread.currentThread()
      "${super.createStackElementTag(this)}:$lineNumber {${currentThread.name}}"
    }
  }

  companion object {
    fun create(): CustomLoggingTree {
      return CustomLoggingTree()
    }
  }
}
