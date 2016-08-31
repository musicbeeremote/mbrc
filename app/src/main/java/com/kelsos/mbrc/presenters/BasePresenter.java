package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.views.BaseView;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BasePresenter<T extends BaseView> implements Presenter<T> {
  private T view;

  private CompositeSubscription compositeSubscription = new CompositeSubscription();

  public T getView() {
    return view;
  }

  boolean isAttached() {
    return view != null;
  }

  @Override
  public void attach(T view) {
    this.view = view;
  }

  @Override
  public void detach() {
    this.view = null;
    compositeSubscription.clear();
  }

  protected void addSubcription(Subscription subscription) {
    this.compositeSubscription.add(subscription);
  }

  public void checkIfAttached() {
    if (!isAttached()) {
      throw new ViewNotAttachedException();
    }
  }

  protected static class ViewNotAttachedException extends RuntimeException {
    public ViewNotAttachedException() {
      super("Please call Presenter.attach(BaseView) before calling a method on the presenter");
    }
  }
}
