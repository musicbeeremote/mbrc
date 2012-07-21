package kelsos.mbremote.Enumerations;

import kelsos.mbremote.Interfaces.IEventType;

public enum RawSocketAction implements IEventType
{
    PacketAvailable,
    StatusChange,
    HandshakeUpdate
}
