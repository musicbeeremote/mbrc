package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.Sorting
import com.kelsos.mbrc.content.library.albums.Sorting.Fields
import com.kelsos.mbrc.content.library.albums.Sorting.ORDER_ASCENDING
import com.kelsos.mbrc.content.library.albums.Sorting.ORDER_DESCENDING
import com.kelsos.mbrc.content.library.albums.Sorting.Order
import com.kelsos.mbrc.extensions.fail

class SortingDialog : DialogFragment() {

  @BindView(R.id.sorting_dialog__order)
  lateinit var orderButton: Button

  @BindView(R.id.sorting_dialog__sorting_options)
  lateinit var sortingOption: RadioGroup

  private lateinit var dialog: AlertDialog
  private lateinit var fm: FragmentManager
  private lateinit var orderChange: (order: Long) -> Unit
  private lateinit var sortingChange: (sorting: Long) -> Unit

  @Fields private var sorting: Long = Sorting.ALBUM_ARTIST__ALBUM
  @Order private var order: Long = Sorting.ORDER_ASCENDING

  @OnClick(R.id.sorting_dialog__order)
  fun onOrderChanged() {
    this.order = when (order) {
      ORDER_DESCENDING -> ORDER_ASCENDING
      ORDER_ASCENDING -> ORDER_DESCENDING
      else -> fail("unknown order value: $order")
    }

    setOrder(this.order)
    orderChange(this.order)
  }

  private fun setOrder(@Order order: Long) {

    fun Button.set(@StringRes stringId: Int, @DrawableRes drawableId: Int) {
      text = getString(stringId)
      val drawable = ContextCompat.getDrawable(context, drawableId) ?: return
      val wrapped = DrawableCompat.wrap(drawable)
      DrawableCompat.setTint(wrapped, ContextCompat.getColor(context, R.color.accent))
      setCompoundDrawablesRelativeWithIntrinsicBounds(wrapped, null, null, null)
    }

    when (order) {
      Sorting.ORDER_ASCENDING -> {
        orderButton.set(R.string.sorting_dialog__descending, R.drawable.ic_arrow_drop_down_black_24dp)
      }
      Sorting.ORDER_DESCENDING -> {
        orderButton.set(R.string.sorting_dialog__ascending, R.drawable.ic_arrow_drop_up_black_24dp)
      }
    }

  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val context = context ?: fail("null context")
    dialog = AlertDialog.Builder(context)
        .setTitle(R.string.album_sorting__dialog_title)
        .setView(R.layout.dialog__sorting)
        .setPositiveButton(android.R.string.ok) { dialogInterface, _ -> dialogInterface.dismiss() }
        .create()

    ButterKnife.bind(this, dialog)

    setOrder(order)
    sortingOption.check(sortingOption.getChildAt(sorting.toInt()).id)
    sortingOption.setOnCheckedChangeListener { radioGroup, _ ->
      val radioButtonID = radioGroup.checkedRadioButtonId
      val radioButton = radioGroup.findViewById<RadioButton>(radioButtonID)
      val idx = radioGroup.indexOfChild(radioButton)
      sortingChange(1 + idx.toLong())
    }

    return dialog
  }

  fun show() {
    show(fm, TAG)
  }

  override fun dismiss() {
    dialog.dismiss()
  }

  companion object {
    const val TAG = "com.kelsos.mbrc.ui.dialog.SortingDialog"

    fun create(
        fm: FragmentManager,
        @Fields sorting: Long,
        @Order order: Long,
        orderChange: (order: Long) -> Unit,
        sortingChange: (sorting: Long) -> Unit
    ): SortingDialog = SortingDialog().apply {
      this.fm = fm
      this.sorting = sorting
      this.order = order
      this.orderChange = orderChange
      this.sortingChange = sortingChange
    }
  }
}
