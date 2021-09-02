import io.github.edadma.iup.extern.LibIUP
import io.github.edadma.iup.facade._

import scala.scalanative.unsafe.{CQuote, Zone, toCString}

//object Main extends App {
//
//  if (IupOpen == IUP_ERROR) {
//    println("Error opening window")
//    sys.exit(1)
//  }
//
//  IupMessage("Hello World 1", "Hello world from IUP.")
//
//  IupClose()
//
//}

//object Main extends App {
//
//  if (IupOpen == IUP_ERROR) {
//    println("Error opening window")
//    sys.exit(1)
//  }
//
//  val label = IupLabel("Hello world from IUP.")
//  val dlg   = IupDialog(IupVbox(label))
//
//  dlg.TITLE = "Hello World 2"
//  dlg.IupShowXY(IUP_CENTER, IUP_CENTER)
//
//  IupMainLoop
//
//  IupClose()
//
//}

object Main extends App {

  def btn_exit_cb(self: Ihandle): IupReturn = {
    IupMessage("Hello World Message", "Hello world from IUP.");

    /* Exits the main loop */
    IUP_CLOSE
  }

  if (IupOpen == IUP_ERROR) {
    println("Error opening window")
    sys.exit(1)
  }

  val button = IupButton("OK", null);
  val vbox   = IupVbox(button);
  val dlg    = IupDialog(IupVbox(vbox))

  dlg.TITLE = "Hello World 3"

  /* Registers callbacks */
  button.ACTION = btn_exit_cb _

  dlg.IupShowXY(IUP_CENTER, IUP_CENTER)

  IupMainLoop

  IupClose()

}
