# deppep.jlox

The code implement these additional features:
* block comments (challenge 4.4)
* comma operator (challenge 6.1)
* ternary operator (challenge 6.2)
* '+' at start expression error production (challenge 6.3)
* sum of strings and arbitrary object (challenge 7.2)
* division by zero runtime error (challenge 7.3)
* REPL will print expression values to standard output (challenge 8.1)
* raises a runtime error when accessing uninitialized variables (challenge 8.2)
* break statement (challenge 9.3)

Solution to challenges which would require mantainance without introducing any interesting feature are given in separate branches:
* reverse polish notation printer (challenge 5.3)

The `extras` directory contains:
* `funvis.hs`, a haskell solution to challenge 5.2

## Building and Running

### Using Maven
```bash
# Compile the project
mvn compile

# Run the REPL
mvn exec:java -Dexec.mainClass="deppep.jlox.Lox"

# Run with a script file
mvn exec:java -Dexec.mainClass="deppep.jlox.Lox" -Dexec.args="script.lox"
````

### Using javac
```bash
# Compile
javac src/main/java/deppep/jlox/*.java -d target/classes

# Run the REPL
java -cp target/classes deppep.jlox.Lox

# Run with a script file
java -cp target/classes deppep.jlox.Lox script.lox
```

## Run tests

```bash
mvn test
```

## Code dump
```bash
repomix --ignore=".idea,target" .
```
