package pl.lolczak.buffer.fs

import org.scalatest._
import java.io.File
import scala.None
import pl.lolczak.buffer.BufferTooSmallException

/**
 *
 *
 * @author Lukasz Olczak
 */
class DurableMediaCyclicBufferTest extends FeatureSpec with Matchers with GivenWhenThen with BeforeAndAfter {

  private var bufferLocation: File = _

  before {
    bufferLocation = File.createTempFile("buffer", ".bin", new File(System.getProperty("java.io.tmpdir")))
    println(s"loc: $bufferLocation")
  }

  after {

  }

  feature("Creation of buffer") {
    scenario("Creation of empty buffer on init") {
      Given("non-existing buffer location")

      When("programmer creates buffer")
      val emptyBuffer = new DurableMediaCyclicBuffer[SampleVo](location = bufferLocation, bufferSize = 100)

      Then("file should be created")
      bufferLocation.exists() shouldBe true
      And("the size of file should be truncated to buffer size")
      bufferLocation.length() shouldBe 100
    }
  }

  feature("Retrieving single data element") {
    scenario("Retrieving data from empty buffer") {
      Given("empty buffer")
      val emptyBuffer = new DurableMediaCyclicBuffer[SampleVo](location = bufferLocation, bufferSize = 100)
      When("element is got")
      val element = emptyBuffer.getLast()
      Then("buffer should return nothing")
      element should be(None)
    }

    scenario("Retrieving data from buffer with one element") {
      Given("buffer with one element")
      val sampleElement = new SampleVo("lukasz", 12)
      val buffer = new DurableMediaCyclicBuffer[SampleVo](location = bufferLocation, bufferSize = 100)
      buffer.put(sampleElement)

      When("retrieve last element")
      val element = buffer.getLast()

      Then("retrieved element should match to saved element")
      element.isDefined shouldBe true

      val result = element.get
      result.name shouldBe sampleElement.name
      result.age shouldBe sampleElement.age
    }

  }

  feature("Saving data into buffer") {
    scenario("Saving to large element") {
      Given("element that is larger than buffer size")
      val sampleElement = new SampleVo("lukasz", 12)
      val buffer = new DurableMediaCyclicBuffer[SampleVo](location = bufferLocation, bufferSize = 20)

      When("element is saved into buffer")
      val ex = intercept[BufferTooSmallException] {
        buffer.put(sampleElement)
      }
      Then("exception is thrown")
      ex shouldNot be (null)
    }
  }

  feature("Bulk load") {

  }

  feature("Restoring buffer") {

  }

  feature("Buffer overlapping") {
    scenario("Guarantee of fixed size of buffer") {
      Given("empty buffer")
      val buffer = new DurableMediaCyclicBuffer[SampleVo](location = bufferLocation, bufferSize = 120)
      When("too much data is saved")
      (1 to 100).foreach { i =>
        val sampleElement = new SampleVo("lukasz", i)
        buffer.put(sampleElement)
      }
      Then("buffer should has fixed size")
      bufferLocation.length() shouldBe 120
    }

    scenario("Stores only not overwritten elements") {
      Given("overlapped bufer")
      val buffer = new DurableMediaCyclicBuffer[SampleVo](location = bufferLocation, bufferSize = 140)
      (1 to 100).foreach { i =>
        val sampleElement = new SampleVo("lukasz", i)
        buffer.put(sampleElement)
      }
      When("retrieve last 100 elements")
      val elements = buffer.getLast(100)
      Then("should return last 2 not overwritten")
      elements.get.size shouldBe 2
      elements.get shouldBe List(new SampleVo("lukasz", 99), new SampleVo("lukasz", 100))
    }
  }

}


