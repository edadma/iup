package io.github.edadma.iup

object Implicits {
  implicit class Width(width: Int) {
    def x(height: Int): (Int, Int) = (width, height)
  }
}
