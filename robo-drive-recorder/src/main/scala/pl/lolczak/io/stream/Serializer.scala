package pl.lolczak.io.stream

import scala.util.Try

/**
 *
 *
 * @author Lukasz Olczak
 */
trait Serializer[A] {

  def deserialize(bytes: Array[Byte]): Try[A]
  
  def serialize(element: A): Array[Byte]

}
