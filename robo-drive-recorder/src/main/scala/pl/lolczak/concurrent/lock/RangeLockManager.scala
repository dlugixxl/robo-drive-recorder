package pl.lolczak.concurrent.lock

import java.util.concurrent.locks.{ReentrantLock, Condition}
import scala.collection.mutable.{Map => MutableMap, HashMap => MutableHashMap}

/**
 *
 *
 * @author Lukasz Olczak
 */
class RangeLockManager extends LockManager[RangeLock] {

  private val fileLocks: MutableMap[RangeLock, Condition] = new MutableHashMap[RangeLock, Condition]()

  private val metaLock: ReentrantLock = new ReentrantLock()

  def acquireLocks(requestedLocks: RangeLock*): LockReference = {
    metaLock.lock()
    try {
      awaitForConflictingLocks(requestedLocks)

      for (requestedLock <- requestedLocks) fileLocks.put(requestedLock, metaLock.newCondition())

      new LockReferenceImpl(this, requestedLocks)
    } finally {
      metaLock.unlock()
    }
  }

  private def awaitForConflictingLocks(requestedLocks: Seq[RangeLock]) {
    var conflictingLocks: Iterable[RangeLock] = null
    do {
      conflictingLocks = findConflictingRanges(requestedLocks)
      if (!conflictingLocks.isEmpty) {
        val key = conflictingLocks.iterator.next()
        val conditionOption = fileLocks.get(key)
        for (condition <- conditionOption) condition.await()
      }
    } while (!conflictingLocks.isEmpty)
  }

  private def findConflictingRanges(requestedLocks: Seq[RangeLock]) = for (
    acquiredLock <- fileLocks.keys;
    requestedLock <- requestedLocks
    if acquiredLock conflictsWith requestedLock
  ) yield acquiredLock

  private[lock] def releaseLocks(fileRangeLocks: Seq[RangeLock]) {
    metaLock.lock()
    try {
      fileRangeLocks.foreach {
        lock =>
          val conditionOption = fileLocks.remove(lock)
          for (condition <- conditionOption) condition.signalAll()
      }
    } finally {
      metaLock.unlock()
    }
  }

}

private class LockReferenceImpl(val manager: RangeLockManager, val locks: Seq[RangeLock]) extends LockReference {

  def release(): Unit = {
    manager.releaseLocks(locks)
  }

}
