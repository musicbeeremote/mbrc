package com.kelsos.mbrc.feature.content.playlists

/**
 * Gets the parent path from the current path.
 * @return Parent path, or null if already at root
 */
fun getParentPath(currentPath: String): String? {
  if (currentPath.isEmpty()) return null
  val lastSeparator = currentPath.lastIndexOf('\\')
  return if (lastSeparator == -1) "" else currentPath.substring(0, lastSeparator)
}

/**
 * Gets the display name for a path (the last segment).
 */
fun getPathDisplayName(path: String): String {
  if (path.isEmpty()) return ""
  val lastSeparator = path.lastIndexOf('\\')
  return if (lastSeparator == -1) path else path.substring(lastSeparator + 1)
}
