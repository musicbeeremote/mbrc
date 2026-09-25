package com.kelsos.mbrc.core.common.utilities.logging

import android.util.Log
import java.io.Closeable
import java.io.File
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import timber.log.Timber

/**
 * Writes the log to [LOGS_DIR] under [filesDir].
 *
 * Must be [close]d once it is uprooted. The handler is attached to a process-wide [Logger] and
 * holds a lock file next to the log, so an uprooted tree that is never closed keeps writing every
 * line, and the next tree finds the log locked and opens a second file beside it.
 */
class FileLoggingTree(filesDir: File) :
  Timber.DebugTree(),
  Closeable {
  private val handler: FileHandler
  private val logger: Logger = Logger.getLogger(LOGGER_NAME)

  init {
    logger.level = Level.ALL
    logger.useParentHandlers = false

    val logDir = File(filesDir, LOGS_DIR)
    if (!logDir.exists()) {
      logDir.mkdir()
    }

    val logFile = File(logDir, LOG_FILE)

    handler = FileHandler(logFile.canonicalPath, LOG_SIZE, MAX_LOGS, true)
    handler.formatter = SimpleFormatter()
    logger.addHandler(handler)
  }

  override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
    val logMessage = "[$tag]\t$message"

    when (priority) {
      Log.VERBOSE -> logger.log(Level.ALL, logMessage)
      Log.DEBUG -> logger.log(Level.FINE, logMessage)
      Log.INFO -> logger.log(Level.INFO, logMessage)
      Log.WARN -> logger.log(Level.WARNING, logMessage)
      Log.ERROR -> logger.log(Level.SEVERE, logMessage)
    }
  }

  override fun close() {
    logger.removeHandler(handler)
    handler.close()
  }

  override fun createStackElementTag(element: StackTraceElement): String =
    "${super.createStackElementTag(element)}:${element.lineNumber} [${Thread.currentThread().name}]"

  companion object {
    const val LOGGER_NAME = "mbrc-logger"
    const val LOGS_DIR = "logs"
    const val LOG_FILE = "mbrc.log"
    const val LOG_SIZE = 1024 * 1024
    const val MAX_LOGS = 3
  }
}
