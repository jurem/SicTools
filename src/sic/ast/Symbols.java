package sic.ast;

import sic.asm.AsmError;
import sic.asm.Location;
import sic.ast.expression.Expr;

import java.util.*;

/**
 * Symbol table
 *
 * @author jure
 */
public class Symbols {

    private int maxLength;                          // max length of a symbol name
    private Map<String, Symbol> syms;               // symbol table
    private Map<String, Set<Symbol>> influences;    // which symbol influences which (used by expressions)

    public Symbols() {
        this.maxLength = 6;
        this.syms = new HashMap<String, Symbol>();
        this.influences = new HashMap<String, Set<Symbol>>();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, Symbol> e : syms.entrySet()) {
            buf.append(e.getKey());
            buf.append('=');
            buf.append(e.getValue().value());
            buf.append(',');
        }
        return buf.toString();
    }

    public int maxLength() {
        return maxLength;
    }

    public List<Symbol> asSortedList() {
        List<Symbol> s = new ArrayList<Symbol>(syms.values());
        Collections.sort(s);
        return s;
    }

    // ************ getting info about symbols

    public Symbol get(String name) {
        return syms.get(name);
    }

    public boolean isDefined(String name) {
        return syms.containsKey(name);
    }

    public boolean isEvaluated(String name) {
        return isDefined(name) && syms.get(name).isEvaluated();
    }

    // ************ adding symbols

    // general use
    private void define(Symbol sym) throws AsmError {
        if (sym.name == null || "".equals(sym.name)) return;
        if (isDefined(sym.name))
            throw new AsmError(sym.loc, "Duplicate symbol '%s'", sym.name);
        if (sym.name.length() > maxLength) maxLength = sym.name.length();
        syms.put(sym.name, sym);
    }

    // for labels
    public void defineLabel(String name, Location loc, int val) throws AsmError {
        define(new Symbol(name, loc, val));
    }

    // for external symbols
    public void importSymbol(String name, Location loc) throws AsmError {
        define(new Symbol(name, loc));
    }

    // for EQUs
    public void defineEQU(String name, Location loc, Expr expr) throws AsmError {
        Symbol sym = new Symbol(name, loc, expr);
        define(sym);
        // update influences
        Set<String> namesUsed = expr.extractSyms();
        if (namesUsed == null) return;
        for (String n : namesUsed) {
            if (!influences.containsKey(n)) influences.put(n, new HashSet<Symbol>());
            influences.get(n).add(sym);
        }
    }

    public void exportSymbol(String name, Location loc) throws AsmError {
        Symbol sym = syms.get(name);
        if (sym == null)
            throw new AsmError(loc, "Cannot export undefined symbol '%s'", name);
        sym.setExported();
    }

    // ************ updating symbols
    // see algorithm from the book

    public void notify(Program program, String name) throws AsmError {
        if (influences.containsKey(name))
            for (Symbol s : influences.get(name))
                if (s.decDependencyCount()) update(program, s.name);
    }

    public void update(Program program, String name) throws AsmError {
        Symbol sym = syms.get(name);
        if (sym.dependencyCount() != 0 || sym.isEvaluated()) return;
        sym.eval(program);
        notify(program, name);
    }

}
