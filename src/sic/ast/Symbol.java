package sic.ast;

import sic.asm.AsmError;
import sic.asm.Location;
import sic.ast.expression.Expr;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class Symbol extends Node implements Comparable<Symbol> {

    static enum Scope {
        LOCAL, IMPORTED, EXPORTED
    }

    enum LabelType {
        NOTLABEL, CODE, DATA
    }

    public final String name;           // name of the symbol
    public final Location loc;          // location of the definition
    private Scope scope;
    private boolean absolute;           // is the symbol absolute
    public LabelType labelType;         // is it before code or data
    // value or expr of the symbol
    private int value;                  // value of the symbol
    public final Expr expr;             // if expr == null then symbol is defined by value
    private boolean evaluated;          // if symbol was already evaluated (with expression)
    private int dependencyCount;        // number of symbols this symbol depends on

    // labels
    public Symbol(String name, Location loc, int value, boolean isData) {
        this.name = name;
        this.loc = loc;
        this.scope = Scope.LOCAL;
        this.value = value;
        this.expr = null;
        this.evaluated = true;
        this.labelType = isData ? LabelType.DATA : LabelType.CODE;
    }

    // EQU expressions
    public Symbol(String name, Location loc, Expr expr) {
        this.name = name;
        this.loc = loc;
        this.scope = Scope.LOCAL;
        this.expr = expr;
        this.dependencyCount = expr.countSyms();
        this.labelType = LabelType.NOTLABEL;
    }

    // imported/external symbols
    public Symbol(String name, Location loc) {
        this.name = name;
        this.loc = loc;
        this.value = 0;
        this.expr = null;
        this.scope = Scope.IMPORTED;
        this.labelType = LabelType.NOTLABEL;
    }

    @Override
    public int compareTo(Symbol that) {
        return name.compareTo(that.name);
    }

    @Override
    public String toString() {
        return name + "=" + valueToString();
    }

    public Scope scope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public boolean isImported() {
        return scope == Scope.IMPORTED;
    }

    public boolean isExported() {
        return scope == Scope.EXPORTED;
    }

    public void setExported() {
        scope = Scope.EXPORTED;
    }

    public String scopeToString() {
        return scope.toString().toLowerCase();
    }

    public String kindToString() {
        if (absolute) return "absolute";
        else return "relative";
    }

    public String labelTypeToString() {
        return this.labelType.toString().toLowerCase();
    }

    public String valueToString() {
        return evaluated ? Integer.toString(value) : "?";
    }

    public String exprToString() {
        if (scope == Scope.IMPORTED) return "";
        return expr == null ? "label" : expr.toString();
    }

    public boolean isAbsolute() {
        return absolute;
    }

    public void setAbsolute(boolean absolute) {
        this.absolute = absolute;
    }

    // ************ evaluation

    public int value() {
        return value;
    }

    public boolean isEvaluated() {
        return evaluated;
    }

    public void eval(Program program) throws AsmError {
        if (evaluated) return;
        if (expr != null) value = expr.eval(program);
        evaluated = true;
    }

    public int dependencyCount() {
        return dependencyCount;
    }

    public boolean decDependencyCount() {
        return --dependencyCount == 0;
    }

}
