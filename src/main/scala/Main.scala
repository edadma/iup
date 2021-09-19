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

////### [3.1 Main Dialog](https://www.tecgraf.puc-rio.br/iup/en/tutorial/tutorial3.html#Main_Dialog)
//
//import io.github.edadma.iup
//
//object Main extends App {
//
//  iup.open
//
//  val multitext = iup.text(null)(multiline = "yes", expand = "yes")
//  val vbox      = iup.vbox(multitext)
//  val dlg       = iup.dialog(vbox)(title = "Simple Notepad", size = "QUARTERxQUARTER")
//
//  dlg.showXY(iup.Position.CENTER, iup.Position.CENTER)
//  iup.mainLoop
//  iup.close()
//
//}

////### [3.2 Adding a Menu](https://www.tecgraf.puc-rio.br/iup/en/tutorial/tutorial3.html#Adding_a_Menu)
//
//import io.github.edadma.iup
//
//object Main extends App {
//
//  iup.open
//
//  val multitext   = iup.text(null)(multiline = "yes", expand = "yes")
//  val item_open   = iup.item("Open", null)
//  val item_saveas = iup.item("Save As", null)
//  val item_exit   = iup.item("Exit", null)(action = (_: iup.Handle) => iup.Return.CLOSE)
//  val file_menu   = iup.menu(item_open, item_saveas, iup.separator, item_exit)
//  val sub1_menu   = iup.submenu("File", file_menu)
//  val menu        = iup.menu(sub1_menu)
//  val vbox        = iup.vbox(multitext)
//  val dlg         = iup.dialog(vbox)(MENU = menu, title = "Simple Notepad", size = "QUARTERxQUARTER")
//
//  dlg.showXY(iup.Position.CENTER, iup.Position.CENTER)
//  iup.mainLoop
//  iup.close()
//
//}

//### [3.3 Using Pre-defined Dialogs](https://www.tecgraf.puc-rio.br/iup/en/tutorial/tutorial3.html#Using_Pre_Dialogs)

import java.nio.file.{Files, Paths}

import io.github.edadma.iup

import scala.util.{Failure, Success}

object Main extends App {

  def read_file(filename: String): String =
    util.Using(scala.io.Source.fromFile(filename))(_.mkString) match {
      case Failure(exception) =>
        iup.message("Error", exception.getMessage)
        null
      case Success(text) => text
    }

  def write_file(filename: String, str: String): Unit =
    try {
      Files.write(Paths.get(filename), str.getBytes)
    } catch {
      case e: Exception => iup.message("Error", e.getMessage)
    }

  iup.open

  val multitext   = iup.text(null)(multiline = "yes", expand = "yes")
  val item_open   = iup.item("Open", null)
  val item_saveas = iup.item("Save As", null)
  val item_exit   = iup.item("Exit", null)(action = (_: iup.Handle) => iup.Return.CLOSE)
  val file_menu   = iup.menu(item_open, item_saveas, iup.separator, item_exit)
  val sub1_menu   = iup.submenu("File", file_menu)
  val menu        = iup.menu(sub1_menu)
  val vbox        = iup.vbox(multitext)
  val dlg         = iup.dialog(vbox)(MENU = menu, title = "Simple Notepad", size = "QUARTERxQUARTER")

  dlg.showXY(iup.Position.CENTER, iup.Position.CENTER)
  iup.mainLoop
  iup.close()

}
