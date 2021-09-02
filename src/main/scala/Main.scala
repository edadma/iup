import io.github.edadma.iup.facade._

//object Main extends App {
//
//  if (IupOpen == IUP_ERROR) {
//    println("Error opening window")
//    sys.exit(1)
//  }
//
//  IupMessage("Hello World 1", "Hello world from IUP.")
//  IupClose()
//
//}

object Main extends App {

  if (IupOpen == IUP_ERROR) {
    println("Error opening window")
    sys.exit(1)
  }

  val label = IupLabel("Hello world from IUP.")
  val dlg   = IupDialog(IupVbox(label))

  dlg.TITLE = "Hello World 2"
  dlg.IupShowXY(IUP_CENTER, IUP_CENTER)

  IupMainLoop
  IupClose()

}
