package io.github.edadma.iup.extern

import scala.scalanative.unsafe._

@link("iup")
@extern
object LibIUP {

  type Ihandle    = CStruct0
  type IhandlePtr = Ptr[Ihandle]
  type Icallback  = CFuncPtr1[IhandlePtr, CInt]

  /************************************************************************/
  /*                        Main API                                      */
  /************************************************************************/
  def IupOpen(argc: Ptr[CInt], argv: Ptr[Ptr[CString]]): CInt                                         = extern //37
  def IupClose(): Unit                                                                                = extern //38
  def IupIsOpened: CInt                                                                               = extern //39
  def IupImageLibOpen(): Unit                                                                         = extern //41
  def IupMainLoop: CInt                                                                               = extern //43
  def IupLoopStep: CInt                                                                               = extern //44
  def IupLoopStepWait: CInt                                                                           = extern //45
  def IupMainLoopLevel: CInt                                                                          = extern //46
  def IupFlush(): Unit                                                                                = extern //47
  def IupExitLoop(): Unit                                                                             = extern //48
  def IupPostMessage(ih: Ptr[Ihandle], s: /*const*/ CString, i: CInt, d: CDouble, p: Ptr[Unit]): Unit = extern //49
  def IupRecordInput(filename: /*const*/ CString, mode: CInt): CInt                                   = extern //51
  def IupPlayInput(filename: /*const*/ CString): CInt                                                 = extern //52
  def IupUpdate(ih: Ptr[Ihandle]): Unit                                                               = extern //54
  def IupUpdateChildren(ih: Ptr[Ihandle]): Unit                                                       = extern //55
  def IupRedraw(ih: Ptr[Ihandle], children: CInt): Unit                                               = extern //56
  def IupRefresh(ih: Ptr[Ihandle]): Unit                                                              = extern //57
  def IupRefreshChildren(ih: Ptr[Ihandle]): Unit                                                      = extern //58
  def IupExecute(filename: /*const*/ CString, parameters: /*const*/ CString): CInt                    = extern //60
  def IupExecuteWait(filename: /*const*/ CString, parameters: /*const*/ CString): CInt                = extern //61
  def IupHelp(url: /*const*/ CString): CInt                                                           = extern //62
  // def IupLog(type: /*const*/ CString, format: /*const*/ CString): Unit = extern //63
  def IupLoad(filename: /*const*/ CString): CString                                           = extern //65
  def IupLoadBuffer(buffer: /*const*/ CString): CString                                       = extern //66
  def IupVersion: CString                                                                     = extern //68
  def IupVersionDate: CString                                                                 = extern //69
  def IupVersionNumber: CInt                                                                  = extern //70
  def IupVersionShow(): Unit                                                                  = extern //71
  def IupSetLanguage(lng: /*const*/ CString): Unit                                            = extern //73
  def IupGetLanguage: CString                                                                 = extern //74
  def IupSetLanguageString(name: /*const*/ CString, str: /*const*/ CString): Unit             = extern //75
  def IupStoreLanguageString(name: /*const*/ CString, str: /*const*/ CString): Unit           = extern //76
  def IupGetLanguageString(name: /*const*/ CString): CString                                  = extern //77
  def IupSetLanguagePack(ih: Ptr[Ihandle]): Unit                                              = extern //78
  def IupDestroy(ih: Ptr[Ihandle]): Unit                                                      = extern //80
  def IupDetach(child: Ptr[Ihandle]): Unit                                                    = extern //81
  def IupAppend(ih: Ptr[Ihandle], child: Ptr[Ihandle]): Ptr[Ihandle]                          = extern //82
  def IupInsert(ih: Ptr[Ihandle], ref_child: Ptr[Ihandle], child: Ptr[Ihandle]): Ptr[Ihandle] = extern //83
  def IupGetChild(ih: Ptr[Ihandle], pos: CInt): Ptr[Ihandle]                                  = extern //84
  def IupGetChildPos(ih: Ptr[Ihandle], child: Ptr[Ihandle]): CInt                             = extern //85
  def IupGetChildCount(ih: Ptr[Ihandle]): CInt                                                = extern //86
  def IupGetNextChild(ih: Ptr[Ihandle], child: Ptr[Ihandle]): Ptr[Ihandle]                    = extern //87
  def IupGetBrother(ih: Ptr[Ihandle]): Ptr[Ihandle]                                           = extern //88
  def IupGetParent(ih: Ptr[Ihandle]): Ptr[Ihandle]                                            = extern //89
  def IupGetDialog(ih: Ptr[Ihandle]): Ptr[Ihandle]                                            = extern //90
  def IupGetDialogChild(ih: Ptr[Ihandle], name: /*const*/ CString): Ptr[Ihandle]              = extern //91
  def IupReparent(ih: Ptr[Ihandle], new_parent: Ptr[Ihandle], ref_child: Ptr[Ihandle]): CInt  = extern //92
  def IupPopup(ih: Ptr[Ihandle], x: CInt, y: CInt): CInt                                      = extern //94
  def IupShow(ih: Ptr[Ihandle]): CInt                                                         = extern //95
  def IupShowXY(ih: Ptr[Ihandle], x: CInt, y: CInt): CInt                                     = extern //96
  def IupHide(ih: Ptr[Ihandle]): CInt                                                         = extern //97
  def IupMap(ih: Ptr[Ihandle]): CInt                                                          = extern //98
  def IupUnmap(ih: Ptr[Ihandle]): Unit                                                        = extern //99
  def IupResetAttribute(ih: Ptr[Ihandle], name: /*const*/ CString): Unit                      = extern //101
  def IupGetAllAttributes(ih: Ptr[Ihandle], names: Ptr[CString], n: CInt): CInt               = extern //102
  def IupCopyAttributes(src_ih: Ptr[Ihandle], dst_ih: Ptr[Ihandle]): Unit                     = extern //103
  // def IupSetAtt(handle_name: /*const*/ CString, ih: Ptr[Ihandle], name: /*const*/ CString): Ptr[Ihandle] = extern //104
  def IupSetAttributes(ih: Ptr[Ihandle], str: /*const*/ CString): Ptr[Ihandle]                      = extern //105
  def IupGetAttributes(ih: Ptr[Ihandle]): CString                                                   = extern //106
  def IupSetAttribute(ih: Ptr[Ihandle], name: /*const*/ CString, value: /*const*/ CString): Unit    = extern //108
  def IupSetStrAttribute(ih: Ptr[Ihandle], name: /*const*/ CString, value: /*const*/ CString): Unit = extern //109
  // def IupSetStrf(ih: Ptr[Ihandle], name: /*const*/ CString, format: /*const*/ CString): Unit = extern //110
  def IupSetInt(ih: Ptr[Ihandle], name: /*const*/ CString, value: CInt): Unit       = extern //111
  def IupSetFloat(ih: Ptr[Ihandle], name: /*const*/ CString, value: CFloat): Unit   = extern //112
  def IupSetDouble(ih: Ptr[Ihandle], name: /*const*/ CString, value: CDouble): Unit = extern //113
  def IupSetRGB(ih: Ptr[Ihandle], name: /*const*/ CString, r: CUnsignedChar, g: CUnsignedChar, b: CUnsignedChar): Unit =
    extern //114
  def IupSetRGBA(ih: Ptr[Ihandle],
                 name: /*const*/ CString,
                 r: CUnsignedChar,
                 g: CUnsignedChar,
                 b: CUnsignedChar,
                 a: CUnsignedChar): Unit                                                          = extern //115
  def IupGetAttribute(ih: Ptr[Ihandle], name: /*const*/ CString): CString                         = extern //117
  def IupGetInt(ih: Ptr[Ihandle], name: /*const*/ CString): CInt                                  = extern //118
  def IupGetInt2(ih: Ptr[Ihandle], name: /*const*/ CString): CInt                                 = extern //119
  def IupGetIntInt(ih: Ptr[Ihandle], name: /*const*/ CString, i1: Ptr[CInt], i2: Ptr[CInt]): CInt = extern //120
  def IupGetFloat(ih: Ptr[Ihandle], name: /*const*/ CString): CFloat                              = extern //121
  def IupGetDouble(ih: Ptr[Ihandle], name: /*const*/ CString): CDouble                            = extern //122
  def IupGetRGB(ih: Ptr[Ihandle],
                name: /*const*/ CString,
                r: Ptr[CUnsignedChar],
                g: Ptr[CUnsignedChar],
                b: Ptr[CUnsignedChar]): Unit = extern //123
  def IupGetRGBA(ih: Ptr[Ihandle],
                 name: /*const*/ CString,
                 r: Ptr[CUnsignedChar],
                 g: Ptr[CUnsignedChar],
                 b: Ptr[CUnsignedChar],
                 a: Ptr[CUnsignedChar]): Unit                                                                   = extern //124
  def IupSetAttributeId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt, value: /*const*/ CString): Unit    = extern //126
  def IupSetStrAttributeId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt, value: /*const*/ CString): Unit = extern //127
  // def IupSetStrfId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt, format: /*const*/ CString): Unit = extern //128
  def IupSetIntId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt, value: CInt): Unit       = extern //129
  def IupSetFloatId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt, value: CFloat): Unit   = extern //130
  def IupSetDoubleId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt, value: CDouble): Unit = extern //131
  def IupSetRGBId(ih: Ptr[Ihandle],
                  name: /*const*/ CString,
                  id: CInt,
                  r: CUnsignedChar,
                  g: CUnsignedChar,
                  b: CUnsignedChar): Unit                                             = extern //132
  def IupGetAttributeId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt): CString = extern //134
  def IupGetIntId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt): CInt          = extern //135
  def IupGetFloatId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt): CFloat      = extern //136
  def IupGetDoubleId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt): CDouble    = extern //137
  def IupGetRGBId(ih: Ptr[Ihandle],
                  name: /*const*/ CString,
                  id: CInt,
                  r: Ptr[CUnsignedChar],
                  g: Ptr[CUnsignedChar],
                  b: Ptr[CUnsignedChar]): Unit = extern //138
  def IupSetAttributeId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt, value: /*const*/ CString): Unit =
    extern //140
  def IupSetStrAttributeId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt, value: /*const*/ CString): Unit =
    extern //141
  // def IupSetStrfId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt, format: /*const*/ CString): Unit = extern //142
  def IupSetIntId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt, value: CInt): Unit       = extern //143
  def IupSetFloatId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt, value: CFloat): Unit   = extern //144
  def IupSetDoubleId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt, value: CDouble): Unit = extern //145
  def IupSetRGBId2(ih: Ptr[Ihandle],
                   name: /*const*/ CString,
                   lin: CInt,
                   col: CInt,
                   r: CUnsignedChar,
                   g: CUnsignedChar,
                   b: CUnsignedChar): Unit                                                         = extern //146
  def IupGetAttributeId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt): CString = extern //148
  def IupGetIntId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt): CInt          = extern //149
  def IupGetFloatId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt): CFloat      = extern //150
  def IupGetDoubleId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt): CDouble    = extern //151
  def IupGetRGBId2(ih: Ptr[Ihandle],
                   name: /*const*/ CString,
                   lin: CInt,
                   col: CInt,
                   r: Ptr[CUnsignedChar],
                   g: Ptr[CUnsignedChar],
                   b: Ptr[CUnsignedChar]): Unit                                             = extern //152
  def IupSetGlobal(name: /*const*/ CString, value: /*const*/ CString): Unit                 = extern //154
  def IupSetStrGlobal(name: /*const*/ CString, value: /*const*/ CString): Unit              = extern //155
  def IupGetGlobal(name: /*const*/ CString): CString                                        = extern //156
  def IupSetFocus(ih: Ptr[Ihandle]): Ptr[Ihandle]                                           = extern //158
  def IupGetFocus: Ptr[Ihandle]                                                             = extern //159
  def IupPreviousField(ih: Ptr[Ihandle]): Ptr[Ihandle]                                      = extern //160
  def IupNextField(ih: Ptr[Ihandle]): Ptr[Ihandle]                                          = extern //161
  def IupGetCallback(ih: Ptr[Ihandle], name: /*const*/ CString): Icallback                  = extern //163
  def IupSetCallback(ih: Ptr[Ihandle], name: /*const*/ CString, func: Icallback): Icallback = extern //164
  // def IupSetCallbacks(ih: Ptr[Ihandle], name: /*const*/ CString, func: Icallback): Ptr[Ihandle] = extern //165
  def IupGetFunction(name: /*const*/ CString): Icallback                                                         = extern //167
  def IupSetFunction(name: /*const*/ CString, func: Icallback): Icallback                                        = extern //168
  def IupGetHandle(name: /*const*/ CString): Ptr[Ihandle]                                                        = extern //170
  def IupSetHandle(name: /*const*/ CString, ih: Ptr[Ihandle]): Ptr[Ihandle]                                      = extern //171
  def IupGetAllNames(names: Ptr[CString], n: CInt): CInt                                                         = extern //172
  def IupGetAllDialogs(names: Ptr[CString], n: CInt): CInt                                                       = extern //173
  def IupGetName(ih: Ptr[Ihandle]): CString                                                                      = extern //174
  def IupSetAttributeHandle(ih: Ptr[Ihandle], name: /*const*/ CString, ih_named: Ptr[Ihandle]): Unit             = extern //176
  def IupGetAttributeHandle(ih: Ptr[Ihandle], name: /*const*/ CString): Ptr[Ihandle]                             = extern //177
  def IupSetAttributeHandleId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt, ih_named: Ptr[Ihandle]): Unit = extern //178
  def IupGetAttributeHandleId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt): Ptr[Ihandle]                 = extern //179
  def IupSetAttributeHandleId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt, ih_named: Ptr[Ihandle]): Unit =
    extern //180
  def IupGetAttributeHandleId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt): Ptr[Ihandle] = extern //181
  def IupGetClassName(ih: Ptr[Ihandle]): CString                                                              = extern //183
  def IupGetClassType(ih: Ptr[Ihandle]): CString                                                              = extern //184
  def IupGetAllClasses(names: Ptr[CString], n: CInt): CInt                                                    = extern //185
  def IupGetClassAttributes(classname: /*const*/ CString, names: Ptr[CString], n: CInt): CInt                 = extern //186
  def IupGetClassCallbacks(classname: /*const*/ CString, names: Ptr[CString], n: CInt): CInt                  = extern //187
  def IupSaveClassAttributes(ih: Ptr[Ihandle]): Unit                                                          = extern //188
  def IupCopyClassAttributes(src_ih: Ptr[Ihandle], dst_ih: Ptr[Ihandle]): Unit                                = extern //189
  def IupSetClassDefaultAttribute(classname: /*const*/ CString, name: /*const*/ CString, value: /*const*/ CString): Unit =
    extern //190
  def IupClassMatch(ih: Ptr[Ihandle], classname: /*const*/ CString): CInt            = extern //191
  def IupCreate(classname: /*const*/ CString): Ptr[Ihandle]                          = extern //193
  def IupCreatev(classname: /*const*/ CString, params: Ptr[Ptr[Unit]]): Ptr[Ihandle] = extern //194
  // def IupCreatep(classname: /*const*/ CString, first: Ptr[Unit]): Ptr[Ihandle] = extern //195

  /************************************************************************/
  /*                        Elements                                      */
  /************************************************************************/
  def IupFill: Ptr[Ihandle]                       = extern //201
  def IupSpace: Ptr[Ihandle]                      = extern //202
  def IupRadio(child: Ptr[Ihandle]): Ptr[Ihandle] = extern //204
  // def IupVbox(child: Ptr[Ihandle]): Ptr[Ihandle] = extern //205
  def IupVboxv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = extern //206
  // def IupZbox(child: Ptr[Ihandle]): Ptr[Ihandle] = extern //207
  def IupZboxv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = extern //208
  // def IupHbox(child: Ptr[Ihandle]): Ptr[Ihandle] = extern //209
  def IupHboxv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = extern //210
  // def IupNormalizer(ih_first: Ptr[Ihandle]): Ptr[Ihandle] = extern //212
  def IupNormalizerv(ih_list: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = extern //213
  // def IupCbox(child: Ptr[Ihandle]): Ptr[Ihandle] = extern //215
  def IupCboxv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle]                = extern //216
  def IupSbox(child: Ptr[Ihandle]): Ptr[Ihandle]                         = extern //217
  def IupSplit(child1: Ptr[Ihandle], child2: Ptr[Ihandle]): Ptr[Ihandle] = extern //218
  def IupScrollBox(child: Ptr[Ihandle]): Ptr[Ihandle]                    = extern //219
  def IupFlatScrollBox(child: Ptr[Ihandle]): Ptr[Ihandle]                = extern //220
  // def IupGridBox(child: Ptr[Ihandle]): Ptr[Ihandle] = extern //221
  def IupGridBoxv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = extern //222
  // def IupMultiBox(child: Ptr[Ihandle]): Ptr[Ihandle] = extern //223
  def IupMultiBoxv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle]                                      = extern //224
  def IupExpander(child: Ptr[Ihandle]): Ptr[Ihandle]                                               = extern //225
  def IupDetachBox(child: Ptr[Ihandle]): Ptr[Ihandle]                                              = extern //226
  def IupBackgroundBox(child: Ptr[Ihandle]): Ptr[Ihandle]                                          = extern //227
  def IupFrame(child: Ptr[Ihandle]): Ptr[Ihandle]                                                  = extern //229
  def IupFlatFrame(child: Ptr[Ihandle]): Ptr[Ihandle]                                              = extern //230
  def IupImage(width: CInt, height: CInt, pixels: Ptr[ /*const*/ CUnsignedChar]): Ptr[Ihandle]     = extern //232
  def IupImageRGB(width: CInt, height: CInt, pixels: Ptr[ /*const*/ CUnsignedChar]): Ptr[Ihandle]  = extern //233
  def IupImageRGBA(width: CInt, height: CInt, pixels: Ptr[ /*const*/ CUnsignedChar]): Ptr[Ihandle] = extern //234
  def IupItem(title: /*const*/ CString, action: /*const*/ CString): Ptr[Ihandle]                   = extern //236
  def IupSubmenu(title: /*const*/ CString, child: Ptr[Ihandle]): Ptr[Ihandle]                      = extern //237
  def IupSeparator: Ptr[Ihandle]                                                                   = extern //238
  // def IupMenu(child: Ptr[Ihandle]): Ptr[Ihandle] = extern //239
  def IupMenuv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle]                          = extern //240
  def IupButton(title: /*const*/ CString, action: /*const*/ CString): Ptr[Ihandle] = extern //242
  def IupFlatButton(title: /*const*/ CString): Ptr[Ihandle]                        = extern //243
  def IupFlatToggle(title: /*const*/ CString): Ptr[Ihandle]                        = extern //244
  def IupDropButton(dropchild: Ptr[Ihandle]): Ptr[Ihandle]                         = extern //245
  def IupFlatLabel(title: /*const*/ CString): Ptr[Ihandle]                         = extern //246
  def IupFlatSeparator: Ptr[Ihandle]                                               = extern //247
  def IupCanvas(action: /*const*/ CString): Ptr[Ihandle]                           = extern //248
  def IupDialog(child: Ptr[Ihandle]): Ptr[Ihandle]                                 = extern //249
  def IupUser: Ptr[Ihandle]                                                        = extern //250
  def IupThread: Ptr[Ihandle]                                                      = extern //251
  def IupLabel(title: /*const*/ CString): Ptr[Ihandle]                             = extern //252
  def IupList(action: /*const*/ CString): Ptr[Ihandle]                             = extern //253
  def IupFlatList: Ptr[Ihandle]                                                    = extern //254
  def IupText(action: /*const*/ CString): Ptr[Ihandle]                             = extern //255
  def IupMultiLine(action: /*const*/ CString): Ptr[Ihandle]                        = extern //256
  def IupToggle(title: /*const*/ CString, action: /*const*/ CString): Ptr[Ihandle] = extern //257
  def IupTimer: Ptr[Ihandle]                                                       = extern //258
  def IupClipboard: Ptr[Ihandle]                                                   = extern //259
  def IupProgressBar: Ptr[Ihandle]                                                 = extern //260
  def IupVal(typ: /*const*/ CString): Ptr[Ihandle]                                 = extern //261
  def IupFlatVal(typ: /*const*/ CString): Ptr[Ihandle]                             = extern //262
  def IupFlatTree: Ptr[Ihandle]                                                    = extern //263
  // def IupTabs(child: Ptr[Ihandle]): Ptr[Ihandle] = extern //264
  def IupTabsv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle] = extern //265
  // def IupFlatTabs(first: Ptr[Ihandle]): Ptr[Ihandle] = extern //266
  def IupFlatTabsv(children: Ptr[Ptr[Ihandle]]): Ptr[Ihandle]                 = extern //267
  def IupTree: Ptr[Ihandle]                                                   = extern //268
  def IupLink(url: /*const*/ CString, title: /*const*/ CString): Ptr[Ihandle] = extern //269
  def IupAnimatedLabel(animation: Ptr[Ihandle]): Ptr[Ihandle]                 = extern //270
  def IupDatePick: Ptr[Ihandle]                                               = extern //271
  def IupCalendar: Ptr[Ihandle]                                               = extern //272
  def IupColorbar: Ptr[Ihandle]                                               = extern //273
  def IupGauge: Ptr[Ihandle]                                                  = extern //274
  def IupDial(typ: /*const*/ CString): Ptr[Ihandle]                           = extern //275
  def IupColorBrowser: Ptr[Ihandle]                                           = extern //276
  def IupSpin: Ptr[Ihandle]                                                   = extern //279
  def IupSpinbox(child: Ptr[Ihandle]): Ptr[Ihandle]                           = extern //280

  /************************************************************************/
  /*                      Utilities                                       */
  /************************************************************************/
  def IupStringCompare(str1: /*const*/ CString, str2: /*const*/ CString, casesensitive: CInt, lexicographic: CInt): CInt =
    extern //288
  def IupSaveImageAsText(ih: Ptr[Ihandle],
                         filename: /*const*/ CString,
                         format: /*const*/ CString,
                         name: /*const*/ CString): CInt                                            = extern //291
  def IupImageGetHandle(name: /*const*/ CString): Ptr[Ihandle]                                     = extern //292
  def IupTextConvertLinColToPos(ih: Ptr[Ihandle], lin: CInt, col: CInt, pos: Ptr[CInt]): Unit      = extern //295
  def IupTextConvertPosToLinCol(ih: Ptr[Ihandle], pos: CInt, lin: Ptr[CInt], col: Ptr[CInt]): Unit = extern //296
  def IupConvertXYToPos(ih: Ptr[Ihandle], x: CInt, y: CInt): CInt                                  = extern //299
  def IupStoreGlobal(name: /*const*/ CString, value: /*const*/ CString): Unit                      = extern //302
  def IupStoreAttribute(ih: Ptr[Ihandle], name: /*const*/ CString, value: /*const*/ CString): Unit = extern //303
  // def IupSetfAttribute(ih: Ptr[Ihandle], name: /*const*/ CString, format: /*const*/ CString): Unit = extern //304
  def IupStoreAttributeId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt, value: /*const*/ CString): Unit = extern //305
  // def IupSetfAttributeId(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt, f: /*const*/ CString): Unit = extern //306
  def IupStoreAttributeId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt, value: /*const*/ CString): Unit =
    extern //307
  // def IupSetfAttributeId2(ih: Ptr[Ihandle], name: /*const*/ CString, lin: CInt, col: CInt, format: /*const*/ CString): Unit = extern //308
  def IupTreeSetUserId(ih: Ptr[Ihandle], id: CInt, userid: Ptr[Unit]): CInt                                        = extern //311
  def IupTreeGetUserId(ih: Ptr[Ihandle], id: CInt): Ptr[Unit]                                                      = extern //312
  def IupTreeGetId(ih: Ptr[Ihandle], userid: Ptr[Unit]): CInt                                                      = extern //313
  def IupTreeSetAttributeHandle(ih: Ptr[Ihandle], name: /*const*/ CString, id: CInt, ih_named: Ptr[Ihandle]): Unit = extern //314

  /************************************************************************/
  /*                      Pre-defined dialogs                             */
  /************************************************************************/
  def IupFileDlg: Ptr[Ihandle]                                           = extern //321
  def IupMessageDlg: Ptr[Ihandle]                                        = extern //322
  def IupColorDlg: Ptr[Ihandle]                                          = extern //323
  def IupFontDlg: Ptr[Ihandle]                                           = extern //324
  def IupProgressDlg: Ptr[Ihandle]                                       = extern //325
  def IupGetFile(arq: CString): CInt                                     = extern //327
  def IupMessage(title: /*const*/ CString, msg: /*const*/ CString): Unit = extern //328
  // def IupMessagef(title: /*const*/ CString, format: /*const*/ CString): Unit = extern //329
  def IupMessageError(parent: Ptr[Ihandle], message: /*const*/ CString): Unit = extern //330
  def IupMessageAlarm(parent: Ptr[Ihandle],
                      title: /*const*/ CString,
                      message: /*const*/ CString,
                      buttons: /*const*/ CString): CInt = extern //331
  def IupAlarm(title: /*const*/ CString,
               msg: /*const*/ CString,
               b1: /*const*/ CString,
               b2: /*const*/ CString,
               b3: /*const*/ CString): CInt = extern //332
  // def IupScanf(format: /*const*/ CString): CInt = extern //333
  def IupListDialog(typ: CInt,
                    title: /*const*/ CString,
                    size: CInt,
                    list: Ptr[ /*const*/ CString],
                    op: CInt,
                    max_col: CInt,
                    max_lin: CInt,
                    marks: Ptr[CInt]): CInt                                                                    = extern //334
  def IupGetText(title: /*const*/ CString, text: CString, maxsize: CInt): CInt                                 = extern //336
  def IupGetColor(x: CInt, y: CInt, r: Ptr[CUnsignedChar], g: Ptr[CUnsignedChar], b: Ptr[CUnsignedChar]): CInt = extern //337
  // def IupGetParam(title: /*const*/ CString, action: Iparamcb, user_data: Ptr[Unit], format: /*const*/ CString): CInt = extern //340
//  def IupGetParamv(title: /*const*/ CString,
//                   action: Iparamcb,
//                   user_data: Ptr[Unit],
//                   format: /*const*/ CString,
//                   param_count: CInt,
//                   param_extra: CInt,
//                   param_data: Ptr[Ptr[Unit]]): CInt    = extern //341
  def IupParam(format: /*const*/ CString): Ptr[Ihandle] = extern //342
  // def IupParamBox(param: Ptr[Ihandle]): Ptr[Ihandle] = extern //343
  def IupParamBoxv(param_array: Ptr[Ptr[Ihandle]]): Ptr[Ihandle]                         = extern //344
  def IupLayoutDialog(dialog: Ptr[Ihandle]): Ptr[Ihandle]                                = extern //346
  def IupElementPropertiesDialog(parent: Ptr[Ihandle], elem: Ptr[Ihandle]): Ptr[Ihandle] = extern //347
  def IupGlobalsDialog(): Ptr[Ihandle]                                                   = extern //348
  def IupClassInfoDialog(parent: Ptr[Ihandle]): Ptr[Ihandle]                             = extern //349

}
