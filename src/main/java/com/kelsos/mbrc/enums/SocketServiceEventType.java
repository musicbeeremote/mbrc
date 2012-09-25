package com.kelsos.mbrc.enums;

import com.kelsos.mbrc.interfaces.IEventType;

public enum SocketServiceEventType implements IEventType
{
	SOCKET_EVENT_PACKET_AVAILABLE,
	SOCKET_EVENT_STATUS_CHANGE,
	SOCKET_EVENT_HANDSHAKE_UPDATE
}
