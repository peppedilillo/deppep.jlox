"""
Generates Java class file from an expression grammar.

Usage:
> python generate_ast.py Expr.java
> python generate_ast.py
"""
from typing import Callable, IO
from pathlib import Path
from datetime import datetime

INDENT = " " * 4

PRODUCTIONS = {
    "Expr": (
        ("Ternary", (("Expr", "left"), ("Token", "first"), ("Expr", "middle"), ("Token", "second"), ("Expr", "right"))),
        ("Binary", (("Expr", "left"), ("Token", "operator"), ("Expr", "right"))),
        ("Unary", (("Token", "operator"), ("Expr", "right"),)),
        ("Grouping", (("Expr", "expression"),)),
        ("Literal", (("Object", "value"),)),
        ("Variable", (("Token", "name"),)),
    ),
    "Stmt": (
        ("Expression", (("Expr", "expression"),)),
        ("Print", (("Expr", "expression"),)),
        ("Var", (("Token", "name"), ("Expr", "initializer"),)),
    )
}


def arglist(body: tuple):
    return ', '.join([' '.join(p) for p in body])

def write_doc(key: str, write: Callable=print):
    write("/**")
    write(" * Implements the syntax grammar:")
    for head, body in PRODUCTIONS[key]:
        write(f" * {INDENT}{head} -> {arglist(body)}")
    write(f" * automatically generated with `{Path(__file__).name}` on {datetime.now().strftime('%d/%m/%y %H:%M')}.")
    write("*/")

def write_package_name(pname: str, write: Callable=print):
    write(f"package {pname};")
    write("")
    write("")
    
def write_productions(key: str, write: Callable=print):
    write(f"abstract class {key} {{")
    
    # declare visitor interfaces
    write(f"{INDENT}interface Visitor<R> {{")
    for head, _ in PRODUCTIONS[key]:
        write(f"{INDENT}{INDENT}R visit{head}{key}({head} expr);")
    write(f"{INDENT}}}")
    write("")
    
    # expression classes
    for head, body in PRODUCTIONS[key]:        
        # builder
        write(f"{INDENT}static class {head} extends {key} {{")
        write(f"{INDENT}{INDENT}{head}({arglist(body)}) {{")
        for _, name in body:
            write(f"{INDENT}{INDENT}{INDENT}this.{name}={name};")
        write(f"{INDENT}{INDENT}}}")
        write("")

        # accepts visitor
        write(f"{INDENT}{INDENT}@Override")
        write(f"{INDENT}{INDENT}<R> R accept(Visitor<R> visitor) {{")
        write(f"{INDENT}{INDENT}{INDENT}return visitor.visit{head}{key}(this);")
        write(f"{INDENT}{INDENT}}}")
        write("")

        # finals
        for type_, name in body:
            write(f"{INDENT}{INDENT}final {type_} {name};")
        write(f"{INDENT}}}")
        write("")

    write(f"{INDENT}abstract <R> R accept(Visitor<R> visitor);")
    write("}")
    write("")

def writer(line: str, f: IO):
    f.write(line + "\n")

    
if __name__ == "__main__":
    import sys

    USAGE = (
            f"Usage: python {Path(__file__).name} expr|stmt [output]\n"
            f"Prints java expression or statement class, and optionally saves it to output."
    )

    if len(sys.argv) != 2 or sys.argv[1] not in ["expr", "stmt"]:
        print(USAGE)
        exit()

    f = sys.stdout
    key = sys.argv[1].capitalize()
    write_doc(key, lambda s: writer(s, f))
    write_package_name("deppep.jlox", lambda s: writer(s, f))
    write_productions(key, lambda s: writer(s, f))
