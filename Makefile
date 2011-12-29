.PHONY: package compile clean
.DEFAULT: package

JAVAC=javac
JAR=jar

BUILD=build
SRC=src
PACKAGE=dist
PACKAGEJAR=$(PACKAGE)/irc.jar
LIBS=$(wildcard lib/*.jar)

CP=$(SRC):$(LIBS: =:)

FILES=$(wildcard $(SRC)/uk/co/harcourtprogramming/internetrelaycats/*.java)
CLASS=$(patsubst $(SRC)/%.java,$(BUILD)/%.class,$(FILES))

package: $(PACKAGEJAR)
compile: $(CLASS)

$(BUILD)/%.class : $(SRC)/%.java $(LIBS)
	$(JAVAC) -classpath $(CP) -d $(BUILD) $<

$(PACKAGEJAR): $(PACKAGE) $(BUILD) $(CLASS) $(LIBS)
	-rm -f $(PACKAGEJAR)
	$(JAR) cfm $(PACKAGEJAR) Manifest.mf -C $(BUILD) .
	cp $(LIBS) $(PACKAGE)

$(BUILD):
	-mkdir $(BUILD)

$(PACKAGE):
	-mkdir $(PACKAGE)

clean:
	-rm -f build/* -r
	-rm -f dist/* -r

