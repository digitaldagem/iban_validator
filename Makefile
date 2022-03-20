JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class: $(JC) $(JFLAGS) $*.java
CLASSES = IbanValidatorService.java IbanValidatorController.java IbanValidatorApplication.java
default: classes
classes: $(CLASSES:.java=.class)
clean: $(RM) *.class
