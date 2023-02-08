OUT = out/make

all: sim vm asm link

sim:
	@mkdir -p "$(OUT)"
	cp -R img "$(OUT)"
	javac -encoding UTF-8 -sourcepath src -classpath "$(OUT)" -d "$(OUT)" src/sic/Sim.java

vm:
	@mkdir -p "$(OUT)"
	javac -encoding UTF-8 -sourcepath src -classpath "$(OUT)" -d "$(OUT)" src/sic/VM.java

asm:
	@mkdir -p "$(OUT)"
	javac -encoding UTF-8 -sourcepath src -classpath "$(OUT)" -d "$(OUT)" src/sic/Asm.java

link:
	@mkdir -p "$(OUT)"
	javac -encoding UTF-8 -sourcepath src -classpath "$(OUT)" -d "$(OUT)" src/sic/Link.java

jar: all
	jar --create --file "$(OUT)/sictools.jar" --manifest MANIFEST.MF -C "$(OUT)" .

clean:
	rm -rf "$(OUT)"

upload:
	scp "$(OUT)/sictools.jar" jure@lalg.fri.uni-lj.si:public_html/
