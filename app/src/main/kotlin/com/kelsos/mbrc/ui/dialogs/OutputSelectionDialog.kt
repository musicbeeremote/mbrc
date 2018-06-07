package com.kelsos.mbrc.ui.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.output.OutputResponse
import kotterknife.bindView
import toothpick.Toothpick
import javax.inject.Inject

class OutputSelectionDialog : androidx.fragment.app.DialogFragment(),
  OutputSelectionView,
  View.OnTouchListener,
  AdapterView.OnItemSelectedListener {

  private var touchInitiated: Boolean = false
  private lateinit var fm: androidx.fragment.app.FragmentManager
  private lateinit var dialog: AlertDialog

  private val availableOutputs: Spinner by bindView(R.id.output_selection__available_outputs)
  private val loadingProgress: ProgressBar by bindView(R.id.output_selection__loading_outputs)
  private val errorMessage: TextView by bindView(R.id.output_selection__error_message)

  @Inject
  internal lateinit var presenter: OutputSelectionPresenter

  @SuppressLint("InflateParams")
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val context = context ?: error("context was null")

    val scopes = Toothpick.openScopes(context.applicationContext, this)
    scopes.installModules(OutputSelectionModule())
    Toothpick.inject(this, scopes)
    val inflater = LayoutInflater.from(context)
    val view = inflater.inflate(R.layout.dialog__output_selection, null, false)

    dialog = AlertDialog.Builder(context)
      .setTitle(R.string.output_selection__select_output)
      .setView(view)
      .setNeutralButton(R.string.output_selection__close_dialog) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .create()

    presenter.load()

    return dialog
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  override fun onNothingSelected(parent: AdapterView<*>?) {
  }

  override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    if (!touchInitiated) {
      return
    }

    val selectedOutput = availableOutputs.adapter.getItem(position) as String
    presenter.changeOutput(selectedOutput)
    touchInitiated = false
  }

  override fun onTouch(view: View?, event: MotionEvent?): Boolean {
    touchInitiated = true
    return view?.performClick() == true
  }

  override fun update(data: OutputResponse) {
    availableOutputs.onItemSelectedListener = null
    availableOutputs.setOnTouchListener(null)
    val (devices, active) = data
    val outputAdapter = ArrayAdapter<String>(
      context,
      R.layout.item__output_device,
      R.id.output_selection__output_device,
      devices
    )
    availableOutputs.adapter = outputAdapter

    val selection = devices.indexOf(active)
    availableOutputs.setSelection(selection)
    availableOutputs.onItemSelectedListener = this
    availableOutputs.setOnTouchListener(this)
    loadingProgress.isVisible = false
    availableOutputs.isVisible = true
  }

  override fun error(@OutputSelectionContract.Code code: Int) {
    val resId = when (code) {
      OutputSelectionContract.CONNECTION_ERROR -> R.string.output_selection__connection_error
      else -> R.string.output_selection__generic_error
    }
    errorMessage.setText(resId)
    loadingProgress.isVisible = false
    availableOutputs.isInvisible = true
    errorMessage.isVisible = true
  }

  override fun dismiss() {
    dialog.dismiss()
  }

  fun show() {
    show(fm, TAG)
  }

  companion object {
    private const val TAG = "output_selection_dialog"

    fun instance(fm: androidx.fragment.app.FragmentManager): OutputSelectionDialog {
      val dialog = OutputSelectionDialog()
      dialog.fm = fm
      return dialog
    }
  }
}