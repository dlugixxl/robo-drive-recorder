package pl.lolczak.ui

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import pl.lolczak.R

/**
 *
 *
 * @author Lukasz Olczak
 */
class MainActivity extends Activity {

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.main, menu)
    return true
  }

}
