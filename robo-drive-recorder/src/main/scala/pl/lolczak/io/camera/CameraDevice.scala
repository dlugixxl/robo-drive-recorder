package pl.lolczak.io.camera

import com.typesafe.scalalogging.slf4j.Logging
import android.hardware.Camera
import android.view.SurfaceView

/**
 *
 *
 * @author Lukasz Olczak
 */
class CameraDevice extends Logging {

  val camera = Camera.open()

  def startPreview(previewSurface: SurfaceView, callback: Camera.PreviewCallback) {
    logger.debug("Starting preview")
    camera.setPreviewDisplay(previewSurface.getHolder)

    camera.setPreviewCallbackWithBuffer(null)

    camera.setPreviewCallbackWithBuffer(callback)
    (1 to 2).foreach {
      i =>
        val buffer = new Array[Byte](115200)
        camera.addCallbackBuffer(buffer)
    }
    camera.startPreview()
    logger.info("Preview started")
  }

}
