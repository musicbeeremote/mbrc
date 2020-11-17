package com.kelsos.mbrc.ui.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.utilities.nonNullObserver
import kotterknife.bindView
import org.koin.android.ext.android.inject

class OutputSelectionDialog : DialogFragment(), View.OnTouchListener {

  private var touchInitiated: Boolean = false
  private lateinit var fm: FragmentManager
  private lateinit var dialog: AlertDialog

  private val availableOutputs: Spinner by bindView(R.id.output_selection__available_outputs)
  private val loadingProgress: ProgressBar by bindView(R.id.output_selection__loading_outputs)
  private val errorMessage: TextView by bindView(R.id.output_selection__error_message)

  private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
      if (!touchInitiated) {
        return
      }

      val selectedOutput = availableOutputs.adapter.getItem(position) as String
      viewModel.setOutput(selectedOutput)
      touchInitiated = false
    }
  }

  private val viewModel: OutputSelectionViewModel by inject()

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel.outputs.nonNullObserver(viewLifecycleOwner) {
      update(it)
    }
    viewModel.selection.nonNullObserver(viewLifecycleOwner) {
    }
    viewModel.emitter.nonNullObserver(viewLifecycleOwner) {
      it.contentIfNotHandled?.let { result ->
        error(result)
      }
    }
  }

  @SuppressLint("InflateParams")
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val context = requireContext()

    val inflater = LayoutInflater.from(context)
    val view = inflater.inflate(R.layout.dialog__output_selection, null, false)

    dialog = AlertDialog.Builder(context)
      .setTitle(R.string.output_selection__select_output)
      .setView(view)
      .setNeutralButton(R.string.output_selection__close_dialog) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .create()

    return dialog
  }

  override fun onTouch(view: View?, event: MotionEvent?): Boolean {
    touchInitiated = true
    return view?.performClick() == true
  }

  private fun update(data: List<String>) {
    availableOutputs.onItemSelectedListener = null
    availableOutputs.setOnTouchListener(null)
    val outputAdapter = ArrayAdapter(
      requireContext(),
      R.layout.item__output_device,
      R.id.output_selection__output_device,
      data
    )
    availableOutputs.adapter = outputAdapter
    availableOutputs.onItemSelectedListener = onItemSelectedListener
    availableOutputs.setOnTouchListener(this)
    loadingProgress.isVisible = false
    availableOutputs.isVisible = true
  }

  fun error(result: OutputSelectionResult) {
    val resId = when (result) {
      OutputSelectionResult.ConnectionError -> R.string.output_selection__connection_error
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

    fun instance(fm: FragmentManager): OutputSelectionDialog {
      val dialog = OutputSelectionDialog()
      dialog.fm = fm
      return dialog
    }
  }
}