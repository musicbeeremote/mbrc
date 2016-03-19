package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.domain.DeviceSettings;
import java.util.List;
import rx.Observable;

public class DeviceRepositoryImpl implements DeviceRepository {
  @Override public Observable<List<DeviceSettings>> getPage(int offset, int limit) {
    return null;
  }

  @Override public Observable<List<DeviceSettings>> getAll() {
    return null;
  }

  @Override public DeviceSettings getById(int id) {
    return null;
  }

  @Override public void save(List<DeviceSettings> items) {

  }

  @Override public void save(DeviceSettings item) {

  }

  @Override public long count() {
    return 0;
  }
}
