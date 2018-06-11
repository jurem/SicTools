## Running the Makefile
Makefile is used for generating reference obj files from asm examples in the `src` folder.

1. Copy the compiled `sictools.jar` in this folder.
2. Run `make build`.

The reference obj files are put in the obj folder.

## Running generator.sh
The `generator.sh` script generates the ref files that are used for diff in the `tester.sh` script.

Reference program is defined in the PROG variable.
Reference program should support:
- Accepting obj as an input file
- Dumping all of the registers: `A,X,L,B,S,T,F` with their final values at the start of dump file.
- Dumping the memory in specified range; `$PROG -m$from,$len obj/${BASE}.obj` where $PROG is command to run reference program, $from is the start of memory to dump, $len is the length of memory to dump
- The output should be put in a file called `dump`.

## Running tester.sh
`tester.sh` is used for testing the output of your program vs. the output of the .ref files.

Your program is defined in the PROG variable.
The script will similarly to the `generator.sh` script and compare the result of your files with the ref folder.
It will also report errors in case your program exits with error code.

---
The testing scripts and tests are made by Tadej Borovšak.


_TODO_: Fix the `float.asm` test, it is currently commented since it produces an error while assembling it with sictools.