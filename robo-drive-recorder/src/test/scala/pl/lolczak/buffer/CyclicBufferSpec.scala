package pl.lolczak.buffer

import org.scalatest._

/**
 *
 *
 * @author Lukasz Olczak
 */
class CyclicBufferSpec extends FlatSpec with Matchers  {

  "A cyclic buffer" should "save chunks" in {
    val sth = 4
    print(s"Dziala $sth")

    sth should be (4)
  }

}
