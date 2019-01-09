package com.kelsos.mbrc.ui.dialogs

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.DialogOutputSelectionBinding
import com.kelsos.mbrc.output.OutputSelectionResult
import com.kelsos.mbrc.output.OutputSelectionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OutputSelectionDialog :
  DialogFragment(),
  View.OnTouchListener,
  AdapterView.OnItemSelectedListener {

  private var touchInitiated: Boolean = false
  private lateinit var dialog: AlertDialog

  private lateinit var availableOutputs: Spinner
  private lateinit var loadingProgress: ProgressBar
  private lateinit var errorMessage: TextView

  private var _binding: DialogOutputSelectionBinding? = null
  private val binding get() = _binding!!

  private val viewModel: OutputSelectionViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel.outputs.observe(viewLifecycleOwner) {
      update(it)
    }
    viewModel.selection.observe(viewLifecycleOwner) {
      val adapter = availableOutputs.adapter as ArrayAdapter<*>
      for (position in 0 until adapter.count) {
        if (it == adapter.getItem(position)) {
          availableOutputs.setSelection(position, false)
          break
        }
      }
    }
    viewModel.events.observe(viewLifecycleOwner) {
      if (it.handled) {
        return@observe
      }
      it.handled = true
      when (it) {
        OutputSelectionResult.Success -> {
          availableOutputs.isVisible = true
          errorMessage.isInvisible = true
        }
        else -> error(it)
      }
    }
    viewModel.reload()
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    _binding = DialogOutputSelectionBinding.inflate(LayoutInflater.from(requireContext()))
    availableOutputs = binding.outputSelectionAvailableOutputs
    errorMessage = binding.outputSelectionErrorMessage
    loadingProgress = binding.outputSelectionLoadingOutputs
    val view = binding.root

    dialog = MaterialAlertDialogBuilder(requireContext())
      .setTitle(R.string.output_selection__select_output)
      .setView(view)
      .setNeutralButton(R.string.output_selection__close_dialog) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .create()

    return dialog
  }

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
    availableOutputs.onItemSelectedListener = this
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
}
