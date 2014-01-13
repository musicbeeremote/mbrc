package com.kelsos.mbrc.constants;


public class ProtocolEventType {

    private ProtocolEventType() {}

    public static final String InitiateProtocolRequest = "InitiateProtocolRequest";
    public static final String ReduceVolume = "ReduceVolume";
    public static final String HandshakeComplete = "HandshakeComplete";
    public static final String InformClientNotAllowed = "InformClientNotAllowed";
    public static final String InformClientPluginOutOfDate = "InformClientPluginOutOfDate";
    public static final String NoSettingsAvailable = "DisplayDialog";
    public static final String UserAction = "UserAction";
    public static final String PluginVersionCheck = "PluginVersionCheck";
}
