# deppep.jlox

The code implement these additional features:
* block comments (challenge 4.4)
* comma operator (challenge 6.1)
* ternary operator (challenge 6.2)
* '+' at start expression error production (challenge 6.3)

Solution to challenges which would not result in interesting features are given in separate branches:
* challenge 5.3 @ rpn

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
