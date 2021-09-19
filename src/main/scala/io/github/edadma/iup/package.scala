package io.github.edadma

import scala.scalanative.unsafe._
import scala.scalanative.unsigned._

import scala.collection.mutable

import io.github.edadma.iup.extern.{LibIUP => lib}

package object iup {

  // Common Flags and Return Values
  implicit class Result(val res: CInt) extends AnyVal
  object Result {
    final val ERROR: Result      = Result(1)
    final val NOERROR: Result    = Result(0)
    final val OPENED: Result     = Result(-1)
    final val INVALID: Result    = Result(-1)
    final val INVALID_ID: Result = Result(-10)
  }

  // IupPopup and IupShowXY Parameter Values
  implicit class Position(val pos: CInt) extends AnyVal
  object Position {
    final val CENTER: Position       = Position(0xFFFF) /* 65535 */
    final val LEFT: Position         = Position(0xFFFE) /* 65534 */
    final val RIGHT: Position        = Position(0xFFFD) /* 65533 */
    final val MOUSEPOS: Position     = Position(0xFFFC) /* 65532 */
    final val CURRENT: Position      = Position(0xFFFB) /* 65531 */
    final val CENTERPARENT: Position = Position(0xFFFA) /* 65530 */
    final val LEFTPARENT: Position   = Position(0xFFF9) /* 65529 */
    final val RIGHTPARENT: Position  = Position(0xFFF8) /* 65528 */
    final val TOP: Position          = LEFT
    final val BOTTOM: Position       = RIGHT
    final val TOPPARENT: Position    = LEFTPARENT
    final val BOTTOMPARENT: Position = RIGHTPARENT
  }

  // Callback Return Values
  implicit class Return(val value: CInt) extends AnyVal
  object Return {
    final val IGNORE: Return   = Return(-1)
    final val DEFAULT: Return  = Return(-2)
    final val CLOSE: Return    = Return(-3)
    final val CONTINUE: Return = Return(-4)
  }

  lazy val BUTTON1 = '1'
  lazy val BUTTON2 = '2'
  lazy val BUTTON3 = '3'
  lazy val BUTTON4 = '4'
  lazy val BUTTON5 = '5'

  // atom allocation Zone is for 'const char*' string arguments
  //   it was discovered that, in this library, strings that are declared 'const char*' can't be freed, at least
  //   until the element is displayed.  So, to be safe, they are never free (but also never duplicated) until
  //   the UI is closed
  // only for UI attributes and text which change seldomly if ever; gets closed by IupClose()
  private implicit val atomZone: Zone = Zone.open()
  private val atoms                   = new mutable.HashMap[String, CString]

  private def atom(s: String) =
    s match {
      case null => null
      case _ =>
        atoms get s match {
          case Some(value) => value
          case None =>
            val res = toCString(s)

            atoms(s) = res
            res
        }
    }

  private def copyChild(child: Seq[Handle]): Ptr[lib.IhandlePtr] = {
    val cs = alloc[lib.IhandlePtr]((child.length + 1).toUInt)

    for ((c, i) <- child.zipWithIndex)
      cs(i) = c.ih

    cs(child.length) = null
    cs
  }

  private implicit def cint2boolean(v: CInt): Boolean = if (v == 0) false else true

  // the callback map also stores the a reference to the object for which the callback was set
  //   so that it doesn't necessarily have to be a value class
  private val callbackMap = new mutable.HashMap[lib.IhandlePtr, (Handle, Handle => Return)]

  private def internalCallback(self: lib.IhandlePtr): CInt = {
    val (arg, func) = callbackMap(self)

    func(arg).value
  }

  implicit class Width(width: Int) {
    def x(height: Int): (Int, Int) = (width, height)
  }

  implicit class Handle(val ih: lib.IhandlePtr) extends AnyVal with Dynamic {
//    def selectDynamic(name: String): String = {}

    def applyDynamicNamed(method: String)(attrs: (String, Any)*): Handle =
      if (method == "apply") {
        attrs foreach { case (k, v) => updateDynamic(k)(v) }
        this
      } else sys.error(s"invalid method: '$method'")

    def updateDynamic(name: String)(value: Any): Unit = {
      value match {
        case s: String                 => lib.IupSetAttribute(ih, atom(name.toUpperCase), atom(s))
        case n: Int                    => lib.IupSetAttribute(ih, atom(name.toUpperCase), atom(n.toString))
        case (width: Int, height: Int) => lib.IupSetAttribute(ih, atom(name.toUpperCase), atom(s"${width}x$height"))
        case callback: Function1[_, _] =>
          callbackMap(ih) = (this, callback.asInstanceOf[Handle => Return])
          lib.IupSetCallback(ih, atom(name.toUpperCase), internalCallback _)
      }
    }

    def showXY(x: Position, y: Position): Result = lib.IupShowXY(ih, x.pos, y.pos)
  }

  // Main API

  def open: Result = lib.IupOpen(null, null)
  def close(): Unit = {
    lib.IupClose()
    atomZone.close()
  }
  def isOpened: Boolean = lib.IupIsOpened

  def mainLoop: Result = lib.IupMainLoop

  // Elements

  def vbox(child: Handle*): Handle = lib.IupVboxv(copyChild(child))

  def button(title: String, action: String): Handle = lib.IupButton(atom(title), atom(action))
  def dialog(child: Handle): Handle                 = lib.IupDialog(child.ih)
  def label(title: String): Handle                  = lib.IupLabel(atom(title))

  // Utilities

  // Pre-defined dialogs

  def message(title: String, msg: String): Unit = lib.IupMessage(atom(title), atom(msg))

}
