package com.kelsos.mbrc.interfaces;

/**
 * Interface that represents the an event.
 */
public interface IEvent {
    /**
     * Gets the type of the event.
     * @return The string representing the type.
     */
    String getType();

    /**
     * Returns the object containing the data of the event.
     * @return Object with the data
     */
    Object getData();

    /**
     * Returns the string representations of the data.
     * @return String representation of the data
     */
    String getDataString();
}
