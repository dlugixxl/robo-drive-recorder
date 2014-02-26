package pl.lolczak.concurrent.lock

import pl.lolczak.concurrent.lock.LockType._

/**
 *
 *
 * @author Lukasz Olczak
 */
class RangeLock(val range: Range, val lockType: LockType) {

  def conflictsWith(that: RangeLock): Boolean =
    (this.range intersects that.range) && (this.lockType == Write || that.lockType == Write)

}

object WriteLock {

  def apply(offset: Long, size: Long) = new RangeLock(new Range(offset, size), Write)

  def apply(range: Range) = new RangeLock(range, Write)

}

object ReadLock {
  def apply(offset: Long, size: Long) = new RangeLock(new Range(offset, size), Read)
}
