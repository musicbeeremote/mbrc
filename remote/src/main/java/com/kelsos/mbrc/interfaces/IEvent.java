package com.kelsos.mbrc.interfaces;

public interface IEvent {
  String getType();

  Object getData();

  String getDataString();
}
