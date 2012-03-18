package kelsos.mbremote.Messaging;

public interface ServerCommunicationEvent {
    public abstract void onRequestConnect();
    public abstract void onRequestConnectionStatus();
}
