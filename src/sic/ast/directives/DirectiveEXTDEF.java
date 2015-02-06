package sic.ast.directives;

import sic.asm.Location;
import sic.common.Mnemonic;

import java.util.List;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class DirectiveEXTDEF extends Directive {

    public final List<String> names;

    public DirectiveEXTDEF(Location loc, String label, Mnemonic mnemonic, List<String> names) {
        super(loc, label, mnemonic);
        this.names = names;
    }

    @Override
    public String operandToString() {
        StringBuilder buf = new StringBuilder();
        buf.append(names.get(0));
        for (int i = 1; i < names.size(); i++) {
            buf.append(',');
            buf.append(names.get(i));
        }
        return buf.toString();
    }

}
