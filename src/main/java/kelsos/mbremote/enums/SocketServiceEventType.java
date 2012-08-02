package kelsos.mbremote.enums;

import kelsos.mbremote.Interfaces.IEventType;

public enum SocketServiceEventType implements IEventType
{
    PacketAvailable,
    StatusChange,
    HandshakeUpdate
}
