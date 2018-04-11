package sic.sim.addons;

import sic.sim.Executor;
import sic.sim.breakpoints.DataBreakpoints;
import sic.sim.vm.Memory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DataBreakpointView {

    Memory memory;
    DataBreakpoints dataBreakpoints;

    // GUI
    JFrame view;

    public DataBreakpointView(Executor executor) {
        this.memory = executor.machine.memory;
        this.dataBreakpoints = this.memory.dataBreakpoints;
        this.view = createView();
    }

    private JFrame createView() {
        JPanel bevel = new JPanel();
        bevel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        bevel.setLayout(new java.awt.BorderLayout());
        //bevel.add(txtScreen);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setLayout(new BorderLayout());
        panel.add(bevel);

        JFrame frame = new JFrame("Memory breakpoints");
        frame.setResizable(false);
//        frame.setBounds(620, 370, 500, 300);
        frame.setContentPane(panel);

        return frame;
    }

}
