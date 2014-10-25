package com.kelsos.mbrc.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.google.inject.Inject;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.enums.DiscoveryStop;
import com.kelsos.mbrc.events.ui.DiscoveryStopped;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

public class ServiceDiscovery {
    private WifiManager manager;
    private WifiManager.MulticastLock mLock;
    private ObjectMapper mapper;
    private Context mContext;

    @Inject public ServiceDiscovery(Context context, ObjectMapper mapper) {
        this.mContext = context;
        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.mapper = mapper;
    }

    public void startDiscovery() {
        if (!isWifiConnected()) {
            new DiscoveryStopped(DiscoveryStop.NO_WIFI);
            return;
        }
        mLock = manager.createMulticastLock("locked");
        mLock.setReferenceCounted(true);
        mLock.acquire();

        Thread mThread = new Thread(new ServiceListener());
        mThread.start();
    }

    public void stopDiscovery() {
        if (mLock != null) {
            mLock.release();
            mLock = null;
        }
    }

    private String getWifiAddress() {
        WifiInfo mInfo = manager.getConnectionInfo();
        int address = mInfo.getIpAddress();
        return String.format(Locale.ENGLISH,
                "%d.%d.%d.%d",
                (address & 0xff),
                (address >> 8 & 0xff),
                (address >> 16 & 0xff),
                (address >> 24 & 0xff));
    }

    private boolean isWifiConnected() {
        ConnectivityManager cMan = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = cMan.getActiveNetworkInfo();
        return current != null && current.getType() == ConnectivityManager.TYPE_WIFI;
    }

    private class ServiceListener implements Runnable {

        public static final int BUFFER = 512;
        public static final int PORT = 45345;
        public static final int TIMEOUT = 2000;
        public static final String HOST = "239.1.5.10";

        @Override public void run() {
            try {

                MulticastSocket mSocket = new MulticastSocket(PORT);
                mSocket.setSoTimeout(TIMEOUT);
                InetAddress group = InetAddress.getByName(HOST);
                mSocket.joinGroup(group);

                DatagramPacket mPacket;
                byte[] buffer = new byte[BUFFER];
                mPacket = new DatagramPacket(buffer, buffer.length);
                Map<String, String> discoveryMessage = new Hashtable<>();
                discoveryMessage.put("context", "discovery");
                discoveryMessage.put("address", getWifiAddress());
                byte[] discovery = mapper.writeValueAsBytes(discoveryMessage);
                mSocket.send(new DatagramPacket(discovery, discovery.length, group, PORT));
                String incoming;

                while (true) {
                    mSocket.receive(mPacket);
                    incoming = new String(mPacket.getData());

                    JsonNode node = mapper.readValue(incoming, JsonNode.class);
                    if (node.path("context").asText().equals("notify")) {
                        ConnectionSettings settings = new ConnectionSettings(node);
                        break;
                    }
                }

                new DiscoveryStopped(DiscoveryStop.COMPLETE);
                mSocket.leaveGroup(group);
                mSocket.close();
                stopDiscovery();

            } catch (InterruptedIOException e) {
                new DiscoveryStopped(DiscoveryStop.NOT_FOUND);
                stopDiscovery();
            } catch (IOException e) {
                new DiscoveryStopped(DiscoveryStop.NOT_FOUND);
            }
        }
    }
}
