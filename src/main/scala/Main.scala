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

////### [3.3 Using Pre-defined Dialogs](https://www.tecgraf.puc-rio.br/iup/en/tutorial/tutorial3.html#Using_Pre_Dialogs)
//
//import java.nio.file.{Files, Paths}
//import scala.util.{Failure, Success}
//import io.github.edadma.iup
//import io.github.edadma.iup.{Handle, Position, Return}
//
//object Main extends App {
//
//  def read_file(filename: String): String =
//    util.Using(scala.io.Source.fromFile(filename))(_.mkString) match {
//      case Failure(exception) =>
//        iup.message("Error", exception.getMessage)
//        null
//      case Success(text) => text
//    }
//
//  def write_file(filename: String, str: String): Unit =
//    try {
//      Files.write(Paths.get(filename), str.getBytes)
//    } catch {
//      case e: Exception => iup.message("Error", e.getMessage)
//    }
//
//  val open_cb = (_: Handle) => {
//    val filedlg = iup.fileDlg.set(dialogtype = "open", extfilter = "Text Files|*.txt|All Files|*.*|")
//
//    filedlg.popup(Position.CENTER, Position.CENTER)
//
//    if (filedlg.int.status != -1)
//      read_file(filedlg.value) match {
//        case null =>
//        case str  => multitext.str.value = str
//      }
//
//    filedlg.destroy()
//    Return.DEFAULT
//  }
//
//  val saveas_cb = (_: Handle) => {
//    val filedlg = iup.fileDlg.set(dialogtype = "save", extfilter = "Text Files|*.txt|All Files|*.*|")
//
//    filedlg.popup(Position.CENTER, Position.CENTER)
//
//    if (filedlg.int.status != -1)
//      write_file(filedlg.value, multitext.value)
//
//    filedlg.destroy()
//    Return.DEFAULT
//  }
//
//  val fond_cb = (_: Handle) => {
//    val fontdlg = iup.fontDlg
//    val font    = multitext.font
//
//    fontdlg.str.value = font
//    fontdlg.popup(Position.CENTER, Position.CENTER)
//
//    if (fontdlg.int.status != -1)
//      multitext.str.font = fontdlg.value
//
//    fontdlg.destroy()
//    Return.DEFAULT
//  }
//
//  val about_cb = (_: Handle) => {
//    iup.message("About", "Simple Notepad\n\nOriginal authors:\n Gustavo Lyrio\n Antonio Scuri")
//    Return.DEFAULT
//  }
//
//  val exit_cb = (_: Handle) => Return.CLOSE
//
//  iup.open
//
//  val multitext       = iup.text(null)(multiline = "yes", expand = "yes")
//  val item_open       = iup.item("Open...", null)(action = open_cb)
//  val item_saveas     = iup.item("Save As...", null)(action = saveas_cb)
//  val item_exit       = iup.item("Exit", null)(action = exit_cb)
//  val item_font       = iup.item("Font...", null)(action = fond_cb)
//  val item_about      = iup.item("About...", null)(action = about_cb)
//  val file_menu       = iup.menu(item_open, item_saveas, iup.separator, item_exit)
//  val format_menu     = iup.menu(item_font)
//  val help_menu       = iup.menu(item_about)
//  val sub_menu_file   = iup.submenu("File", file_menu)
//  val sub_format_file = iup.submenu("Format", format_menu)
//  val sub_menu_help   = iup.submenu("Help", help_menu)
//  val menu            = iup.menu(sub_menu_file, sub_format_file, sub_menu_help)
//  val vbox            = iup.vbox(multitext)
//  val dlg             = iup.dialog(vbox)(menu = menu, title = "Simple Notepad", size = "QUARTERxQUARTER")
//
//  dlg.showXY(Position.CENTER, Position.CENTER)
//  iup.mainLoop
//  iup.close()
//
//}

//### [3.4 Custom Dialogs](https://www.tecgraf.puc-rio.br/iup/en/tutorial/tutorial3.html#Custom_Dialogs)

import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success}
import io.github.edadma.iup
import io.github.edadma.iup.{Handle, Position, Return}

object Main extends App {

  def str_compare(l: String, idx: Int, r: String, casesensitive: Boolean): Boolean =
    if (l.length - idx < r.length) false
    else {
      for (i <- r.indices) {
        val equal = if (casesensitive) l(idx + i).toLower == r(i).toLower else l(idx + i) == r(i)

        if (!equal)
          return false
      }

      true
    }

  def str_find(str: String, str_to_find: String, casesensitive: Boolean): Int = {
    for (i <- str.indices)
      if (str_compare(str, i, str_to_find, casesensitive))
        return i

    -1
  }

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

  val item_open_action_cb = (item_open: Handle) => {
    val filedlg =
      iup.fileDlg.set(dialogtype = "open", extfilter = "Text Files|*.txt|All Files|*.*|", parentdialog = item_open.getDialog)

    filedlg.popup(Position.CENTERPARENT, Position.CENTERPARENT)

    if (filedlg.int.status != -1)
      read_file(filedlg.value) match {
        case null =>
        case str  => multitext.str.value = str
      }

    filedlg.destroy()
    Return.DEFAULT
  }

  val item_saveas_action_cb = (item_saveas: Handle) => {
    val filedlg =
      iup.fileDlg.set(dialogtype = "save", extfilter = "Text Files|*.txt|All Files|*.*|", parentdialog = item_saveas.getDialog)

    filedlg.popup(Position.CENTERPARENT, Position.CENTERPARENT)

    if (filedlg.int.status != -1)
      write_file(filedlg.value, multitext.value)

    filedlg.destroy()
    Return.DEFAULT
  }

  val goto_ok_action_cb = (bt_ok: Handle) => {
    val line_count = bt_ok.int.text_linecount
    val txt        = bt_ok.getDialogChild("LINE_TEXT")
    val line       = txt.int.value

    if (line < 1 || line >= line_count) {
      iup.message("Error", "Invalid line number.")
      Return.DEFAULT
    } else {
      bt_ok.getDialog.status = 1
      Return.CLOSE
    }
  }

  val fond_cb = (_: Handle) => {
    val fontdlg = iup.fontDlg
    val font    = multitext.font

    fontdlg.str.value = font
    fontdlg.popup(Position.CENTER, Position.CENTER)

    if (fontdlg.int.status != -1)
      multitext.str.font = fontdlg.value

    fontdlg.destroy()
    Return.DEFAULT
  }

  val about_cb = (_: Handle) => {
    iup.message("About", "Simple Notepad\n\nOriginal authors:\n Gustavo Lyrio\n Antonio Scuri")
    Return.DEFAULT
  }

  val exit_cb = (_: Handle) => Return.CLOSE

  iup.open

  val multitext       = iup.text(null)(multiline = "yes", expand = "yes")
  val item_open       = iup.item("Open...", null)(action = open_cb)
  val item_saveas     = iup.item("Save As...", null)(action = saveas_cb)
  val item_exit       = iup.item("Exit", null)(action = exit_cb)
  val item_font       = iup.item("Font...", null)(action = fond_cb)
  val item_about      = iup.item("About...", null)(action = about_cb)
  val file_menu       = iup.menu(item_open, item_saveas, iup.separator, item_exit)
  val format_menu     = iup.menu(item_font)
  val help_menu       = iup.menu(item_about)
  val sub_menu_file   = iup.submenu("File", file_menu)
  val sub_format_file = iup.submenu("Format", format_menu)
  val sub_menu_help   = iup.submenu("Help", help_menu)
  val menu            = iup.menu(sub_menu_file, sub_format_file, sub_menu_help)
  val vbox            = iup.vbox(multitext)
  val dlg             = iup.dialog(vbox)(menu = menu, title = "Simple Notepad", size = "QUARTERxQUARTER")

  dlg.showXY(Position.CENTER, Position.CENTER)
  iup.mainLoop
  iup.close()

}
