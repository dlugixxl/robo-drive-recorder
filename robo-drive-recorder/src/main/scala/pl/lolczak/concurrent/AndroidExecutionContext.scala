package pl.lolczak.concurrent

import scala.concurrent.ExecutionContext
import com.typesafe.scalalogging.slf4j.Logging

/**
 *
 *
 * @author Lukasz Olczak
 */
object AndroidExecutionContext extends ExecutionContext with Logging {

  def execute(runnable: Runnable): Unit = {
    runnable.run()
  }

  def reportFailure(t: Throwable): Unit = {
    logger.error("Exception occurred", t)
  }
}
