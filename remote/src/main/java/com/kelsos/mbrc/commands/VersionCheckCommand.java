package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.util.SettingsManager;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import roboguice.util.Ln;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class VersionCheckCommand implements ICommand {
    private Model model;
    private ObjectMapper mapper;
    private SettingsManager manager;

    @Inject
    public VersionCheckCommand(Model model, ObjectMapper mapper, SettingsManager manager) {
        this.model = model;
        this.mapper = mapper;
        this.manager = manager;
    }

    @Override
    public void execute(IEvent e) {
        new Thread(new VersionChecker()).start();
    }

    private class VersionChecker implements Runnable {
        @Override
        public void run() {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(manager.getLastUpdated().getTime());
                calendar.add(Calendar.DATE, 2);
                Date nextCheck = new Date(calendar.getTimeInMillis());
                Date now = new Date();

                if (nextCheck.after(now)) {
                    if (BuildConfig.DEBUG) {
                        Ln.e("waiting for next check: " + Long.toString(nextCheck.getTime()));
                    }
                    return;
                }

                JsonNode jsonNode = mapper.readValue(new URL("http://kelsos.net/musicbeeremote/versions.json"), JsonNode.class);
                String version = BuildConfig.VERSION_NAME;
                JsonNode vNode = jsonNode.path("versions").path(version);

                String suggestedVersion = vNode.path("plugin").asText();

                if (!suggestedVersion.equals(model.getPluginVersion())) {
                    boolean isOutOfDate = false;

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

                    if (isOutOfDate) {
                        new MessageEvent(ProtocolEventType.INFORM_CLIENT_PLUGIN_OUT_OF_DATE);
                    }
                }

                manager.setLastUpdated(now);
                if (BuildConfig.DEBUG) {
                    Ln.e("last check on: " + Long.toString(now.getTime()));
                    Ln.e("plugin reported version: " + model.getPluginVersion());
                    Ln.e("plugin suggested version: " + suggestedVersion);
                }

            } catch (MalformedURLException e) {
                if (BuildConfig.DEBUG) {
                    Ln.e(e, "version check MalformedURLException");
                }
            } catch (JsonMappingException e) {
                if (BuildConfig.DEBUG) {
                    Ln.e(e, "version check JsonMappingException");
                }
            } catch (JsonParseException e) {
                if (BuildConfig.DEBUG) {
                    Ln.e(e, "version check parse");
                }
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    Ln.e(e, "version check IOException");
                }
            } catch (NumberFormatException e) {
                if (BuildConfig.DEBUG) {
                    Ln.e(e, "version check NumberFormatException");
                }
            }
        }
    }

}
