package io.github.edadma.iup.extern

import scala.scalanative.unsafe._

@link("iup")
@extern
object LibIUP {

  type Ihandle_ = Ptr[CStruct0]

  // Main API

  def IupOpen(argc: Ptr[CInt], argv: Ptr[Ptr[CString]]): CInt = extern
  def IupClose(): Unit                                        = extern
  def IupIsOpened: CInt                                       = extern

  def IupMainLoop: CInt = extern

  def IupShowXY(ih: Ihandle_, x: CInt, y: CInt): CInt = extern

  def IupSetAttribute(ih: Ihandle_, name: CString, value: CString): Unit = extern

  // Elements

  def IupVboxv(child: Ptr[Ihandle_]): Ihandle_ = extern

  def IupDialog(child: Ihandle_): Ihandle_ = extern
  def IupLabel(title: CString): Ihandle_   = extern

  // Utilities

  // Pre-defined dialogs

  def IupMessage(title: CString, msg: CString): Unit = extern

}
