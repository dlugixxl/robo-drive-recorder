package pl.lolczak.concurrent.lock

/**
 *
 *
 * @author Lukasz Olczak
 */
object LockType extends Enumeration {

  type LockType = Value

  val Read, Write = Value
}
