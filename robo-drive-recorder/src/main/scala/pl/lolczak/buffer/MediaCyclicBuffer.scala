package pl.lolczak.buffer

/**
 *
 *
 * @author Lukasz Olczak
 */
trait MediaCyclicBuffer[A] {

  @throws(classOf[BufferTooSmallException])
  def put(element: A)

  def getLast: Option[A]

  def getLast(max: Int): Option[List[A]]

  def size: Long

}
