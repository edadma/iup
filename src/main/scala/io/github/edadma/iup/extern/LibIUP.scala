package io.github.edadma.iup.extern

import scala.scalanative.unsafe._

@link("iup")
@extern
object LibIUP {

  // Main API

  def IupOpen(argc: Ptr[CInt], argv: Ptr[Ptr[CString]]): CInt = extern
  def IupClose(): Unit                                        = extern

  // Elements

  // Utilities

  // Pre-defined dialogs

  def IupMessage(title: CString, msg: CString): Unit = extern

}
