package com.kelsos.mbrc.interfaces;

/**
 * The base of a command that is executed by the {@link com.kelsos.mbrc.controller.Controller#executeCommand(IEvent)}
 */
public interface ICommand {
    /**
     * Used to execute the code associated with the event
     * @param e The event passed for execution.
     */
    void execute(final IEvent e);
}
