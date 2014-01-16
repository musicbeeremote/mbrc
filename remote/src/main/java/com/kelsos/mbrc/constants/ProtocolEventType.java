package com.kelsos.mbrc.constants;


public final class ProtocolEventType {

    private ProtocolEventType() { }

    public static final String INITIATE_PROTOCOL_REQUEST = "InitiateProtocolRequest";
    public static final String REDUCE_VOLUME = "ReduceVolume";
    public static final String HANDSHAKE_COMPLETE = "HandshakeComplete";
    public static final String INFORM_CLIENT_NOT_ALLOWED = "InformClientNotAllowed";
    public static final String INFORM_CLIENT_PLUGIN_OUT_OF_DATE = "InformClientPluginOutOfDate";
    public static final String NO_SETTINGS_AVAILABLE = "DisplayDialog";
    public static final String USER_ACTION = "UserAction";
    public static final String PLUGIN_VERSION_CHECK = "PluginVersionCheck";
}
