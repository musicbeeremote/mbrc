package com.kelsos.mbrc.connection_manager;

import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.presenters.BasePresenter;
import com.kelsos.mbrc.repository.ConnectionRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;


public class ConnectionManagerPresenterImpl extends BasePresenter<ConnectionManagerView>
    implements ConnectionManagerPresenter {

  private ConnectionRepository repository;

  @Inject
  public ConnectionManagerPresenterImpl(ConnectionRepository repository) {
    this.repository = repository;
  }

  @Override
  public void load() {
    checkIfAttached();
    Observable<List<ConnectionSettings>> all = Observable.defer(() -> Observable.just(repository.getAll()));
    Observable<Long> defaultId = Observable.defer(() -> Observable.just(repository.getDefaultId()));

    addSubcription(Observable.zip(defaultId, all, ConnectionModel::create)
        .subscribe(getView()::updateModel, this::onLoadError));
  }

  @Override
  public void setDefault(ConnectionSettings settings) {
    repository.setDefault(settings);
  }

  @Override
  public void update(ConnectionSettings settings) {
    repository.update(settings);
  }

  @Override
  public void delete(ConnectionSettings settings) {
    repository.delete(settings);
  }

  private void onLoadError(Throwable throwable) {
    checkIfAttached();

  }
}
