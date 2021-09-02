package io.github.edadma.iup.facade

import io.github.edadma.iup.extern.{LibIUP => iup}

import scala.scalanative.unsafe._

object Util {

  def wrapCallback(callback: (Ihandle => IupReturn)): iup.IhandlePtr => CInt /*: iup.Icallback*/ =
    (ptr: iup.IhandlePtr) => callback(ptr).ret

}
