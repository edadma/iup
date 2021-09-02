package io.github.edadma.iup.extern

import scala.scalanative.unsafe._

@link("iup")
@extern
object LibIUP {

  type IhandlePtr = Ptr[CStruct0]
  type Icallback  = CFuncPtr1[IhandlePtr, CInt]

  // Main API

  def IupOpen(argc: Ptr[CInt], argv: Ptr[Ptr[CString]]): CInt = extern
  def IupClose(): Unit                                        = extern
  def IupIsOpened: CInt                                       = extern

  def IupMainLoop: CInt = extern

  def IupShowXY(ih: IhandlePtr, x: CInt, y: CInt): CInt = extern

  def IupSetAttribute(ih: IhandlePtr, name: CString, value: CString): Unit = extern

  def IupSetCallback(ih: IhandlePtr, name: CString, func: Icallback): Icallback = extern

  // Elements

  def IupVboxv(child: Ptr[IhandlePtr]): IhandlePtr = extern

  def IupButton(title: CString, action: CString): IhandlePtr = extern
  def IupDialog(child: IhandlePtr): IhandlePtr               = extern
  def IupLabel(title: CString): IhandlePtr                   = extern

  // Utilities

  // Pre-defined dialogs

  def IupMessage(title: CString, msg: CString): Unit = extern

}
