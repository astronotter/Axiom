.PHONY: all run
test: all
	java -cp ".:axiom/*:junit-4.10.jar" org.junit.runner.JUnitCore axiom.QuestionTest
run: all
	java -jar Axiom.jar
all: axiom/*.java
	javac -cp ".:axiom/*:junit-4.10.jar" axiom/*.java
	jar cfm Axiom.jar Manifest.txt axiom/*.class
clean:
	rm axiom/*.class
	rm Axiom.jar