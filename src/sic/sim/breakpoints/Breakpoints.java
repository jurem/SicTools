package sic.sim.breakpoints;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: jure
 */
public class Breakpoints {

    // list of addresses of breakpoints
    private List<Integer> addrs;

    public boolean has(int addr) {
        return addrs.contains(addr);
    }

    public void add(int addr) {
        if (!has(addr)) addrs.add(addr);
    }

    public void remove(int addr) {
        addrs.remove((Integer)addr);
    }

    public void removeAll() {
        addrs = new ArrayList<Integer>();
    }

    public void toggleBreakpoint(int addr) {
        if (has(addr)) remove(addr); else add(addr);
    }

    public Breakpoints() {
        addrs = new ArrayList<Integer>();
    }

}
