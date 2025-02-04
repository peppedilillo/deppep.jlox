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

PRODUCTIONS = (
    ("Ternary", (("Expr", "left"), ("Token", "first"), ("Expr", "middle"), ("Token", "second"), ("Expr", "right"))),
    ("Binary", (("Expr", "left"), ("Token", "operator"), ("Expr", "right"))),
    ("Grouping", (("Expr", "expression"),)),
    ("Literal", (("Object", "value"),)),
    ("Unary", (("Token", "operator"), ("Expr", "right"))),
)


def arglist(body: tuple):
    return ', '.join([' '.join(p) for p in body])

def write_doc(prodlist: tuple, write: Callable=print):
    write("/**")
    write(" * Implements the  syntax grammar:")
    for head, body in prodlist:
        write(f" * {INDENT}{head} -> {arglist(body)}")
    write(f" * automatically generated with `{Path(__file__).name}` on {datetime.now().strftime('%d/%m/%y %H:%M')}.")
    write("*/")

def write_package_name(pname: str, write: Callable=print):
    write(f"package {pname};")
    write("")
    write("")
    
def write_productions(prodlist: tuple, write: Callable=print):
    write("abstract class Expr {")
    
    # declare visitor interfaces
    write(f"{INDENT}interface Visitor<R> {{")
    for head, _ in prodlist:
        write(f"{INDENT}{INDENT}R visit{head}Expr({head} expr);")
    write(f"{INDENT}}}")
    write("")
    
    # expression classes
    for head, body in prodlist:        
        # builder
        write(f"{INDENT}static class {head} extends Expr {{")
        write(f"{INDENT}{INDENT}{head}({arglist(body)}) {{")
        for _, name in body:
            write(f"{INDENT}{INDENT}{INDENT}this.{name}={name};")
        write(f"{INDENT}{INDENT}}}")
        write("")

        # accepts visitor
        write(f"{INDENT}{INDENT}@Override")
        write(f"{INDENT}{INDENT}<R> R accept(Visitor<R> visitor) {{")
        write(f"{INDENT}{INDENT}{INDENT}return visitor.visit{head}Expr(this);")
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
    
    if len(sys.argv) > 2:
        print(
            f"Usage: python {Path(__file__).name} [output]\n"
            f"Prints java expression class or saves it to output.")
        exit()
    elif len(sys.argv) == 2:
        # to file
        f = open(Path(sys.argv[1]), "w")
    else:
        # to stdout
        f = sys.stdout

    write_doc(PRODUCTIONS, lambda s: writer(s, f))
    write_package_name("deppep.jlox", lambda s: writer(s, f))
    write_productions(PRODUCTIONS, lambda s: writer(s, f))
