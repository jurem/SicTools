OUT=out/make

all: outdir asm sim

outdir:
	mkdir -p $(OUT)

asm:
	cd src; javac -d "../$(OUT)" sic/Asm.java

sim:
	cd src; javac -d "../$(OUT)" sic/Sim.java
	cp -R img "$(OUT)"


manifest:
	printf "Manifest-Version: 1.0\nClass-Path: .\nMain-Class: sic.Sim\n"  >"$(OUT)/MANIFEST.MF"

jar: outdir asm sim manifest
	cd "$(OUT)"; jar cfm sictools.jar MANIFEST.MF *

clean:
	rm -rf "$(OUT)"

upload:
	scp "$(OUT)/sictools.jar" jure@lalg.fri.uni-lj.si:public_html/
