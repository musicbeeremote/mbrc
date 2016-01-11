package com.kelsos.mbrc.ui.activities;

import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.utilities.LibrarySyncManager;

public class DebugActivity extends RoboAppCompatActivity {

  @Inject private LibrarySyncManager manager;

  @OnClick(R.id.debug_action) void onAction() {
    manager.sync();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_debug);
    ButterKnife.bind(this);
  }


}
