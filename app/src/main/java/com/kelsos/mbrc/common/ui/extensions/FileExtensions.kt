package com.kelsos.mbrc.common.ui.extensions

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

fun File.md5(): String? {
  try {
    val fin = FileInputStream(this)
    val md5er = MessageDigest.getInstance("MD5")
    val buffer = ByteArray(1024)
    var read: Int
    do {
      read = fin.read(buffer)
      if (read > 0)
        md5er.update(buffer, 0, read)
    } while (read != -1)
    fin.close()
    val digest = md5er.digest() ?: return null
    var str = ""
    digest.indices.forEach {
      str += ((digest[it].toInt() and 0xff) + 0x100).toString(16).substring(1)
    }
    return str
  } catch (e: Exception) {
    return null
  }
}