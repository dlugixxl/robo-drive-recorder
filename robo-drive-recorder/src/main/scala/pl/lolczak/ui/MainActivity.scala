package pl.lolczak.ui

import android.app.Activity
import android.os.{Environment, Bundle}
import android.view.{View, Menu}
import pl.lolczak.R
import com.typesafe.scalalogging.slf4j.Logging
import scala.util.control.NonFatal
import java.io.File
import pl.lolczak.context.ApplicationContext
import pl.lolczak.buffer.fs.DurableMediaCyclicBuffer
import pl.lolczak.io.camera.{DriveRecorder, CameraDevice, VideoFrameSerializer, VideoFrame}
import pl.lolczak.constant.RdrConstants
import pl.lolczak.ui.camera.OverlayPreview
import scala.util.{Failure, Success}
import pl.lolczak.concurrent.AndroidExecutionContext

/**
 *
 *
 * @author Lukasz Olczak
 */
class MainActivity extends Activity with Logging {

  implicit val androidExecutionContext = AndroidExecutionContext

  override def onCreate(savedInstanceState: Bundle) {
    logger.debug("Creating Main activity")
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    initOverlayPreview()
    initBuffer()

  }

  def initOverlayPreview() {
    logger.debug("Initializing overlay preview")
    val overlayFuture = OverlayPreview.newOverlayPreview(this)
    overlayFuture.onComplete({
      case Success(overlay) => {
        logger.info("Future success")
        ApplicationContext.preview = Some(overlay)
      }
      case Failure(ex) => logger.error("Exception occurred during overlay initialization", ex)
    })
  }

  def initBuffer() {
    logger.debug("Initializing media buffer")
    val bufferLocation = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "rdr_buffer.bin")

    ApplicationContext.buffer = Some(new DurableMediaCyclicBuffer[VideoFrame](bufferLocation, RdrConstants.BufferSize)(VideoFrameSerializer))
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    logger.debug("On create options menu")
    getMenuInflater.inflate(R.menu.main, menu)
    true
  }

  def preview(view: View) {
    logger.debug("Preview clicked")

    logger.info("Preview closed")
  }

  def start(view: View) {
    logger.debug("Start clicked")
    if (ApplicationContext.preview.isEmpty) {
      logger.error("Preview not set")
      return
    }
    try {
      val camera = new CameraDevice
      camera.startPreview(ApplicationContext.preview.get, new DriveRecorder)

      ApplicationContext.cameraDevice = Some(camera)
    } catch {
      case NonFatal(ex) => logger.error(s"Exception occurred $ex")
    }
  }

  def stop(view: View) {
    logger.debug("Stop clicked")
  }

  def save(view: View) {
    logger.debug("save clicked")
  }

}
