.PHONY: all run
all: 
	javac -cp ".:axiom/*:lib/*" axiom/*.java
	jar cfm Axiom.jar Manifest.txt axiom/*.class META-INF/persistence.xml
run: all
	java -javaagent:lib/openjpa-3.0.0.jar -jar Axiom.jar