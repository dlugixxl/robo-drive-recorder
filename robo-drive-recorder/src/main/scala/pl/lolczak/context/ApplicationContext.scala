package pl.lolczak.context

import pl.lolczak.ui.camera.OverlayPreview
import pl.lolczak.buffer.MediaCyclicBuffer
import pl.lolczak.io.camera.{CameraDevice, VideoFrame}

/**
 *
 *
 * @author Lukasz Olczak
 */
object ApplicationContext {

  var preview: Option[OverlayPreview] = None

  var buffer: Option[MediaCyclicBuffer[VideoFrame]] = None

  var cameraDevice: Option[CameraDevice] = None

}
