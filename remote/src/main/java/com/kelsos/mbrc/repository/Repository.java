package com.kelsos.mbrc.repository;

import java.util.List;
import rx.Observable;

public interface Repository<T> {
  Observable<List<T>> getPageObservable(int offset, int limit);

  Observable<List<T>> getAllObservable();

  List<T> getPage(int offset, int limit);

  List<T> getAll();

  T getById(long id);

  void save(List<T> items);

  void save(T item);

  long count();
}
