package com.kelsos.mbrc.logging

import android.content.Context
import android.util.Log
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import org.slf4j.LoggerFactory
import timber.log.Timber
import java.io.File

class FileLoggingTree(context: Context): Timber.Tree() {

  init {
    initFileLogging(context)
  }

  override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {

    val logger = LoggerFactory.getLogger(FileLoggingTree::class.java)
    val logMessage = "$tag\n\n$message"

    when (priority) {
      Log.VERBOSE -> logger.trace(logMessage)
      Log.DEBUG -> logger.debug(logMessage)
      Log.INFO -> logger.info(logMessage)
      Log.WARN -> logger.warn(logMessage)
      Log.ERROR ->logger.error(logMessage)
    }
  }

  private fun initFileLogging(context: Context) {
    val LOG_DIR = context.filesDir
    val LOG_FILE = File(LOG_DIR, "mbrc.log");
    val lc = LoggerFactory.getILoggerFactory() as LoggerContext
    lc.reset()

    // setup FileAppender
    val encoder1 = PatternLayoutEncoder()
    encoder1.context = lc
    encoder1.pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    encoder1.start()

    val namingPolicy = SizeAndTimeBasedFNATP<ILoggingEvent>()
    namingPolicy.maxFileSize = "2MB"
    namingPolicy.start()

    val rollingPolicy = TimeBasedRollingPolicy<ILoggingEvent>()
    rollingPolicy.maxHistory = 3
    rollingPolicy.fileNamePattern = "$LOG_DIR/log.%d{yyyy-MM-dd}.%i.html"
    rollingPolicy.timeBasedFileNamingAndTriggeringPolicy = namingPolicy
    rollingPolicy.start()

    val fileAppender = RollingFileAppender<ILoggingEvent>()
    fileAppender.triggeringPolicy = rollingPolicy
    fileAppender.context = lc
    fileAppender.file = LOG_FILE.absolutePath
    fileAppender.encoder = encoder1
    fileAppender.start()

    // setup LogcatAppender
    val encoder2 = PatternLayoutEncoder()
    encoder2.context = lc
    encoder2.pattern = "[%thread] %msg%n"
    encoder2.start()

    // add the newly created appenders to the root logger;
    // qualify Logger to disambiguate from org.slf4j.Logger
    val root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
    root.addAppender(fileAppender)
  }
}
