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

    public void requestConnect() {
        if (serverCommunicationEvent != null)
            serverCommunicationEvent.onRequestConnect();
    }

    public void requestConnectionStatus() {
        if (serverCommunicationEvent != null)
            serverCommunicationEvent.onRequestConnectionStatus();
    }

    public void setUserInterfaceEventsListener(UserInterfaceEvent listener) {
        userInterfaceEvent = listener;
    }

    public void activityButtonClicked(ClickSource clickSource) {
        if (userInterfaceEvent != null)
            userInterfaceEvent.onActivityButtonClicked(clickSource);
    }

    public void seekBarChanged(int volume) {
        if (userInterfaceEvent != null)
            userInterfaceEvent.onSeekBarChanged(volume);
    }

    public void playNowRequest(String track) {
        if (userInterfaceEvent != null)
            userInterfaceEvent.onPlayNowRequest(track);
    }
}
