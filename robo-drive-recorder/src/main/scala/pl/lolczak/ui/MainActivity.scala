package pl.lolczak.ui

import android.app.Activity
import android.os.Bundle
import android.view.{View, Menu}
import pl.lolczak.R
import com.typesafe.scalalogging.slf4j.Logging

/**
 *
 *
 * @author Lukasz Olczak
 */
class MainActivity extends Activity with Logging {

//  private val logger = LoggerFactory.getLogger("pl.lolczak.MainActivity")

  override def onCreate(savedInstanceState: Bundle) {
    logger.debug("Creating Main activity")
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
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

}
