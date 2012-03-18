package kelsos.mbremote.Messaging;

public class Communicator {
    private static Communicator ourInstance = new Communicator();

    private UserInterfaceEvent userInterfaceEvent;
    private ServerCommunicationEvent serverCommunicationEvent;

    public static Communicator getInstance() {
        return ourInstance;
    }

    private Communicator() {
        userInterfaceEvent = null;
        serverCommunicationEvent = null;
    }

    public void setServerCommunicationEventListener(ServerCommunicationEvent listener) {
        serverCommunicationEvent = listener;
    }

    public void onRequestConnect() {
        if (serverCommunicationEvent != null)
            serverCommunicationEvent.onRequestConnect();
    }

    public void onRequestConnectionStatus() {
        if (serverCommunicationEvent != null)
            serverCommunicationEvent.onRequestConnectionStatus();
    }

    public void setUserInterfaceEventsListener(UserInterfaceEvent listener) {
        userInterfaceEvent = listener;
    }

    public void onActivityButtonClicked(ClickSource clickSource) {
        if (userInterfaceEvent != null)
            userInterfaceEvent.onActivityButtonClicked(clickSource);
    }

    public void onSeekBarChanged(int volume) {
        if (userInterfaceEvent != null)
            userInterfaceEvent.onSeekBarChanged(volume);
    }

    public void onPlayNowRequest(String track) {
        if (userInterfaceEvent != null)
            userInterfaceEvent.onPlayNowRequest(track);
    }
}
