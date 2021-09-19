//import io.github.edadma.iup.extern.{LibIUP => iup}
import io.github.edadma.iup.facade._

import scala.collection.mutable
import scala.scalanative.unsafe.{CFuncPtr1, CInt, CQuote, Zone, toCString}

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

//object Main extends App {
//
//  val btn_exit_cb = (h: Ihandle) => {
//    IupMessage("Hello World Message", "Hello world from IUP.")
//
//    /* Exits the main loop */
//    IUP_CLOSE
//  }
//
//  if (IupOpen == IUP_ERROR) {
//    println("Error opening window")
//    sys.exit(1)
//  }
//
//  val button = IupButton("OK", null);
//  val vbox   = IupVbox(button);
//  val dlg    = IupDialog(IupVbox(vbox))
//
//  dlg.TITLE = "Hello World 3"
//
//  /* Registers callbacks */
//  button.ACTION = btn_exit_cb
//
//  dlg.IupShowXY(IUP_CENTER, IUP_CENTER)
//
//  IupMainLoop
//
//  IupClose()
//
//}

object Main extends App {

  val btn_exit_cb =
    (h: Ihandle) =>
      /* Exits the main loop */
    IUP_CLOSE

  if (IupOpen == IUP_ERROR) {
    println("Error opening window")
    sys.exit(1)
  }

  val label  = IupLabel("Hello world from IUP.")
  val button = IupButton("OK", null)
  val vbox   = IupVbox(label, button)
  val dlg    = IupDialog(vbox)

  dlg.TITLE = "Hello World 4"

  /* Registers callbacks */
  button.ACTION = btn_exit_cb

  dlg.IupShowXY(IUP_CENTER, IUP_CENTER)

  IupMainLoop

  IupClose()

}
