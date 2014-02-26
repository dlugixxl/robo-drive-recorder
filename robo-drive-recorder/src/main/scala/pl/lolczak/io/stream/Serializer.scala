package pl.lolczak.io.stream

/**
 *
 *
 * @author Lukasz Olczak
 */
trait Serializer[A] {

  @throws(classOf[SerializationException])
  def deserialize(bytes: Array[Byte]): A

  def serialize(element: A): Array[Byte]

}
