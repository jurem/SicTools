NAME=${name}
# MAIN is the class which extends the abstract class sic.sim.plugins.Plugin.
MAIN="${name}.Main"
OUT=out/make
# SICPATH should be changed if building from a different tree.
# It can also point to sictools.jar file.
SICPATH=../out/make


.PHONY: main
main: outdir
	javac -encoding UTF-8 -cp ".:$(SICPATH)" -d "$(OUT)/$(NAME)" $(NAME)/*.java
	jar --create --file "$(OUT)/$(NAME).jar" --main-class $(MAIN) -C "$(OUT)/$(NAME)" .

.PHONY: outdir
outdir:
	@mkdir -p "$(OUT)"

.PHONY: clean
clean:
	rm -rf "$(OUT)"
