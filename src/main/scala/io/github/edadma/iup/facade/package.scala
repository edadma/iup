package io.github.edadma.iup

import io.github.edadma.iup.extern.{LibIUP => iup}

import scala.collection.mutable
import scala.scalanative.unsafe._
import scala.scalanative.unsigned._

package object facade {

  // Common Flags and Return Values
  implicit class IupResut(val res: CInt) extends AnyVal

  lazy val IUP_ERROR: IupResut   = IupResut(1)
  lazy val IUP_NOERROR: IupResut = IupResut(0)
  lazy val IUP_INVALID: IupResut = IupResut(-1)

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

  private lazy val atomZone = Zone.open() // only for UI attributes and text (const* char) which change seldomly if ever; gets closed by IupClose()
  private lazy val atoms    = mutable.HashMap[String, CString]()

  private def atom(s: String) =
    atoms get s match {
      case Some(value) => value
      case None =>
        val res = toCString(s)(atomZone)

        atoms(s) = res
        res
    }

  private def copyChild(child: Seq[Ihandle])(implicit z: Zone): Ptr[iup.Ihandle_] = {
    val cs = alloc[iup.Ihandle_]((child.length + 1).toUInt)

    for ((c, i) <- child.zipWithIndex)
      cs(i) = c.ih

    cs(child.length) = null
    cs
  }

  private implicit def cint2boolean(v: CInt): Boolean = if (v == 0) false else true

  implicit class Ihandle(val ih: iup.Ihandle_) extends AnyVal with Dynamic {
//    def selectDynamic(name: String): String = {}

    def updateDynamic(name: String)(value: String): Ihandle = {
      iup.IupSetAttribute(ih, atom(name), atom(value))
      this
    }

//    def int(attr: String): Int = {}
//
//    def int(attr: String, value: Int): Unit = {}

    def IupShowXY(x: IupPosition, y: IupPosition): IupResut = iup.IupShowXY(ih, x.pos, y.pos)
  }

  // Main API

  def IupOpen: IupResut = iup.IupOpen(null, null)
  def IupClose(): Unit = {
    iup.IupClose()
    atomZone.close()
  }
  def IupIsOpened: Boolean = iup.IupIsOpened

  def IupMainLoop: IupResut = iup.IupMainLoop

  // Elements

  def IupVbox(child: Ihandle*): Ihandle = Zone(implicit z => iup.IupVboxv(copyChild(child)))

  def IupDialog(child: Ihandle): Ihandle = iup.IupDialog(child.ih)
  def IupLabel(title: String): Ihandle   = iup.IupLabel(atom(title))

  // Utilities

  // Pre-defined dialogs

  def IupMessage(title: String, msg: String): Unit = Zone(implicit z => iup.IupMessage(toCString(title), toCString(msg)))

}
