package com.kelsos.mbrc.logging

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class LogHelper {

  suspend fun logsExist(filesDir: File): Boolean = withContext(Dispatchers.IO) {
    try {
      val logDir = File(filesDir, FileLoggingTree.LOGS_DIR)
      logDir.listFiles()?.any { it.extension != "lck" } ?: false
    } catch (e: Exception) {
      return@withContext false
    }
  }

  suspend fun zipLogs(filesDir: File, cacheDir: File?): File = withContext(Dispatchers.IO) {
    val logDir = File(filesDir, FileLoggingTree.LOGS_DIR)
    if (!logDir.exists()) {
      throw FileNotFoundException(logDir.canonicalPath)
    }

    val logFiles = logDir.listFiles()?.filter {
      it.extension != "lck"
    }

    if (logFiles.isNullOrEmpty()) {
      throw RuntimeException("No log files found")
    }

    try {
      val buffer = ByteArray(1024)
      val zipFile = File(cacheDir, LOG_ZIP)

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

      zipFile
    } catch (e: IOException) {
      throw RuntimeException(e)
    }
  }

  companion object {
    private const val LOG_ZIP = "mbrc_logs.zip"
  }
}
