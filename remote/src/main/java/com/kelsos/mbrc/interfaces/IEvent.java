package com.kelsos.mbrc.interfaces;

/**
 * Interface that represents the an event.
 */
public interface IEvent {
  /**
   * Gets the type of the event.
   *
   * @return The string representing the type.
   */
  String getType();

  /**
   * Returns a string contained in the message
   *
   * @return Object with the data
   */
  String getMessage();
}
