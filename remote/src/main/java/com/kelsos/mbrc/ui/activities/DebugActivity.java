package com.kelsos.mbrc.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.utilities.LibrarySyncManager;
import roboguice.RoboGuice;

public class DebugActivity extends AppCompatActivity {

  @Inject private LibrarySyncManager manager;

  @OnClick(R.id.debug_action) void onAction() {
    manager.sync();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_debug);
    ButterKnife.bind(this);
    RoboGuice.getInjector(this).injectMembers(this);
  }
}
