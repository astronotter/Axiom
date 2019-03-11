.PHONY: all run
all: 
	javac -cp ".:axiom/*:lib/*" axiom/*.java
	jar cfm Axiom.jar Manifest.txt axiom/*.class
run: all
	java -jar Axiom.jar
