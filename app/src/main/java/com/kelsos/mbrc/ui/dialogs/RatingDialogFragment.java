package com.kelsos.mbrc.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.RatingBar;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.RatingChanged;
import javax.inject.Inject;
import toothpick.Scope;
import toothpick.Toothpick;

public class RatingDialogFragment extends DialogFragment {

  @Inject RxBus bus;
  private RatingBar ratingBar;
  private float rating;
  private Scope scope;

  @Override public void onCreate(Bundle savedInstanceState) {
    scope = Toothpick.openScopes(getActivity().getApplication(), getActivity(), this);
    super.onCreate(savedInstanceState);
    Toothpick.inject(this, scope);
    bus.register(this, RatingChanged.class, this::handleRatingChange);
  }

  @Override public void onDestroy() {
    Toothpick.closeScope(this);
    bus.unregister(this);
    super.onDestroy();
  }

  private void handleRatingChange(RatingChanged event) {
    rating = event.getRating();
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
    builder.customView(R.layout.ui_dialog_rating, false);
    final MaterialDialog dialog = builder.build();
    ratingBar = ButterKnife.findById(dialog.getCustomView(), R.id.ratingBar);
    ratingBar.setOnRatingBarChangeListener((ratingBar, ratingValue, isUserInitiated) -> {
      if (isUserInitiated) {
        bus.post(new MessageEvent(ProtocolEventType.UserAction,
            new UserAction(Protocol.NowPlayingRating, ratingValue)));
      }
    });
    ratingBar.setRating(rating);

    return dialog;
  }
}
