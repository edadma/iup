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
//  val dlg    = IupDialog(vbox)
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

//object Main extends App {
//
//  val btn_exit_cb =
//    (h: Ihandle) =>
//      /* Exits the main loop */
//    IUP_CLOSE
//
//  if (IupOpen == IUP_ERROR) {
//    println("Error opening window")
//    sys.exit(1)
//  }
//
//  val label  = IupLabel("Hello world from IUP.")
//  val button = IupButton("OK", null)
//  val vbox   = IupVbox(label, button)
//  val dlg    = IupDialog(vbox)
//
//  dlg.TITLE = "Hello World 4"
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

import io.github.edadma.iup._

object Main extends App {

  val buttonExitCallback = (h: Handle) => Return.CLOSE

  if (open == Result.ERROR) {
    println("Error opening window")
    sys.exit(1)
  }

  val lbl = label("Hello world from IUP.")
  val btn = button("OK", null)(action = buttonExitCallback)
  val vbx = vbox(lbl, btn)(alignment = "acenter", gap = 10, margin = 30 x 10)
  val dlg = dialog(vbx)(title = "Hello World 5")

  dlg.showXY(Position.CENTER, Position.CENTER)
  mainLoop
  close()

}
