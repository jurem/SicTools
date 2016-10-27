OUT=out/make


all: outdir sim vm asm link


outdir:
	mkdir -p $(OUT)


sim:
	cd src; javac -d "../$(OUT)" sic/Sim.java
	cp -R img "$(OUT)"


vm:
	cd src; javac -d "../$(OUT)" sic/VM.java


asm:
	cd src; javac -d "../$(OUT)" sic/Asm.java


link:
	cd src; javac -d "../$(OUT)" sic/Link.java


manifest:
	printf "Manifest-Version: 1.0\nClass-Path: .\nMain-Class: sic.Sim\n"  >"$(OUT)/MANIFEST.MF"


jar: all manifest
	cd "$(OUT)"; jar cfm sictools.jar MANIFEST.MF *


clean:
	rm -rf "$(OUT)"


upload:
	scp "$(OUT)/sictools.jar" jure@lalg.fri.uni-lj.si:public_html/
