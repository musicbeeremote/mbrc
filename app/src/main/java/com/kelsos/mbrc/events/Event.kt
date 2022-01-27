package com.kelsos.mbrc.events

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(
  private val content: T,
) {
  private var hasBeenHandled = false

  /**
   * Returns the content and prevents its use again.
   */
  val contentIfNotHandled: T?
    get() {
      return if (hasBeenHandled) {
        null
      } else {
        hasBeenHandled = true
        content
      }
    }
}
