package kelsos.mbremote.enums;

import kelsos.mbremote.Interfaces.IEventType;

public enum SocketServiceEventType implements IEventType
{
	SOCKET_EVENT_PACKET_AVAILABLE,
	SOCKET_EVENT_STATUS_CHANGE,
	SOCKET_EVENT_HANDSHAKE_UPDATE
}
