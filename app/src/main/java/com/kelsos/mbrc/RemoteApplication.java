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
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class RemoteApplication extends Application {

  public void onCreate() {
    super.onCreate();
    Configuration.setConfiguration(Configuration.reflectionFree());
    MemberInjectorRegistryLocator.setRootRegistry(new MemberInjectorRegistry());
    FactoryRegistryLocator.setRootRegistry(new FactoryRegistry());
    Scope applicationScope = Toothpick.openScope(this);
    applicationScope.installModules(new ApplicationModule(this), new RemoteModule());

    CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
        .setDefaultFontPath("fonts/roboto_regular.ttf")
        .setFontAttrId(R.attr.fontPath)
        .build());

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
