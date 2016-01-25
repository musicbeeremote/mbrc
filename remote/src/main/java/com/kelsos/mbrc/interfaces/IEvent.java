package com.kelsos.mbrc.interfaces;

import com.kelsos.mbrc.constants.UserInputEventType;

/**
 * Interface that represents the an event.
 */
public interface IEvent {
  /**
   * Gets the type of the event.
   *
   * @return The string representing the type.
   */
  @UserInputEventType.Event String getType();
}
