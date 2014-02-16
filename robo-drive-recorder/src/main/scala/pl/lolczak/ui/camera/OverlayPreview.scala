package pl.lolczak.ui.camera

import android.content.Context
import android.view.{WindowManager, SurfaceHolder, SurfaceView}
import com.typesafe.scalalogging.slf4j.Logging
import scala.concurrent.{Promise, Future}
import android.graphics.PixelFormat

/**
 *
 *
 * @author Lukasz Olczak
 */
class OverlayPreview(context: Context, private val promise: Promise[OverlayPreview]) extends SurfaceView(context) with SurfaceHolder.Callback with Logging {

  def init() {
    logger.debug("Initializing preview")
    val holder = getHolder
    holder.addCallback(this)
    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
  }

  def surfaceDestroyed(holder: SurfaceHolder) = {
    logger.debug("OverlayPreview destroyed")
  }

  def surfaceCreated(holder: SurfaceHolder) = {
    logger.debug("OverlayPreview created")
    promise.success(this)
  }

  def surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = {
    logger.debug(s"OverlayPreview changed w:$width h:$height")
  }

}

object OverlayPreview {

  def newOverlayPreview(context: Context): Future[OverlayPreview] = {
    val promise = Promise[OverlayPreview]()
    val overlayPreview = new OverlayPreview(context, promise)

    val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager];
    val params = new WindowManager.LayoutParams(1,
      1,
      WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
      WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
      PixelFormat.TRANSLUCENT);

    wm.addView(overlayPreview, params);
    //    sw.setZOrderOnTop(true);
    //    sw.getHolder.setFormat(PixelFormat.TRANSPARENT)
    overlayPreview.init()

    promise.future
  }

}
