package com.kelsos.mbrc.changelog

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.RawRes
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kelsos.mbrc.changelog.databinding.ChangelogDialogLayoutBinding

object ChangelogDialog {
  fun show(context: Context, @RawRes resId: Int): AlertDialog {
    val binding = ChangelogDialogLayoutBinding.inflate(LayoutInflater.from(context))
    val parser = ChangelogParser(context)
    val dialog = MaterialAlertDialogBuilder(context)
      .setTitle(R.string.changelog_dialog__title)
      .setView(binding.root)
      .setPositiveButton(android.R.string.ok) { materialDialog, _ -> materialDialog.dismiss() }
      .show()

    val log = parser.changelog(resId)
    binding.changelog.layoutManager = LinearLayoutManager(context)
    binding.changelog.adapter = VersionAdapter(log)
    return dialog
  }
}
