.PHONY: package compile clean
.DEFAULT: package

JAVAC=javac
JAR=jar

BUILD=build
SRC=src
PACKAGE=dist
PACKAGEJAR=$(PACKAGE)/netcat.jar
LIBS=$(wildcard lib/*.jar)

CP=$(SRC):$(LIBS: =:)

FILES=$(wildcard $(SRC)/uk/co/harcourtprogramming/netcat/*.java)
CLASS=$(patsubst $(SRC)/%.java,$(BUILD)/%.class,$(FILES))

package: $(PACKAGEJAR)
compile: $(CLASS)

$(BUILD)/%.class : $(SRC)/%.java $(LIBS)
	$(JAVAC) -classpath $(CP) -d $(BUILD) $<

$(PACKAGEJAR): $(PACKAGE) $(BUILD) $(CLASS) $(LIBS)
	$(JAR) cfm $(PACKAGEJAR) Manifest.mf -C $(BUILD) .
	cp $(LIBS) $(PACKAGE)

$(BUILD):
	-mkdir $(BUILD)

$(PACKAGE):
	-mkdir $(PACKAGE)

clean:
	-rm -f build/* -r
	-rm -f dist/* -r

