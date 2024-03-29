iup
====

![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/edadma/iup?include_prereleases) ![GitHub (Pre-)Release Date](https://img.shields.io/github/release-date-pre/edadma/iup) ![GitHub last commit](https://img.shields.io/github/last-commit/edadma/iup) ![GitHub](https://img.shields.io/github/license/edadma/iup)

*iup* provides Scala Native bindings for the [IUP](https://www.tecgraf.puc-rio.br/iup/) multi-platform toolkit for building graphical user interfaces.

Overview
--------

The goal of this project is to provide easy-to-use Scala Native bindings for the IUP user interface library.  Ultimately, all functions will be covered, and all sample C applications in chapters 3 and 4 of the [Tutorial](https://www.tecgraf.puc-rio.br/iup/en/tutorial/tutorial.html) as well as in the chapter [7GUIs Implementation in IUP](https://www.tecgraf.puc-rio.br/iup/en/7gui/7gui.html) will be translated.

The more "programmer friendly" part of this library is found in the `io.github.edadma.iup` package. That's the only
package you need to import from, as seen in the example below. The other package in the library
is `io.github.edadma.iup.extern` which provides for interaction with the *Libiup* C library using Scala Native
interoperability elements from the so-call `unsafe` namespace. There are no public declarations in
the `io.github.edadma.iup` package that use `unsafe` types in their parameter or return types, making it a pure
Scala bindings library. Consequently, you never have to worry about memory allocation or type conversions.

Usage
-----

Two of the libraries (`iup` and `img`) have been built and placed in the `native-lib` folder.  To use them, include the following in your `build.sbt`:

```sbt
nativeConfig ~= { c => c.withLinkingOptions(c.linkingOptions :+ "-L${baseDirectory.value}/native-lib") }
```


Include the following in your `project/plugins.sbt`:

```sbt
addSbtPlugin("com.codecommit" % "sbt-github-packages" % "0.5.3")
```

Include the following in your `build.sbt`:

```sbt
resolvers += Resolver.githubPackages("edadma")

libraryDependencies += "io.github.edadma" %%% "iup" % "0.1.2"
```

Use the following `import` statement in your code:

```scala
import io.github.edadma.iup._

```

Examples
--------

The following examples are translated directly from chapter 2 of the [Tutorial](https://www.tecgraf.puc-rio.br/iup/en/tutorial/tutorial2.html) without any changes, except for increasing the margin in the last example.

### Example [2.1 Initialization](https://www.tecgraf.puc-rio.br/iup/en/tutorial/tutorial2.html#Initialization)

```scala
import io.github.edadma.iup
import io.github.edadma.iup.Result

object Main extends App {

  if (iup.open == Result.ERROR) {
    println("Error opening window")
    sys.exit(1)
  }

  iup.message("Hello World 1", "Hello world from IUP.")
  iup.close()

}
```

Screenshot:

![2.1](https://github.com/edadma/iup/blob/stable/readme/2-1.png)

### Example [2.2 Creating a Dialog](https://www.tecgraf.puc-rio.br/iup/en/tutorial/tutorial2.html#Dialog)

```scala
import io.github.edadma.iup
import io.github.edadma.iup.{Position, Result}

object Main extends App {

  if (iup.open == Result.ERROR) {
    println("Error opening window")
    sys.exit(1)
  }

  val label = iup.label("Hello world from IUP.")
  val dlg   = iup.dialog(iup.vbox(label))

  dlg.TITLE = "Hello World 2"
  dlg.showXY(Position.CENTER, Position.CENTER)

  iup.mainLoop
  iup.close()

}
```

Screenshot:

![2.2](https://github.com/edadma/iup/blob/stable/readme/2-2.png)

### Example [2.3 Adding Interaction](https://www.tecgraf.puc-rio.br/iup/en/tutorial/tutorial2.html#Interaction)

```scala
import io.github.edadma.iup
import io.github.edadma.iup.{Handle, Position, Return, Result}

object Main extends App {

  val btn_exit_cb = (_: Handle) => {
    iup.message("Hello World Message", "Hello world from IUP.")

    /* Exits the main loop */
    Return.CLOSE
  }

  if (iup.open == Result.ERROR) {
    println("Error opening window")
    sys.exit(1)
  }

  val button = iup.button("OK", null);
  val vbox   = iup.vbox(button);
  val dlg    = iup.dialog(vbox)

  dlg.TITLE = "Hello World 3"

  /* Registers callbacks */
  button.ACTION = btn_exit_cb

  dlg.showXY(Position.CENTER, Position.CENTER)

  iup.mainLoop
  iup.close()

}
```

Screenshots:

![2.3 1](https://github.com/edadma/iup/blob/stable/readme/2-3-1.png) ![2.3 2](https://github.com/edadma/iup/blob/stable/readme/2-3-2.png)

### Example [2.4 Adding Layout Elements](https://www.tecgraf.puc-rio.br/iup/en/tutorial/tutorial2.html#Adding_Layout_Elements)

```scala
import io.github.edadma.iup
import io.github.edadma.iup.{Handle, Position, Return, Result}

object Main extends App {

  val btn_exit_cb = (_: Handle) =>
    /* Exits the main loop */
    Return.CLOSE

  if (iup.open == Result.ERROR) {
    println("Error opening window")
    sys.exit(1)
  }

  val label  = iup.label("Hello world from IUP.")
  val button = iup.button("OK", null);
  val vbox   = iup.vbox(label, button)
  val dlg    = iup.dialog(vbox)

  dlg.TITLE = "Hello World 4"

  /* Registers callbacks */
  button.ACTION = btn_exit_cb

  dlg.showXY(Position.CENTER, Position.CENTER)

  iup.mainLoop
  iup.close()

}
```

Screenshot:

![2.4](https://github.com/edadma/iup/blob/stable/readme/2-4.png)

### Example [2.5 Improving the Layout](https://www.tecgraf.puc-rio.br/iup/en/tutorial/tutorial2.html#Improving_the_Layout)

```scala
import io.github.edadma.iup
import io.github.edadma.iup.Implicits._
import io.github.edadma.iup.{Handle, Position, Return, Result}

object Main extends App {

  val btn_exit_cb = (_: Handle) =>
    /* Exits the main loop */
    Return.CLOSE

  if (iup.open == Result.ERROR) {
    println("Error opening window")
    sys.exit(1)
  }

  val label  = iup.label("Hello world from IUP.")
  val button = iup.button("OK", null);
  val vbox   = iup.vbox(label, button)(alignment = "acenter", gap = 10, margin = 30 x 10)
  val dlg    = iup.dialog(vbox)

  dlg.TITLE = "Hello World 5"

  /* Registers callbacks */
  button.ACTION = btn_exit_cb

  dlg.showXY(Position.CENTER, Position.CENTER)

  iup.mainLoop
  iup.close()

}
```

Screenshot:

![2.5](https://github.com/edadma/iup/blob/stable/readme/2-5.png)


Documentation
-------------

API documentation is forthcoming, however documentation for the *IUP* C library is
found [here](https://www.tecgraf.puc-rio.br/iup/).  Build instructions for the C libraries are [here](https://www.tecgraf.puc-rio.br/iup/en/guide.html#buildlib).


License
-------

This project is licensed under the [ISC](https://github.com/edadma/iup/blob/main/LICENSE).  The IUP C libraries (included in the `native-lib` folder) are under the [Tecgraf Library License](https://www.tecgraf.puc-rio.br/iup/en/copyright.html), which is a permissive MIT type license.
