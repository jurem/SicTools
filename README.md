# SicTools
Tools for SIC/XE hypothetical computer from the Leland Beck's book System Software. Includes:
  * Assembler
  * Simulator
  * Linker

See also http://jurem.github.io/SicTools/ for the main page as well as https://github.com/jurem/SicDemos for several examples.

## Assembler
Assembler supports all instructions and directives described in the book. This includes load/store instructions, arithmetic instructions, jumps etc. And directives START, END, ORG, LTORG, BASE, NOBASE, CSECT, USE, EQU, RESB, RESW, EXTDEF, EXTREF. Some features:
  * immediate addressing, indirect addressing, simple addressing
  * PC-relative addressing, base addressing (BASE and NOBASE directive), indexed addressing
  * standard directives START, END, ORG
  * support for literals, LTORG directive
  * support for EQU expressions, full forward and backward references resolved via the algorithm described in the book
  * support for sections via CSECT directive
  * support for blocks via USE directive
  * assembler syntax is (see command line options) more flexbile and free than original
  * floating-point syntax extensions (FLOT and RESF directives)
  * syntax extension for specifying numbers in binary, octal, decimal and hexadecimal format
  * generates debugging-friendly listing file showing original source and corresponding address and generated code
  * generates log file showing code statistics, list of blocks, list of sections, list of symbols, list of literals, list of relocations
  * and more

## Simulator
Simulator is user-friendly GUI based application that loads asm or obj files. Features:
  * CPU view of registers and current instructions, shows changed registers in different color, supports changing registers values
  * disassembly view with breakpoints and data breakpoints
  * memory view with full edit support in hexadecimal and character mode
  * watch view with symbols from assembly file
  * textual screen support
  * devices 0, 1, 2 are redirected to standard input, output and error
  * detected pseudo HALT instruction (jump on itself)
  * automatic execution with set speed (from 1 Hz to 1 MHz)
  * keyboard input
  * and more

## Linker
Linker supports linking .obj files produced by the assembler into one. Each object file can have multiple control sections and needs to be relative. Other features include:
  * a graphical interface for selecting .obj files and choosing the linker settings
  * inspecting, editing and reordering control sections or symbols in a gui or textual interface
  * partial linking when some of the references are not present
  * option to keep symbols in the output file to allow further linking
  * and more

## Addons
Simulator can be extended with external addons.
They can provide additional devices, different ways of how the virtual processor can communicate with the host computer and much more.

### Usage
Currently, addons can be loaded only via the cli with `-a path[@params]` argument.
You can use as many `-a` arguments as there are addons you want to load.
If the addon uses parametres, you can specify them after the addons path, separated by a `@` character.

You can run the example addon (see [#examples](Examples) for building instructions) with the command
    `java -cp out/make/sictools.jar sic.Sim -a addons/out/make/rand.jar@5`
Loaded program can use the device `5` to read random bytes instead of file `05.dev`.

### Writing an addon
Addon is a jar file whose main class extends `sic.sim.addons.Addon` class.
It can override any of the methods defined there according to its requirements.

One of the most important methods is `init(sic.sim.Executor)`,
with which the addon can obtain the simulator's executor object.
The executor contains virtual machine, which can be used to access memory and registers.

For more explanation, please read the code of the `Addon` class and see examples
in `addons` and `src/sic/sim/addons` directories.

#### Examples
Directory `addons` contains an example of a standalone addon (intended use of addons),
which can be build out of the source tree.
You can build it with `name=rand make` command.

When building your own addon, replace `rand` with the name of your addon.
Also, don't forget to change `SICPATH` in Makefile according to location of your addon.

Directory `src/sic/sim/addons` contains some other addons, which are built differently
than a regular addon.
They are included in the SicTools jar and loaded automatically,
but can serve as more complex example of addons.


Installation
------------

SicTools may be downloaded as a JAR file or built from source.

### JAR
Download the latest stable version from [releases](https://github.com/jurem/SicTools/releases). You may need to change the file permissions to allow execution.

### Building from source
Download / clone source code and run make.

    git clone https://github.com/jurem/SicTools.git
    cd SicTools
    make jar
    
Usage
-----

To run simulator

    java -jar out/make/sictools.jar

To run assembler

    java -cp out/make/sictools.jar sic.Asm source.asm

where `source.asm` is the file to be compiled.

To get assembler help

    java -cp out/make/sictools.jar sic.Asm -help

To run linker

    java -cp out/make/sictools.jar sic.Link -o out.obj in1.obj in2.obj ...

where `out.obj` is the output file and `in1.obj`, `in2.obj`,... are .obj files to be linked.

To get linker help

    java -cp out/make/sictools.jar sic.Link -help

To get graphical linker interface

    java -cp out/make/sictools.jar sic.Link -g
