package com.kelsos.mbrc.commands.model

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.util.Base64
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.data.CoverPayload
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.CoverChangedEvent
import com.kelsos.mbrc.extensions.coverFile
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.services.CoverService
import com.kelsos.mbrc.widgets.UpdateWidgets
import rx.Single
import rx.schedulers.Schedulers
import timber.log.Timber
import java.io.FileOutputStream
import javax.inject.Inject

class UpdateCover
@Inject constructor(private val bus: RxBus,
                    private val context: Application,
                    private val mapper: ObjectMapper,
                    private val coverService: CoverService,
                    private val model: MainDataModel) : ICommand {

  override fun execute(e: IEvent) {
    val payload = mapper.treeToValue((e.data as JsonNode), CoverPayload::class.java)

    if (payload.status == CoverPayload.NOT_FOUND) {
      clearCover()
      UpdateWidgets.update(context)
    } else if (payload.status == CoverPayload.READY) {
      retrieveCover()
    }
  }

  private fun clearCover() {
    val file = context.coverFile()
    file.delete()
    bus.post(CoverChangedEvent())
  }

  private fun retrieveCover() {
    coverService.getCover().subscribeOn(Schedulers.io()).flatMap {
      Single.fromCallable { getBitmap(it) }
    }.flatMap {
      return@flatMap Single.fromCallable { storeCover(it) }
    }.subscribeOn(Schedulers.io()).subscribe({
      bus.post(CoverChangedEvent())
      model.updateRemoteClient()
      UpdateWidgets.update(context)
    }, {
      removeCover(it)
    })

    Timber.v("Message received for available cover")
    return
  }

  private fun getBitmap(base64: String): Bitmap {
    val decodedImage = Base64.decode(base64, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
    if (bitmap != null) {
      return bitmap
    } else {
      throw RuntimeException("Base64 was not an image")
    }

  }

  private fun removeCover(it: Throwable? = null) {
    val coverFile = context.coverFile()
    if (coverFile.exists()) {
      coverFile.delete()
    }

    it?.let {
      Timber.v(it, "Failed to store path")
    }

    bus.post(CoverChangedEvent())
  }

  private fun storeCover(bitmap: Bitmap): String {
    val file = context.coverFile()
    file.delete()
    val fileStream = FileOutputStream(file)
    val success = bitmap.compress(JPEG, 100, fileStream)
    if (success) {
      return file.absolutePath
    } else {
      throw RuntimeException("unable to store cover")
    }
  }

}

