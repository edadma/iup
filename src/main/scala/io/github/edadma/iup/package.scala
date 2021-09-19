package io.github.edadma

import scala.scalanative.unsafe._
import scala.scalanative.unsigned._

import scala.collection.mutable

import io.github.edadma.iup.extern.{LibIUP => lib}

package object iup {

  // Common Flags and Return Values
  implicit class IupResult(val res: CInt) extends AnyVal

  lazy val IUP_ERROR: IupResult   = IupResult(1)
  lazy val IUP_NOERROR: IupResult = IupResult(0)
  lazy val IUP_INVALID: IupResult = IupResult(-1)

  lazy val IUP_OPENED: Int = -1

  // Callback Return Values

  // IupPopup and IupShowXY Parameter Values
  implicit class IupPosition(val pos: CInt) extends AnyVal

  lazy val IUP_CENTER: IupPosition       = IupPosition(0xFFFF) /* 65535 */
  lazy val IUP_LEFT: IupPosition         = IupPosition(0xFFFE) /* 65534 */
  lazy val IUP_RIGHT: IupPosition        = IupPosition(0xFFFD) /* 65533 */
  lazy val IUP_MOUSEPOS: IupPosition     = IupPosition(0xFFFC) /* 65532 */
  lazy val IUP_CURRENT: IupPosition      = IupPosition(0xFFFB) /* 65531 */
  lazy val IUP_CENTERPARENT: IupPosition = IupPosition(0xFFFA) /* 65530 */
  lazy val IUP_LEFTPARENT: IupPosition   = IupPosition(0xFFF9) /* 65529 */
  lazy val IUP_RIGHTPARENT: IupPosition  = IupPosition(0xFFF8) /* 65528 */
  lazy val IUP_TOP: IupPosition          = IUP_LEFT
  lazy val IUP_BOTTOM: IupPosition       = IUP_RIGHT
  lazy val IUP_TOPPARENT: IupPosition    = IUP_LEFTPARENT
  lazy val IUP_BOTTOMPARENT: IupPosition = IUP_RIGHTPARENT

  // Callback Return Values
  implicit class IupReturn(val value: CInt) extends AnyVal

  lazy val IUP_IGNORE: IupReturn   = IupReturn(-1)
  lazy val IUP_DEFAULT: IupReturn  = IupReturn(-2)
  lazy val IUP_CLOSE: IupReturn    = IupReturn(-3)
  lazy val IUP_CONTINUE: IupReturn = IupReturn(-4)

  // atom allocation Zone is for 'const char*' string arguments
  //   it was discovered that, in this library, strings that are declared 'const char*' can't be freed, at least
  //   until the element is displayed.  So, to be safe, they are never free (but also never duplicated) until
  //   the UI is closed
  // only for UI attributes and text which change seldomly if ever; gets closed by IupClose()
  private lazy implicit val atomZone: Zone = Zone.open()
  private lazy val atoms                   = new mutable.HashMap[String, CString]

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

  private def copyChild(child: Seq[Ihandle]): Ptr[lib.IhandlePtr] = {
    val cs = alloc[lib.IhandlePtr]((child.length + 1).toUInt)

    for ((c, i) <- child.zipWithIndex)
      cs(i) = c.ih

    cs(child.length) = null
    cs
  }

  private implicit def cint2boolean(v: CInt): Boolean = if (v == 0) false else true

  // the callback map also stores the a reference to the object for which the callback was set
  //   so that it doesn't necessarily have to be a value class
  private val callbackMap = new mutable.HashMap[lib.IhandlePtr, (Ihandle, Ihandle => IupReturn)]

  private def internalCallback(self: lib.IhandlePtr): CInt = {
    val (arg, func) = callbackMap(self)

    func(arg).value
  }

  implicit class Width(width: Int) {
    def x(height: Int): (Int, Int) = (width, height)
  }

  implicit class Ihandle(val ih: lib.IhandlePtr) extends AnyVal with Dynamic {
//    def selectDynamic(name: String): String = {}

    def updateDynamic(name: String)(valueOrCallback: Any): Unit = {
      valueOrCallback match {
        case value: String             => lib.IupSetAttribute(ih, atom(name.toUpperCase), atom(value))
        case value: Int                => lib.IupSetAttribute(ih, atom(name.toUpperCase), atom(value.toString))
        case (width: Int, height: Int) => lib.IupSetAttribute(ih, atom(name.toUpperCase), atom(s"${width}x$height"))
        case callback: Function1[_, _] =>
          callbackMap(ih) = (this, callback.asInstanceOf[Ihandle => IupReturn])
          lib.IupSetCallback(ih, atom(name.toUpperCase), internalCallback _)
      }
    }

    def IupShowXY(x: IupPosition, y: IupPosition): IupResult = lib.IupShowXY(ih, x.pos, y.pos)
  }

  // Main API

  def IupOpen: IupResult = lib.IupOpen(null, null)
  def IupClose(): Unit = {
    lib.IupClose()
    atomZone.close()
  }
  def IupIsOpened: Boolean = lib.IupIsOpened

  def IupMainLoop: IupResult = lib.IupMainLoop

  // Elements

  def IupVbox(child: Ihandle*): Ihandle = lib.IupVboxv(copyChild(child))

  def IupButton(title: String, action: String): Ihandle = lib.IupButton(atom(title), atom(action))
  def IupDialog(child: Ihandle): Ihandle                = lib.IupDialog(child.ih)
  def IupLabel(title: String): Ihandle                  = lib.IupLabel(atom(title))

  // Utilities

  // Pre-defined dialogs

  def IupMessage(title: String, msg: String): Unit = lib.IupMessage(atom(title), atom(msg))

}
