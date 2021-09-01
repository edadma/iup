package io.github.edadma.iup

import io.github.edadma.iup.extern.{LibIUP => iup}

import scala.scalanative.unsafe._

package object facade {

  lazy val IUP_ERROR       = 1
  lazy val IUP_NOERROR     = 0
  lazy val IUP_OPENED: Int = -1

  // Main API

  def IupOpen: Int = iup.IupOpen(null, null)

  def IupClose(): Unit = iup.IupClose()

  // Elements

  // Utilities

  // Pre-defined dialogs

  def IupMessage(title: String, msg: String): Unit = Zone(implicit z => iup.IupMessage(toCString(title), toCString(msg)))

}
