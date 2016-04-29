package com.kelsos.mbrc.interfaces

import com.kelsos.mbrc.constants.UserInputEventType

/**
 * Interface that represents the an event.
 */
interface IEvent {
  /**
   * Gets the type of the event.

   * @return The string representing the type.
   */
  val type: String
}
