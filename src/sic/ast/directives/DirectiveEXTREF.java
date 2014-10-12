package sic.ast.directives;

import sic.asm.Location;
import sic.common.Mnemonic;

import java.util.List;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class DirectiveEXTREF extends DirectiveEXTDEF {

    public DirectiveEXTREF(Location loc, String label, Mnemonic mnemonic, List<String> names) {
        super(loc, label, mnemonic, names);
    }

}
