package com.kelsos.mbrc;

import android.app.Application;
import com.kelsos.mbrc.di.modules.ApplicationModule;
import com.kelsos.mbrc.di.modules.RemoteModule;
import timber.log.Timber;
import toothpick.Configuration;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.registries.FactoryRegistryLocator;
import toothpick.registries.MemberInjectorRegistryLocator;

public class RemoteApplication extends Application {

  public void onCreate() {
    super.onCreate();
    Configuration.setConfiguration(Configuration.reflectionFree());
    MemberInjectorRegistryLocator.setRootRegistry(new MemberInjectorRegistry());
    FactoryRegistryLocator.setRootRegistry(new FactoryRegistry());
    Scope applicationScope = Toothpick.openScope(this);
    applicationScope.installModules(new ApplicationModule(this), new RemoteModule());

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree() {
        @Override protected String createStackElementTag(StackTraceElement element) {
          return super.createStackElementTag(element) + ":" +
              element.getLineNumber() +
              " [" + Thread.currentThread().getName() + "]";
        }
      });
    }

  }
}
