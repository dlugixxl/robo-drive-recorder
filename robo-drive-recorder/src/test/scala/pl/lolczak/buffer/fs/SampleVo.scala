package pl.lolczak.buffer.fs

import pl.lolczak.io.stream.{SerializerInputStream, SerializerOutputStream, Serializer}
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

/**
 *
 *
 * @author Lukasz Olczak
 */
class SampleVo(val name: String, val age: Int, val mature: Boolean) {

  override def equals(o: scala.Any): Boolean = {
    if (!o.isInstanceOf[SampleVo]) return false
    val that: SampleVo = o.asInstanceOf[SampleVo]
    if (that.eq(this)) return true
    if (that.name != name) return false
    if (that.age != age) return false
    if (that.mature != mature) return false

    return true
  }

  override def hashCode(): Int = {
    var result = name.hashCode
    result = 31*result + age.hashCode()
    result = 31*result + mature.hashCode()
    result
  }
}

object SampleVo {
  implicit val serializer = SampleVoSerializer
}
