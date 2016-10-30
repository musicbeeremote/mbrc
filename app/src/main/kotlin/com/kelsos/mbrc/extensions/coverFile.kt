package com.kelsos.mbrc.extensions

import android.content.Context
import java.io.File

fun Context.coverFile(): File {
  val storage = this.filesDir
  val file = File(storage, "cover.jpg")
  return file
}
