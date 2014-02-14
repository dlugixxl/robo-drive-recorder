package pl.lolczak.ui.camera

import android.content.Context
import android.view.{SurfaceHolder, SurfaceView}
import android.util.Log

/**
 *
 *
 * @author Lukasz Olczak
 */
class OverlayPreview(context: Context) extends SurfaceView(context) with SurfaceHolder.Callback {

  def init() {
    Log.d("OverlayPreview", "Initializing preview")
    val holder = getHolder
    holder.addCallback(this)
    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
  }

  def surfaceDestroyed(holder: SurfaceHolder) = {
    Log.d("OverlayPreview", "destroyed")
  }

  def surfaceCreated(holder: SurfaceHolder) = {
    Log.d("OverlayPreview", "created")

  }

  def surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = {
    Log.d("OverlayPreview", "changed")
  }

}
