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

  /************************************************************************/
  /*                      Pre-Defined Masks                               */
  /************************************************************************/
  lazy val MASK_FLOAT       = "[+/-]?(/d+/.?/d*|/./d+)"
  lazy val MASK_UFLOAT      = "(/d+/.?/d*|/./d+)"
  lazy val MASK_EFLOAT      = "[+/-]?(/d+/.?/d*|/./d+)([eE][+/-]?/d+)?"
  lazy val MASK_UEFLOAT     = "(/d+/.?/d*|/./d+)([eE][+/-]?/d+)?"
  lazy val MASK_FLOATCOMMA  = "[+/-]?(/d+/,?/d*|/,/d+)"
  lazy val MASK_UFLOATCOMMA = "(/d+/,?/d*|/,/d+)"
  lazy val MASK_INT         = "[+/-]?/d+"
  lazy val MASK_UINT        = "/d+"

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
      cs(i) = c.ptr

    cs(child.length) = null
    cs
  }

  private implicit def cint2boolean(v: CInt): Boolean = if (v == 0) false else true

  // the callback map also stores the a reference to the object for which the callback was set
  //   so that it doesn't necessarily have to be a value class
  private val callbackMapType1 = new mutable.HashMap[lib.IhandlePtr, (Handle, Handle => Return)]

  private def callbackType1(self: lib.IhandlePtr): CInt = {
    val (arg, func) = callbackMapType1(self)

    func(arg).value
  }

  class AttributeHandle(val ptr: lib.IhandlePtr) extends AnyVal with Dynamic {
    def selectDynamic(name: String): Handle = lib.IupGetAttribute(ptr, atom(name.toUpperCase)).asInstanceOf[Handle]
  }

  class AttributeInt(val ptr: lib.IhandlePtr) extends AnyVal with Dynamic {
    def updateDynamic(name: String)(value: Int): Unit = ptr.setInt(name.toUpperCase, value)
    def selectDynamic(name: String): Int              = ptr.getInt(name.toUpperCase)
  }

  class AttributeStr(val ptr: lib.IhandlePtr) extends AnyVal with Dynamic {
    def updateDynamic(name: String)(value: String): Unit = ptr.setStrAttribute(name.toUpperCase, value)
    def selectDynamic(name: String): String              = ptr.getAttribute(name.toUpperCase)
  }

  implicit class Handle(val ptr: lib.IhandlePtr) extends AnyVal with Dynamic {

    /************************************************************************/
    /*                        Main API                                      */
    /************************************************************************/
    def int = new AttributeInt(ptr)

    def str = new AttributeStr(ptr)

    def handle = new AttributeHandle(ptr)

    def selectDynamic(name: String): String = ptr.getAttribute(name.toUpperCase)

    def applyDynamicNamed(method: String)(attrs: (String, Any)*): Handle =
      if (method == "apply" || method == "set") {
        attrs foreach { case (k, v) => updateDynamic(k)(v) }
        this
      } else sys.error(s"invalid method: '$method'")

    def updateDynamic(name: String)(value: Any): Unit = {
      val uname          = name.toUpperCase
      lazy val className = getClassName

      value match {
        case s: String                 => lib.IupSetAttribute(ptr, atom(uname), atom(s))
        case n: Int                    => lib.IupSetAttribute(ptr, atom(uname), atom(n.toString))
        case (width: Int, height: Int) => lib.IupSetAttribute(ptr, atom(uname), atom(s"${width}x$height"))
        case type1: Function1[_, _] =>
          callbackMapType1(ptr) = (this, type1.asInstanceOf[Handle => Return])
          lib.IupSetCallback(ptr, atom(uname), callbackType1 _)
        case handle: Handle => lib.IupSetAttributeHandle(ptr, atom(uname), handle.ptr)
      }
    }

    //  def postMessage(ih: Handle, s: /*const*/ String, i: Int, d: Double, p: Ptr[Unit]): Unit = lib.IupPostMessage(ih, s, i, d, p)
    def update(): Unit         = lib.IupUpdate(ptr)
    def updateChildren(): Unit = lib.IupUpdateChildren(ptr)
    //  def redraw(ih: Handle, children: Int): Unit = lib.IupRedraw(ih, children)
    def refresh(): Unit         = lib.IupRefresh(ptr)
    def refreshChildren(): Unit = lib.IupRefreshChildren(ptr)
    def setLanguagePack(): Unit = lib.IupSetLanguagePack(ptr)

    def destroy(): Unit = lib.IupDestroy(ptr)

    def detach(): Unit = lib.IupDetach(ptr)
    //  def append(ih: Handle, child: Handle): Handle = lib.IupAppend(ih, child)
    //  def insert(ih: Handle, ref_child: Handle, child: Handle): Handle = lib.IupInsert(ih, ref_child, child)
    //  def getChild(ih: Handle, pos: Int): Handle = lib.IupGetChild(ih, pos)
    //  def getChildPos(ih: Handle, child: Handle): Int = lib.IupGetChildPos(ih, child)
    def getChildCount: Int = lib.IupGetChildCount(ptr)
    //  def getNextChild(ih: Handle, child: Handle): Handle = lib.IupGetNextChild(ih, child)
    def getBrother: Handle                             = lib.IupGetBrother(ptr)
    def getParent: Handle                              = lib.IupGetParent(ptr)
    def getDialog: Handle                              = lib.IupGetDialog(ptr)
    def getDialogChild(name: /*const*/ String): Handle = Zone(z => lib.IupGetDialogChild(ptr, toCString(name.toUpperCase)(z)))
    //  def reparent(ih: Handle, new_parent: Handle, ref_child: Handle): Int = lib.IupReparent(ih, new_parent, ref_child)

    def popup(x: Position, y: Position): Int = lib.IupPopup(ptr, x.pos, y.pos)
    def show: Int                            = lib.IupShow(ptr)

    def showXY(x: Position, y: Position): Result = lib.IupShowXY(ptr, x.pos, y.pos)

    def hide: Int     = lib.IupHide(ptr)
    def map: Int      = lib.IupMap(ptr)
    def unmap(): Unit = lib.IupUnmap(ptr)
    //  def resetAttribute(ih: Handle, name: /*const*/ String): Unit = lib.IupResetAttribute(ih, name)
    //  def getAllAttributes(ih: Handle, names: Ptr[String], n: Int): Int = lib.IupGetAllAttributes(ih, names, n)
    //  def copyAttributes(src_ih: Handle, dst_ih: Handle): Unit = lib.IupCopyAttributes(src_ih, dst_ih)
    //  // def setAtt(handle_name: /*const*/ String, ih: Handle, name: /*const*/ String): Handle = lib.IupSetAtt(handle_name, ih, name)
    def setAttributes(str: /*const*/ String): Handle = Zone(z => lib.IupSetAttributes(ptr, toCString(str)(z)))
    def getAttributes: String                        = fromCString(lib.IupGetAttributes(ptr))
    //  def setAttribute(ih: Handle, name: /*const*/ String, value: /*const*/ String): Unit = lib.IupSetAttribute(ih, name, value)
    def setStrAttribute(name: /*const*/ String, value: /*const*/ String): Unit =
      Zone(z => lib.IupSetStrAttribute(ptr, atom(name), toCString(value)(z)))
    //  // def setStrf(ih: Handle, name: /*const*/ String, format: /*const*/ String): Unit = lib.IupSetStrf(ih, name, format)
    def setInt(name: /*const*/ String, value: Int): Unit = lib.IupSetInt(ptr, atom(name), value)
    //  def setDouble(ih: Handle, name: /*const*/ String, value: Double): Unit = lib.IupSetDouble(ih, name, value)
    //  def setRGB(ih: Handle, name: /*const*/ String, r: Char, g: Char, b: Char): Unit = lib.IupSetRGB(ih, name, r, g, b)
    //  def setRGBA(ih: Handle, name: /*const*/ String, r: Char, g: Char, b: Char, a: Char): Unit = lib.IupSetRGBA(ih, name, r, g, b, a)
    def getAttribute(name: /*const*/ String): String = fromCString(lib.IupGetAttribute(ptr, atom(name)))
    def getInt(name: /*const*/ String): Int          = lib.IupGetInt(ptr, atom(name))
    //  def getInt2(ih: Handle, name: /*const*/ String): Int = lib.IupGetInt2(ih, name)
    //  def getIntInt(ih: Handle, name: /*const*/ String, i1: Ptr[Int], i2: Ptr[Int]): Int = lib.IupGetIntInt(ih, name, i1, i2)
    //  def getDouble(ih: Handle, name: /*const*/ String): Double = lib.IupGetDouble(ih, name)
    //  def getRGB(ih: Handle, name: /*const*/ String, r: Ptr[Char], g: Ptr[Char], b: Ptr[Char]): Unit = lib.IupGetRGB(ih, name, r, g, b)
    //  def getRGBA(ih: Handle, name: /*const*/ String, r: Ptr[Char], g: Ptr[Char], b: Ptr[Char], a: Ptr[Char]): Unit = lib.IupGetRGBA(ih, name, r, g, b, a)
    //  def setAttributeId(ih: Handle, name: /*const*/ String, id: Int, value: /*const*/ String): Unit = lib.IupSetAttributeId(ih, name, id, value)
    //  def setStrAttributeId(ih: Handle, name: /*const*/ String, id: Int, value: /*const*/ String): Unit = lib.IupSetStrAttributeId(ih, name, id, value)
    //  // def setStrfId(ih: Handle, name: /*const*/ String, id: Int, format: /*const*/ String): Unit = lib.IupSetStrfId(ih, name, id, format)
    //  def setIntId(ih: Handle, name: /*const*/ String, id: Int, value: Int): Unit = lib.IupSetIntId(ih, name, id, value)
    //  def setDoubleId(ih: Handle, name: /*const*/ String, id: Int, value: Double): Unit = lib.IupSetDoubleId(ih, name, id, value)
    //  def setRGBId(ih: Handle, name: /*const*/ String, id: Int, r: Char, g: Char, b: Char): Unit = lib.IupSetRGBId(ih, name, id, r, g, b)
    //  def getAttributeId(ih: Handle, name: /*const*/ String, id: Int): String = lib.IupGetAttributeId(ih, name, id)
    //  def getIntId(ih: Handle, name: /*const*/ String, id: Int): Int = lib.IupGetIntId(ih, name, id)
    //  def getDoubleId(ih: Handle, name: /*const*/ String, id: Int): Double = lib.IupGetDoubleId(ih, name, id)
    //  def getRGBId(ih: Handle, name: /*const*/ String, id: Int, r: Ptr[Char], g: Ptr[Char], b: Ptr[Char]): Unit = lib.IupGetRGBId(ih, name, id, r, g, b)
    //  def setAttributeId2(ih: Handle, name: /*const*/ String, lin: Int, col: Int, value: /*const*/ String): Unit = lib.IupSetAttributeId2(ih, name, lin, col, value)
    //  def setStrAttributeId2(ih: Handle, name: /*const*/ String, lin: Int, col: Int, value: /*const*/ String): Unit = lib.IupSetStrAttributeId2(ih, name, lin, col, value)
    //  // def setStrfId2(ih: Handle, name: /*const*/ String, lin: Int, col: Int, format: /*const*/ String): Unit = lib.IupSetStrfId2(ih, name, lin, col, format)
    //  def setIntId2(ih: Handle, name: /*const*/ String, lin: Int, col: Int, value: Int): Unit = lib.IupSetIntId2(ih, name, lin, col, value)
    //  def setDoubleId2(ih: Handle, name: /*const*/ String, lin: Int, col: Int, value: Double): Unit = lib.IupSetDoubleId2(ih, name, lin, col, value)
    //  def setRGBId2(ih: Handle, name: /*const*/ String, lin: Int, col: Int, r: Char, g: Char, b: Char): Unit = lib.IupSetRGBId2(ih, name, lin, col, r, g, b)
    //  def getAttributeId2(ih: Handle, name: /*const*/ String, lin: Int, col: Int): String = lib.IupGetAttributeId2(ih, name, lin, col)
    //  def getIntId2(ih: Handle, name: /*const*/ String, lin: Int, col: Int): Int = lib.IupGetIntId2(ih, name, lin, col)
    //  def getDoubleId2(ih: Handle, name: /*const*/ String, lin: Int, col: Int): Double = lib.IupGetDoubleId2(ih, name, lin, col)
    //  def getRGBId2(ih: Handle, name: /*const*/ String, lin: Int, col: Int, r: Ptr[Char], g: Ptr[Char], b: Ptr[Char]): Unit = lib.IupGetRGBId2(ih, name, lin, col, r, g, b)
    def setFocus: Handle = lib.IupSetFocus(ptr)
    //  def getFocus: Handle = lib.IupGetFocus()
    def previousField: Handle = lib.IupPreviousField(ptr)
    def nextField: Handle     = lib.IupNextField(ptr)
    //  def getCallback(ih: Handle, name: /*const*/ String): Icallback = lib.IupGetCallback(ih, name)
    //  def setCallback(ih: Handle, name: /*const*/ String, func: Icallback): Icallback = lib.IupSetCallback(ih, name, func)
    //  // def setCallbacks(ih: Handle, name: /*const*/ String, func: Icallback): Handle = lib.IupSetCallbacks(ih, name, func)
    def getName: String = fromCString(lib.IupGetName(ptr))
    //  def setAttributeHandle(ih: Handle, name: /*const*/ String, ih_named: Handle): Unit = lib.IupSetAttributeHandle(ih, name, ih_named)
    //  def getAttributeHandle(ih: Handle, name: /*const*/ String): Handle = lib.IupGetAttributeHandle(ih, name)
    //  def setAttributeHandleId(ih: Handle, name: /*const*/ String, id: Int, ih_named: Handle): Unit = lib.IupSetAttributeHandleId(ih, name, id, ih_named)
    //  def getAttributeHandleId(ih: Handle, name: /*const*/ String, id: Int): Handle = lib.IupGetAttributeHandleId(ih, name, id)
    //  def setAttributeHandleId2(ih: Handle, name: /*const*/ String, lin: Int, col: Int, ih_named: Handle): Unit = lib.IupSetAttributeHandleId2(ih, name, lin, col, ih_named)
    //  def getAttributeHandleId2(ih: Handle, name: /*const*/ String, lin: Int, col: Int): Handle = lib.IupGetAttributeHandleId2(ih, name, lin, col)
    def getClassName: String        = fromCString(lib.IupGetClassName(ptr))
    def getClassType: String        = fromCString(lib.IupGetClassType(ptr))
    def saveClassAttributes(): Unit = lib.IupSaveClassAttributes(ptr)
    //  def copyClassAttributes(src_ih: Handle, dst_ih: Handle): Unit = lib.IupCopyClassAttributes(src_ih, dst_ih)

    /************************************************************************/
    /*                      Utilities                                       */
    /************************************************************************/
    //  def saveImageAsText(ih: Handle, filename: /*const*/ String, format: /*const*/ String, name: /*const*/ String): Int = lib.IupSaveImageAsText(ih, filename, format, name)
    def textConvertLinColToPos(lin: Int, col: Int): Int = {
      val pos = stackalloc[CInt]

      lib.IupTextConvertLinColToPos(ptr, lin, col, pos)
      !pos
    }

    def textConvertPosToLinCol(pos: Int): (Int, Int) = {
      val lin = stackalloc[CInt]
      val col = stackalloc[CInt]

      lib.IupTextConvertPosToLinCol(ptr, pos, lin, col)
      (!lin, !col)
    }

    def convertXYToPos(x: Int, y: Int): Int = lib.IupConvertXYToPos(ptr, x, y)
    //  def treeSetUserId(ih: Handle, id: Int, userid: Ptr[Unit]): Int = lib.IupTreeSetUserId(ih, id, userid)
    //  def treeGetUserId(ih: Handle, id: Int): Ptr[Unit] = lib.IupTreeGetUserId(ih, id)
    //  def treeGetId(ih: Handle, userid: Ptr[Unit]): Int = lib.IupTreeGetId(ih, userid)
    //  def treeSetAttributeHandle(ih: Handle, name: /*const*/ String, id: Int, ih_named: Handle): Unit = lib.IupTreeSetAttributeHandle(ih, name, id, ih_named)

    /************************************************************************/
    /*                      Pre-defined dialogs                             */
    /************************************************************************/

    //  def messageError(parent: Handle, message: /*const*/ String): Unit = lib.IupMessageError(parent, message)
    //  def messageAlarm(parent: Handle, title: /*const*/ String, message: /*const*/ String, buttons: /*const*/ String): Int = lib.IupMessageAlarm(parent, title, message, buttons)
    //  def IupLayoutDialog(dialog: Handle): Handle = extern //346
    //  def IupElementPropertiesDialog(parent: Handle, elem: Handle): Handle = extern //347
    //  def layoutDialog(dialog: Handle): Handle = lib.IupLayoutDialog(dialog)
    //  def elementPropertiesDialog(parent: Handle, elem: Handle): Handle = lib.IupElementPropertiesDialog(parent, elem)
    //  def classInfoDialog(parent: Handle): Handle = lib.IupClassInfoDialog(parent)

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
//  def recordInput(filename: /*const*/ String, mode: Int): Int = lib.IupRecordInput(filename, mode)
//  def playInput(filename: /*const*/ String): Int = lib.IupPlayInput(filename)
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
//  def setGlobal(name: /*const*/ String, value: /*const*/ String): Unit = lib.IupSetGlobal(name, value)
//  def setStrGlobal(name: /*const*/ String, value: /*const*/ String): Unit = lib.IupSetStrGlobal(name, value)
//  def getGlobal(name: /*const*/ String): String = lib.IupGetGlobal(name)
//  def getFunction(name: /*const*/ String): Icallback = lib.IupGetFunction(name)
//  def setFunction(name: /*const*/ String, func: Icallback): Icallback = lib.IupSetFunction(name, func)
//  def getHandle(name: /*const*/ String): Handle = lib.IupGetHandle(name)
//  def setHandle(name: /*const*/ String, ih: Handle): Handle = lib.IupSetHandle(name, ih)
//  def getAllNames(names: Ptr[String], n: Int): Int = lib.IupGetAllNames(names, n)
//  def getAllDialogs(names: Ptr[String], n: Int): Int = lib.IupGetAllDialogs(names, n)
//  def getAllClasses(names: Ptr[String], n: Int): Int = lib.IupGetAllClasses(names, n)
//  def getClassAttributes(classname: /*const*/ String, names: Ptr[String], n: Int): Int = lib.IupGetClassAttributes(classname, names, n)
//  def getClassCallbacks(classname: /*const*/ String, names: Ptr[String], n: Int): Int = lib.IupGetClassCallbacks(classname, names, n)
//  def setClassDefaultAttribute(classname: /*const*/ String, name: /*const*/ String, value: /*const*/ String): Unit = lib.IupSetClassDefaultAttribute(classname, name, value)
//  def classMatch(ih: Handle, classname: /*const*/ String): Int = lib.IupClassMatch(ih, classname)
//  def create(classname: /*const*/ String): Handle = lib.IupCreate(classname)
//  def createv(classname: /*const*/ String, params: Ptr[Ptr[Unit]]): Handle = lib.IupCreatev(classname, params)
//  // def createp(classname: /*const*/ String, first: Ptr[Unit]): Handle = lib.IupCreatep(classname, first)

  /************************************************************************/
  /*                        Elements                                      */
  /************************************************************************/
  def fill: Handle                    = lib.IupFill
  def space: Handle                   = lib.IupSpace
  def radio(child: Handle): Handle    = lib.IupRadio(child.ptr)
  def vbox(children: Handle*): Handle = lib.IupVboxv(copyChild(children))
//  // def zbox(child: Handle): Handle = lib.IupZbox(child)
  def zboxv(children: Handle*): Handle = lib.IupZboxv(copyChild(children))
//  // def hbox(child: Handle): Handle = lib.IupHbox(child)
  def hbox(children: Handle*): Handle = lib.IupHboxv(copyChild(children))
//  // def normalizer(ih_first: Handle): Handle = lib.IupNormalizer(ih_first)
//  def normalizerv(ih_list: Ptr[Handle]): Handle = lib.IupNormalizerv(ih_list)
//  // def cbox(child: Handle): Handle = lib.IupCbox(child)
  def cbox(children: Handle*): Handle               = lib.IupCboxv(copyChild(children))
  def sbox(child: Handle): Handle                   = lib.IupSbox(child.ptr)
  def split(child1: Handle, child2: Handle): Handle = lib.IupSplit(child1.ptr, child2.ptr)
  def scrollBox(child: Handle): Handle              = lib.IupScrollBox(child.ptr)
  def flatScrollBox(child: Handle): Handle          = lib.IupFlatScrollBox(child.ptr)
//  // def gridBox(child: Handle): Handle = lib.IupGridBox(child)
  def gridBox(children: Handle*): Handle = lib.IupGridBoxv(copyChild(children))
//  // def multiBox(child: Handle): Handle = lib.IupMultiBox(child)
  def multiBoxv(children: Handle*): Handle = lib.IupMultiBoxv(copyChild(children))
//  def expander(child: Handle): Handle = lib.IupExpander(child)
//  def detachBox(child: Handle): Handle = lib.IupDetachBox(child)
//  def backgroundBox(child: Handle): Handle = lib.IupBackgroundBox(child)
//  def frame(child: Handle): Handle = lib.IupFrame(child)
//  def flatFrame(child: Handle): Handle = lib.IupFlatFrame(child)
//  def image(width: Int, height: Int, pixels: Ptr[/*const*/ Char]): Handle = lib.IupImage(width, height, pixels)
//  def imageRGB(width: Int, height: Int, pixels: Ptr[/*const*/ Char]): Handle = lib.IupImageRGB(width, height, pixels)
//  def imageRGBA(width: Int, height: Int, pixels: Ptr[/*const*/ Char]): Handle = lib.IupImageRGBA(width, height, pixels)
  def item(title: /*const*/ String, action: /*const*/ String): Handle = lib.IupItem(atom(title), atom(action))
  def submenu(title: /*const*/ String, child: Handle): Handle         = lib.IupSubmenu(atom(title), child.ptr)
  def separator: Handle                                               = lib.IupSeparator
//  // def menu(child: Handle): Handle = lib.IupMenu(child)
  def menu(children: Handle*): Handle               = lib.IupMenuv(copyChild(children))
  def button(title: String, action: String): Handle = lib.IupButton(atom(title), atom(action))
//  def flatButton(title: /*const*/ String): Handle = lib.IupFlatButton(title)
//  def flatToggle(title: /*const*/ String): Handle = lib.IupFlatToggle(title)
//  def dropButton(dropchild: Handle): Handle = lib.IupDropButton(dropchild)
//  def flatLabel(title: /*const*/ String): Handle = lib.IupFlatLabel(title)
//  def flatSeparator: Handle = lib.IupFlatSeparator()
//  def canvas(action: /*const*/ String): Handle = lib.IupCanvas(action)
  def dialog(child: Handle): Handle = lib.IupDialog(child.ptr)
//  def user: Handle = lib.IupUser()
//  def thread: Handle = lib.IupThread()
  def label(title: String): Handle = lib.IupLabel(atom(title))
//  def list(action: /*const*/ String): Handle = lib.IupList(action)
//  def flatList: Handle = lib.IupFlatList()
  def text(action: /*const*/ String): Handle = lib.IupText(atom(action)) // todo: action?
//  def multiLine(action: /*const*/ String): Handle = lib.IupMultiLine(action)
//  def toggle(title: /*const*/ String, action: /*const*/ String): Handle = lib.IupToggle(title, action)
//  def timer: Handle = lib.IupTimer()
//  def clipboard: Handle = lib.IupClipboard()
//  def progressBar: Handle = lib.IupProgressBar()
//  def val(type: /*const*/ String): Handle = lib.IupVal(type)
//  def flatVal(type: /*const*/ String): Handle = lib.IupFlatVal(type)
//  def flatTree: Handle = lib.IupFlatTree()
//  // def tabs(child: Handle): Handle = lib.IupTabs(child)
//  def tabsv(children: Ptr[Handle]): Handle = lib.IupTabsv(children)
//  // def flatTabs(first: Handle): Handle = lib.IupFlatTabs(first)
//  def flatTabsv(children: Ptr[Handle]): Handle = lib.IupFlatTabsv(children)
//  def tree: Handle = lib.IupTree()
//  def link(url: /*const*/ String, title: /*const*/ String): Handle = lib.IupLink(url, title)
//  def animatedLabel(animation: Handle): Handle = lib.IupAnimatedLabel(animation)
//  def datePick: Handle = lib.IupDatePick()
//  def calendar: Handle = lib.IupCalendar()
//  def colorbar: Handle = lib.IupColorbar()
//  def gauge: Handle = lib.IupGauge()
//  def dial(type: /*const*/ String): Handle = lib.IupDial(type)
//  def colorBrowser: Handle = lib.IupColorBrowser()
//  def spin: Handle = lib.IupSpin()
//  def spinbox(child: Handle): Handle = lib.IupSpinbox(child)

  /************************************************************************/
  /*                      Utilities                                       */
  /************************************************************************/
//  def stringCompare(str1: /*const*/ String, str2: /*const*/ String, casesensitive: Int, lexicographic: Int): Int = lib.IupStringCompare(str1, str2, casesensitive, lexicographic)
//  def imageGetHandle(name: /*const*/ String): Handle = lib.IupImageGetHandle(name)

  /************************************************************************/
  /*                      Pre-defined dialogs                             */
  /************************************************************************/
  def fileDlg: Handle = lib.IupFileDlg
//  def messageDlg: Handle = lib.IupMessageDlg()
//  def colorDlg: Handle = lib.IupColorDlg()
  def fontDlg: Handle = lib.IupFontDlg
//  def progressDlg: Handle = lib.IupProgressDlg()
//  def getFile(arq: String): Int = lib.IupGetFile(arq)
  def message(title: String, msg: String): Unit = lib.IupMessage(atom(title), atom(msg))
//  // def messagef(title: /*const*/ String, format: /*const*/ String): Unit = lib.IupMessagef(title, format)
//  def alarm(title: /*const*/ String, msg: /*const*/ String, b1: /*const*/ String, b2: /*const*/ String, b3: /*const*/ String): Int = lib.IupAlarm(title, msg, b1, b2, b3)
//  // def scanf(format: /*const*/ String): Int = lib.IupScanf(format)
//  def listDialog(type: Int, title: /*const*/ String, size: Int, list: Ptr[/*const*/ String], op: Int, max_col: Int, max_lin: Int, marks: Ptr[Int]): Int = lib.IupListDialog(type, title, size, list, op, max_col, max_lin, marks)
//  def getText(title: /*const*/ String, text: String, maxsize: Int): Int = lib.IupGetText(title, text, maxsize)
//  def getColor(x: Int, y: Int, r: Ptr[Char], g: Ptr[Char], b: Ptr[Char]): Int = lib.IupGetColor(x, y, r, g, b)
// def IupGetParam(title: /*const*/ CString, action: Iparamcb, user_data: Ptr[Unit], format: /*const*/ CString): CInt = extern //340
//def IupGetParamv(title: /*const*/ CString, action: Iparamcb, user_data: Ptr[Unit], format: /*const*/ CString, param_count: CInt, param_extra: CInt, param_data: Ptr[Ptr[Unit]]): CInt = extern //341
//  def IupParam(format: /*const*/ CString): Handle = extern //342
//  // def IupParamBox(param: Handle): Handle = extern //343
//  def IupParamBoxv(param_array: Ptr[Handle]): Handle = extern //344
//  def IupGlobalsDialog(): Handle = extern //348
//  def IupClassInfoDialog(parent: Handle): Handle = extern //349
//  // def getParam(title: /*const*/ String, action: Iparamcb, user_data: Ptr[Unit], format: /*const*/ String): Int = lib.IupGetParam(title, action, user_data, format)
//  def getParamv(title: /*const*/ String, action: Iparamcb, user_data: Ptr[Unit], format: /*const*/ String, param_count: Int, param_extra: Int, param_data: Ptr[Ptr[Unit]]): Int = lib.IupGetParamv(title, action, user_data, format, param_count, param_extra, param_data)
//  def param(format: /*const*/ String): Handle = lib.IupParam(format)
//  // def paramBox(param: Handle): Handle = lib.IupParamBox(param)
//  def paramBoxv(param_array: Ptr[Handle]): Handle = lib.IupParamBoxv(param_array)
//  def globalsDialog(): Handle = lib.IupGlobalsDialog()

}
