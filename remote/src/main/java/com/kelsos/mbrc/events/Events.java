package com.kelsos.mbrc.events;

import com.kelsos.mbrc.events.actions.ButtonPressedEvent;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.events.ui.DiscoveryStatus;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.events.ui.SettingsChange;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public final class Events {
  public static final PublishSubject<Message> messages = PublishSubject.create();
  public static final BehaviorSubject<CoverAvailable> coverAvailableSub = BehaviorSubject.create();
  public static final BehaviorSubject<TrackInfoChange> trackInfoSub = BehaviorSubject.create();
  public static final PublishSubject<SettingsChange> settingsChangeSub = PublishSubject.create();
  public static final PublishSubject<DiscoveryStatus> discoveryStatusSub = PublishSubject.create();
  public static final PublishSubject<ButtonPressedEvent> buttonPressedSub = PublishSubject.create();
  public static final PublishSubject<NotifyUser> userMessageSub = PublishSubject.create();
  public static final BehaviorSubject<ConnectionSettingsChanged> connectionSettingsSub =
      BehaviorSubject.create();

  private Events() { }
}
