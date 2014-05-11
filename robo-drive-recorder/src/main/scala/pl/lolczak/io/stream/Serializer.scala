package pl.lolczak.io.stream

/**
 *
 *
 * @author Lukasz Olczak
 */
trait Serializer[A] {

  @throws(classOf[SerializationException])
  def deserialize(inStream: SerializerInputStream): A

  def serialize(element: A, outStream: SerializerOutputStream)

  def sizeOf(element: A): Long

}
