OUT = out/make

all: outdir img
	javac -encoding UTF-8 -sourcepath src -d "$(OUT)" src/sic/*.java

sim: outdir img
	javac -encoding UTF-8 -sourcepath src -d "$(OUT)" src/sic/Sim.java

vm: outdir
	javac -encoding UTF-8 -sourcepath src -d "$(OUT)" src/sic/VM.java

asm: outdir
	javac -encoding UTF-8 -sourcepath src -d "$(OUT)" src/sic/Asm.java

link: outdir
	javac -encoding UTF-8 -sourcepath src -d "$(OUT)" src/sic/Link.java

jar: all
	jar --create --file "$(OUT)/sictools.jar" --manifest MANIFEST.MF -C "$(OUT)" .

outdir:
	@mkdir -p "$(OUT)"

img: outdir
	cp -R img "$(OUT)"

clean:
	rm -rf "$(OUT)"

upload:
	scp "$(OUT)/sictools.jar" jure@lalg.fri.uni-lj.si:public_html/
