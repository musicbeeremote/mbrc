package com.kelsos.mbrc.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provider;

public class ObjectMapperProvider implements Provider<ObjectMapper> {
  @Override public ObjectMapper get() {
    return new ObjectMapper();
  }
}
