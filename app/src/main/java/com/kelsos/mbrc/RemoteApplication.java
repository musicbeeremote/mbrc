package com.kelsos.mbrc;

import android.app.Application;
import android.support.annotation.CallSuper;

import com.kelsos.mbrc.di.modules.RemoteModule;

import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;
import toothpick.registries.FactoryRegistryLocator;
import toothpick.registries.MemberInjectorRegistryLocator;
import toothpick.smoothie.module.SmoothieApplicationModule;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class RemoteApplication extends Application {

  @CallSuper
  public void onCreate() {
    super.onCreate();

    Configuration configuration;
    if (BuildConfig.DEBUG) {
      configuration = Configuration.forDevelopment().disableReflection();
    } else {
      configuration = Configuration.forProduction().disableReflection();
    }

    Toothpick.setConfiguration(configuration);

    MemberInjectorRegistryLocator.setRootRegistry(new MemberInjectorRegistry());
    FactoryRegistryLocator.setRootRegistry(new FactoryRegistry());
    Scope applicationScope = Toothpick.openScope(this);
    applicationScope.installModules(new SmoothieApplicationModule(this), new RemoteModule());

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
