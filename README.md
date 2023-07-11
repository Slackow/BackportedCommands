# Fabric Example Mod

## Setup

1. Edit gradle.properties, build.gradle and mod.json to suit your needs.
    * The "mixins" object can be removed from mod.json if you do not need to use mixins.
    * Please replace all occurences of "modid" with your own mod ID - sometimes, a different string may also suffice.
2. Run the following command:

```
./gradlew
```

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.

Commands:
- /fill <x1 y1 z1> <x2 y2 z2> <block> [state] [replace|destroy|keep|hollow|outline]
- /clone <x y z> <x2 y2 z2> <x3 y3 z3> [replace|masked|filtered] [normal|force|move]
- /dir -> open minecraft directory
- /function <function name> -> run a function, stored in functions folder under world or run directory,
only works with mcfunction files, these files are lists of commands, blank lines and lines starting with # will be ignored.
- /reload -> reload functions from disk
