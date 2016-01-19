package com.kelsos.mbrc.interfaces;

public interface ItemProvider<T> {
  T getById(long id);
}
