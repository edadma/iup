import io.github.edadma.iup.facade._

object Main extends App {

  if (IupOpen == IUP_ERROR) {
    println("Error opening window")
    sys.exit(1)
  }

  IupMessage("Hello World!", "Hello world from IUP.")
  IupClose()

}
