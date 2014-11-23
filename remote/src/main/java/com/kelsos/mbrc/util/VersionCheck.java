package com.kelsos.mbrc.util;

import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.data.model.Model;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import roboguice.util.Ln;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class VersionCheck {
    private Model model;
    private ObjectMapper mapper;
    private SettingsManager manager;

    @Inject
    public VersionCheck(Model model, ObjectMapper mapper, SettingsManager manager) {
        this.model = model;
        this.mapper = mapper;
        this.manager = manager;
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
                Ln.d("waiting for next check: %s", Long.toString(nextCheck.getTime()));
            }
            return true;
        }

        JsonNode jsonNode = mapper.readValue(new URL("http://kelsos.net/musicbeeremote/versions.json"), JsonNode.class);
        String version = BuildConfig.VERSION_NAME;
        JsonNode vNode = jsonNode.path("versions").path(version);

        String suggestedVersion = vNode.path("plugin").asText();

        if (!suggestedVersion.equals(model.getPluginVersion())) {

            String[] currentVersion = model.getPluginVersion().split("\\.");
            String[] latestVersion = suggestedVersion.split("\\.");

            int i = 0;
            while (i < currentVersion.length
                    && i < latestVersion.length
                    && currentVersion[i].equals(latestVersion[i])) {
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
            Ln.d("plugin reported version: %s", model.getPluginVersion());
            Ln.d("plugin suggested version: %s", suggestedVersion);
        }

        return isOutOfDate;
    }

    private class VersionChecker implements Runnable {
        @Override
        public void run() {
            try {
                if (isOutOfDate()) return;

            } catch (IOException | NumberFormatException e) {
                if (BuildConfig.DEBUG) {
                    Ln.d(e);
                }
            }
        }
    }

}
