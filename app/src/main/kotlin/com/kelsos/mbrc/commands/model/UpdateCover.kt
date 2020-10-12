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
import com.kelsos.mbrc.events.ui.RemoteClientMetaData
import com.kelsos.mbrc.extensions.getDimens
import com.kelsos.mbrc.extensions.md5
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.services.CoverService
import com.kelsos.mbrc.widgets.UpdateWidgets
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import rx.Single
import rx.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import javax.inject.Inject

class UpdateCover
@Inject constructor(private val bus: RxBus,
                    private val context: Application,
                    private val mapper: ObjectMapper,
                    private val coverService: CoverService,
                    private val model: MainDataModel) : ICommand {
  private val coverDir: File

  init {
    coverDir = File(context.filesDir, COVER_DIR)
  }

  override fun execute(e: IEvent) {
    val payload = mapper.treeToValue((e.data as JsonNode), CoverPayload::class.java)

    if (payload.status == CoverPayload.NOT_FOUND) {
      bus.post(CoverChangedEvent())
      UpdateWidgets.updateCover(context)
    } else if (payload.status == CoverPayload.READY) {
      retrieveCover()
    }
  }

  private fun retrieveCover() {
    coverService.getCover().subscribeOn(Schedulers.io()).flatMap {
      Single.fromCallable { getBitmap(it) }
    }.flatMap {
      return@flatMap Single.fromCallable { storeCover(it) }
    }.flatMap {
      return@flatMap prefetch(it)
    }.subscribeOn(Schedulers.io()).subscribe({
      model.coverPath = it.absolutePath
      bus.post(CoverChangedEvent(it.absolutePath))
      bus.post(RemoteClientMetaData(model.trackInfo, model.coverPath))
      UpdateWidgets.updateCover(context, it.absolutePath)
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
    clearPreviousCovers(0)

    it?.let {
      Timber.v(it, "Failed to store path")
    }

    bus.post(CoverChangedEvent())
  }

  private fun storeCover(bitmap: Bitmap): File {
    checkIfExists()
    clearPreviousCovers()
    val file = temporaryCover()
    val fileStream = FileOutputStream(file)
    val success = bitmap.compress(JPEG, 100, fileStream)
    fileStream.close()
    if (success) {
      val md5 = file.md5()
      val extension = file.extension
      val newFile = File(context.filesDir, "$md5.$extension")
      file.renameTo(newFile)
      Timber.v("file was renamed to %s", newFile.absolutePath)
      return newFile
    } else {
      throw RuntimeException("unable to store cover")
    }
  }

  private fun prefetch(newFile: File): Single<File> {
    return Single.create {
      val dimens = context.getDimens()
      Picasso.get().load(newFile)
          .config(Bitmap.Config.RGB_565)
          .resize(dimens, dimens)
          .centerCrop().fetch(object : Callback {
        override fun onSuccess() {
          it.onSuccess(newFile)
        }

        override fun onError(e: Exception) {
          it.onError(e)
        }
      })
    }
  }

  private fun checkIfExists() {
    if (!coverDir.exists()) {
      coverDir.mkdir()
    }
  }

  private fun clearPreviousCovers(keep: Int = 2) {
    val storedCovers = coverDir.listFiles()
    storedCovers.sortByDescending(File::lastModified)
    val elementsToKeep = if (storedCovers.size - keep < 0) 0 else storedCovers.size - keep
    storedCovers.takeLast(elementsToKeep).forEach {
      it.delete()
    }
  }

  private fun temporaryCover(): File {
    val file = File(context.cacheDir, TEMP_COVER)
    if (file.exists()) {
      file.delete()
    }
    return file
  }

  companion object {
    const val COVER_DIR = "cover"
    const val TEMP_COVER = "temp_cover.jpg"
  }

}

