package deppep.jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Lox {
	private static final Interpreter interpreter = new Interpreter();
	static boolean hadError = false;
	static boolean hadRuntimeError = false;
	
	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			System.out.println("Usage: jlox [script]");
			System.exit(64);
		} else if (args.length == 1) {
			runFile(args[0]);
		} else {
			runPrompt();
		}
	}

	// runs interpreter over a source file wrapper
	private static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()), false);
		if (hadError) System.exit(65);
		if (hadRuntimeError) System.exit(70);
	}

	// runs a prompt wrapper
	private static void runPrompt() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		System.out.print("Hey, you!g\nThis a REPL to the deppep's implementation of jlox, hf.\n\n");
		for (;;) {
			System.out.print("> ");
			String line = reader.readLine();
			if (line == null) break;
			run(line, true);
			hadError = false;
		}
	}

	// run core function
	private static void run(String source, boolean repl) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();

		Parser parser = new Parser(tokens);
		List<Stmt> statements = parser.parse();

		// challenge 8.1
		// print expression values to std out when in REPL
		if (repl) {
			for (int i = 0; i < statements.size(); i++)
				if (statements.get(i) instanceof Stmt.Expression)
					statements.set(i, new Stmt.Print(((Stmt.Expression) statements.get(i)).expression));
		}

		// stop if there was a syntax error
		if (hadError) return;
		interpreter.interpret(statements);
	}

	static void error(int line, String message) {
		report(line, "", message);
	}

	static void error(Token token, String message) {
		if (token.type == TokenType.EOF) {
			report(token.line, "at end", message);
			
		} else {
			report(token.line, "at '" + token.lexeme + "'", message);
		}
	}

	static void runtimeError(RuntimeError error) {
		System.err.println("[line " + error.token.line + "] " + error.getMessage());
		hadRuntimeError = true;
	}

	private static void report(int line, String where, String message) {
		System.err.println(
			"[line " + line + "] Error " + where + ": " + message
			);
		hadError = true;
	}
}
