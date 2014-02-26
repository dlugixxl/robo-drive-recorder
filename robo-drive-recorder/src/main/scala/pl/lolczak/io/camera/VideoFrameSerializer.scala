package pl.lolczak.io.camera

import pl.lolczak.io.stream.Serializer


/**
 *
 *
 * @author Lukasz Olczak
 */
object VideoFrameSerializer extends Serializer[VideoFrame] {

  def deserialize(bytes: Array[Byte]): VideoFrame = {
    //todo
    new VideoFrame(0, bytes)
  }

  def serialize(element: VideoFrame): Array[Byte] = {
    //todo
    element.content
  }

}
