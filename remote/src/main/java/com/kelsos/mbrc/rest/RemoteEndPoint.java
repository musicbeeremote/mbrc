package com.kelsos.mbrc.rest;

import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;

@Singleton
public class RemoteEndPoint {

  private static final String DEFAULT_ENDPOINT =
      String.format("http://%s:8188", BuildConfig.DEVHOST);
  private String mEndPoint;

  public void setConnectionSettings(String address, int port) {
    mEndPoint = String.format("http://%s:%d", address, port);
  }

  public String getUrl() {
    return mEndPoint != null ? mEndPoint : DEFAULT_ENDPOINT;
  }

  public String getName() {
    return "remote";
  }
}
