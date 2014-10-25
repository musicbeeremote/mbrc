package com.kelsos.mbrc.controller;

import com.google.inject.Inject;
import com.kelsos.mbrc.commands.*;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.SocketEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.net.Notification;

/**
 * Used to initialize the controller's configuration.
 */
public final class Configuration {

    private Configuration() { }

    /**
     * Initializes the mapping between events and commands for the passed controller.
     * @param controller The active controller.
     */
    @Inject public static void initialize(Controller controller) {
        controller.register(ProtocolEventType.REDUCE_VOLUME, ReduceVolumeOnRingCommand.class);
        controller.register(ProtocolEventType.INFORM_CLIENT_PLUGIN_OUT_OF_DATE, NotifyPluginOutOfDateCommand.class);
        controller.register(ProtocolEventType.PLUGIN_VERSION_CHECK, VersionCheckCommand.class);
        controller.register(Notification.TRACK_CHANGED, RequestTrackData.class);
        controller.register(Notification.PLAY_STATUS_CHANGED, RequestPlayState.class);
        controller.register(Notification.REPEAT_STATUS_CHANGED, RequestRepeatState.class);
        controller.register(Notification.VOLUME_CHANGED, RequestVolume.class);
        controller.register(Notification.MUTE_STATUS_CHANGED, RequestMuteState.class);
        controller.register(Notification.SHUFFLE_STATUS_CHANGED, RequestShuffleState.class);
        controller.register(Notification.SCROBBLE_STATUS_CHANGED, RequestScrobbleState.class);
        controller.register(Notification.LYRICS_CHANGED, RequestLyrics.class);


        controller.register(Notification.POSITION_CHANGED, RequestPosition.class);

        controller.register(UserInputEventType.SETTINGS_CHANGED, RestartConnectionCommand.class);
        controller.register(UserInputEventType.CANCEL_NOTIFICATION, CancelNotificationCommand.class);
        controller.register(UserInputEventType.START_CONNECTION, InitiateConnectionCommand.class);
        controller.register(UserInputEventType.RESET_CONNECTION, RestartConnectionCommand.class);
        controller.register(UserInputEventType.START_DISCOVERY, StartDiscoveryCommand.class);
        controller.register(UserInputEventType.KEY_VOLUME_UP, KeyVolumeUpCommand.class);
        controller.register(UserInputEventType.KEY_VOLUME_DOWN, KeyVolumeDownCommand.class);

        controller.register(SocketEventType.STATUS_CHANGED, ConnectionStatusChangedCommand.class);
    }
}
