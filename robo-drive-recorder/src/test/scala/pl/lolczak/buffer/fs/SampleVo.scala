package pl.lolczak.buffer.fs

import pl.lolczak.io.stream.Serializer
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal
import scala.pickling._
import binary._

/**
 *
 *
 * @author Lukasz Olczak
 */
class SampleVo(val name: String, val age: Int) {

  override def equals(o: scala.Any): Boolean = {
    if (!o.isInstanceOf[SampleVo]) return false
    val that: SampleVo = o.asInstanceOf[SampleVo]
    if (that.eq(this)) return true
    if (that.name != name) return false
    if (that.age != age) return false

    return true
  }

  override def hashCode(): Int = {
    var result = name.hashCode
    result = 31*result + age.hashCode()
    result
  }
}

object SampleVo {
  implicit val serializer = SampleVoSerializer
}

object SampleVoSerializer extends Serializer[SampleVo] {
  def deserialize(bytes: Array[Byte]): Try[SampleVo] = {
    try {
      Success(bytes.unpickle[SampleVo])
    } catch {
      case NonFatal(ex) => Failure(ex)
    }
  }

  def serialize(element: SampleVo): Array[Byte] = {
    element.pickle.value
  }
}
