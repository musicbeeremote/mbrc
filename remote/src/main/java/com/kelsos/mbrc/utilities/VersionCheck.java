package com.kelsos.mbrc.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import roboguice.util.Ln;

public class VersionCheck {
  private ObjectMapper mapper;
  private SettingsManager manager;
  private String pluginVersion;

  @Inject public VersionCheck(ObjectMapper mapper, SettingsManager manager) {
    this.mapper = mapper;
    this.manager = manager;
  }

  public void setPluginVersion(String pluginVersion) {
    this.pluginVersion = pluginVersion.substring(0, pluginVersion.lastIndexOf('.'));
  }

  public void start() {
    new Thread(new VersionChecker()).start();
  }

  private boolean isOutOfDate() throws IOException {
    boolean isOutOfDate = false;

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(manager.getLastUpdated().getTime());
    calendar.add(Calendar.DATE, 2);
    Date nextCheck = new Date(calendar.getTimeInMillis());
    Date now = new Date();

    if (nextCheck.after(now)) {
      if (BuildConfig.DEBUG) {
        Ln.d("next check: %s", Long.toString(nextCheck.getTime()));
      }
      return true;
    }

    JsonNode jsonNode =
        mapper.readValue(new URL("http://kelsos.net/musicbeeremote/versions.json"), JsonNode.class);
    String version = BuildConfig.VERSION_NAME;
    JsonNode vNode = jsonNode.path("versions").path(version);

    String suggestedVersion = vNode.path("plugin").asText();

    if (!suggestedVersion.equals(pluginVersion)) {

      String[] currentVersion = pluginVersion.split("\\.");
      String[] latestVersion = suggestedVersion.split("\\.");

      int i = 0;
      while (i < currentVersion.length && i < latestVersion.length && currentVersion[i].equals(
          latestVersion[i])) {
        i++;
      }

      if (i < currentVersion.length && i < latestVersion.length) {
        int diff = Integer.valueOf(currentVersion[i]).compareTo(Integer.valueOf(latestVersion[i]));
        isOutOfDate = diff < 0;
      }
    }

    manager.setLastUpdated(now);
    if (BuildConfig.DEBUG) {
      Ln.d("last check on: %s", Long.toString(now.getTime()));
      Ln.d("plugin reported version: %s", pluginVersion);
      Ln.d("plugin suggested version: %s", suggestedVersion);
    }

    return isOutOfDate;
  }

  private class VersionChecker implements Runnable {
    @Override public void run() {
      try {
        if (isOutOfDate()) {

        }
      } catch (IOException | NumberFormatException e) {
        if (BuildConfig.DEBUG) {
          Ln.d(e);
        }
      }
    }
  }
}
