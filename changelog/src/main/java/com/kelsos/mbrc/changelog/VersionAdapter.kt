package com.kelsos.mbrc.changelog

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.changelog.databinding.ChangelogDialogEntryBinding
import com.kelsos.mbrc.changelog.databinding.ChangelogDialogHeaderBinding

class VersionAdapter(
  private val changeLog: List<ChangeLogEntry>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  class VersionViewHolder(
    binding: ChangelogDialogHeaderBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    private val version: TextView = binding.changelogVersionVersion
    private val release: TextView = binding.changelogVersionRelease

    fun bind(version: ChangeLogEntry.Version) {
      this.version.text = version.version
      this.release.text = version.release
    }

    companion object {
      fun from(parent: ViewGroup): VersionViewHolder {
        val binding = ChangelogDialogHeaderBinding.inflate(LayoutInflater.from(parent.context))
        return VersionViewHolder(binding)
      }
    }
  }

  class EntryViewHolder(
    binding: ChangelogDialogEntryBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    private val text: TextView = binding.changelogEntryText
    private val type: TextView = binding.changelogEntryType

    fun bind(entry: ChangeLogEntry.Entry) {
      val typeResId = when (entry.type) {
        is EntryType.BUG -> R.string.entry__bug
        is EntryType.FEATURE -> R.string.entry__feature
        is EntryType.REMOVED -> R.string.entry__removed
      }
      type.setText(typeResId)
      text.text = HtmlCompat.fromHtml(entry.text, FROM_HTML_MODE_LEGACY)
    }

    companion object {
      fun from(parent: ViewGroup): EntryViewHolder {
        val binding = ChangelogDialogEntryBinding.inflate(LayoutInflater.from(parent.context))
        return EntryViewHolder(binding)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      ITEM_VIEW_TYPE_HEADER -> VersionViewHolder.from(parent)
      ITEM_VIEW_TYPE_ITEM -> EntryViewHolder.from(parent)
      else -> throw ClassCastException("Unknown viewType $viewType")
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val version = changeLog[position]
    when (holder) {
      is VersionViewHolder -> holder.bind(version as ChangeLogEntry.Version)
      is EntryViewHolder -> holder.bind(version as ChangeLogEntry.Entry)
    }
  }

  override fun getItemViewType(position: Int): Int {
    return when (changeLog[position]) {
      is ChangeLogEntry.Version -> ITEM_VIEW_TYPE_HEADER
      is ChangeLogEntry.Entry -> ITEM_VIEW_TYPE_ITEM
    }
  }

  override fun getItemCount(): Int {
    return changeLog.size
  }

  companion object {
    private const val ITEM_VIEW_TYPE_HEADER = 0
    private const val ITEM_VIEW_TYPE_ITEM = 1
  }
}
