.PHONY: package compile clean test-build test
.DEFAULT: package

JAVAC=javac
JAR=jar
JUNIT=/usr/share/java/junit4.jar

SRC=src
TEST=test

BUILD=build
TBUILD=tb
PACKAGE=dist

PACKAGEJAR=$(PACKAGE)/irc.jar
LIBS=$(wildcard lib/*.jar)

CP=$(SRC):$(LIBS: =:)
TCP=$(TEST):$(BUILD):$(JUNIT):$(LIBS: =:)

FILES=$(wildcard $(SRC)/uk/co/harcourtprogramming/internetrelaycats/*.java) $(wildcard $(SRC)/uk/co/harcourtprogramming/mewler/*.java)
CLASS=$(patsubst $(SRC)/%.java,$(BUILD)/%.class,$(FILES))

TFILES=$(wildcard $(TEST)/uk/co/harcourtprogramming/internetrelaycats/*.java) $(wildcard $(TEST)/uk/co/harcourtprogramming/mewler/*.java)
TCLASS=$(patsubst $(TEST)/%.java,$(TBUILD)/%.class,$(TFILES))

TESTABLE=$(wildcard $(TEST)/uk/co/harcourtprogramming/internetrelaycats/*Test.java) $(wildcard $(TEST)/uk/co/harcourtprogramming/mewler/*Test.java)
TESTS=$(patsubst $(TEST).%.java,%,$(subst /,.,$(TESTABLE)))

package: $(PACKAGEJAR)
compile: $(CLASS)
test-build: $(TCLASS)
test:
	java -cp $(TBUILD):$(TCP) org.junit.runner.JUnitCore $(TESTS)

$(BUILD)/%.class : $(SRC)/%.java $(LIBS) $(BUILD)
	$(JAVAC) -classpath $(CP) -d $(BUILD) $<

$(TBUILD)/%.class : $(TEST)/%.java $(LIBS) compile $(TBUILD)
	$(JAVAC) -classpath $(TCP) -d $(TBUILD) $<

$(PACKAGEJAR): $(PACKAGE) $(BUILD) $(CLASS) $(LIBS)
	-rm -f $(PACKAGEJAR)
	$(JAR) cfm $(PACKAGEJAR) Manifest.mf -C $(BUILD) .
	-cp $(LIBS) $(PACKAGE)

$(BUILD):
	-mkdir $@
$(TBUILD):
	-mkdir $@
$(PACKAGE):
	-mkdir $@

clean:
	-rm -f $(BUILD) -r
	-rm -f $(TBUILD) -r
	-rm -f $(PACKAGE) -r

