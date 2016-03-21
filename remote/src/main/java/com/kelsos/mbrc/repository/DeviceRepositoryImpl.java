package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.domain.DeviceSettings;
import com.kelsos.mbrc.domain.DeviceSettings_Table;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import java.util.List;
import rx.Observable;

public class DeviceRepositoryImpl implements DeviceRepository {
  @Override public Observable<List<DeviceSettings>> getPageObservable(int offset, int limit) {
    return Observable.defer(() -> Observable.just(getPage(offset, limit)));
  }

  @Override public Observable<List<DeviceSettings>> getAllObservable() {
    return Observable.defer(() -> Observable.just(getAll()));
  }

  @Override public List<DeviceSettings> getPage(int offset, int limit) {
    return SQLite.select().from(DeviceSettings.class).limit(limit).offset(offset).queryList();
  }

  @Override public List<DeviceSettings> getAll() {
    return SQLite.select().from(DeviceSettings.class).queryList();
  }

  @Override public DeviceSettings getById(long id) {
    return SQLite.select().from(DeviceSettings.class).where(DeviceSettings_Table.id.eq(id)).querySingle();
  }

  @Override public void save(List<DeviceSettings> items) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Observable.from(items).forEach(DeviceSettings::save);
    });
  }

  @Override public void save(DeviceSettings item) {
    item.save();
  }

  @Override public long count() {
    return SQLite.selectCountOf().from(DeviceSettings.class).count();
  }
}
