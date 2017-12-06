package com.kelsos.mbrc.logging

import android.content.Context
import com.kelsos.mbrc.logging.FileLoggingTree.Companion.LOGS_DIR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object LogHelper {

  fun logsExist(context: Context, result: (exists: Boolean) -> Unit) = runBlocking {
    withContext(Dispatchers.IO) {
      val exists = try {
        val filesDir = context.filesDir
        val logDir = File(filesDir, FileLoggingTree.LOGS_DIR)
        val logFiles = logDir.listFiles()?.filter { it.extension != "lck" }
        logFiles.isNullOrEmpty()
      } catch (e: Exception) {
        false
      }
      withContext(Dispatchers.Main) {
        result(exists)
      }
    }
  }

  fun zipLogs(context: Context, result: (logs: File?) -> Unit) = runBlocking {
    withContext(Dispatchers.IO) {
      val filesDir = context.filesDir
      val cacheDir = context.externalCacheDir
      val logDir = File(filesDir, FileLoggingTree.LOGS_DIR)
      if (!logDir.exists()) {
        Timber.v("No dir found %s", logDir.canonicalPath)
        result(null)
      }

      val logFiles = logDir.listFiles()?.filter {
        it.extension != "lck"
      }

      if (logFiles.isNullOrEmpty()) {
        Timber.v("No log files found")
        result(null)
        return@withContext
      }

      try {
        val buffer = ByteArray(1024)

        val zipDir = File(cacheDir, LOGS_DIR)
        if (!zipDir.exists()) {
          zipDir.mkdir()
        }
        val zipFile = File(zipDir, LOG_ZIP)

        if (zipFile.exists()) {
          zipFile.delete()
        }

        val fos = FileOutputStream(zipFile)
        val zos = ZipOutputStream(fos)
        logFiles.forEach {
          val ze = ZipEntry(it.name)
          zos.putNextEntry(ze)
          val fin = FileInputStream(it)

          var len: Int
          do {
            len = fin.read(buffer)
            if (len <= 0) {
              break
            }
            zos.write(buffer, 0, len)
          } while (true)

          fin.close()
          zos.closeEntry()
        }

        zos.close()
        fos.flush()
        fos.close()

        withContext(Dispatchers.Main) {
          result(zipFile)
        }
      } catch (e: IOException) {
        throw RuntimeException(e)
      }
    }
  }

  const val LOG_ZIP = "mbrc_logs.zip"
}
