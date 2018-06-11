RUN := java -cp
SIC := ../../../sicsim.jar
ASM := sic.Asm

OBJECTS := $(patsubst %.asm, %.obj, $(wildcard src/*.asm))

all:

build: $(OBJECTS)
	rm -f obj/*
	rm -f src/*.log
	mv src/*.obj obj

%.obj: %.asm
	$(RUN) $(SIC) $(ASM) $<

