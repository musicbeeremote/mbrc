package com.kelsos.mbrc.constants;

public final class SocketEventType {

    private SocketEventType() { }

    public static final String DATA_AVAILABLE = "SocketDataAvailable";
    public static final String STATUS_CHANGED = "SocketStatusChanged";
    public static final String HANDSHAKE_UPDATE = "SocketHandshakeUpdate";
}
