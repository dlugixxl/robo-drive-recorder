package pl.lolczak.io.camera

import pl.lolczak.io.stream.Serializer
import scala.util.{Failure, Success, Try}

import scala.util.control.NonFatal

/**
 *
 *
 * @author Lukasz Olczak
 */
object VideoFrameSerializer extends Serializer[VideoFrame] {

  def deserialize(bytes: Array[Byte]): Try[VideoFrame] = {
    try {
      Success(new VideoFrame(0, bytes))
    } catch {
      case NonFatal(ex) => Failure(ex)
    }
  }

  def serialize(element: VideoFrame): Array[Byte] = {
    element.content
  }

}
