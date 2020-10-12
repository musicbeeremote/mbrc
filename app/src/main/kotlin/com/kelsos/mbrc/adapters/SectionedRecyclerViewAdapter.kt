package com.kelsos.mbrc.adapters

import androidx.annotation.IntRange
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.ViewGroup

/**
 * @author Aidan Follestad (afollestad)
 */
abstract class SectionedRecyclerViewAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

  private val mHeaderLocationMap: ArrayMap<Int, Int>
  private var mLayoutManager: GridLayoutManager? = null
  private val mSpanMap: ArrayMap<Int, Int>? = null
  private var mShowHeadersForEmptySections: Boolean = false

  init {
    mHeaderLocationMap = ArrayMap<Int, Int>()
  }

  abstract val sectionCount: Int

  abstract fun getItemCount(section: Int): Int

  abstract fun onBindHeaderViewHolder(holder: VH, section: Int)

  abstract fun onBindViewHolder(holder: VH, section: Int, relativePosition: Int, absolutePosition: Int)

  fun isHeader(position: Int): Boolean {
    return mHeaderLocationMap[position] != null
  }

  /**
   * Instructs the list view adapter to whether show headers for empty sections or not.

   * @param show flag indicating whether headers for empty sections ought to be shown.
   */
  fun shouldShowHeadersForEmptySections(show: Boolean) {
    mShowHeadersForEmptySections = show
  }

  fun setLayoutManager(lm: GridLayoutManager?) {
    mLayoutManager = lm
    if (lm == null) return
    lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
      override fun getSpanSize(position: Int): Int {
        if (isHeader(position)) return mLayoutManager!!.spanCount
        val sectionAndPos = getSectionIndexAndRelativePosition(position)
        val absPos = position - (sectionAndPos[0] + 1)
        return getRowSpan(mLayoutManager!!.spanCount, sectionAndPos[0], sectionAndPos[1], absPos)
      }
    }
  }

  @Suppress("UNUSED_PARAMETER")
  protected fun getRowSpan(fullSpanSize: Int, section: Int, relativePosition: Int, absolutePosition: Int): Int {
    return 1
  }

  // returns section along with offsetted position
  private fun getSectionIndexAndRelativePosition(itemPosition: Int): IntArray {
    synchronized(mHeaderLocationMap) {
      var lastSectionIndex: Int = -1
      mHeaderLocationMap.keys
              .takeWhile { itemPosition > it }
              .forEach { lastSectionIndex = it }
      return intArrayOf(mHeaderLocationMap[lastSectionIndex] ?: -1, itemPosition - lastSectionIndex - 1)
    }
  }

  override fun getItemCount(): Int {
    var count = 0
    mHeaderLocationMap.clear()
    for (s in 0..sectionCount - 1) {
      val itemCount = getItemCount(s)
      if (mShowHeadersForEmptySections || itemCount > 0) {
        mHeaderLocationMap.put(count, s)
        count += itemCount + 1
      }
    }
    return count
  }

  /**
   * @hide
   * *
   */
  @Deprecated("")
  override fun getItemViewType(position: Int): Int {
    if (isHeader(position)) {
      return getHeaderViewType(mHeaderLocationMap[position] ?: -1)
    } else {
      val sectionAndPos = getSectionIndexAndRelativePosition(position)
      return getItemViewType(sectionAndPos[0],
          // offset section view positions
          sectionAndPos[1], position - (sectionAndPos[0] + 1))
    }
  }

  @Suppress("UNUSED_PARAMETER")
  @IntRange(from = 0, to = Integer.MAX_VALUE.toLong())
  fun getHeaderViewType(section: Int): Int {
    //noinspection ResourceType
    return VIEW_TYPE_HEADER
  }

  @Suppress("UNUSED_PARAMETER")
  @IntRange(from = 0, to = Integer.MAX_VALUE.toLong())
  open fun getItemViewType(section: Int, relativePosition: Int, absolutePosition: Int): Int {
    //noinspection ResourceType
    return VIEW_TYPE_ITEM
  }

  /**
   * @hide
   * *
   */
  @Deprecated("")
  override fun onBindViewHolder(holder: VH, position: Int) {
    var layoutParams: StaggeredGridLayoutManager.LayoutParams? = null
    if (holder.itemView.layoutParams is GridLayoutManager.LayoutParams) {
      layoutParams = StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT)
    } else if (holder.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams) {
      layoutParams = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
    }
    if (isHeader(position)) {
      if (layoutParams != null) layoutParams.isFullSpan = true
      onBindHeaderViewHolder(holder, mHeaderLocationMap[position] ?: -1)
    } else {
      if (layoutParams != null) layoutParams.isFullSpan = false
      val sectionAndPos = getSectionIndexAndRelativePosition(position)
      val absPos = position - (sectionAndPos[0] + 1)
      onBindViewHolder(holder, sectionAndPos[0],
          // offset section view positions
          sectionAndPos[1], absPos)
    }
    if (layoutParams != null) holder.itemView.layoutParams = layoutParams
  }

  companion object {

    const val VIEW_TYPE_HEADER = -2
    const val VIEW_TYPE_ITEM = -1
  }
}
