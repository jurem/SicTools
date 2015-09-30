# SicTools
Tools for SIC/XE hypothetical computer from the Leland Beck's book System Software. Includes:
  * Assembler
  * Simulator

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

Simulator is user-friendly GUI based application that loads asm or obj files. Features:
  * CPU view of registers and current instructions, shows changed registers in different color, supports changing registers values
  * disassembly view and breakpoints
  * memory view with full edit support in hexadecimal and character mode
  * textual screen support
  * devices 0, 1, 2 are redirected to standard input, output and error
  * detected pseudo HALT instruction (jump on itself)
  * automatic execution with set speed (from 1 Hz to 1 MHz)
  * and more

See also http://jurem.github.io/SicTools/ for the main page as well as https://github.com/jurem/SicDemos for several examples.

Installation
------------

Download or clone source code and run make.

    git clone https://github.com/jurem/SicTools.git
    cd SicTools
    make jar

Usage
-----

To run simulator

    java -jar out/make/sictools.jar

To run assembler

    java -cp out/make/sictools.jar sic.Asm source.asm

where source.asm is the file to be compiled.

To get assembler help

    java -cp out/make/sictools.jar sic.Asm -help
