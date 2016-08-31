package com.kelsos.mbrc.connection_manager;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.kelsos.mbrc.data.ConnectionSettings;

import java.util.List;

@AutoValue
public abstract class ConnectionModel {
  abstract long defaultId();

  abstract List<ConnectionSettings> settings();

  @NonNull
  public static Builder builder() {
    return new AutoValue_ConnectionModel.Builder();
  }

  public static ConnectionModel create(long defaultId, List<ConnectionSettings> settings) {
    return new AutoValue_ConnectionModel.Builder().defaultId(defaultId).settings(settings).build();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder defaultId(long defaultId);

    public abstract Builder settings(List<ConnectionSettings> settings);

    public abstract ConnectionModel build();
  }
}
