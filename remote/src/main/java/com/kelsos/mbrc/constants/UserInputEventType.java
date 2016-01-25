package com.kelsos.mbrc.constants;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UserInputEventType {
  public static final String StartConnection = "StartConnection";
  public static final String SettingsChanged = "SettingsChanged";
  public static final String ResetConnection = "ResetConnection";
  public static final String CancelNotification = "CancelNotification";
  public static final String StartDiscovery = "StartDiscovery";
  public static final String KeyVolumeUp = "KeyVolumeUp";
  public static final String KeyVolumeDown = "KeyVolumeDown";

  @StringDef({
      StartConnection,
      SettingsChanged,
      ResetConnection,
      CancelNotification,
      StartDiscovery,
      KeyVolumeDown,
      KeyVolumeUp
  }) @Retention(RetentionPolicy.SOURCE) public @interface Event {

  }
}
