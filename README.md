iup
====

![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/edadma/iup?include_prereleases) ![GitHub (Pre-)Release Date](https://img.shields.io/github/release-date-pre/edadma/iup) ![GitHub last commit](https://img.shields.io/github/last-commit/edadma/iup) ![GitHub](https://img.shields.io/github/license/edadma/iup)

*iup* provides Scala Native bindings for the [LibYAML](https://pyyaml.org/wiki/LibYAML) C library for parsing [YAML](https://yaml.org/).

Overview
--------

The goal of this project is to provide easy-to-use Scala Native bindings for the LibYAML YAML parser library.  Currently, all the event based YAML parsing capabilities of LibYAML are supported.  All twenty-eight examples in [Chapter 2](https://yaml.org/spec/1.2.1/#Preview) of the YAML Specification can be parsed.  There is also support for constructing a Scala data structure corresponding to the YAML being parsed.  However, support for application specific tags is lacking. The LibYAML bindings are not complete, but work is ongoing.

The more "programmer friendly" part of this library is found in the `io.github.edadma.iup` package. That's the only
package you need to import from, as seen in the example below. The other package in the library
is `io.github.edadma.iup.extern` which provides for interaction with the *LibYAML* C library using Scala Native
interoperability elements from the so-call `unsafe` namespace. There are no public declarations in
the `io.github.edadma.iup` package that use `unsafe` types in their parameter or return types, making it a pure
Scala bindings library. Consequently, you never have to worry about memory allocation or type conversions.

Usage
-----

To use this library, `iup-dev` needs to be installed:

```shell
sudo apt install iup-dev
```

Include the following in your `project/plugins.sbt`:

```sbt
addSbtPlugin("com.codecommit" % "sbt-github-packages" % "0.5.2")

```

Include the following in your `build.sbt`:

```sbt
resolvers += Resolver.githubPackages("edadma")

libraryDependencies += "io.github.edadma" %%% "iup" % "0.1.0"

```

Use the following `import` statement in your code:

```scala
import io.github.edadma.iup._

```

Examples
--------

The following examples are taken directly from the [YAML Specification](https://yaml.org/spec/1.2.1/) without any changes.

### Example 24

This example is [Example 24](https://yaml.org/spec/1.2.1/#id2761292) of the YAML spec.

```scala
import io.github.edadma.libyaml._

import pprint._

object Main extends App {

  val example_24 =
    """
      |%TAG ! tag:clarkevans.com,2002:
      |--- !shape
      |  # Use the ! handle for presenting
      |  # tag:clarkevans.com,2002:circle
      |- !circle
      |  center: &ORIGIN {x: 73, y: 129}
      |  radius: 7
      |- !line
      |  start: *ORIGIN
      |  finish: { x: 89, y: 102 }
      |- !label
      |  start: *ORIGIN
      |  color: 0xFFEEBB
      |  text: Pretty vector drawing.
      |""".stripMargin

  pprintln(parseFromString(example_24))

}
```

Output:

```scala
YAMLStream(
  documents = List(
    YAMLDocument(
      document = YAMLTaggedSequence(
        tag = "tag:clarkevans.com,2002:shape",
        elems = List(
          YAMLTaggedMapping(
            tag = "tag:clarkevans.com,2002:circle",
            pairs = List(
              YAMLPair(
                key = YAMLString(v = "center"),
                value = YAMLMapping(
                  pairs = List(
                    YAMLPair(key = YAMLString(v = "x"), value = YAMLInteger(v = 73)),
                    YAMLPair(key = YAMLString(v = "y"), value = YAMLInteger(v = 129))
                  )
                )
              ),
              YAMLPair(key = YAMLString(v = "radius"), value = YAMLInteger(v = 7))
            )
          ),
          YAMLTaggedMapping(
            tag = "tag:clarkevans.com,2002:line",
            pairs = List(
              YAMLPair(
                key = YAMLString(v = "start"),
                value = YAMLMapping(
                  pairs = List(
                    YAMLPair(key = YAMLString(v = "x"), value = YAMLInteger(v = 73)),
                    YAMLPair(key = YAMLString(v = "y"), value = YAMLInteger(v = 129))
                  )
                )
              ),
              YAMLPair(
                key = YAMLString(v = "finish"),
                value = YAMLMapping(
                  pairs = List(
                    YAMLPair(key = YAMLString(v = "x"), value = YAMLInteger(v = 89)),
                    YAMLPair(key = YAMLString(v = "y"), value = YAMLInteger(v = 102))
                  )
                )
              )
            )
          ),
          YAMLTaggedMapping(
            tag = "tag:clarkevans.com,2002:label",
            pairs = List(
              YAMLPair(
                key = YAMLString(v = "start"),
                value = YAMLMapping(
                  pairs = List(
                    YAMLPair(key = YAMLString(v = "x"), value = YAMLInteger(v = 73)),
                    YAMLPair(key = YAMLString(v = "y"), value = YAMLInteger(v = 129))
                  )
                )
              ),
              YAMLPair(key = YAMLString(v = "color"), value = YAMLInteger(v = 16772795)),
              YAMLPair(
                key = YAMLString(v = "text"),
                value = YAMLString(v = "Pretty vector drawing.")
              )
            )
          )
        )
      )
    )
  )
)
```

### Example 26

This example is [Example 26](https://yaml.org/spec/1.2.1/#id2761292) of the YAML spec.

```scala
import io.github.edadma.libyaml._

import pprint._

object Main extends App {

  val example_26 =
    """
        |# Ordered maps are represented as
        |# A sequence of mappings, with
        |# each mapping having one key
        |--- !!omap
        |- Mark McGwire: 65
        |- Sammy Sosa: 63
        |- Ken Griffy: 58
        |""".stripMargin

  pprintln(parseFromString(example_26))
  pprintln(constructFromString(example_26))

}

```

Output:

```scala
YAMLStream(
  documents = List(
    YAMLDocument(
      document = YAMLOrderedMapping(
        pairs = List(
          YAMLPair(key = YAMLString(v = "Mark McGwire"), value = YAMLInteger(v = 65)),
          YAMLPair(key = YAMLString(v = "Sammy Sosa"), value = YAMLInteger(v = 63)),
          YAMLPair(key = YAMLString(v = "Ken Griffy"), value = YAMLInteger(v = 58))
        )
      )
    )
  )
)
List(VectorMap("Mark McGwire" -> 65, "Sammy Sosa" -> 63, "Ken Griffy" -> 58))
```

Documentation
-------------

API documentation is forthcoming, however documentation for the *Libyaml* C library is
found [here](https://pyyaml.org/wiki/PyYAMLDocumentation).

License
-------

[ISC](https://github.com/edadma/iup/blob/main/LICENSE)
