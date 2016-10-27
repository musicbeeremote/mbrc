package com.kelsos.mbrc.logging

import android.content.Context
import timber.log.Timber

class FileLoggingTree(context: Context): Timber.Tree() {

  init {
    initFileLogging(context)
  }

  override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {


    val logMessage = "$tag\n\n$message"

    when (priority) {
//      Log.VERBOSE -> logger.trace(logMessage)
//      Log.DEBUG -> logger.debug(logMessage)
//      Log.INFO -> logger.info(logMessage)
//      Log.WARN -> logger.warn(logMessage)
//      Log.ERROR ->logger.error(logMessage)
    }
  }

  private fun initFileLogging(context: Context) {

  }
}
