package sic.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class ErrorCatcher {

    public final List<AsmError> errs;

    private int lastPrinted;

    public ErrorCatcher() {
        this.errs = new ArrayList<AsmError>();
    }

    public void clear() {
        errs.clear();
        lastPrinted = 0;
    }

    public int count() {
        return errs.size();
    }

    public void add(AsmError err) {
        errs.add(err);
        Collections.sort(errs);
    }

    public void printByRow(int row) {
        for (AsmError err : errs)
            if (err.loc.row == row)
                System.err.println(err);
    }

    public void print() {
        for ( ; lastPrinted < errs.size(); lastPrinted++) {
            AsmError err = errs.get(lastPrinted);
            System.err.println(err);
        }
    }

    public boolean shouldEnd() {
        return errs.stream().anyMatch(AsmError::isBreaking);
    }

}
