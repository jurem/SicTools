package sic.ast;

import sic.asm.AsmError;
import sic.ast.storage.StorageData;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class Literals {

    public final List<StorageData> literals;	// list of all literals (BYTE directives)
    private int lastFlushed;			// index of last literal flush to code
    private int maxLabelLength;

    public Literals() {
        literals = new ArrayList<StorageData>();
        maxLabelLength = 6;
    }

    public int size() {
        return literals.size();
    }

    public int maxLabelLength() {
        return maxLabelLength;
    }

    public String makeUniqLabel() {
        return "*" + literals.size();
    }

    public void append(StorageData literal) {
        literals.add(literal);
        if (literal.label().length() > maxLabelLength) maxLabelLength = literal.label().length();
    }

    public StorageData find(StorageData me) {
        for (int i = lastFlushed; i < literals.size(); i++) {
            StorageData literal = literals.get(i);
            if (me.equals(literal)) return literal;
        }
        return null;
    }

    public void flush(Program program) throws AsmError {
        while (lastFlushed < literals.size())
            program.append(literals.get(lastFlushed++));
    }

}
