package com.kelsos.mbrc.extensions

val String.Companion.empty: String
    get() = "";

fun String.escapeLike(): String {
  return this.replace("%", "_")
}
