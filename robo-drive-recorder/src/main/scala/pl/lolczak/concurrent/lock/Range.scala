package pl.lolczak.concurrent.lock

/**
 *
 *
 * @author Lukasz Olczak
 */
class Range(val offset: Long, val size: Long) {

  if (size <= 0) throw new IllegalArgumentException("Size has to be greater than zero")

  def intersects(that: Range): Boolean = {
    if (this.offset == that.offset) true
    else if (this.offset < that.offset) {   //this on the left
      if (this.end >= that.offset) true
      else false
    } else {
      if (that.end >= this.offset) true  //that on the left
      else false
    }
  }

  private def end = offset + size - 1

}
