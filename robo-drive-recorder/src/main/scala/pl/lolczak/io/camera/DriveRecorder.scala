package pl.lolczak.io.camera

import android.hardware.Camera
import com.typesafe.scalalogging.slf4j.Logging
import pl.lolczak.context.ApplicationContext._

/**
 *
 *
 * @author Lukasz Olczak
 */
class DriveRecorder extends Camera.PreviewCallback with Logging {

  var counter = 0

  def onPreviewFrame(data: Array[Byte], camera: Camera) {
    val frameTimestamp = System.currentTimeMillis()
    if (counter % 25 == 0) {
      val fps = new Array[Int](2)
      camera.getParameters.getPreviewFpsRange(fps)
      val currentFps = camera.getParameters.getPreviewFrameRate
      logger.debug("PreviewCallback", s"Frame received ${data.length} min fps: ${fps(0)} max fps: ${fps(1)} current: ${currentFps}")
    }
    counter += 1

    if (buffer.isDefined) {
      val frame = new VideoFrame(frameTimestamp, data) //copy buffer
      buffer.get.put(frame)
    }

    camera.addCallbackBuffer(data)
  }

}
