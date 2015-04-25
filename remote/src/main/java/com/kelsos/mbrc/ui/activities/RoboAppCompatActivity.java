package com.kelsos.mbrc.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import roboguice.activity.RoboFragmentActivity;

public class RoboAppCompatActivity extends RoboFragmentActivity implements AppCompatCallback,
    TaskStackBuilder.SupportParentable, ActionBarDrawerToggle.DelegateProvider {
  private AppCompatDelegate mDelegate;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    getDelegate().installViewFactory();
    super.onCreate(savedInstanceState);
    getDelegate().onCreate(savedInstanceState);
  }
  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    getDelegate().onPostCreate(savedInstanceState);
  }

  public ActionBar getSupportActionBar() {
    return getDelegate().getSupportActionBar();
  }

  public void setSupportActionBar(@Nullable Toolbar toolbar) {
    getDelegate().setSupportActionBar(toolbar);
  }

  @NonNull
  @Override
  public MenuInflater getMenuInflater() {
    return getDelegate().getMenuInflater();
  }
  @Override
  public void setContentView(@LayoutRes int layoutResID) {
    getDelegate().setContentView(layoutResID);
  }
  @Override
  public void setContentView(View view) {
    getDelegate().setContentView(view);
  }
  @Override
  public void setContentView(View view, ViewGroup.LayoutParams params) {
    getDelegate().setContentView(view, params);
  }
  @Override
  public void addContentView(View view, ViewGroup.LayoutParams params) {
    getDelegate().addContentView(view, params);
  }
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    getDelegate().onConfigurationChanged(newConfig);
  }
  @Override
  protected void onStop() {
    super.onStop();
    getDelegate().onStop();
  }
  @Override
  protected void onPostResume() {
    super.onPostResume();
    getDelegate().onPostResume();
  }
  @Override
  public final boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
    if (super.onMenuItemSelected(featureId, item)) {
      return true;
    }

    final ActionBar ab = getSupportActionBar();
    if (item.getItemId() == android.R.id.home && ab != null
        && (ab.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0) {
      return onSupportNavigateUp();
    }
    return false;
  }
  @Override
  protected void onDestroy() {
    super.onDestroy();
    getDelegate().onDestroy();
  }
  @Override
  protected void onTitleChanged(CharSequence title, int color) {
    super.onTitleChanged(title, color);
    getDelegate().setTitle(title);
  }

  public boolean supportRequestWindowFeature(int featureId) {
    return getDelegate().requestWindowFeature(featureId);
  }
  @Override
  public void supportInvalidateOptionsMenu() {
    getDelegate().invalidateOptionsMenu();
  }

  public void invalidateOptionsMenu() {
    getDelegate().invalidateOptionsMenu();
  }

  public ActionMode startSupportActionMode(ActionMode.Callback callback) {
    return getDelegate().startSupportActionMode(callback);
  }

  public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
    builder.addParentStack(this);
  }

  public void onPrepareSupportNavigateUpTaskStack(TaskStackBuilder builder) {
  }

  public boolean onSupportNavigateUp() {
    Intent upIntent = getSupportParentActivityIntent();
    if (upIntent != null) {
      if (supportShouldUpRecreateTask(upIntent)) {
        TaskStackBuilder b = TaskStackBuilder.create(this);
        onCreateSupportNavigateUpTaskStack(b);
        onPrepareSupportNavigateUpTaskStack(b);
        b.startActivities();
        try {
          ActivityCompat.finishAffinity(this);
        } catch (IllegalStateException e) {
          finish();
        }
      } else {


        supportNavigateUpTo(upIntent);
      }
      return true;
    }
    return false;
  }

  public Intent getSupportParentActivityIntent() {
    return NavUtils.getParentActivityIntent(this);
  }

  public boolean supportShouldUpRecreateTask(Intent targetIntent) {
    return NavUtils.shouldUpRecreateTask(this, targetIntent);
  }

  public void supportNavigateUpTo(Intent upIntent) {
    NavUtils.navigateUpTo(this, upIntent);
  }

  @Nullable
  @Override
  public ActionBarDrawerToggle.Delegate getDrawerToggleDelegate() {
    return getDelegate().getDrawerToggleDelegate();
  }


  public AppCompatDelegate getDelegate() {
    if (mDelegate == null) {
      mDelegate = AppCompatDelegate.create(this, this);
    }
    return mDelegate;
  }

  @Override public void onSupportActionModeStarted(ActionMode actionMode) {

  }

  @Override public void onSupportActionModeFinished(ActionMode actionMode) {

  }
}
