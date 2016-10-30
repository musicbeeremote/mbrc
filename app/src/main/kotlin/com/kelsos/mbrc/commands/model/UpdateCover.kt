package com.kelsos.mbrc.commands.model

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.util.Base64
import com.fasterxml.jackson.databind.node.TextNode
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.CoverChangedEvent
import com.kelsos.mbrc.extensions.coverFile
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import rx.Emitter
import rx.Emitter.BackpressureMode
import rx.Observable
import rx.schedulers.Schedulers
import timber.log.Timber
import java.io.FileOutputStream
import javax.inject.Inject

class UpdateCover
@Inject constructor(private val bus: RxBus,
                    private val context: Application,
                    private val model: MainDataModel) : ICommand {

  override fun execute(e: IEvent) {
    val cover = (e.data as TextNode).textValue()
    Observable.fromEmitter<Bitmap>({
      emitter: Emitter<Bitmap> ->
      val decodedImage = Base64.decode(cover, Base64.DEFAULT)
      val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)

      if  (bitmap != null) {
        emitter.onNext(bitmap)
        emitter.onCompleted()
      } else {
        emitter.onError(RuntimeException("no cover found"))
      }

    }, BackpressureMode.LATEST).flatMap {
      storeCover(it)
    }.subscribeOn(Schedulers.io()).subscribe({
      bus.post(CoverChangedEvent(it))
      model.updateRemoteClient()
    }, {
      val coverFile = context.coverFile()
      if (coverFile.exists()) {
        coverFile.delete()
      }
      Timber.v(it, "Failed to store path")
      bus.post(CoverChangedEvent())
    })
  }

  private fun storeCover(bitmap: Bitmap): Observable<String> {
    return Observable.fromEmitter<String>({
      val file = context.coverFile()
      file.delete()
      val fileStream = FileOutputStream(file)
      val success = bitmap.compress(JPEG, 100, fileStream)
      if (success) {
        it.onNext(file.absolutePath)
      } else {
        it.onNext("")
      }
      it.onCompleted()
    }, BackpressureMode.LATEST)
  }

}

