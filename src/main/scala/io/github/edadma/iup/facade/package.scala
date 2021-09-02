package io.github.edadma.iup

import io.github.edadma.iup.extern.{LibIUP => iup}

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

    def updateDynamic(name: String)(value: String): Ihandle = Zone { implicit z =>
      iup.IupSetAttribute(ih, toCString(name), toCString(value))
      this
    }

//    def int(attr: String): Int = {}
//
//    def int(attr: String, value: Int): Unit = {}

    def IupShowXY(x: IupPosition, y: IupPosition): IupResut = iup.IupShowXY(ih, x.pos, y.pos)
  }

  // Main API

  def IupOpen: IupResut    = iup.IupOpen(null, null)
  def IupClose(): Unit     = iup.IupClose()
  def IupIsOpened: Boolean = iup.IupIsOpened

  def IupMainLoop: IupResut = iup.IupMainLoop

  // Elements

  def IupVbox(child: Ihandle*): Ihandle = Zone(implicit z => iup.IupVboxv(copyChild(child)))

  def IupDialog(child: Ihandle): Ihandle = iup.IupDialog(child.ih)
  def IupLabel(title: String): Ihandle   = Zone(implicit z => iup.IupLabel(toCString(title)))

  // Utilities

  // Pre-defined dialogs

  def IupMessage(title: String, msg: String): Unit = Zone(implicit z => iup.IupMessage(toCString(title), toCString(msg)))

}
