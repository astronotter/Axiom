.PHONY: all run
all: axiom/*.java
	javac -cp ".:axiom/*" axiom/*.java
	jar cfm Axiom.jar Manifest.txt axiom/*.class
run: all
	java -jar Axiom.jar
clean:
	rm axiom/*.class
	rm Axiom.jar