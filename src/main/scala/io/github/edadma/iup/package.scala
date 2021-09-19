package io.github.edadma

import scala.scalanative.unsafe._
import scala.scalanative.unsigned._

import scala.collection.mutable

import io.github.edadma.iup.extern.{LibIUP => lib}

package object iup {

  // Common Flags and Return Values
  implicit class Result(val res: CInt) extends AnyVal
  object Result {
    final val ERROR: Result      = Result(1)
    final val NOERROR: Result    = Result(0)
    final val OPENED: Result     = Result(-1)
    final val INVALID: Result    = Result(-1)
    final val INVALID_ID: Result = Result(-10)
  }

  // IupPopup and IupShowXY Parameter Values
  implicit class Position(val pos: CInt) extends AnyVal
  object Position {
    final val CENTER: Position       = Position(0xFFFF) /* 65535 */
    final val LEFT: Position         = Position(0xFFFE) /* 65534 */
    final val RIGHT: Position        = Position(0xFFFD) /* 65533 */
    final val MOUSEPOS: Position     = Position(0xFFFC) /* 65532 */
    final val CURRENT: Position      = Position(0xFFFB) /* 65531 */
    final val CENTERPARENT: Position = Position(0xFFFA) /* 65530 */
    final val LEFTPARENT: Position   = Position(0xFFF9) /* 65529 */
    final val RIGHTPARENT: Position  = Position(0xFFF8) /* 65528 */
    final val TOP: Position          = LEFT
    final val BOTTOM: Position       = RIGHT
    final val TOPPARENT: Position    = LEFTPARENT
    final val BOTTOMPARENT: Position = RIGHTPARENT
  }

  // Callback Return Values
  implicit class Return(val value: CInt) extends AnyVal
  object Return {
    final val IGNORE: Return   = Return(-1)
    final val DEFAULT: Return  = Return(-2)
    final val CLOSE: Return    = Return(-3)
    final val CONTINUE: Return = Return(-4)
  }

  lazy val BUTTON1 = '1'
  lazy val BUTTON2 = '2'
  lazy val BUTTON3 = '3'
  lazy val BUTTON4 = '4'
  lazy val BUTTON5 = '5'

  // atom allocation Zone is for 'const char*' string arguments
  //   it was discovered that, in this library, strings that are declared 'const char*' can't be freed, at least
  //   until the element is displayed.  So, to be safe, they are never free (but also never duplicated) until
  //   the UI is closed
  // only for UI attributes and text which change seldomly if ever; gets closed by IupClose()
  private implicit val atomZone: Zone = Zone.open()
  private val atoms                   = new mutable.HashMap[String, CString]

  private def atom(s: String) =
    s match {
      case null => null
      case _ =>
        atoms get s match {
          case Some(value) => value
          case None =>
            val res = toCString(s)

            atoms(s) = res
            res
        }
    }

  private def copyChild(child: Seq[Handle]): Ptr[lib.IhandlePtr] = {
    val cs = alloc[lib.IhandlePtr]((child.length + 1).toUInt)

    for ((c, i) <- child.zipWithIndex)
      cs(i) = c.ih

    cs(child.length) = null
    cs
  }

  private implicit def cint2boolean(v: CInt): Boolean = if (v == 0) false else true

  // the callback map also stores the a reference to the object for which the callback was set
  //   so that it doesn't necessarily have to be a value class
  private val callbackMap = new mutable.HashMap[lib.IhandlePtr, (Handle, Handle => Return)]

  private def internalCallback(self: lib.IhandlePtr): CInt = {
    val (arg, func) = callbackMap(self)

    func(arg).value
  }

  implicit class Width(width: Int) {
    def x(height: Int): (Int, Int) = (width, height)
  }

  implicit class Handle(val ih: lib.IhandlePtr) extends AnyVal with Dynamic {
//    def selectDynamic(name: String): String = {}

    def applyDynamicNamed(method: String)(attrs: (String, Any)*): Handle =
      if (method == "apply") {
        attrs foreach { case (k, v) => updateDynamic(k)(v) }
        this
      } else sys.error(s"invalid method: '$method'")

    def updateDynamic(name: String)(value: Any): Unit = {
      value match {
        case s: String                 => lib.IupSetAttribute(ih, atom(name.toUpperCase), atom(s))
        case n: Int                    => lib.IupSetAttribute(ih, atom(name.toUpperCase), atom(n.toString))
        case (width: Int, height: Int) => lib.IupSetAttribute(ih, atom(name.toUpperCase), atom(s"${width}x$height"))
        case callback: Function1[_, _] =>
          callbackMap(ih) = (this, callback.asInstanceOf[Handle => Return])
          lib.IupSetCallback(ih, atom(name.toUpperCase), internalCallback _)
      }
    }

    def showXY(x: Position, y: Position): Result = lib.IupShowXY(ih, x.pos, y.pos)
  }

  /************************************************************************/
  /*                        Main API                                      */
  /************************************************************************/
  def open: Result = lib.IupOpen(null, null) // todo: why argc/argv?
  def close(): Unit = {
    lib.IupClose()
    atomZone.close()
  }
  def isOpened: Boolean = lib.IupIsOpened
//  def imageLibOpen(): Unit = lib.IupImageLibOpen()
  def mainLoop: Result = lib.IupMainLoop
//  def loopStep: Int = lib.IupLoopStep()
//  def loopStepWait: Int = lib.IupLoopStepWait()
//  def mainLoopLevel: Int = lib.IupMainLoopLevel()
//  def flush(): Unit = lib.IupFlush()
//  def exitLoop(): Unit = lib.IupExitLoop()
//  def postMessage(ih: Ptr[Ihandle], s: /*const*/ String, i: Int, d: Double, p: Ptr[Unit]): Unit = lib.IupPostMessage(ih, s, i, d, p)
//  def recordInput(filename: /*const*/ String, mode: Int): Int = lib.IupRecordInput(filename, mode)
//  def playInput(filename: /*const*/ String): Int = lib.IupPlayInput(filename)
//  def update(ih: Ptr[Ihandle]): Unit = lib.IupUpdate(ih)
//  def updateChildren(ih: Ptr[Ihandle]): Unit = lib.IupUpdateChildren(ih)
//  def redraw(ih: Ptr[Ihandle], children: Int): Unit = lib.IupRedraw(ih, children)
//  def refresh(ih: Ptr[Ihandle]): Unit = lib.IupRefresh(ih)
//  def refreshChildren(ih: Ptr[Ihandle]): Unit = lib.IupRefreshChildren(ih)
//  def execute(filename: /*const*/ String, parameters: /*const*/ String): Int = lib.IupExecute(filename, parameters)
//  def executeWait(filename: /*const*/ String, parameters: /*const*/ String): Int = lib.IupExecuteWait(filename, parameters)
//  def help(url: /*const*/ String): Int = lib.IupHelp(url)
//  // def log(type: /*const*/ String, format: /*const*/ String): Unit = lib.IupLog(type, format)
//  def load(filename: /*const*/ String): String = lib.IupLoad(filename)
//  def loadBuffer(buffer: /*const*/ String): String = lib.IupLoadBuffer(buffer)
//  def version: String = lib.IupVersion()
//  def versionDate: String = lib.IupVersionDate()
//  def versionNumber: Int = lib.IupVersionNumber()
//  def versionShow(): Unit = lib.IupVersionShow()
//  def setLanguage(lng: /*const*/ String): Unit = lib.IupSetLanguage(lng)
//  def getLanguage: String = lib.IupGetLanguage()
//  def setLanguageString(name: /*const*/ String, str: /*const*/ String): Unit = lib.IupSetLanguageString(name, str)
//  def storeLanguageString(name: /*const*/ String, str: /*const*/ String): Unit = lib.IupStoreLanguageString(name, str)
//  def getLanguageString(name: /*const*/ String): String = lib.IupGetLanguageString(name)
//  def setLanguagePack(ih: Ptr[Ihandle]): Unit = lib.IupSetLanguagePack(ih)
//  def destroy(ih: Ptr[Ihandle]): Unit = lib.IupDestroy(ih)
//  def detach(child: Ptr[Ihandle]): Unit = lib.IupDetach(child)
//  def append(ih: Ptr[Ihandle], child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupAppend(ih, child)
//  def insert(ih: Ptr[Ihandle], ref_child: Ptr[Ihandle], child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupInsert(ih, ref_child, child)
//  def getChild(ih: Ptr[Ihandle], pos: Int): Ptr[Ihandle] = lib.IupGetChild(ih, pos)
//  def getChildPos(ih: Ptr[Ihandle], child: Ptr[Ihandle]): Int = lib.IupGetChildPos(ih, child)
//  def getChildCount(ih: Ptr[Ihandle]): Int = lib.IupGetChildCount(ih)
//  def getNextChild(ih: Ptr[Ihandle], child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupGetNextChild(ih, child)
//  def getBrother(ih: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupGetBrother(ih)
//  def getParent(ih: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupGetParent(ih)
//  def getDialog(ih: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupGetDialog(ih)
//  def getDialogChild(ih: Ptr[Ihandle], name: /*const*/ String): Ptr[Ihandle] = lib.IupGetDialogChild(ih, name)
//  def reparent(ih: Ptr[Ihandle], new_parent: Ptr[Ihandle], ref_child: Ptr[Ihandle]): Int = lib.IupReparent(ih, new_parent, ref_child)
//  def popup(ih: Ptr[Ihandle], x: Int, y: Int): Int = lib.IupPopup(ih, x, y)
//  def show(ih: Ptr[Ihandle]): Int = lib.IupShow(ih)
//  def showXY(ih: Ptr[Ihandle], x: Int, y: Int): Int = lib.IupShowXY(ih, x, y)
//  def hide(ih: Ptr[Ihandle]): Int = lib.IupHide(ih)
//  def map(ih: Ptr[Ihandle]): Int = lib.IupMap(ih)
//  def unmap(ih: Ptr[Ihandle]): Unit = lib.IupUnmap(ih)
//  def resetAttribute(ih: Ptr[Ihandle], name: /*const*/ String): Unit = lib.IupResetAttribute(ih, name)
//  def getAllAttributes(ih: Ptr[Ihandle], names: Ptr[String], n: Int): Int = lib.IupGetAllAttributes(ih, names, n)
//  def copyAttributes(src_ih: Ptr[Ihandle], dst_ih: Ptr[Ihandle]): Unit = lib.IupCopyAttributes(src_ih, dst_ih)
//  // def setAtt(handle_name: /*const*/ String, ih: Ptr[Ihandle], name: /*const*/ String): Ptr[Ihandle] = lib.IupSetAtt(handle_name, ih, name)
//  def setAttributes(ih: Ptr[Ihandle], str: /*const*/ String): Ptr[Ihandle] = lib.IupSetAttributes(ih, str)
//  def getAttributes(ih: Ptr[Ihandle]): String = lib.IupGetAttributes(ih)
//  def setAttribute(ih: Ptr[Ihandle], name: /*const*/ String, value: /*const*/ String): Unit = lib.IupSetAttribute(ih, name, value)
//  def setStrAttribute(ih: Ptr[Ihandle], name: /*const*/ String, value: /*const*/ String): Unit = lib.IupSetStrAttribute(ih, name, value)
//  // def setStrf(ih: Ptr[Ihandle], name: /*const*/ String, format: /*const*/ String): Unit = lib.IupSetStrf(ih, name, format)
//  def setInt(ih: Ptr[Ihandle], name: /*const*/ String, value: Int): Unit = lib.IupSetInt(ih, name, value)
//  def setDouble(ih: Ptr[Ihandle], name: /*const*/ String, value: Double): Unit = lib.IupSetDouble(ih, name, value)
//  def setRGB(ih: Ptr[Ihandle], name: /*const*/ String, r: Char, g: Char, b: Char): Unit = lib.IupSetRGB(ih, name, r, g, b)
//  def setRGBA(ih: Ptr[Ihandle], name: /*const*/ String, r: Char, g: Char, b: Char, a: Char): Unit = lib.IupSetRGBA(ih, name, r, g, b, a)
//  def getAttribute(ih: Ptr[Ihandle], name: /*const*/ String): String = lib.IupGetAttribute(ih, name)
//  def getInt(ih: Ptr[Ihandle], name: /*const*/ String): Int = lib.IupGetInt(ih, name)
//  def getInt2(ih: Ptr[Ihandle], name: /*const*/ String): Int = lib.IupGetInt2(ih, name)
//  def getIntInt(ih: Ptr[Ihandle], name: /*const*/ String, i1: Ptr[Int], i2: Ptr[Int]): Int = lib.IupGetIntInt(ih, name, i1, i2)
//  def getDouble(ih: Ptr[Ihandle], name: /*const*/ String): Double = lib.IupGetDouble(ih, name)
//  def getRGB(ih: Ptr[Ihandle], name: /*const*/ String, r: Ptr[Char], g: Ptr[Char], b: Ptr[Char]): Unit = lib.IupGetRGB(ih, name, r, g, b)
//  def getRGBA(ih: Ptr[Ihandle], name: /*const*/ String, r: Ptr[Char], g: Ptr[Char], b: Ptr[Char], a: Ptr[Char]): Unit = lib.IupGetRGBA(ih, name, r, g, b, a)
//  def setAttributeId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int, value: /*const*/ String): Unit = lib.IupSetAttributeId(ih, name, id, value)
//  def setStrAttributeId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int, value: /*const*/ String): Unit = lib.IupSetStrAttributeId(ih, name, id, value)
//  // def setStrfId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int, format: /*const*/ String): Unit = lib.IupSetStrfId(ih, name, id, format)
//  def setIntId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int, value: Int): Unit = lib.IupSetIntId(ih, name, id, value)
//  def setDoubleId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int, value: Double): Unit = lib.IupSetDoubleId(ih, name, id, value)
//  def setRGBId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int, r: Char, g: Char, b: Char): Unit = lib.IupSetRGBId(ih, name, id, r, g, b)
//  def getAttributeId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int): String = lib.IupGetAttributeId(ih, name, id)
//  def getIntId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int): Int = lib.IupGetIntId(ih, name, id)
//  def getDoubleId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int): Double = lib.IupGetDoubleId(ih, name, id)
//  def getRGBId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int, r: Ptr[Char], g: Ptr[Char], b: Ptr[Char]): Unit = lib.IupGetRGBId(ih, name, id, r, g, b)
//  def setAttributeId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int, value: /*const*/ String): Unit = lib.IupSetAttributeId2(ih, name, lin, col, value)
//  def setStrAttributeId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int, value: /*const*/ String): Unit = lib.IupSetStrAttributeId2(ih, name, lin, col, value)
//  // def setStrfId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int, format: /*const*/ String): Unit = lib.IupSetStrfId2(ih, name, lin, col, format)
//  def setIntId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int, value: Int): Unit = lib.IupSetIntId2(ih, name, lin, col, value)
//  def setDoubleId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int, value: Double): Unit = lib.IupSetDoubleId2(ih, name, lin, col, value)
//  def setRGBId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int, r: Char, g: Char, b: Char): Unit = lib.IupSetRGBId2(ih, name, lin, col, r, g, b)
//  def getAttributeId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int): String = lib.IupGetAttributeId2(ih, name, lin, col)
//  def getIntId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int): Int = lib.IupGetIntId2(ih, name, lin, col)
//  def getDoubleId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int): Double = lib.IupGetDoubleId2(ih, name, lin, col)
//  def getRGBId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int, r: Ptr[Char], g: Ptr[Char], b: Ptr[Char]): Unit = lib.IupGetRGBId2(ih, name, lin, col, r, g, b)
//  def setGlobal(name: /*const*/ String, value: /*const*/ String): Unit = lib.IupSetGlobal(name, value)
//  def setStrGlobal(name: /*const*/ String, value: /*const*/ String): Unit = lib.IupSetStrGlobal(name, value)
//  def getGlobal(name: /*const*/ String): String = lib.IupGetGlobal(name)
//  def setFocus(ih: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupSetFocus(ih)
//  def getFocus: Ptr[Ihandle] = lib.IupGetFocus()
//  def previousField(ih: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupPreviousField(ih)
//  def nextField(ih: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupNextField(ih)
//  def getCallback(ih: Ptr[Ihandle], name: /*const*/ String): Icallback = lib.IupGetCallback(ih, name)
//  def setCallback(ih: Ptr[Ihandle], name: /*const*/ String, func: Icallback): Icallback = lib.IupSetCallback(ih, name, func)
//  // def setCallbacks(ih: Ptr[Ihandle], name: /*const*/ String, func: Icallback): Ptr[Ihandle] = lib.IupSetCallbacks(ih, name, func)
//  def getFunction(name: /*const*/ String): Icallback = lib.IupGetFunction(name)
//  def setFunction(name: /*const*/ String, func: Icallback): Icallback = lib.IupSetFunction(name, func)
//  def getHandle(name: /*const*/ String): Ptr[Ihandle] = lib.IupGetHandle(name)
//  def setHandle(name: /*const*/ String, ih: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupSetHandle(name, ih)
//  def getAllNames(names: Ptr[String], n: Int): Int = lib.IupGetAllNames(names, n)
//  def getAllDialogs(names: Ptr[String], n: Int): Int = lib.IupGetAllDialogs(names, n)
//  def getName(ih: Ptr[Ihandle]): String = lib.IupGetName(ih)
//  def setAttributeHandle(ih: Ptr[Ihandle], name: /*const*/ String, ih_named: Ptr[Ihandle]): Unit = lib.IupSetAttributeHandle(ih, name, ih_named)
//  def getAttributeHandle(ih: Ptr[Ihandle], name: /*const*/ String): Ptr[Ihandle] = lib.IupGetAttributeHandle(ih, name)
//  def setAttributeHandleId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int, ih_named: Ptr[Ihandle]): Unit = lib.IupSetAttributeHandleId(ih, name, id, ih_named)
//  def getAttributeHandleId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int): Ptr[Ihandle] = lib.IupGetAttributeHandleId(ih, name, id)
//  def setAttributeHandleId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int, ih_named: Ptr[Ihandle]): Unit = lib.IupSetAttributeHandleId2(ih, name, lin, col, ih_named)
//  def getAttributeHandleId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int): Ptr[Ihandle] = lib.IupGetAttributeHandleId2(ih, name, lin, col)
//  def getClassName(ih: Ptr[Ihandle]): String = lib.IupGetClassName(ih)
//  def getClassType(ih: Ptr[Ihandle]): String = lib.IupGetClassType(ih)
//  def getAllClasses(names: Ptr[String], n: Int): Int = lib.IupGetAllClasses(names, n)
//  def getClassAttributes(classname: /*const*/ String, names: Ptr[String], n: Int): Int = lib.IupGetClassAttributes(classname, names, n)
//  def getClassCallbacks(classname: /*const*/ String, names: Ptr[String], n: Int): Int = lib.IupGetClassCallbacks(classname, names, n)
//  def saveClassAttributes(ih: Ptr[Ihandle]): Unit = lib.IupSaveClassAttributes(ih)
//  def copyClassAttributes(src_ih: Ptr[Ihandle], dst_ih: Ptr[Ihandle]): Unit = lib.IupCopyClassAttributes(src_ih, dst_ih)
//  def setClassDefaultAttribute(classname: /*const*/ String, name: /*const*/ String, value: /*const*/ String): Unit = lib.IupSetClassDefaultAttribute(classname, name, value)
//  def classMatch(ih: Ptr[Ihandle], classname: /*const*/ String): Int = lib.IupClassMatch(ih, classname)
//  def create(classname: /*const*/ String): Ptr[Ihandle] = lib.IupCreate(classname)
//  def createv(classname: /*const*/ String, params: Ptr[Ptr[Unit]]): Ptr[Ihandle] = lib.IupCreatev(classname, params)
//  // def createp(classname: /*const*/ String, first: Ptr[Unit]): Ptr[Ihandle] = lib.IupCreatep(classname, first)

  /************************************************************************/
  /*                        Elements                                      */
  /************************************************************************/
//  def fill: Ptr[Ihandle] = lib.IupFill()
//  def space: Ptr[Ihandle] = lib.IupSpace()
//  def radio(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupRadio(child)
  def vbox(child: Handle*): Handle = lib.IupVboxv(copyChild(child))
//  // def zbox(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupZbox(child)
//  def zboxv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = lib.IupZboxv(children)
//  // def hbox(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupHbox(child)
//  def hboxv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = lib.IupHboxv(children)
//  // def normalizer(ih_first: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupNormalizer(ih_first)
//  def normalizerv(ih_list: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = lib.IupNormalizerv(ih_list)
//  // def cbox(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupCbox(child)
//  def cboxv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = lib.IupCboxv(children)
//  def sbox(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupSbox(child)
//  def split(child1: Ptr[Ihandle], child2: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupSplit(child1, child2)
//  def scrollBox(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupScrollBox(child)
//  def flatScrollBox(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupFlatScrollBox(child)
//  // def gridBox(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupGridBox(child)
//  def gridBoxv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = lib.IupGridBoxv(children)
//  // def multiBox(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupMultiBox(child)
//  def multiBoxv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = lib.IupMultiBoxv(children)
//  def expander(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupExpander(child)
//  def detachBox(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupDetachBox(child)
//  def backgroundBox(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupBackgroundBox(child)
//  def frame(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupFrame(child)
//  def flatFrame(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupFlatFrame(child)
//  def image(width: Int, height: Int, pixels: Ptr[/*const*/ Char]): Ptr[Ihandle] = lib.IupImage(width, height, pixels)
//  def imageRGB(width: Int, height: Int, pixels: Ptr[/*const*/ Char]): Ptr[Ihandle] = lib.IupImageRGB(width, height, pixels)
//  def imageRGBA(width: Int, height: Int, pixels: Ptr[/*const*/ Char]): Ptr[Ihandle] = lib.IupImageRGBA(width, height, pixels)
//  def item(title: /*const*/ String, action: /*const*/ String): Ptr[Ihandle] = lib.IupItem(title, action)
//  def submenu(title: /*const*/ String, child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupSubmenu(title, child)
//  def separator: Ptr[Ihandle] = lib.IupSeparator()
//  // def menu(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupMenu(child)
//  def menuv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = lib.IupMenuv(children)
  def button(title: String, action: String): Handle = lib.IupButton(atom(title), atom(action))
//  def flatButton(title: /*const*/ String): Ptr[Ihandle] = lib.IupFlatButton(title)
//  def flatToggle(title: /*const*/ String): Ptr[Ihandle] = lib.IupFlatToggle(title)
//  def dropButton(dropchild: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupDropButton(dropchild)
//  def flatLabel(title: /*const*/ String): Ptr[Ihandle] = lib.IupFlatLabel(title)
//  def flatSeparator: Ptr[Ihandle] = lib.IupFlatSeparator()
//  def canvas(action: /*const*/ String): Ptr[Ihandle] = lib.IupCanvas(action)
  def dialog(child: Handle): Handle = lib.IupDialog(child.ih)
//  def user: Ptr[Ihandle] = lib.IupUser()
//  def thread: Ptr[Ihandle] = lib.IupThread()
  def label(title: String): Handle = lib.IupLabel(atom(title))
//  def list(action: /*const*/ String): Ptr[Ihandle] = lib.IupList(action)
//  def flatList: Ptr[Ihandle] = lib.IupFlatList()
//  def text(action: /*const*/ String): Ptr[Ihandle] = lib.IupText(action)
//  def multiLine(action: /*const*/ String): Ptr[Ihandle] = lib.IupMultiLine(action)
//  def toggle(title: /*const*/ String, action: /*const*/ String): Ptr[Ihandle] = lib.IupToggle(title, action)
//  def timer: Ptr[Ihandle] = lib.IupTimer()
//  def clipboard: Ptr[Ihandle] = lib.IupClipboard()
//  def progressBar: Ptr[Ihandle] = lib.IupProgressBar()
//  def val(type: /*const*/ String): Ptr[Ihandle] = lib.IupVal(type)
//  def flatVal(type: /*const*/ String): Ptr[Ihandle] = lib.IupFlatVal(type)
//  def flatTree: Ptr[Ihandle] = lib.IupFlatTree()
//  // def tabs(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupTabs(child)
//  def tabsv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = lib.IupTabsv(children)
//  // def flatTabs(first: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupFlatTabs(first)
//  def flatTabsv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = lib.IupFlatTabsv(children)
//  def tree: Ptr[Ihandle] = lib.IupTree()
//  def link(url: /*const*/ String, title: /*const*/ String): Ptr[Ihandle] = lib.IupLink(url, title)
//  def animatedLabel(animation: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupAnimatedLabel(animation)
//  def datePick: Ptr[Ihandle] = lib.IupDatePick()
//  def calendar: Ptr[Ihandle] = lib.IupCalendar()
//  def colorbar: Ptr[Ihandle] = lib.IupColorbar()
//  def gauge: Ptr[Ihandle] = lib.IupGauge()
//  def dial(type: /*const*/ String): Ptr[Ihandle] = lib.IupDial(type)
//  def colorBrowser: Ptr[Ihandle] = lib.IupColorBrowser()
//  def spin: Ptr[Ihandle] = lib.IupSpin()
//  def spinbox(child: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupSpinbox(child)

  /************************************************************************/
  /*                      Utilities                                       */
  /************************************************************************/
//  def stringCompare(str1: /*const*/ String, str2: /*const*/ String, casesensitive: Int, lexicographic: Int): Int = lib.IupStringCompare(str1, str2, casesensitive, lexicographic)
//  def saveImageAsText(ih: Ptr[Ihandle], filename: /*const*/ String, format: /*const*/ String, name: /*const*/ String): Int = lib.IupSaveImageAsText(ih, filename, format, name)
//  def imageGetHandle(name: /*const*/ String): Ptr[Ihandle] = lib.IupImageGetHandle(name)
//  def textConvertLinColToPos(ih: Ptr[Ihandle], lin: Int, col: Int, pos: Ptr[Int]): Unit = lib.IupTextConvertLinColToPos(ih, lin, col, pos)
//  def textConvertPosToLinCol(ih: Ptr[Ihandle], pos: Int, lin: Ptr[Int], col: Ptr[Int]): Unit = lib.IupTextConvertPosToLinCol(ih, pos, lin, col)
//  def convertXYToPos(ih: Ptr[Ihandle], x: Int, y: Int): Int = lib.IupConvertXYToPos(ih, x, y)
//  def storeGlobal(name: /*const*/ String, value: /*const*/ String): Unit = lib.IupStoreGlobal(name, value)
//  def storeAttribute(ih: Ptr[Ihandle], name: /*const*/ String, value: /*const*/ String): Unit = lib.IupStoreAttribute(ih, name, value)
//  // def setfAttribute(ih: Ptr[Ihandle], name: /*const*/ String, format: /*const*/ String): Unit = lib.IupSetfAttribute(ih, name, format)
//  def storeAttributeId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int, value: /*const*/ String): Unit = lib.IupStoreAttributeId(ih, name, id, value)
//  // def setfAttributeId(ih: Ptr[Ihandle], name: /*const*/ String, id: Int, f: /*const*/ String): Unit = lib.IupSetfAttributeId(ih, name, id, f)
//  def storeAttributeId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int, value: /*const*/ String): Unit = lib.IupStoreAttributeId2(ih, name, lin, col, value)
//  // def setfAttributeId2(ih: Ptr[Ihandle], name: /*const*/ String, lin: Int, col: Int, format: /*const*/ String): Unit = lib.IupSetfAttributeId2(ih, name, lin, col, format)
//  def treeSetUserId(ih: Ptr[Ihandle], id: Int, userid: Ptr[Unit]): Int = lib.IupTreeSetUserId(ih, id, userid)
//  def treeGetUserId(ih: Ptr[Ihandle], id: Int): Ptr[Unit] = lib.IupTreeGetUserId(ih, id)
//  def treeGetId(ih: Ptr[Ihandle], userid: Ptr[Unit]): Int = lib.IupTreeGetId(ih, userid)
//  def treeSetAttributeHandle(ih: Ptr[Ihandle], name: /*const*/ String, id: Int, ih_named: Ptr[Ihandle]): Unit = lib.IupTreeSetAttributeHandle(ih, name, id, ih_named)

  /************************************************************************/
  /*                      Pre-defined dialogs                             */
  /************************************************************************/
//  def fileDlg: Ptr[Ihandle] = lib.IupFileDlg()
//  def messageDlg: Ptr[Ihandle] = lib.IupMessageDlg()
//  def colorDlg: Ptr[Ihandle] = lib.IupColorDlg()
//  def fontDlg: Ptr[Ihandle] = lib.IupFontDlg()
//  def progressDlg: Ptr[Ihandle] = lib.IupProgressDlg()
//  def getFile(arq: String): Int = lib.IupGetFile(arq)
  def message(title: String, msg: String): Unit = lib.IupMessage(atom(title), atom(msg))
//  // def messagef(title: /*const*/ String, format: /*const*/ String): Unit = lib.IupMessagef(title, format)
//  def messageError(parent: Ptr[Ihandle], message: /*const*/ String): Unit = lib.IupMessageError(parent, message)
//  def messageAlarm(parent: Ptr[Ihandle], title: /*const*/ String, message: /*const*/ String, buttons: /*const*/ String): Int = lib.IupMessageAlarm(parent, title, message, buttons)
//  def alarm(title: /*const*/ String, msg: /*const*/ String, b1: /*const*/ String, b2: /*const*/ String, b3: /*const*/ String): Int = lib.IupAlarm(title, msg, b1, b2, b3)
//  // def scanf(format: /*const*/ String): Int = lib.IupScanf(format)
//  def listDialog(type: Int, title: /*const*/ String, size: Int, list: Ptr[/*const*/ String], op: Int, max_col: Int, max_lin: Int, marks: Ptr[Int]): Int = lib.IupListDialog(type, title, size, list, op, max_col, max_lin, marks)
//  def getText(title: /*const*/ String, text: String, maxsize: Int): Int = lib.IupGetText(title, text, maxsize)
//  def getColor(x: Int, y: Int, r: Ptr[Char], g: Ptr[Char], b: Ptr[Char]): Int = lib.IupGetColor(x, y, r, g, b)
// def IupGetParam(title: /*const*/ CString, action: Iparamcb, user_data: Ptr[Unit], format: /*const*/ CString): CInt = extern //340
//def IupGetParamv(title: /*const*/ CString, action: Iparamcb, user_data: Ptr[Unit], format: /*const*/ CString, param_count: CInt, param_extra: CInt, param_data: Ptr[Ptr[Unit]]): CInt = extern //341
//  def IupParam(format: /*const*/ CString): Ptr[Ihandle] = extern //342
//  // def IupParamBox(param: Ptr[Ihandle]): Ptr[Ihandle] = extern //343
//  def IupParamBoxv(param_array: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = extern //344
//  def IupLayoutDialog(dialog: Ptr[Ihandle]): Ptr[Ihandle] = extern //346
//  def IupElementPropertiesDialog(parent: Ptr[Ihandle], elem: Ptr[Ihandle]): Ptr[Ihandle] = extern //347
//  def IupGlobalsDialog(): Ptr[Ihandle] = extern //348
//  def IupClassInfoDialog(parent: Ptr[Ihandle]): Ptr[Ihandle] = extern //349
//  // def getParam(title: /*const*/ String, action: Iparamcb, user_data: Ptr[Unit], format: /*const*/ String): Int = lib.IupGetParam(title, action, user_data, format)
//  def getParamv(title: /*const*/ String, action: Iparamcb, user_data: Ptr[Unit], format: /*const*/ String, param_count: Int, param_extra: Int, param_data: Ptr[Ptr[Unit]]): Int = lib.IupGetParamv(title, action, user_data, format, param_count, param_extra, param_data)
//  def param(format: /*const*/ String): Ptr[Ihandle] = lib.IupParam(format)
//  // def paramBox(param: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupParamBox(param)
//  def paramBoxv(param_array: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = lib.IupParamBoxv(param_array)
//  def layoutDialog(dialog: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupLayoutDialog(dialog)
//  def elementPropertiesDialog(parent: Ptr[Ihandle], elem: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupElementPropertiesDialog(parent, elem)
//  def globalsDialog(): Ptr[Ihandle] = lib.IupGlobalsDialog()
//  def classInfoDialog(parent: Ptr[Ihandle]): Ptr[Ihandle] = lib.IupClassInfoDialog(parent)

}
