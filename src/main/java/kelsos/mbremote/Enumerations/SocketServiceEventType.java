package kelsos.mbremote.Enumerations;

import kelsos.mbremote.Interfaces.IEventType;

public enum SocketServiceEventType implements IEventType
{
    PacketAvailable,
    StatusChange,
    HandshakeUpdate
}
