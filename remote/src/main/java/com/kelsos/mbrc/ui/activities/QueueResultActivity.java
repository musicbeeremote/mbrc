package com.kelsos.mbrc.ui.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.ui.fragments.queue.QueueResultFragment;
import roboguice.activity.RoboActionBarActivity;

public class QueueResultActivity extends RoboActionBarActivity {

    public static final String QUEUE_FILTER = "QueueFilter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_result);

        String filter = null;
        Intent intent = getIntent();
        if (intent != null) {
            filter = intent.getStringExtra(QUEUE_FILTER);
        }

        QueueResultFragment fragment = QueueResultFragment.newInstance(filter);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, "main_fragment");
        fragmentTransaction.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
}
