
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

## Generate summary for AI
```bash
repomix --ignore=".idea,target" .
```