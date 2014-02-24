package pl.lolczak.concurrent.lock


/**
 *
 *
 * @author Lukasz Olczak
 */
trait LockManager[A] {

  def acquireLocks(lockDescriptors: A*): LockReference

}
